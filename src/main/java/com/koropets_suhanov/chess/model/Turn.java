package com.koropets_suhanov.chess.model;

/**
 * @author AndriiKoropets
 */
public class Turn {

    private Figure figure;
    private Field destinationField;
    private boolean killing;
    private String turn;

    private int numberOfTurn;


    public Turn(Figure figure, Field destinationField, boolean killing, String turn, int numberOfTurn) {
        this.figure = figure;
        this.destinationField = destinationField;
        this.killing = killing;
        this.turn = turn;
        this.numberOfTurn = numberOfTurn;
    }

    public String getTurn() {
        return turn;
    }

    public boolean isKilling(){
        return killing;
    }

    public Figure getFigure() {
        return figure;
    }

    public Field getDestinationField() {
        return destinationField;
    }

    public int getNumberOfTurn() {
        return numberOfTurn;
    }
}