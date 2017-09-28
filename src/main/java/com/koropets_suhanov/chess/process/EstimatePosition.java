package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Rock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.abs;

import com.koropets_suhanov.chess.process.pojo.Parameter;
import com.koropets_suhanov.chess.process.pojo.Turn;
import com.koropets_suhanov.chess.utils.ProcessingUtils;
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

    private static int estimateSecondParameter(final Turn turn, final Set<Turn> possibleTurns){
        return secParamActualSubstitution(turn) + secParamSubstitutionViaFigure(turn);
    }

     private static int secParamSubstitutionViaFigure(final Turn turn){
        int param = 0;
        Figure figure = retrieveFigureFromTurn(turn);
         Set<Observer> enemies = (whoseTurn == Color.BLACK) ? Board.getFigures(Color.WHITE) : Board.getFigures(Color.BLACK);
        //TODO
        return param;
     }

    private static int secParamActualSubstitution(final Turn turn) {
        int parameter = 0;
        Figure figure = retrieveFigureFromTurn(turn);
        Set<Observer> enemies = (whoseTurn == Color.BLACK) ? Board.getFigures(Color.WHITE) : Board.getFigures(Color.BLACK);
        for (Observer enemyObserver : enemies){
            Figure enemy = ((Figure) enemyObserver);
            for (Figure prey : enemy.getWhoCouldBeEaten()){
                if (prey.equals(figure)){
                    parameter += figure.getPoint();
                }
            }
        }
        return parameter;
    }

    private static Figure retrieveFigureFromTurn(final Turn turn){
        if (turn.getFigures().size() == 1){
            return turn.getFigures().keySet().iterator().next();
        }
        for (Figure temp : turn.getFigures().keySet()){
            if (temp.getClass() == Rock.class){
                return  temp;
            }
        }
        throw new RuntimeException("Could not choose figure from actual turn");
    }

    private static int estimateFirstParameter(final Turn turn, final Set<Turn> possibleTurns){
        return firstParamActualAttack(turn) + firstParamAttackViaAlly(turn) + firstParamAttackOthersAllies(turn);
    }

    private static int firstParamAttackOthersAllies(Turn turn){
        int parameter = 0;
        Set<Figure> acceptedFigures = null;
        for (Figure f : turn.getFigures().keySet()){
            if (acceptedFigures == null){
                acceptedFigures = ProcessingUtils.getFiguresAffectField(turn.getFigures().get(f), whoseTurn);
                acceptedFigures.addAll(ProcessingUtils.getFiguresAffectField(f.getField(), whoseTurn));
            }else {
                acceptedFigures.addAll(ProcessingUtils.getFiguresAffectField(turn.getFigures().get(f), whoseTurn));
                acceptedFigures.addAll(ProcessingUtils.getFiguresAffectField(f.getField(), whoseTurn));
            }
        }

        Set<Figure> firstLineFigure = new HashSet<>();
        for (Figure f : acceptedFigures){
            if (isCollectionsChanged(f)){
                firstLineFigure.add(f);
            }
        }

        Map<Figure, Set<Figure>> figureToItsPreys = new HashMap<>();

        for (Figure f : firstLineFigure){
            Set<Figure> preys = appearedPreys(f);
            figureToItsPreys.put(f, preys);
        }

        for (Figure curFigure : figureToItsPreys.keySet()){
            for (Figure prey : figureToItsPreys.get(curFigure)){
                for (Figure ally : curFigure.getAlliesIProtect()){
                    updateWhoCouldBeEaten(curFigure, ally, prey);
                }
            }
        }

        for (Figure f : acceptedFigures){
            for (Figure prey : f.getWhoCouldBeEaten()){
                if (!f.getWhoCouldBeEatenPreviousState().contains(prey)){
                    parameter += prey.getPoint();
                }
            }
        }

        return parameter;
    }

    private static int firstParamAttackViaAlly(Turn turn) {
        int parameter = 0;
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
                    if (curFigure.getAttackedFields().contains(preyField) && !curFigure.getPreyField().contains(preyField)
                            && isOnTheSameLine(curFigure, ally, prey)){
                        curFigure.getWhoCouldBeEaten().add(prey);
                        parameter += prey.getPoint();
                        System.out.println("Parameter = " + parameter);
                    }
                }
            }
        }
//        System.out.println("Parameter via = " + parameter);
        return parameter;
    }

    private static boolean isOnTheSameLine(Figure f1, Figure f2, Figure f3){
        return ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX())) ||
                ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY())) ||
                (((abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
                        && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY())))
                        && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY())));
    }

    private static boolean isCollectionsChanged(Figure figure){
        return !figure.getWhoCouldBeEatenPreviousState().containsAll(figure.getWhoCouldBeEaten());
    }

    private static void updateWhoCouldBeEaten(Figure curFigure, Figure ally, Figure prey){
        if (!curFigure.getWhoCouldBeEaten().contains(prey) && ally.getWhoCouldBeEaten().contains(prey)
                && curFigure.getAttackedFields().contains(prey.getField()) && isOnTheSameLine(curFigure, ally, prey)){
            curFigure.getWhoCouldBeEaten().add(prey);
            for (Figure f : curFigure.getAlliesIProtect()){
                if (!f.equals(ally)){
                    updateWhoCouldBeEaten(f, curFigure, prey);
                }
            }
        }
    }

    private static Set<Figure> appearedPreys(final Figure curFigure){
        Set<Figure> appearedPreys = new HashSet<>();
        Set<Figure> preysBefore = curFigure.getWhoCouldBeEatenPreviousState();
        Set<Figure> preysNow = curFigure.getWhoCouldBeEaten();
        for (Figure f : preysNow){
            if (!preysBefore.contains(f)){
                appearedPreys.add(f);
            }
        }
        return appearedPreys;
    }

    private static int firstParamActualAttack(final Turn turn) {
        int parameter = 0;
        for (Figure figure : turn.getFigures().keySet()){
            System.out.println(figure.getAttackedFields());
            System.out.println(figure.getPossibleFieldsToMove());
            System.out.println("Previoud = " + figure.getWhoCouldBeEatenPreviousState());
            System.out.println(figure.getWhoCouldBeEaten());
            System.out.println(figure.getAlliesProtectMe());
            System.out.println(" ---- " + figure.getAlliesIProtect());
            for (Figure prey : figure.getWhoCouldBeEaten()){
                if (prey.getEnemiesAttackMe().size() >= prey.getAlliesProtectMe().size() || prey.getValue() >= figure.getValue()){
                    parameter += prey.getPoint();
                }
            }
        }
//        System.out.println("Parameter actual = " + parameter);
        return parameter;
    }
}
