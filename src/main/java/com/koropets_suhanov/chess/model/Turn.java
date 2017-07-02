package com.koropets_suhanov.chess.model;

import java.util.Map;

/**
 * @author AndriiKoropets
 */
public class Turn {

    private Map<Figure, Field> figureToDestinationField;
    private boolean killing;
    private String writtenStyle;
    private int numberOfTurn;


    private Turn(Map<Figure, Field> figureToDestinationField, boolean killing, String turn, int numberOfTurn) {
        this.figureToDestinationField = figureToDestinationField;
        this.killing = killing;
        this.writtenStyle = turn;
        this.numberOfTurn = numberOfTurn;
    }

    public String getWrittenStyle() {
        return writtenStyle;
    }

    public boolean isKilling(){
        return killing;
    }

    public Map<Figure, Field> getFigures() {
        return figureToDestinationField;
    }

    public int getNumberOfTurn() {
        return numberOfTurn;
    }

    @Override
    public String toString() {
        return "Turn{" +
                "figureToDestinationField=" + figureToDestinationField +
                ", killing=" + killing +
                ", writtenStyle='" + writtenStyle + '\'' +
                ", numberOfTurn=" + numberOfTurn +
                '}';
    }

    public static class Builder{
        private Map<Figure, Field> figureToDestinationField;
        private boolean killing;
        private String writtenStyle;
        private int numberOfTurn;

        public Builder figureToDestinationField(final Map<Figure, Field> figureToDestinationField){
            this.figureToDestinationField = figureToDestinationField;
            return this;
        }

        public Builder killing(final boolean killing){
            this.killing = killing;
            return this;
        }

        public Builder writtenStyle(final String writtenStyle){
            this.writtenStyle = writtenStyle;
            return this;
        }

        public Builder numberOfTurn(final int numberOfTurn){
            this.numberOfTurn = numberOfTurn;
            return this;
        }

        public Turn build(){
            return new Turn(figureToDestinationField, killing, writtenStyle, numberOfTurn);
        }
    }
}