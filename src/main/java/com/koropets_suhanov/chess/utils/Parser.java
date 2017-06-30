package com.koropets_suhanov.chess.utils;

import com.koropets_suhanov.chess.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author AndriiKoropets
 */
public class Parser {

    private static List<Observer> candidates = new ArrayList<Observer>();
    private static String mainTurn;
    private static Field field;
    private static Board board = Board.getInstance();


    public static void parseTurn(String turn, boolean isWhite){
        candidates.clear();
        mainTurn = turn;
        field = parseTargetField(turn);
        if (isWhite){
            findingAndUpdating(turn, true);
        }else {
            findingAndUpdating(turn, false);
        }
    }

    private static void findingAndUpdating(String turn, boolean isWhite){
        if (turn.equalsIgnoreCase("O-O")){
            if (isWhite){
                board.setNewCoordinates(new Field(7, 6), Board.getFieldToFigure().get(new Field(7, 4)));
                board.setNewCoordinates(new Field(7, 5), Board.getFieldToFigure().get(new Field(7, 7)));
            }else {
                board.setNewCoordinates(new Field(0, 6), Board.getFieldToFigure().get(new Field(0, 4)));
                board.setNewCoordinates(new Field(0, 5), Board.getFieldToFigure().get(new Field(0, 7)));
            }
            return;
        }
        if (turn.equalsIgnoreCase("O-O-O")){
            if (isWhite){
                board.setNewCoordinates(new Field(7, 2), Board.getFieldToFigure().get(new Field(7, 4)));
                board.setNewCoordinates(new Field(7, 3), Board.getFieldToFigure().get(new Field(7, 0)));
            }else {
                board.setNewCoordinates(new Field(0, 2), Board.getFieldToFigure().get(new Field(0, 4)));
                board.setNewCoordinates(new Field(0, 3), Board.getFieldToFigure().get(new Field(0, 0)));
            }
            return;
        }
        if (turn.contains("R")){
            if (turn.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Rock.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Rock.class, isWhite, false));
            }
            return;
        }
        if (turn.contains("N")){
            if (turn.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Knight.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Knight.class, isWhite, false));
            }
            return;
        }
        if (turn.contains("B")){
            if (turn.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Bishop.class, isWhite, true));
//                Main.printFigures();
            }else {
                board.setNewCoordinates(field, fetchFigure(Bishop.class, isWhite, false));
            }
            return;
        }
        if (turn.contains("Q")){
            if (turn.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Queen.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Queen.class, isWhite, false));
            }
            return;
        }
        if (turn.contains("K")){
            if (turn.contains("x")){
                board.setNewCoordinates(field, fetchFigure(King.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(King.class, isWhite, false));
            }
            return;
        }else {
            if (turn.contains("x")){
                board.setNewCoordinates(field, fetchFigure(Pawn.class, isWhite, true));
            }else {
                board.setNewCoordinates(field, fetchFigure(Pawn.class, isWhite, false));
            }
        }
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
                    Iterator<Field> possibleTurns = ((Figure)figure).getPossibleFieldsToMove().iterator();
                    while (possibleTurns.hasNext()){
                        Field currentField = possibleTurns.next();
                        if (currentField.equals(field)){
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