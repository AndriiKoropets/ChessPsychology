package com.koropets_suhanov.chess.model;

import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.process.service.FillBoard;
import com.koropets_suhanov.chess.process.service.UpdatePositionOnTheBoard;
import com.koropets_suhanov.chess.process.service.ParseWrittenTurn;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
public class Board implements Subject {

  @Getter
  private static List<Figure> figures = new ArrayList<>();
  private static List<Figure> whiteFigures = new ArrayList<>();
  private static List<Figure> blackFigures = new ArrayList<>();
  @Getter
  private static Set<Field> fieldsUnderWhiteInfluence = new LinkedHashSet<>();
  @Getter
  private static Set<Field> fieldsUnderBlackInfluence = new LinkedHashSet<>();
  @Getter
  private static final Set<Field> takenFields = new LinkedHashSet<>();
  @Getter
  private static final Map<Field, Figure> fieldToFigure = new HashMap<>();
  @Getter
  @Setter
  private static Figure enPassantPrey;
  private static Field newFieldOccupation;
  private static Board instance;
  private static boolean transformation;

  public static Board getInstance() {
    if (instance == null) {
      instance = new Board();
      return instance;
    } else {
      return instance;
    }
  }

  private Board() {
    FillBoard.createWhiteFigures().forEach(this::register);
    FillBoard.createBlackFigures().forEach(this::register);

    setTakenFields();
    figures.forEach(Figure::possibleTurns);
    whiteFigures.forEach(white -> fieldsUnderWhiteInfluence.addAll(white.getFieldsUnderMyInfluence()));
    blackFigures.forEach(black -> fieldsUnderBlackInfluence.addAll(black.getFieldsUnderMyInfluence()));
  }

  @Override
  public void register(Observer observerFigure) {
    Figure figure = (Figure) observerFigure;
    figures.add(figure);
    if ((figure).getColor() == Color.WHITE) {
      whiteFigures.add((figure));
    } else {
      blackFigures.add(figure);
    }
    Field field = figure.getField();
    fieldToFigure.put(field, figure);
    takenFields.add(field);
  }

  private static void setTakenFields() {
    figures.forEach(f -> takenFields.add(f.getField()));
  }

  private static void updateFieldsUnderWhiteInfluence() {
    fieldsUnderWhiteInfluence.clear();
    whiteFigures.forEach(w -> fieldsUnderWhiteInfluence.addAll(w.getFieldsUnderMyInfluence()));
  }

  private static void updateFieldsUnderBlackInfluence() {
    fieldsUnderBlackInfluence.clear();
    blackFigures.forEach(b -> fieldsUnderBlackInfluence.addAll(b.getFieldsUnderMyInfluence()));
  }

  public void setNewCoordinates(Turn turn, Figure figureWillBeUpdated, Field previousAndFutureOccupiedField, boolean isUndoing) {
    if (!(figures.contains(figureWillBeUpdated) || turn.isTransformation())) {
      throw new RuntimeException("There is no such figure on the Board: " + figureWillBeUpdated
          + (isUndoing ? ", previousField = " + previousAndFutureOccupiedField
          : ", field = " + previousAndFutureOccupiedField));
    }

    Figure eatenFigure = turn.getTargetedFigure();
    if (eatenFigure != null) {
      removeFigure(eatenFigure);
    }

    transformation = turn.isTransformation();

    if (isUndoing) {
      if (turn.isTransformation()) {
        undoTransformation(turn, previousAndFutureOccupiedField);
      } else if (turn.isEnPassant()) {
        undoEnPassant(figureWillBeUpdated, previousAndFutureOccupiedField);
      } else {
        undoCasualTurn(figureWillBeUpdated, previousAndFutureOccupiedField);
      }
    } else {
      if (turn.isTransformation()) {
        makeTransformation(turn, previousAndFutureOccupiedField);
      } else if (turn.isEnPassant()) {
        makeEnPassant(turn, figureWillBeUpdated, previousAndFutureOccupiedField);
      } else {
        makeCasualTurn(figureWillBeUpdated, previousAndFutureOccupiedField);
      }
    }
  }

