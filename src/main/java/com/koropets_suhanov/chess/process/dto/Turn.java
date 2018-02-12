package com.koropets_suhanov.chess.process.dto;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.AccessLevel;
import lombok.Builder;
import scala.Tuple2;

import java.util.List;
import java.util.Objects;

@Builder
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Turn {

    private List<Tuple2<Figure, Field>> figureToDestinationField;
    private Figure figureFromTransformation;
    private boolean eating;
    private boolean transformation;
    private Figure targetedFigure;
    private String writtenStyle;
    private int numberOfTurn;
    private boolean enPassant;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turn turn = (Turn) o;
        boolean equals = false;
        for (Tuple2<Figure, Field> originalFigureToField : this.figureToDestinationField){
            for (Tuple2<Figure, Field> comparableFigureToField : turn.figureToDestinationField){
                equals = originalFigureToField._1.equals(comparableFigureToField._1) && originalFigureToField._2.equals(comparableFigureToField._2);
                if (!equals){
                    return false;
                }
            }
        }
        return  equals && eating == turn.eating &&
                transformation == turn.transformation &&
                numberOfTurn == turn.numberOfTurn &&
                enPassant == turn.enPassant &&
                Objects.equals(figureFromTransformation, turn.figureFromTransformation) &&
                Objects.equals(targetedFigure, turn.targetedFigure);
    }

    @Override
    public int hashCode() {

        return Objects.hash(figureToDestinationField, figureFromTransformation, eating, transformation, targetedFigure, writtenStyle, numberOfTurn, enPassant);
    }
}