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
    private Figure figureFromTransformation;
    private boolean eating;
    private boolean transformation;
    private Figure targetedFigure;
    private String writtenStyle;
    private int numberOfTurn;

    private Turn(List<Tuple2<Figure, Field>> figureToDestinationField, Figure figureToReborn, boolean eating,
                 boolean transformation, Figure targetedFigure, String turn, int numberOfTurn) {
        this.figureToDestinationField = figureToDestinationField;
        this.figureFromTransformation = figureToReborn;
        this.eating = eating;
        this.transformation = transformation;
        this.targetedFigure = targetedFigure;
        this.writtenStyle = turn;
        this.numberOfTurn = numberOfTurn;
    }

    public List<Tuple2<Figure, Field>> getFigureToDestinationField() {
        return figureToDestinationField;
    }

    public String getWrittenStyle() {
        return writtenStyle;
    }

    public Figure getFigureFromTransformation(){
        return figureFromTransformation;
    }

    public boolean isEating(){
        return eating;
    }

    public boolean isTransformation(){
        return transformation;
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
        if (o == null || this.getClass() != o.getClass()) return false;

        Turn otherTurn = (Turn) o;

        if (this.getNumberOfTurn() != otherTurn.getNumberOfTurn()) return false;
        return this.getFigureToDestinationField().equals(otherTurn.getFigureToDestinationField());
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
                "figureFromTransformation=" + figureFromTransformation +
                ", eating=" + eating +
                ", transformation = " + transformation +
                ", targetedFigure=" + targetedFigure +
                ", writtenStyle='" + writtenStyle + '\'' +
                ", numberOfTurn=" + numberOfTurn +
                '}';
    }

    public static class Builder{
        private List<Tuple2<Figure, Field>> figureToDestinationField;
        private Figure figureFromTransformation;
        private boolean eating;
        private boolean transformation;
        private Figure targetedFigure;
        private String writtenStyle;
        private int numberOfTurn;

        public Builder figureToDestinationField(final List<Tuple2<Figure, Field>> figureToDestinationField){
            this.figureToDestinationField = figureToDestinationField;
            return this;
        }

        public Builder figureToReborn(final Figure figureFromTransformation){
            this.figureFromTransformation = figureFromTransformation;
            return this;
        }

        public Builder eating(final boolean eating){
            this.eating = eating;
            return this;
        }

        public Builder transformation(final boolean transformation){
            this.transformation = transformation;
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
            return new Turn(figureToDestinationField, figureFromTransformation, eating, transformation,
                    targetedFigure, writtenStyle, numberOfTurn);
        }
    }
}