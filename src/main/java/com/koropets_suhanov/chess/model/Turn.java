package com.koropets_suhanov.chess.model;

import java.util.Set;

/**
 * @author AndriiKoropets
 */
public class Turn {

    private Set<Figure> figures;
    private Field destinationField;
    private boolean killing;
    private String turn;

    private int numberOfTurn;


    public Turn(Set<Figure> figures, Field destinationField, boolean killing, String turn, int numberOfTurn) {
        this.figures = figures;
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

    public Set<Figure> getFigures() {
        return figures;
    }

    public Field getDestinationField() {
        return destinationField;
    }

    public int getNumberOfTurn() {
        return numberOfTurn;
    }
}