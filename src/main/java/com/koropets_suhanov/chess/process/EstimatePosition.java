package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.model.*;

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
        whoseTurn = side;
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
        return firstParamActualAttack(turn) + firstParamAttackViaAlly(turn)/* + firstParamAttackOthersAllies(turn)*/;
    }

    private static int firstParamAttackOthersAllies(Turn turn){
        int curFirParExceptActiveFigures = 0;
        int previousState = (whoseTurn == Color.WHITE) ? Process.fullWhiteEstimation.getFirstAttackEnemy() :
                Process.fullBlackEstimation.getFirstAttackEnemy();
        Set<Observer> allies = Board.getFigures(whoseTurn);
        for (Observer observer : allies){
            Figure ally = (Figure)observer;
            for (Figure figureOfTheTurn : turn.getFigures().keySet()){
                if (!ally.equals(figureOfTheTurn)){
                    curFirParExceptActiveFigures += ally.getWhoCouldBeEaten().size();
                }
            }
        }
        return previousState - curFirParExceptActiveFigures;
    }

    private static int firstParamAttackViaAlly(Turn turn) {
        int parameter = 0;
//        System.out.println("All =========");
//        for (Observer observer : allies){
//            System.out.println(observer);
//        }
//        System.out.println("--------------");
        for (Figure curFigure : turn.getFigures().keySet()){
            if (curFigure.getClass() == Queen.class){
                System.out.println("here");
                System.out.println(curFigure.getAttackedFields());
            }
            for (Figure ally : curFigure.getAlliesIProtect()){
                System.out.println("Ally = " + ally);
                for (Figure prey : ally.getWhoCouldBeEaten()){
                    Field preyField = prey.getField();
                    System.out.println("PreyField = " + preyField + " for such ally" + ally);
                    if (curFigure.getAttackedFields().contains(preyField) && !curFigure.getPreyField().contains(preyField)){
                        parameter += prey.getPoint();
                        System.out.println("Parameter = " + parameter);
                    }
                }
//                for (Figure prey : currentFigure.getWhoCouldBeEaten()){
//                    Field preyField = prey.getField();
//                    if (ally.getFieldsUnderMyInfluence().contains(preyField) && !ally.getPossibleFieldsToMove().contains(preyField) && !ally.getPreyField().contains(preyField)){
//                        parameter++;
//                        prey.getEnemiesAttackMe().add(ally);
//                    }
//                }
            }
        }
        System.out.println("Parameter via = " + parameter);
        return parameter;
    }

    private static int firstParamActualAttack(final Turn turn) {
        int parameter = 0;
        for (Figure figure : turn.getFigures().keySet()){
            for (Figure prey : figure.getWhoCouldBeEaten()){
                if (prey.getEnemiesAttackMe().size() >= prey.getAlliesProtectMe().size() || prey.getValue() >= figure.getValue()){
                    parameter += prey.getPoint();
                }
            }
        }
        System.out.println("Parameter actual = " + parameter);
        return parameter;
    }
}
