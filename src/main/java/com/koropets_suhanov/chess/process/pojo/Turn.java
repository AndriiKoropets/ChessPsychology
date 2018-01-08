package com.koropets_suhanov.chess.process.pojo;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;
import scala.Tuple2;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Turn {

    private List<Tuple2<Figure, Field>> figureToDestinationField;
    private Figure figureFromTransformation;
    private boolean eating;
    private boolean transformation;
    private Figure targetedFigure;
    private String writtenStyle;
    private int numberOfTurn;
    private boolean enPassant;

    private Turn(List<Tuple2<Figure, Field>> figureToDestinationField, Figure figureToReborn, boolean eating,
                 boolean transformation, boolean enPassant, Figure targetedFigure, String turn, int numberOfTurn) {
        this.figureToDestinationField = figureToDestinationField;
        this.figureFromTransformation = figureToReborn;
        this.eating = eating;
        this.transformation = transformation;
        this.enPassant = enPassant;
        this.targetedFigure = targetedFigure;
        this.writtenStyle = turn;
        this.numberOfTurn = numberOfTurn;
    }

    public static class Builder{
        private List<Tuple2<Figure, Field>> figureToDestinationField;
        private Figure figureFromTransformation;
        private boolean eating;
        private boolean transformation;
        private boolean enPassant;
        private Figure targetedFigure;
        private String writtenStyle;
        private int numberOfTurn;

        public Builder figureToDestinationField(final List<Tuple2<Figure, Field>> figureToDestinationField){
            this.figureToDestinationField = figureToDestinationField;
            return this;
        }

        public Builder figureFromTransformation(final Figure figureFromTransformation){
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

        public Builder enPassant(final boolean enPassant){
            this.enPassant = enPassant;
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
            return new Turn(figureToDestinationField, figureFromTransformation, eating, transformation, enPassant,
                    targetedFigure, writtenStyle, numberOfTurn);
        }
    }
}