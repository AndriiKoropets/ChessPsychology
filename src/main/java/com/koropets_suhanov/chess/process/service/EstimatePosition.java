package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Board;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.koropets_suhanov.chess.process.dto.AntiParameter;
import com.koropets_suhanov.chess.process.dto.Parameter;
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.utils.ProcessingUtils;
import scala.Tuple2;

public class EstimatePosition {

    private static Color whoseTurn;

    static Parameter estimate(Turn turn, Set<Turn> possibleTurns, Color side){
        whoseTurn = side;
        int firstParam = estimateFirstParameter();
        int secondParam = estimateSecondParameter();
        int thirdParam = estimateThirdParameter();
        int fourthParam = estimateFourthParameter();
        List<Tuple2<Turn, AntiParameter>> turnToAntiParameter = estimateAntiParameter(turn, possibleTurns);
        Tuple2<Turn, Integer> estimatedFifthParameter = estimateFifthParameter(turnToAntiParameter);
        Tuple2<Turn, Integer> estimatedSixthParameter = estimateSixthParameter(turnToAntiParameter);
        Tuple2<Turn, Integer> estimatedSeventhParameter = estimateSeventhParameter(turnToAntiParameter);
        Tuple2<Turn, Integer> estimatedEighthParameter = estimateEightParameter(turnToAntiParameter);
        Tuple2<Integer, List<Tuple2<Figure, Field>>> fifthParamToInvolvedFigures = new Tuple2<>(estimatedFifthParameter._2 - firstParam, estimatedFifthParameter._1.getFigureToDestinationField());
        Tuple2<Integer, List<Tuple2<Figure, Field>>> sixthParamToInvolvedFigures = new Tuple2<>(estimatedSixthParameter._2 - secondParam, estimatedSixthParameter._1.getFigureToDestinationField());
        Tuple2<Integer, List<Tuple2<Figure, Field>>> seventhParamToInvolvedFigures = new Tuple2<>(estimatedSeventhParameter._2 - thirdParam, estimatedSeventhParameter._1.getFigureToDestinationField());
        Tuple2<Integer, List<Tuple2<Figure, Field>>> eighthParamToInvolvedFigures = new Tuple2<>(estimatedEighthParameter._2 - fourthParam, estimatedEighthParameter._1.getFigureToDestinationField());
        ProcessingUtils.makeTurn(turn);
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

    private static Tuple2<Turn, Integer> estimateEightParameter(final List<Tuple2<Turn, AntiParameter>> turnAntiParameterMap) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (Tuple2<Turn, AntiParameter> curTuple : turnAntiParameterMap){
            int paramPerTurn = curTuple._2.getEighthParam();
            if (paramPerTurn >= max){
                max = paramPerTurn;
                turnOfTheMaxParam = curTuple._1;
            }
        }
        if (turnOfTheMaxParam == null){
            throw new RuntimeException("Unexpected exception during estimating eighth parameter");
        }
        return new Tuple2<>(turnOfTheMaxParam, max);
    }

