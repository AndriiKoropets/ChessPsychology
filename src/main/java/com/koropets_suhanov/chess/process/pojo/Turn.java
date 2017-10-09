package com.koropets_suhanov.chess.process.pojo;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import scala.Tuple2;

import java.util.List;

/**
 * @author AndriiKoropets
 */
public class Turn {

    private List<Tuple2<Figure, Field>> figureToDestinationField;
    private Figure figureToReborn;
    private boolean eating;
    private Figure targetedFigure;
    private String writtenStyle;
    private int numberOfTurn;

    private Turn(List<Tuple2<Figure, Field>> figureToDestinationField, Figure figureToReborn, boolean eating,
                 Figure targetedFigure, String turn, int numberOfTurn) {
        this.figureToDestinationField = figureToDestinationField;
        this.figureToReborn = figureToReborn;
        this.eating = eating;
        this.targetedFigure = targetedFigure;
        this.writtenStyle = turn;
        this.numberOfTurn = numberOfTurn;
    }

    public String getWrittenStyle() {
        return writtenStyle;
    }

    public Figure getFigureToReborn(){
        return figureToReborn;
    }

    public boolean isEating(){
        return eating;
    }

    public Figure getTargetedFigure() {
        return targetedFigure;
    }

    public List<Tuple2<Figure, Field>> getFigures() {
        return figureToDestinationField;
    }

    public int getNumberOfTurn() {
        return numberOfTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Turn turn = (Turn) o;

        if (numberOfTurn != turn.numberOfTurn) return false;
        return figureToDestinationField.equals(turn.figureToDestinationField);
    }

    @Override
    public int hashCode() {
        int result = figureToDestinationField.hashCode();
        result = 31 * result + numberOfTurn;
        return result;
    }

    @Override
    public String toString() {
        return "Turn{" +
                "figureToDestinationField=" + figureToDestinationField +
                ", eating=" + eating +
                ", targetedFigure=" + targetedFigure +
                ", writtenStyle='" + writtenStyle + '\'' +
                ", numberOfTurn=" + numberOfTurn +
                '}';
    }

    public static class Builder{
        private List<Tuple2<Figure, Field>> figureToDestinationField;
        private Figure figureToReborn;
        private boolean eating;
        private Figure targetedFigure;
        private String writtenStyle;
        private int numberOfTurn;

        public Builder figureToDestinationField(final List<Tuple2<Figure, Field>> figureToDestinationField){
            this.figureToDestinationField = figureToDestinationField;
            return this;
        }

        public Builder figureToReborn(final Figure figureToReborn){
            this.figureToReborn = figureToReborn;
            return this;
        }

        public Builder eating(final boolean eating){
            this.eating = eating;
            return this;
        }

        public Builder targetedFigure(final Figure eatenFigure){
            this.targetedFigure = eatenFigure;
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
            return new Turn(figureToDestinationField, figureToReborn, eating, targetedFigure, writtenStyle, numberOfTurn);
        }
    }
}