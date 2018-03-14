package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Board;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.koropets_suhanov.chess.process.dto.AntiParameter;
import com.koropets_suhanov.chess.process.dto.TurnAntiParameter;
import com.koropets_suhanov.chess.process.dto.TurnWeight;
import com.koropets_suhanov.chess.process.dto.WeightAndDestinations;
import com.koropets_suhanov.chess.process.dto.Parameter;
import com.koropets_suhanov.chess.process.dto.Turn;

import static com.koropets_suhanov.chess.process.service.Process.positionInfluence;
import static com.koropets_suhanov.chess.process.service.Process.currentColor;

public class EstimatePosition {

    public Parameter estimate(Turn turn, Set<Turn> possibleTurns) {
        int firstParam = estimateFirstParameter();
        int secondParam = estimateSecondParameter();
        int thirdParam = estimateThirdParameter();
        int fourthParam = estimateFourthParameter();

        List<TurnAntiParameter> turnToAntiParameter = estimateAntiParameter(turn, possibleTurns);
        TurnWeight estimatedFifthParameter = estimateFifthParameter(turnToAntiParameter);
        TurnWeight estimatedSixthParameter = estimateSixthParameter(turnToAntiParameter);
        TurnWeight estimatedSeventhParameter = estimateSeventhParameter(turnToAntiParameter);
        TurnWeight estimatedEighthParameter = estimateEightParameter(turnToAntiParameter);

        WeightAndDestinations fifthParamToInvolvedFigures = WeightAndDestinations.builder().weight(estimatedFifthParameter.getWeight() - firstParam).figureToFields(estimatedFifthParameter.getTurn().getFigureToDestinationField()).build();
        WeightAndDestinations sixthParamToInvolvedFigures = WeightAndDestinations.builder().weight(estimatedSixthParameter.getWeight() - secondParam).figureToFields(estimatedSixthParameter.getTurn().getFigureToDestinationField()).build();
        WeightAndDestinations seventhParamToInvolvedFigures = WeightAndDestinations.builder().weight(estimatedSeventhParameter.getWeight() - thirdParam).figureToFields(estimatedSeventhParameter.getTurn().getFigureToDestinationField()).build();
        WeightAndDestinations eighthParamToInvolvedFigures = WeightAndDestinations.builder().weight(estimatedEighthParameter.getWeight() - fourthParam).figureToFields(estimatedEighthParameter.getTurn().getFigureToDestinationField()).build();

        positionInfluence.makeTurn(turn);

        return Parameter.builder()
                .firstAttackEnemy(firstParam)
                .secondBeUnderAttack(secondParam)
                .thirdWithdrawAttackOnEnemy(thirdParam)
                .fourthWithdrawAttackOnMe(fourthParam)
                .fifthDontTakeAChanceToAttack(fifthParamToInvolvedFigures)
                .sixthDontTakeAChanceToBeUnderAttack(sixthParamToInvolvedFigures)
                .seventhDontTakeAChanceToWithdrawAttackOnEnemy(seventhParamToInvolvedFigures)
                .eighthDontTakeAChanceToWithdrawAttackOnMe(eighthParamToInvolvedFigures)
                .build();
    }

    private int estimateFirstParameter() {
        List<Observer> chosenFigures = Board.getFiguresByColor(currentColor);
        return calculateAttackAndBeUnderAttack(chosenFigures);
    }

    private int estimateSecondParameter() {
        List<Observer> enemies = (currentColor == Color.WHITE) ? Board.getFiguresByColor(Color.BLACK) : Board.getFiguresByColor(Color.WHITE);
        return calculateAttackAndBeUnderAttack(enemies);
    }

    private int calculateAttackAndBeUnderAttack(List<Observer> figures) {
        int param = 0;
        Set<Figure> chosenFigures = new HashSet<>();
        figures.forEach(o -> {
            if (isPreysBecameBigger(((Figure) o).getWhoCouldBeEatenPreviousState(), ((Figure) o).getWhoCouldBeEaten())) {
                chosenFigures.add(((Figure) o));
            }
        });
        for (Figure curFigure : chosenFigures) {
            for (Figure prey : curFigure.getWhoCouldBeEaten()) {
                if (!curFigure.getWhoCouldBeEatenPreviousState().contains(prey) &&
                        (prey.getEnemiesAttackMe().size() >= prey.getAlliesProtectMe().size() ||
                                curFigure.getValue() < prey.getValue())) {
                    param += prey.getPoint();
                }
            }
        }
        return param;
    }

    private boolean isPreysBecameBigger(Set<Figure> previousPreys, Set<Figure> curPreys) {
        for (Figure curPrey : curPreys) {
            if (!previousPreys.contains(curPrey)) {
                return true;
            }
        }
        return false;
    }

    private int estimateThirdParameter() {
        List<Observer> alliesObservers = Board.getFiguresByColor(currentColor);
        return calculateWithdrawingAttackAndBeUnderAttack(alliesObservers);
    }