  private void undoTransformation(Turn turn, Field previousOccupiedField) {
    Figure pawnToReborn = turn.getFigureToDestinationField().get(0).getFigure();
    newFieldOccupation = previousOccupiedField;
    removeFigure(turn.getFigureFromTransformation());
    register(pawnToReborn);
    notify(pawnToReborn);
    figures.forEach(f -> {
      f.possibleTurns();
      f.attackedFields();
    });
    fulfillResurrection();
  }

  private void undoEnPassant(Figure figureFoUpdate, Field previousOccupiedField) {
    undoCasualTurn(figureFoUpdate, previousOccupiedField);
  }

  private void undoCasualTurn(Figure figureForUpdate, Field previousOccupiedField) {
    newFieldOccupation = previousOccupiedField;
    notify(figureForUpdate);
    fulfillResurrection();
    updateStateOfAllFigures(figureForUpdate);
  }

  private void fulfillResurrection() {
    Figure figureToResurrect = UpdatePositionOnTheBoard.eatenFigureToResurrection;
    if (figureToResurrect != null) {
      register(figureToResurrect);
    }
  }

  private void makeTransformation(Turn turn, Field updatedField) {
    newFieldOccupation = updatedField;
    Figure newFigureForUpdate = ParseWrittenTurn.getFigureBornFromTransformation();
    register(newFigureForUpdate);
    removeFigure(turn.getFigureToDestinationField().get(0).getFigure());
    notify(newFigureForUpdate);
    updateStateOfAllFigures(newFigureForUpdate);
  }

  private void makeEnPassant(Turn turn, Figure updatedFigure, Field updatedField) {
    enPassantPrey = turn.getTargetedFigure();
    makeCasualTurn(updatedFigure, updatedField);
  }

  private void makeCasualTurn(Figure updatedFigure, Field updatedField) {
    newFieldOccupation = updatedField;
    System.out.println("Updated figure = " + updatedFigure + " updatedField = " + updatedField);
    notify(updatedFigure);
    updateStateOfAllFigures(updatedFigure);
  }

  private void updateStateOfAllFigures(Figure justUpdatedFigure) {
    figures.stream().filter(f -> !f.equals(justUpdatedFigure)).forEach(f -> {
      f.possibleTurns();
      f.attackedFields();
    });
  }

  @Override
  public void notify(Observer observerFigure) {
    Figure figure = (Figure) observerFigure;
    updateTakenFields(figure);
    figure.update(newFieldOccupation);
    figures.forEach(f -> {
      if (!f.equals(figure)) {
        f.update();
      }
    });
    updateFieldsUnderWhiteInfluence();
    updateFieldsUnderBlackInfluence();
  }

  private static void updateTakenFields(Figure figure) {
    if (!transformation) {
      takenFields.remove(figure.getField());
      takenFields.add(newFieldOccupation);
      System.out.println("NewFieldOccupation = " + newFieldOccupation);
      fieldToFigure.put(newFieldOccupation, figure);
      fieldToFigure.replace(figure.getField(), null);
    }
  }

  @Override
  public void removeFigure(Observer observerFigure) {
    Figure figure = (Figure) observerFigure;
    figures.remove(figure);
    if (figure.getColor() == Color.BLACK) {
      blackFigures.remove(figure);
    } else {
      whiteFigures.remove(figure);
    }
    fieldToFigure.replace(figure.getField(), null);
    takenFields.remove(figure.getField());
  }

  public static List<Figure> getTypeOfFigures(Class clazz, Color color) {
    return getFiguresByColor(color).stream().filter(f -> f.getClass() == clazz).collect(Collectors.toList());
  }

  public static King getKingByColor(Color color) {
    return (King) getFiguresByColor(color).stream().filter(f -> f.getClass() == King.class).collect(Collectors.toList()).get(0);
  }

  public static List<Figure> getFiguresByColor(Color color) {
    return color == Color.WHITE ? whiteFigures : blackFigures;
  }
}