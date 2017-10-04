package com.koropets_suhanov.chess.utils;

import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Knight;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.process.Game;
import com.koropets_suhanov.chess.process.Process;
import com.koropets_suhanov.chess.process.pojo.Turn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import static java.lang.Math.abs;

/**
 * @author AndriiKoropets
 */
public class ProcessingUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingUtils.class);
    private static List<Observer> candidates;
    private static String mainTurn;
    private static Field field;
    private static int number;
    private static Set<Field> affectedFields;
    private static Map<Figure, Field> figureToField = new HashMap<>();
    private static Figure figure;
    private static boolean isEating;
    private static Figure targetedFigure;
    public static final String shortCastling = "0-0";
    public static final String longCastling = "0-0-0";
    private static final Field whiteKingShortCastling = new Field(7, 6);
    private static final Field whiteKingLongCastling = new Field(7, 2);
    private static final Field blackKingShortCastling = new Field(0, 6);
    private static final Field blackKingLongCastling = new Field(0, 2);
    private static final Field whiteRockShortCastling = new Field(7, 5);
    private static final Field whiteRockLongCastling = new Field(7, 3);
    private static final Field blackRockShortCastling = new Field(0, 5);
    private static final Field blackRockLongCastling = new Field(0, 3);
    private static final FrequentFigure whiteFrequent = new FrequentFigure();
    private static final FrequentFigure blackFrequent = new FrequentFigure();

    public static Turn getActualTurn(final String turnWrittenStyle, final boolean isWhite, int numberOfTurn){
        candidates = new ArrayList<>();
        mainTurn = turnWrittenStyle;
        number = numberOfTurn;
        field = parseTargetField(turnWrittenStyle);
        return setTurn(turnWrittenStyle, isWhite);
    }

    public static Turn getPossibleTurn(){
        //TODO implement logic for possible turns which was not made in party
        return null;
    }

    public static Tuple2<FrequentFigure, FrequentFigure> countFrequent(boolean isWhite, String writtenTurn){
        FrequentFigure frequent = isWhite ? whiteFrequent : blackFrequent;
        char figure = writtenTurn.charAt(0);
        switch (figure){
            case 'R' :  frequent.updateRock();
            case 'N' : frequent.updateKnight();
            case 'B' : frequent.updateBishop();
            case 'Q' : frequent.updateQueen();
            case 'K' : frequent.updateKing();
            case '0' : frequent.updateKing();
            default: frequent.updatePawn();
        }
        return new Tuple2<>(whiteFrequent, blackFrequent);
    }

    private static void initialize(){
        candidates.clear();
        figureToField.clear();
        figure = null;
        targetedFigure = null;
        isEating = false;
    }