    private static Tuple2<Turn, Integer> estimateSeventhParameter(final List<Tuple2<Turn, AntiParameter>> turnAntiParameterMap) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (Tuple2<Turn, AntiParameter> curTuple : turnAntiParameterMap){
            int paramPerTurn = curTuple._2.getSeventhParam();
            if (paramPerTurn >= max){
                max = paramPerTurn;
                turnOfTheMaxParam = curTuple._1;
            }
        }
        if (turnOfTheMaxParam == null){
            throw new RuntimeException("Unexpected exception during estimating seventh parameter");
        }
        return new Tuple2<>(turnOfTheMaxParam, max);
    }

    private static Tuple2<Turn, Integer> estimateSixthParameter(final List<Tuple2<Turn, AntiParameter>> turnAntiParameterMap) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (Tuple2<Turn, AntiParameter> curTuple2 : turnAntiParameterMap){
            int paramPerTurn = curTuple2._2.getSixthParam();
            if (paramPerTurn >= max){
                max = paramPerTurn;
                turnOfTheMaxParam = curTuple2._1;
            }
        }
        if (turnOfTheMaxParam == null){
            throw new RuntimeException("Unexpected exception during estimating sixth parameter");
        }
        return new Tuple2<>(turnOfTheMaxParam, max);
    }

    private static Tuple2<Turn, Integer> estimateFifthParameter(final List<Tuple2<Turn, AntiParameter>> turnAntiParameterMap) {
        int max = 0;
        Turn turnOfTheMaxParam = null;
        for (Tuple2<Turn, AntiParameter> curTuple : turnAntiParameterMap){
            int paramPerTurn = curTuple._2.getFifthParam();
            if (paramPerTurn >= max){
                max = paramPerTurn;
                turnOfTheMaxParam = curTuple._1;
            }
        }
        if (turnOfTheMaxParam == null){
            throw new RuntimeException("Unexpected exception during estimating fifth parameter");
        }
        return new Tuple2<>(turnOfTheMaxParam, max);
    }

    private static List<Tuple2<Turn, AntiParameter>> estimateAntiParameter(final Turn turn, final Set<Turn> possibleTurns){
        ProcessingUtils.undoTurn(turn);
        List<Tuple2<Turn, AntiParameter>> turnAntiParameterMap = new ArrayList<>();
        for (Turn posTurn : possibleTurns){
            if (!posTurn.equals(turn)){
                ProcessingUtils.makeTurn(posTurn);
                AntiParameter antiParameter = AntiParameter.builder()
                        .fifthParam(estimateFirstParameter())
                        .sixthParam(estimateSecondParameter())
                        .seventhParam(estimateThirdParameter())
                        .eighthParam(estimateFourthParameter())
                        .build();
                turnAntiParameterMap.add(new Tuple2<>(posTurn, antiParameter));
                ProcessingUtils.undoTurn(posTurn);
            }
        }
        return turnAntiParameterMap;
    }

    private static int estimateFourthParameter() {
        List<Observer> enemies = (whoseTurn == Color.WHITE) ? Board.getFigures(Color.BLACK) : Board.getFigures(Color.WHITE);
        return calculateWithdrawingAttackAndBeUnderAttack(enemies);
    }

    private static int estimateThirdParameter() {
        List<Observer> alliesObservers = Board.getFigures(whoseTurn);
        return calculateWithdrawingAttackAndBeUnderAttack(alliesObservers);
    }

    private static int estimateSecondParameter(){
        List<Observer> enemies = (whoseTurn == Color.WHITE) ? Board.getFigures(Color.BLACK) : Board.getFigures(Color.WHITE);
        return calculateAttackAndBeUnderAttack(enemies);
    }

    private static int estimateFirstParameter(){
        List<Observer> chosenFigures = Board.getFigures(whoseTurn);
        return calculateAttackAndBeUnderAttack(chosenFigures);
    }

    private static int calculateAttackAndBeUnderAttack(List<Observer> figures){
        int param = 0;
        Set<Figure> chosenFigures = new HashSet<>();
        figures.forEach(o -> {
            if (isPreysBecameBigger(((Figure)o).getWhoCouldBeEatenPreviousState(), ((Figure)o).getWhoCouldBeEaten())){
                chosenFigures.add(((Figure)o));
            }
        });
        for (Figure curFigure : chosenFigures){
            for (Figure prey : curFigure.getWhoCouldBeEaten()){
                if (!curFigure.getWhoCouldBeEatenPreviousState().contains(prey) &&
                        (prey.getEnemiesAttackMe().size() >= prey.getAlliesProtectMe().size() ||
                                curFigure.getValue() < prey.getValue())){
                    param += prey.getPoint();
                }
            }
        }
        return param;
    }

    private static int calculateWithdrawingAttackAndBeUnderAttack(List<Observer> figures){
        int param = 0;
        Set<Figure> chosenFigures = new HashSet<>();
        figures.forEach(o -> {
            if (isPreysBecameSmaller(((Figure)o).getWhoCouldBeEatenPreviousState(), ((Figure)o).getWhoCouldBeEaten())){
                chosenFigures.add(((Figure)o));
            }
        });
        for (Figure curFigure : chosenFigures){
            for (Figure prevPrey : curFigure.getWhoCouldBeEatenPreviousState()){
                if (!curFigure.getWhoCouldBeEaten().contains(prevPrey)){
                    param += prevPrey.getPoint();
                }
            }
        }
        return param;
    }

    private static boolean isPreysBecameBigger(Set<Figure> previousPreys, Set<Figure> curPreys){
        for (Figure curPrey : curPreys){
            if (!previousPreys.contains(curPrey)){
                return true;
            }
        }
        return false;
    }

    private static boolean isPreysBecameSmaller(Set<Figure> previousPreys, Set<Figure> curPreys){
        for (Figure prevPrey : previousPreys){
            if (!curPreys.contains(prevPrey)) {
                return true;
            }
        }
        return false;
    }
}