package com.koropets_suhanov.chess.model;

import java.util.*;

/**
 * @author AndriiKoropets
 */
public class Board implements Subject{

    public final static byte SIZE = 8;
    private static Set<Observer> figures = new LinkedHashSet<Observer>();
    private static Set<Observer> whiteFigures = new LinkedHashSet<Observer>();
    private static Set<Observer> blackFigures = new LinkedHashSet<Observer>();
    private static Set<Field> fieldsUnderWhiteInfluence = new LinkedHashSet<Field>();
    private static Set<Field> fieldsUnderBlackInfluence = new LinkedHashSet<Field>();
    private static List<Turn> possibleTurnsAndKillings = new ArrayList<Turn>();
    private static final Set<Field> takenFields = new LinkedHashSet<Field>();
    private static final Map<Field, Figure> fieldToFigure = new HashMap<Field, Figure>();
    private Field field;
    private Field previousTurn;
    private volatile static Board uniqueInstance;

    private Board(){
        //Putting white figures on the board
        Figure whitePawnA = new Pawn(new Field(6,0), Color.WHITE);
        Figure whitePawnB = new Pawn(new Field(6,1), Color.WHITE);
        Figure whitePawnC = new Pawn(new Field(6,2), Color.WHITE);
        Figure whitePawnD = new Pawn(new Field(6,3), Color.WHITE);
        Figure whitePawnE = new Pawn(new Field(6,4), Color.WHITE);
        Figure whitePawnF = new Pawn(new Field(6,5), Color.WHITE);
        Figure whitePawnG = new Pawn(new Field(6,6), Color.WHITE);
        Figure whitePawnH = new Pawn(new Field(6,7), Color.WHITE);
        Figure whiteRockA = new Rock(new Field(7,0), Color.WHITE);
        Figure whiteRockH = new Rock(new Field(7,7), Color.WHITE);
        Figure whiteKnightB = new Knight(new Field(7,1), Color.WHITE);
        Figure whiteKnightG = new Knight(new Field(7,6), Color.WHITE);
        Figure whiteBishopC = new Bishop(new Field(7,2), Color.WHITE);
        Figure whiteBishopF = new Bishop(new Field(7,5), Color.WHITE);
        Figure whiteQueen = new Queen(new Field(7,3), Color.WHITE);
        Figure whiteKing = new King(new Field(7,4), Color.WHITE);
        //Adding to figures collection
        register(whitePawnA);
        register(whitePawnB);
        register(whitePawnC);
        register(whitePawnD);
        register(whitePawnE);
        register(whitePawnF);
        register(whitePawnG);
        register(whitePawnH);
        register(whiteRockA);
        register(whiteRockH);
        register(whiteKnightB);
        register(whiteKnightG);
        register(whiteBishopC);
        register(whiteBishopF);
        register(whiteQueen);
        register(whiteKing);
        //Putting black figures on the board
        Figure blackPawnA = new Pawn(new Field(1,0), Color.BLACK);
        Figure blackPawnB = new Pawn(new Field(1,1), Color.BLACK);
        Figure blackPawnC = new Pawn(new Field(1,2), Color.BLACK);
        Figure blackPawnD = new Pawn(new Field(1,3), Color.BLACK);
        Figure blackPawnE = new Pawn(new Field(1,4), Color.BLACK);
        Figure blackPawnF = new Pawn(new Field(1,5), Color.BLACK);
        Figure blackPawnG = new Pawn(new Field(1,6), Color.BLACK);
        Figure blackPawnH = new Pawn(new Field(1,7), Color.BLACK);
        Figure blackRockA = new Rock(new Field(0,0), Color.BLACK);
        Figure blackRockH = new Rock(new Field(0,7), Color.BLACK);
        Figure blackKnightB = new Knight(new Field(0,1), Color.BLACK);
        Figure blackKnightG = new Knight(new Field(0,6), Color.BLACK);
        Figure blackBishopC = new Bishop(new Field(0,2), Color.BLACK);
        Figure blackBishopF = new Bishop(new Field(0,5), Color.BLACK);
        Figure blackQueen = new Queen(new Field(0,3), Color.BLACK);
        Figure blackKing = new King(new Field(0,4), Color.BLACK);
        //Adding to figures collection
        register(blackPawnA);
        register(blackPawnB);
        register(blackPawnC);
        register(blackPawnD);
        register(blackPawnE);
        register(blackPawnF);
        register(blackPawnG);
        register(blackPawnH);
        register(blackRockA);
        register(blackRockH);
        register(blackKnightB);
        register(blackKnightG);
        register(blackBishopC);
        register(blackBishopF);
        register(blackQueen);
        register(blackKing);
        //TODO change the logic for searching possible turns for each figure.
        //TODO Because it throws StackOverFlowError
        setTakenFields();
//        blackPawnA.possibleTurns();
        for (Observer figure : figures){
//            if (((Figure) figure).getColor() == Color.WHITE && (figure.getClass() == Pawn.class || figure.getClass() == Rock.class))
                ((Figure) figure).possibleTurns();
        }
        for (Observer whiteFigure : whiteFigures){
            for (Field field : ((Figure) whiteFigure).getFieldsUnderMyInfluence()){
                fieldsUnderWhiteInfluence.add(field);
            }
        }
        for (Observer blackFigure : blackFigures){
            for (Field field : ((Figure) blackFigure).getFieldsUnderMyInfluence()){
                fieldsUnderBlackInfluence.add(field);
            }
        }
    }

