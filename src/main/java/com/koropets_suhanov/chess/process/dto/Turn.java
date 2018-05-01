package com.koropets_suhanov.chess.process.dto;

import com.koropets_suhanov.chess.model.Figure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

@Builder
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Turn {
  private List<FigureToField> figureToDestinationField;
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
    for (FigureToField originalFigureToField : this.figureToDestinationField) {
      for (FigureToField comparableFigureToField : turn.figureToDestinationField) {
        equals = originalFigureToField.getFigure().equals(comparableFigureToField.getFigure())
            && originalFigureToField.getField().equals(comparableFigureToField.getField());
        if (!equals) {
          return false;
        }
      }
    }
    return true;
//    return equals && eating == turn.eating
////            && transformation == turn.transformation
////            && enPassant == turn.enPassant
//        && Objects.equals(figureFromTransformation, turn.figureFromTransformation)
//        && Objects.equals(targetedFigure, turn.targetedFigure);
  }

  @Override
  public int hashCode() {

    return Objects.hash(figureToDestinationField, figureFromTransformation, eating, transformation, targetedFigure, writtenStyle, numberOfTurn, enPassant);
  }
}