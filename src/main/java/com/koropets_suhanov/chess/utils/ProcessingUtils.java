package com.koropets_suhanov.chess.utils;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Knight;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.process.pojo.Turn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author AndriiKoropets
 */
public class ProcessingUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingUtils.class);
    private static List<Observer> candidates = new ArrayList<Observer>();
    private static String mainTurn;
    private static Field field;
    private static int number;
    private static Map<Figure, Field> figureToField = new HashMap<>();
    private static Figure figure;
    private static boolean isEating;
    private static Board board = Board.getInstance();
    private static final String shortCastling = "0-0";
    private static final String longCastling = "0-0-0";
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


    public static Turn getActualTurn(final String turn, final boolean isWhite, int numberOfTurn){
        candidates.clear();
        mainTurn = turn;
        number = numberOfTurn;
        field = parseTargetField(turn);
        return setTurn(turn, isWhite);
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
        figureToField.clear();
        figure = null;
        isEating = false;
    }

    private static void update(boolean eating){
        if (figure != null){
            figureToField.put(figure, field);
            isEating = eating;
        }else {
            throw new RuntimeException("Could not read written turn");
        }
    }

    private static Turn setTurn(final String writtenStyle, final boolean isWhite){
        initialize();
        if (shortCastling.equals(writtenStyle)){
            Map<Figure, Field> figureToField = new HashMap<>();
            if (isWhite){
                Figure whiteKing = Board.getFieldToFigure().get(new Field(7, 4));
                Figure whiteRock_H = Board.getFieldToFigure().get(new Field(7, 7));
                figureToField.put(whiteKing, whiteKingShortCastling);
                figureToField.put(whiteRock_H, whiteRockShortCastling);
            }else {
                Figure blackKing = Board.getFieldToFigure().get(new Field(0, 4));
                Figure blackRock_H = Board.getFieldToFigure().get(new Field(0, 7));
                figureToField.put(blackKing, blackKingShortCastling);
                figureToField.put(blackRock_H, blackRockShortCastling);
            }
            return createTurn(figureToField, writtenStyle, false);
        }
        if (longCastling.equals(writtenStyle)){
            Map<Figure, Field> figureToField = new HashMap<>();
            if (isWhite){
                Figure whiteKing = Board.getFieldToFigure().get(new Field(7, 4));
                Figure whiteRock_A = Board.getFieldToFigure().get(new Field(7, 0));
                figureToField.put(whiteKing, whiteKingLongCastling);
                figureToField.put(whiteRock_A, whiteRockLongCastling);
            }else {
                Figure blackKing = Board.getFieldToFigure().get(new Field(0, 4));
                Figure blackRock_A = Board.getFieldToFigure().get(new Field(0, 0));
                figureToField.put(blackKing, blackKingLongCastling);
                figureToField.put(blackRock_A, blackRockLongCastling);
            }
            return createTurn(figureToField, writtenStyle, false);
        }
        if (writtenStyle.contains("R")){
            if (writtenStyle.contains("x")){
                figure = fetchFigure(Rock.class, isWhite, true);
                update(true);
            }else {
                figure = fetchFigure(Rock.class, isWhite, false);
                update(false);
            }
            return createTurn(figureToField, writtenStyle, isEating);
        }
        if (writtenStyle.contains("N")){
            if (writtenStyle.contains("x")){
                figure = fetchFigure(Knight.class, isWhite, true);
                update(true);
            }else {
                figure = fetchFigure(Knight.class, isWhite, false);
                update(false);
            }
            return createTurn(figureToField, writtenStyle, isEating);
        }
        if (writtenStyle.contains("B")){
            if (writtenStyle.contains("x")){
                figure = fetchFigure(Bishop.class, isWhite, true);
                update(true);
            }else {
                figure = fetchFigure(Bishop.class, isWhite, false);
                update(false);
            }
            return createTurn(figureToField, writtenStyle, isEating);
        }
        if (writtenStyle.contains("Q")){
            if (writtenStyle.contains("x")){
                figure = fetchFigure(Queen.class, isWhite, true);
                update(true);
            }else {
                figure = fetchFigure(Queen.class, isWhite, false);
                update(false);
            }
            return createTurn(figureToField, writtenStyle, isEating);
        }
        if (writtenStyle.contains("K")){
            if (writtenStyle.contains("x")){
                figure = fetchFigure(King.class, isWhite, true);
                update(true);
            }else {
                figure = fetchFigure(King.class, isWhite, false);
                update(false);
            }
            return createTurn(figureToField, writtenStyle, isEating);
        }else {
            if (writtenStyle.contains("x")){
                figure = fetchFigure(Pawn.class, isWhite, true);
                update(true);
            }else {
                figure = fetchFigure(Pawn.class, isWhite, false);
                update(false);
            }
            return createTurn(figureToField, writtenStyle, isEating);
        }

    }

    private static Figure fetchFigure(Class clazz, boolean isWhite, boolean isEating){
        Figure targetFigure = null;
        List<Observer> targets = new ArrayList<Observer>();
        Set<Observer> figures;
        figures = isWhite ? Board.getWhiteFigures() : Board.getBlackFigures();
        for (Observer figure : figures){
            if (figure.getClass() == clazz){
                if (isEating){
                    Set<Figure> couldBeEaten = ((Figure)figure).getWhoCouldBeEaten();
                    for (Figure figureUnderAttack : couldBeEaten){
//                        Field couldBeUnderAttack = figureUnderAttack.getField();
                        if (figureUnderAttack.getField().equals(field)){
                            targets.add(figure);
                            targetFigure = figureUnderAttack;
                        }
                    }
                }else {
                    for (Field field : ((Figure)figure).getPossibleFieldsToMove()){
                        if (field.equals(field)){
                            candidates.add(figure);
                        }
                    }
                }
            }
        }
        //TODO probably this should be deleted
        if (targetFigure != null) {
            board.removeFigure(targetFigure);
        }
        if (!targets.isEmpty()){
            if (targets.size() == 1){
                return (Figure)targets.get(0);
            }else{
                return choseFigureWhichAttack(targets, clazz);
            }
        }
        if (candidates.size() > 1){
            return choseProperFigure();
        }else {
            return (Figure) candidates.get(0);
        }
    }

    private static Figure choseFigureWhichAttack(List list, Class clazz){
        if (clazz == Pawn.class){
            char verticalPawn = mainTurn.charAt(0);
            int integer = Character.getNumericValue(verticalPawn);
            for (Object currentFigure : list){
                if (((Figure) currentFigure).getField().getY() == Field.getInvertedHorizontal().get(verticalPawn)){
                    return (Figure) currentFigure;
                }
            }
        }else {
            char secondPosition = mainTurn.charAt(1);
            int integer = Character.getNumericValue(secondPosition);
            if (integer > Board.SIZE){
                for (Object figure : list){
                    if (((Figure) figure).getField().getY() == Field.getInvertedHorizontal().get(secondPosition)){
                        return (Figure) figure;
                    }
                }
            }else {
                for (Object figure : list){
                    if (((Figure) figure).getField().getX() == Field.getInvertedVertical().get(integer)){
                        return (Figure) figure;
                    }
                }
            }
        }
        return null;
    }

    private static Figure choseProperFigure(){
        char secondPosition = mainTurn.charAt(1);
        int integer = Character.getNumericValue(secondPosition);
        if (integer > Board.SIZE){
            for (Observer figure : candidates){
                if (((Figure) figure).getField().getY() == Field.getInvertedHorizontal().get(secondPosition)){
                    return (Figure) figure;
                }
            }
        }else {
            for (Observer figure : candidates){
                if (((Figure) figure).getField().getX() == Field.getInvertedVertical().get(integer)){
                    return (Figure) figure;
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

    private static Turn createTurn(Map<Figure, Field> figureToField, String writtenStyle, boolean isEating){
        return new Turn.Builder().figureToDestinationField(figureToField)
                .writtenStyle(writtenStyle)
                .eating(isEating)
                .numberOfTurn(number)
                .build();
    }

    public static FrequentFigure getWhiteFrequent() {
        return whiteFrequent;
    }

    public static FrequentFigure getBlackFrequent() {
        return blackFrequent;
    }
}