package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.process.dto.FigureToField;
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.process.utils.ProcessUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.koropets_suhanov.chess.process.service.Process.currentColor;

public class EnPassantAndTransformation {

  public Set<Turn> turnsInCaseEnPassant(Pawn ally) {
    Set<Turn> possibleTurns = new HashSet<>();
    Figure enPassantEnemy = ally.getEnPassantEnemy();
    for (Field field : ally.getPossibleFieldsToMove()) {
      List<FigureToField> figureToField = new ArrayList<>();
      figureToField.add(FigureToField.builder().figure(ally).field(field).build());
      possibleTurns.add(Turn.builder().figureToDestinationField(figureToField).build());

    }
    for (Figure enemy : ally.getWhoCouldBeEaten()) {
      if (!enemy.equals(enPassantEnemy)) {
        List<FigureToField> figureToField = new ArrayList<>();
        figureToField.add(FigureToField.builder().figure(ally).field(enemy.getField()).build());
        possibleTurns.add(Turn.builder()
                .figureToDestinationField(figureToField)
                .eating(true)
                .targetedFigure(enemy)
                .build());
      }
    }
    if (enPassantEnemy != null) {
      List<FigureToField> figureToFieldList = new ArrayList<>();
      figureToFieldList.add(FigureToField.builder().figure(ally).field(ally.getEnPassantField()).build());
      possibleTurns.add(Turn.builder()
              .figureToDestinationField(figureToFieldList)
              .eating(true)
              .targetedFigure(enPassantEnemy)
              .build());
    }
    return possibleTurns;
  }

  public Set<Turn> turnsInCaseTransformation(Figure ally) {
    Set<Turn> possibleTurns = new HashSet<>();
    for (Field possibleFieldToMove : ally.getPossibleFieldsToMove()) {
      for (String writtenStyle : ParseWrittenTurn.ALL_FIGURES) {
        List<FigureToField> figureToField = new ArrayList<>();
        figureToField.add(FigureToField.builder().figure(ally).field(possibleFieldToMove).build());
        possibleTurns.add(Turn.builder()
                .figureToDestinationField(figureToField)
                .transformation(true)
                .figureFromTransformation(ProcessUtils.createFigure(possibleFieldToMove, writtenStyle, currentColor))
                .build());
      }
    }
    for (Figure enemy : ally.getWhoCouldBeEaten()) {
      for (String writtenStyle : ParseWrittenTurn.ALL_FIGURES) {
        List<FigureToField> figureToField = new ArrayList<>();
        figureToField.add(FigureToField.builder().figure(ally).field(enemy.getField()).build());
        possibleTurns.add(Turn.builder()
                .figureToDestinationField(figureToField)
                .transformation(true)
                .figureFromTransformation(ProcessUtils.createFigure(enemy.getField(), writtenStyle, currentColor)).eating(true)
                .targetedFigure(enemy)
                .build());
      }
    }
    return possibleTurns;
  }

  public Set<Turn> setTransformationFields(Pawn pawn, Figure enemy) {
    Set<Turn> transformationSet = new HashSet<>();
    for (String writtenStyleOfFigure : ParseWrittenTurn.ALL_FIGURES) {
      List<FigureToField> allyToFieldList = new ArrayList<>();
      allyToFieldList.add(FigureToField.builder().figure(pawn).field(enemy.getField()).build());

      Turn newTransformationTurn = Turn.builder()
              .figureToDestinationField(allyToFieldList)
              .transformation(true)
              .eating(true)
              .targetedFigure(enemy)
              .figureFromTransformation(ProcessUtils.createFigure(enemy.getField(), writtenStyleOfFigure, currentColor))
              .build();
      transformationSet.add(newTransformationTurn);
    }
    return transformationSet;
  }
}