    public static Board getInstance(){
        if (uniqueInstance == null){
            uniqueInstance = new Board();
        }
        return uniqueInstance;
    }

    public List<Figure> getFiguresByClass(Class clazz){
        List<Figure> returnedFigures = new ArrayList<Figure>();
        for (Object figure : figures){
            if (figure.getClass() == clazz){
                returnedFigures.add((Figure) figure);
            }
        }
        return returnedFigures;
    }

//    public Field getPreviousTurn() {
//        return previousTurn;
//    }
//
//    public void setPreviousTurn(Field previousTurn) {
//        this.previousTurn = previousTurn;
//    }

    private void updateFieldsUnderWhiteInfluence(){
        fieldsUnderWhiteInfluence.clear();
        for (Observer whiteFigure : whiteFigures){
            for (Field field : ((Figure) whiteFigure).getFieldsUnderMyInfluence()){
                fieldsUnderWhiteInfluence.add(field);
            }
        }
    }

    private void updateFieldsUnderBlackInfluence(){
        fieldsUnderBlackInfluence.clear();
        for (Observer blackFigure : blackFigures){
            for (Field field : ((Figure) blackFigure).getFieldsUnderMyInfluence()){
                fieldsUnderBlackInfluence.add(field);
            }
        }
    }

    public static boolean isFieldValid(Field field){
        return field.getX() >= 0 && field.getX() < SIZE && field.getY() >= 0 && field.getY() < SIZE;
    }

    public static Set<Observer> getWhiteFigures() {
        return whiteFigures;
    }

    public static List<Turn> getPossibleTurnsAndKillings() {
        return possibleTurnsAndKillings;
    }

    public static Set<Observer> getBlackFigures() {
        return blackFigures;
    }

    public static Set<Observer> getFigures() {
        return figures;
    }

    public static Set<Field> getFieldsUnderWhiteInfluence() {
        return fieldsUnderWhiteInfluence;
    }

    public static Set<Field> getFieldsUnderBlackInfluence() {
        return fieldsUnderBlackInfluence;
    }

    public static Map<Field, Figure> getFieldToFigure(){
        return fieldToFigure;
    }

    public Field getPreviousTurn() {
        return previousTurn;
    }

    public void notify(Observer figure) {
        figure.update(field);
        for (Observer currentFigure : figures){
            ((Figure)currentFigure).update();
        }
        updateFieldsUnderWhiteInfluence();
        updateFieldsUnderBlackInfluence();
        updateTakenFields(figure);
    }

    public void register(Observer figure) {
        figures.add(figure);
        if (((Figure) figure).getColor() == Color.WHITE){
            whiteFigures.add(figure);
        } else {
            blackFigures.add(figure);
        }
        fieldToFigure.put(((Figure) figure).getField(), ((Figure)figure));
    }

    public void removeFigure(Observer figure) {
        figures.remove(figure);
        if (((Figure)figure).getColor() == Color.BLACK){
            blackFigures.remove(figure);
        } else {
            whiteFigures.remove(figure);
        }
        fieldToFigure.remove(((Figure) figure).getField());
    }

    public void setNewCoordinates(Field field, Observer figure){
        if (figures.contains(figure)){
            this.field = field;
            notify(figure);
            for (Observer figure1 : figures){
                ((Figure)figure1).possibleTurns();
                ((Figure)figure1).attackedFields();
            }
        }
    }

    public static Set<Field> getTakenFields() {
        return takenFields;
    }

    private void updateTakenFields(Observer figure){
        takenFields.remove(((Figure) figure).getField());
        takenFields.add(field);
    }

    private void setTakenFields(){
        for (Observer figure : figures) {
            takenFields.add(((Figure) figure).getField());
        }
    }
}