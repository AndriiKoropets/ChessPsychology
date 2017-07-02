package com.koropets_suhanov.chess.utils;

import com.koropets_suhanov.chess.model.*;
import com.koropets_suhanov.chess.model.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author AndriiKoropets
 */
public class Parser {

    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
    private static List<Observer> candidates = new ArrayList<Observer>();
    private static String mainTurn;
    private static Field field;
    private static int number;
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

    private static Turn setTurn(final String writtenStyle, final boolean isWhite){
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
            return new Turn.Builder().figureToDestinationField(figureToField)
                    .writtenStyle(writtenStyle)
                    .killing(false)
                    .numberOfTurn(number)
                    .build();
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
            return new Turn.Builder().figureToDestinationField(figureToField)
                    .writtenStyle(writtenStyle)
                    .killing(false)
                    .numberOfTurn(number)
                    .build();
        }
        if (writtenStyle.contains("R")){
            if (writtenStyle.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Rock.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Rock.class, isWhite, false));
            }
            return;
        }
        if (writtenStyle.contains("N")){
            if (writtenStyle.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Knight.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Knight.class, isWhite, false));
            }
            return;
        }
        if (writtenStyle.contains("B")){
            if (writtenStyle.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Bishop.class, isWhite, true));
//                Process.printFigures();
            }else {
                board.setNewCoordinates(field, fetchFigure(Bishop.class, isWhite, false));
            }
            return;
        }
        if (writtenStyle.contains("Q")){
            if (writtenStyle.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Queen.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Queen.class, isWhite, false));
            }
            return;
        }
        if (writtenStyle.contains("K")){
            if (writtenStyle.contains("x")){
                board.setNewCoordinates(field, fetchFigure(King.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(King.class, isWhite, false));
            }
            return;
        }else {
            if (writtenStyle.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Pawn.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Pawn.class, isWhite, false));
            }
        }
        throw new RuntimeException("Could not read written turn");
    }

    private static Figure fetchFigure(Class clazz, boolean isWhite, boolean isKilling){
        Field fieldUnderAttack = null;
        Figure figureIsUnderAttack = null;
        List<Observer> whoAttacks = new ArrayList<Observer>();
        Set<Observer> figures;
        if (isWhite){
            figures = Board.getWhiteFigures();
        }else {
            figures = Board.getBlackFigures();
        }
        for (Observer figure : figures){
            if (figure.getClass() == clazz){
                if (isKilling){
                    Set<Figure> couldBeKilled = ((Figure)figure).getWhoCouldBeKilled();
                    for (Figure figureUnderAttack : couldBeKilled){
//                        Field couldBeUnderAttack = figureUnderAttack.getField();
                        if (figureUnderAttack.getField().equals(field)){
                            whoAttacks.add(figure);
                            figureIsUnderAttack = figureUnderAttack;
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
        if (figureIsUnderAttack != null) {
            board.removeFigure(figureIsUnderAttack);
        }
        if (!whoAttacks.isEmpty()){
            if (whoAttacks.size() == 1){
                return (Figure)whoAttacks.get(0);
            }else{
                return choseFigureWhichAttack(whoAttacks, clazz);
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
        return null;
    }

    private static Field parseTargetField(String turn){
        int x;
        int y;
        if (!turn.equalsIgnoreCase("O-O") && !turn.equalsIgnoreCase("O-O-O")){
            if (turn.contains("+")){
                x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length()-2)));
                y = Field.getInvertedHorizontal().get(turn.charAt(turn.length()-3));
            }else {
                x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length()-1)));
                y = Field.getInvertedHorizontal().get(turn.charAt(turn.length()-2));
            }
            return new Field(x,y);
        }else {

            return null;
        }

    }
}