//    private static void update(){
//        if (figure != null){
//            figureToField.put(figure, field);
//            isEating = eating;
//        }/*else {
//            throw new RuntimeException("Could not read written turn");
//        }*/
//    }

    private static Turn setTurn(final String writtenStyle, final boolean isWhite){
        initialize();
        if (shortCastling.equals(writtenStyle)){
            Map<Figure, Field> figureToField = new HashMap<>();
            if (isWhite){
                Figure whiteKing = Board.getFieldToFigure().get(Game.e1);
                Figure whiteRock_H = Board.getFieldToFigure().get(Game.h1);
                figureToField.put(whiteKing, whiteKingShortCastling);
                figureToField.put(whiteRock_H, whiteRockShortCastling);
            }else {
                Figure blackKing = Board.getFieldToFigure().get(Game.e8);
                Figure blackRock_H = Board.getFieldToFigure().get(Game.h8);
                figureToField.put(blackKing, blackKingShortCastling);
                figureToField.put(blackRock_H, blackRockShortCastling);
            }
            return createTurn(figureToField, writtenStyle, false, null, number);
        }
        if (longCastling.equals(writtenStyle)){
            Map<Figure, Field> figureToField = new HashMap<>();
            if (isWhite){
                Figure whiteKing = Board.getFieldToFigure().get(Game.e1);
                Figure whiteRock_A = Board.getFieldToFigure().get(Game.a1);
                figureToField.put(whiteKing, whiteKingLongCastling);
                figureToField.put(whiteRock_A, whiteRockLongCastling);
            }else {
                Figure blackKing = Board.getFieldToFigure().get(Game.e8);
                Figure blackRock_A = Board.getFieldToFigure().get(Game.a8);
                figureToField.put(blackKing, blackKingLongCastling);
                figureToField.put(blackRock_A, blackRockLongCastling);
            }
            return createTurn(figureToField, writtenStyle, false, null, number);
        }
        char firstCharacter = writtenStyle.charAt(0);
        switch (firstCharacter){
            case 'R' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Rock.class, isWhite, true) : fetchFigureToTargetField(Rock.class, isWhite, false);
                        return createTurn(figureToField, writtenStyle, isEating, targetedFigure, number);
            case 'N' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Knight.class, isWhite, true) : fetchFigureToTargetField(Knight.class, isWhite, false);
                        return createTurn(figureToField, writtenStyle, isEating, targetedFigure, number);
            case 'B' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Bishop.class, isWhite, true) : fetchFigureToTargetField(Bishop.class, isWhite, false);
                        return createTurn(figureToField, writtenStyle, isEating, targetedFigure, number);
            case 'Q' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Queen.class, isWhite, true) : fetchFigureToTargetField(Queen.class, isWhite, false);
                        return createTurn(figureToField, writtenStyle, isEating, targetedFigure, number);
            case 'K' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(King.class, isWhite, true) : fetchFigureToTargetField(King.class, isWhite, false);
                        return createTurn(figureToField, writtenStyle, isEating, targetedFigure, number);
            default :   figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Pawn.class, isWhite, true) : fetchFigureToTargetField(Pawn.class, isWhite, false);
                        return createTurn(figureToField, writtenStyle, isEating, targetedFigure, number);
        }
    }

    private static Map<Figure, Field> fetchFigureToTargetField(Class clazz, boolean isWhite, boolean eating){
        isEating = eating;
        List<Observer> targets = new ArrayList<Observer>();
        List<Figure> figures = isWhite ? Board.getFiguresByClass(clazz, Color.WHITE) : Board.getFiguresByClass(clazz, Color.BLACK);
        for (Observer curFigure : figures){
            if (eating){
                if (((Figure) curFigure).getPreyField().contains(field)){
                    targets.add(curFigure);
                    targetedFigure = Board.getFieldToFigure().get(field);
                }
            }else {
                if (((Figure) curFigure).getPossibleFieldsToMove().contains(field)){
                    candidates.add(curFigure);
                }
            }
        }
        if (!targets.isEmpty()){
            if (targets.size() == 1){
                figure = (Figure)targets.get(0);
            }else{
                figure = choseFigureWhichAttack(targets, clazz);
            }
        }
        if (!candidates.isEmpty()){
            if (candidates.size() > 1){
                figure = choseExactFigure();
            }else {
                figure = (Figure) candidates.get(0);
            }
        }
        if (figure != null){
            figureToField.put(figure, field);
        }
        if (figureToField.size() == 0){
            throw new RuntimeException("Could not fetch figure. Turn must be wrong written. Turn = " + mainTurn);
        }
        return figureToField;
    }

    private static Figure choseFigureWhichAttack(List<Observer> list, Class clazz){
        if (clazz == Pawn.class){
            char verticalPawn = mainTurn.charAt(0);
            for (Object currentFigure : list){
                if (((Figure) currentFigure).getField().getY() == Field.getInvertedHorizontal().get(verticalPawn)){
                    return (Figure) currentFigure;
                }
            }
        }else {
            char secondPosition = mainTurn.charAt(1);
            int integer = Character.getNumericValue(secondPosition);
            chose(integer, secondPosition);
        }
        return null;
    }

    private static Figure choseExactFigure(){
        char secondPosition = mainTurn.charAt(1);
        int integer = Character.getNumericValue(secondPosition);
        return chose(integer, secondPosition);
    }

    private static Figure chose(int integer, char secondPosition){
        for (Observer observer : candidates){
            if (integer > Board.SIZE){
                if (((Figure) observer).getField().getY() == Field.getInvertedHorizontal().get(secondPosition)){
                    return (Figure) observer;
                }
            }else {
                if (((Figure) observer).getField().getX() == Field.getInvertedVertical().get(integer)){
                    return (Figure) observer;
                }
            }
        }
        throw new RuntimeException("Could not choose exact figure. Turn must be wrong written. Turn = " + mainTurn);
    }

    private static Field parseTargetField(String turn){
        int x;
        int y;
        if (!turn.equalsIgnoreCase(shortCastling) && !turn.equalsIgnoreCase(longCastling)){
            if (turn.contains("+")){
                x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length()-2)));
                y = Field.getInvertedHorizontal().get(turn.charAt(turn.length()-3));
            }else {
                x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length()-1)));
                y = Field.getInvertedHorizontal().get(turn.charAt(turn.length()-2));
            }
            return new Field(x,y);
        }else {
            LOG.debug("Target field is null. Castling");
            return null;
        }
    }

    public static Turn createTurn(Map<Figure, Field> figureToField, String writtenStyle, boolean isEating, Figure targetedFigure, int numberOfTurn){
        return new Turn.Builder().figureToDestinationField(figureToField)
                .writtenStyle(writtenStyle)
                .eating(isEating)
                .targetedFigure(targetedFigure)
                .numberOfTurn(numberOfTurn)
                .build();
    }

    public static Set<Field> fieldsBetweenRockAndKing(final King king, final Field rockPosition){
        Set<Field> fieldsBetween = new HashSet<>();
        if (king.getField().getX() == rockPosition.getX()){
            if (king.getField().getX() > rockPosition.getX()){
                for (int i = king.getField().getX() - 1; i > rockPosition.getX(); i--){
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            }else {
                for (int i = king.getField().getX() + 1; i < rockPosition.getX(); i++){
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            }
        }
        if (king.getField().getY() == rockPosition.getY()){
            if (king.getField().getY() > rockPosition.getY()){
                for (int j = king.getField().getY() - 1; j > rockPosition.getY(); j--){
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            }else {
                for (int j = king.getField().getY() + 1; j < rockPosition.getY(); j++){
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            }
        }
        return fieldsBetween;
    }

    public static Set<Field> fieldsBetweenBishopAndKing(final King king, final Field bishopPosition){
        Set<Field> fieldsBetween =  new HashSet<>();
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()){
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }
        }
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()){
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()){
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }

        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()){
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        return fieldsBetween;
    }

    public static Set<Field> fieldsBetweenQueenAndKing(final King king, final Field queenPosition){
        Set<Field> fieldsBetweenQueenAndKing = new HashSet<>();
        if (king.getField().getX() == queenPosition.getX() || king.getField().getY() == queenPosition.getY()){
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenRockAndKing(king, queenPosition));
        }else {
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenBishopAndKing(king, queenPosition));
        }
        return fieldsBetweenQueenAndKing;
    }

    public static Set<Figure> getAffectedFigures(Color color){
        Set<Figure> acceptedFigures = new HashSet<>();
        Set<Observer> observers = Board.getFigures(color);
        affectedFields.forEach(f -> {
            observers.forEach(o -> {
                if (((Figure)o).getAttackedFields().contains(f)){
                    acceptedFigures.add((Figure)o);
                }
            });
        });
        return acceptedFigures;
    }

    private static void getAffectedFields(Turn turn){
        affectedFields = new HashSet<>();
        for (Figure f : turn.getFigures().keySet()){
            affectedFields.add(f.getField());
        }
        affectedFields.addAll(turn.getFigures().values());
        System.out.println("Affected fields =====================" + affectedFields);
    }

    public static FrequentFigure getWhiteFrequent() {
        return whiteFrequent;
    }

    public static FrequentFigure getBlackFrequent() {
        return blackFrequent;
    }

    public static void makeTurn(Turn turn){
        getAffectedFields(turn);
        Process.BOARD.setNewCoordinates(turn.getFigures().keySet().iterator().next(), turn.getFigures().values().iterator().next(), turn.getTargetedFigure());
        makePullAdditionalAlliesAndEnemies();
    }

    public static void undoTurn(Turn turn){
        //TODO add the logic
        makePullAdditionalAlliesAndEnemies();
    }

    private static void makePullAdditionalAlliesAndEnemies(){
        Map<Figure, Set<Figure>> figureToChosenAllies = new HashMap<>();
        Board.getFigures().forEach(f -> {
            Set<Figure> chosenAllies = ((Figure)f).pullAdditionalAlliesAndEnemies();
            if (!isEmpty(chosenAllies)){
                figureToChosenAllies.put((Figure)f, chosenAllies);
            }
        });
        System.out.println(figureToChosenAllies);
        for (int i = 0; i < Board.SIZE; i++){
            for (Figure curFigure : figureToChosenAllies.keySet()){
                for (Figure ally : figureToChosenAllies.get(curFigure)){
                    if (ally != null){
                        doUpdate(curFigure, ally);
                    }
                }
            }
        }
    }

    private static void doUpdate(Figure curFigure, Figure ally){
        for (Figure undefendedAlly : ally.getAlliesIProtect()){
            updateProtectionOfUndefendedAllies(curFigure, ally, undefendedAlly);
        }
        for (Figure prey : ally.getWhoCouldBeEaten()){
            updateWhoCouldBeEaten(curFigure, ally, prey);
        }
    }

    private static void updateWhoCouldBeEaten(Figure curFigure, Figure ally, Figure prey){
        if (!curFigure.getWhoCouldBeEaten().contains(prey) && ally.getWhoCouldBeEaten().contains(prey)
                && curFigure.getAttackedFields().contains(prey.getField()) && isOnTheSameLine(curFigure, ally, prey)){
            curFigure.getWhoCouldBeEaten().add(prey);
            prey.addEnemy(curFigure);
        }
    }

    private static void updateProtectionOfUndefendedAllies(Figure curFigure, Figure ally, Figure undefendedAlly){
        if (!curFigure.getAlliesIProtect().contains(undefendedAlly) && ally.getAlliesIProtect().contains(undefendedAlly)
                && curFigure.getAttackedFields().contains(undefendedAlly.getField()) && isOnTheSameLine(curFigure, ally, undefendedAlly)
                && !curFigure.equals(undefendedAlly) && !ally.equals(undefendedAlly)){
            curFigure.addAllyIProtect(undefendedAlly);
            undefendedAlly.addAllyProtectMe(curFigure);
        }
    }

    private static boolean isOnTheSameLine(Figure f1, Figure f2, Figure f3){
        if (f1.getClass() == Bishop.class || f2.getClass() == Bishop.class  || f3.getClass() == Bishop.class){
            return (abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
                    && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY()))
                    && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY()));
        }
        if (f1.getClass() == Rock.class || f2.getClass() == Rock.class  || f3.getClass() == Rock.class){
            return  ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX())) ||
                    ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY()));
        }
        return ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX())) ||
                ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY())) ||
                (((abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
                        && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY())))
                        && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY())));
    }

    public static boolean isEmpty(Set<?> set){
        return set == null || set.isEmpty();
    }
}