package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Rock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.koropets_suhanov.chess.process.pojo.Parameter;
import com.koropets_suhanov.chess.process.pojo.Turn;
import scala.Tuple2;

/**
 * @author AndriiKoropets
 */
public class EstimatePosition {

    Map<String, Tuple2<Observer, Parameter>> turnToReflection = new HashMap<>();
    private static Color whoseTurn;

    static Parameter estimate(Turn turn, Set<Turn> possibleTurns, Color side){
        return new Parameter.Builder().first(estimateFirstParameter(turn, possibleTurns))
                .second(estimateSecondParameter(turn, possibleTurns))
                .third(estimateThirdParameter(turn, possibleTurns))
                .fourth(estimateFourthParameter(turn, possibleTurns))
                .fifth(estimateFifthParameter(turn, possibleTurns))
                .sixth(estimateSixthParameter(turn, possibleTurns))
                .seventh(estimateSeventhParameter(turn, possibleTurns))
                .eighth(estimateEightParameter(turn, possibleTurns))
                .build();
    }

    private static int estimateEightParameter(final Turn turn, final Set<Turn> possibleTurns) {
        int parameter = 0;
        return 0;
    }

    private static int estimateSeventhParameter(final Turn turn, final Set<Turn> possibleTurns) {
        return 0;
    }

    private static int estimateSixthParameter(final Turn turn, final Set<Turn> possibleTurns) {
        return 0;
    }

    private static int estimateFifthParameter(final Turn turn, final Set<Turn> possibleTurns) {
        return 0;
    }

    private static int estimateFourthParameter(final Turn turn, final Set<Turn> possibleTurns) {
        return 0;
    }

    private static int estimateThirdParameter(final Turn turn, final Set<Turn> possibleTurns) {
        return 0;
    }

    private static int estimateSecondParameter(final Turn turn, final Set<Turn> possibleTurns) {
        int parameter = 0;
        Figure figure = null;
        Set<Observer> enemies = Board.getFigures(whoseTurn);
        for (Figure temp : turn.getFigures().keySet()){
            if (turn.getFigures().size() == 1){
                figure = temp;
                break;
            }else if (temp.getClass() == Rock.class){
                figure = temp;
            }
        }
        if (figure != null){
            for (Observer enemyObserver : enemies){
                Figure enemy = ((Figure) enemyObserver);
                for (Figure prey : enemy.getWhoCouldBeEaten()){
                    if (prey.equals(figure)){
                        parameter++;
                    }
                }
            }
        }else {
            throw new RuntimeException("Could not choose figure from actual turn");
        }
        return parameter;
    }

    private static int estimateFirstParameter(final Turn turn, final Set<Turn> possibleTurns){
        return estimateTurnFirstParam(turn) + estimatePositionFirstParam();
    }

    private static int estimatePositionFirstParam() {
        int parameter = 0;
        Set<Observer> figures = Board.getFigures(whoseTurn);
        for (Observer observer : figures){
            Figure currentFigure = ((Figure) observer);
            for (Figure alien : ((Figure) observer).getAliensProtectMe()){
                for (Figure prey : currentFigure.getWhoCouldBeEaten()){
                    Field preyField = prey.getField();
                    if (alien.getFieldsUnderMyInfluence().contains(preyField) && !alien.getPossibleFieldsToMove().contains(preyField) && !alien.getPreyField().contains(preyField)){
                        parameter++;
                        prey.getEnemiesAttackMe().add(alien);
                    }
                }
            }
        }
        return parameter;
    }

    private static int estimateTurnFirstParam(final Turn turn) {
        int parameter = 0;
        //TODO add logic for attacking via ally.
        for (Figure figure : turn.getFigures().keySet()){
            for (Figure prey : figure.getWhoCouldBeEaten()){
                if (prey.getEnemiesAttackMe().size() >= prey.getAliensProtectMe().size() || prey.getValue() >= figure.getValue()){
                    parameter += prey.getPoint();
                }
            }
        }
        return parameter;
    }
}