    private int estimateFourthParameter() {
        List<Observer> enemies = (currentColor == Color.WHITE) ? Board.getFiguresByColor(Color.BLACK) : Board.getFiguresByColor(Color.WHITE);
        return calculateWithdrawingAttackAndBeUnderAttack(enemies);
    }

    private int calculateWithdrawingAttackAndBeUnderAttack(List<Observer> figures) {
        int param = 0;
        Set<Figure> chosenFigures = new HashSet<>();
        figures.forEach(o -> {
            if (isPreysBecameSmaller(((Figure) o).getWhoCouldBeEatenPreviousState(), ((Figure) o).getWhoCouldBeEaten())) {
                chosenFigures.add(((Figure) o));
            }
        });
        for (Figure curFigure : chosenFigures) {
            for (Figure prevPrey : curFigure.getWhoCouldBeEatenPreviousState()) {
                if (!curFigure.getWhoCouldBeEaten().contains(prevPrey)) {
                    param += prevPrey.getPoint();
                }
            }
        }
        return param;
    }

    private boolean isPreysBecameSmaller(Set<Figure> previousPreys, Set<Figure> curPreys) {
        for (Figure prevPrey : previousPreys) {
            if (!curPreys.contains(prevPrey)) {
                return true;
            }
        }
        return false;
    }

    private List<TurnAntiParameter> estimateAntiParameter(final Turn turn, final Set<Turn> possibleTurns) {
        positionInfluence.undoTurn(turn);
        List<TurnAntiParameter> turnAntiParameterMap = new ArrayList<>();
        for (Turn posTurn : possibleTurns) {
            if (!posTurn.equals(turn)) {
                positionInfluence.makeTurn(posTurn);
                AntiParameter antiParameter = AntiParameter.builder()
                        .fifthParam(estimateFirstParameter())
                        .sixthParam(estimateSecondParameter())
                        .seventhParam(estimateThirdParameter())
                        .eighthParam(estimateFourthParameter())
                        .build();
                turnAntiParameterMap.add(TurnAntiParameter.builder().turn(posTurn).antiParameter(antiParameter).build());
                positionInfluence.undoTurn(posTurn);
            }
        }
        return turnAntiParameterMap;
    }

    private TurnWeight estimateFifthParameter(final List<TurnAntiParameter> allTurnsWithAntiParameters) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (TurnAntiParameter candidate : allTurnsWithAntiParameters) {
            int paramPerTurn = candidate.getAntiParameter().getFifthParam();
            if (paramPerTurn >= max) {
                max = paramPerTurn;
                turnOfTheMaxParam = candidate.getTurn();
            }
        }
        if (turnOfTheMaxParam == null) {
            throw new RuntimeException("Estimating fifth parameter. Could not define turn with highest weight");
        }
        return TurnWeight.builder().turn(turnOfTheMaxParam).weight(max).build();
    }

    private TurnWeight estimateSixthParameter(final List<TurnAntiParameter> allTurnsWithAntiParameters) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (TurnAntiParameter candidate : allTurnsWithAntiParameters) {
            int paramPerTurn = candidate.getAntiParameter().getSixthParam();
            if (paramPerTurn >= max) {
                max = paramPerTurn;
                turnOfTheMaxParam = candidate.getTurn();
            }
        }
        if (turnOfTheMaxParam == null) {
            throw new RuntimeException("Estimating sixth parameter. Could not define turn with highest weight");
        }
        return TurnWeight.builder().turn(turnOfTheMaxParam).weight(max).build();
    }

    private TurnWeight estimateSeventhParameter(final List<TurnAntiParameter> allTurnsWithAntiParameters) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (TurnAntiParameter candidate : allTurnsWithAntiParameters) {
            int paramPerTurn = candidate.getAntiParameter().getSeventhParam();
            if (paramPerTurn >= max) {
                max = paramPerTurn;
                turnOfTheMaxParam = candidate.getTurn();
            }
        }
        if (turnOfTheMaxParam == null) {
            throw new RuntimeException("Estimating seventh parameter. Could not define turn with highest weight");
        }
        return TurnWeight.builder().turn(turnOfTheMaxParam).weight(max).build();
    }

    private TurnWeight estimateEightParameter(final List<TurnAntiParameter> allTurnsWithAntiParameters) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (TurnAntiParameter candidate : allTurnsWithAntiParameters) {
            int paramPerTurn = candidate.getAntiParameter().getEighthParam();
            if (paramPerTurn >= max) {
                max = paramPerTurn;
                turnOfTheMaxParam = candidate.getTurn();
            }
        }
        if (turnOfTheMaxParam == null) {
            throw new RuntimeException("Estimating eight parameter. Could not define turn with highest weight");
        }
        return TurnWeight.builder().turn(turnOfTheMaxParam).weight(max).build();
    }
}