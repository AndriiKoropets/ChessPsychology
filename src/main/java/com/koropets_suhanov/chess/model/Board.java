package com.koropets_suhanov.chess.model;

import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.process.service.FillBoard;
import com.koropets_suhanov.chess.process.service.UpdatePositionOnTheBoard;
import com.koropets_suhanov.chess.process.service.ParseWrittenTurn;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class Board implements Subject {

  private List<Figure> figures = new ArrayList<>();
  private List<Figure> whiteFigures = new ArrayList<>();
  private List<Figure> blackFigures = new ArrayList<>();
  private Set<Field> fieldsUnderWhiteInfluence = new LinkedHashSet<>();
  @Getter
  private static final Set<Field> fieldsUnderBlackInfluence = new LinkedHashSet<>();
  @Getter
  private static final Set<Field> takenFields = new LinkedHashSet<>();
  @Getter
  private static final Map<Field, Figure> fieldToFigure = new HashMap<>();
  private Figure enPassantPrey;
  private Field newFigureOccupation;
  private static Board instance;

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
  public void register(Observer observer) {
    Figure figure = (Figure) observer;
    figures.add(figure);
    if (figure.getColor() == Color.WHITE) {
      whiteFigures.add((figure));
    } else {
      blackFigures.add(figure);
    }
    Field field = figure.getField();
    fieldToFigure.put(figure.getField(), figure);
    takenFields.add(field);
  }

  private void setTakenFields() {
    figures.forEach(f -> takenFields.add(f.getField()));
  }

  private void updateTakenFields(Figure figure) {
    takenFields.remove(figure.getField());
    takenFields.add(newFigureOccupation);
    fieldToFigure.put(newFigureOccupation, figure);
    fieldToFigure.replace(figure.getField(), null);
  }

  private void updateFieldsUnderWhiteInfluence() {
    fieldsUnderWhiteInfluence.clear();
    whiteFigures.forEach(w -> fieldsUnderWhiteInfluence.addAll(w.getFieldsUnderMyInfluence()));
  }

  private void updateFieldsUnderBlackInfluence() {
    fieldsUnderBlackInfluence.clear();
    blackFigures.forEach(b -> fieldsUnderBlackInfluence.addAll(b.getFieldsUnderMyInfluence()));
  }

  public void setNewCoordinates(Turn turn, Figure updatedFigure, Field updatedField, boolean isUndoing) {
    if (!figures.contains(updatedFigure)){
      throw new RuntimeException("There is no such figure on the Board: " + updatedFigure + ", field = " + updatedField);
    }

    Figure eatenFigure = turn.getTargetedFigure();
    if (eatenFigure != null) {
      removeFigure(eatenFigure);
    }

    if (isUndoing){
      if (turn.isTransformation()){
        undoTransformation(turn, updatedField);
      }else if (turn.isEnPassant()){
        undoEnPassant();
      }else {
        undoCasualTurn();
      }
    }else {
      if (turn.isTransformation()){
        makeTransformation(turn, updatedField);
      }else if (turn.isEnPassant()){
        makeEnPassant(turn, updatedFigure, updatedField);
      }else {
        makeCasualTurn(updatedFigure, updatedField);
      }
    }
  }

  private void undoTransformation(Turn turn, Field updatedField){
    Figure pawnToReborn = turn.getFigureToDestinationField().get(0).getFigure();
    Figure transformedFigureToRemove = fieldToFigure.get(turn.getFigureToDestinationField().get(0).getField());
    newFigureOccupation = updatedField;
    removeFigure(transformedFigureToRemove);
    register(pawnToReborn);
    notify(pawnToReborn);
    figures.forEach(f -> {
      f.possibleTurns();
      f.attackedFields();
    });

//    if (turn.isTransformation()) {
//      register(turn.getFigureToDestinationField().get(0).getFigure());
//      Figure figureToDelete = Board.getFieldToFigure().get(turn.getFigureToDestinationField().get(0).getField());
//      removeFigure(figureToDelete);
//    }


    fulfillResurrection();
  }

  private void undoEnPassant(){
    fulfillResurrection();
  }

  private void undoCasualTurn(){
    fulfillResurrection();
  }

  private void fulfillResurrection(){
    Figure figureToResurrect = UpdatePositionOnTheBoard.eatenFigureToResurrection;
    if (figureToResurrect != null) {
      register(figureToResurrect);
    }
  }

  private void makeTransformation(Turn turn, Field updatedField){
    Figure newFigureForUpdate = ParseWrittenTurn.getFigureBornFromTransformation();
    register(newFigureForUpdate);
    removeFigure(turn.getFigureToDestinationField().get(0).getFigure());
    newFigureOccupation = updatedField;
    notify(newFigureForUpdate);
    figures.forEach(f -> {
      f.possibleTurns();
      f.attackedFields();
    });
  }

  private void makeEnPassant(Turn turn, Figure updatedFigure, Field updatedField){
    enPassantPrey = turn.getTargetedFigure();
    makeCasualTurn(updatedFigure, updatedField);
  }

  private void makeCasualTurn(Figure updatedFigure, Field updatedField){
    newFigureOccupation = updatedField;
    notify(updatedFigure);
    figures.forEach(f -> {
      f.possibleTurns();
      f.attackedFields();
    });
  }

  @Override
  public void notify(Observer figure) {
    updateTakenFields((Figure) figure);
    figure.update(newFigureOccupation);
    figures.forEach(cf -> {
      if (!cf.equals(figure)) {
        cf.update();
      }
    });
    updateFieldsUnderWhiteInfluence();
    updateFieldsUnderBlackInfluence();
  }

  @Override
  public void removeFigure(Observer figure) {
    figures.remove(figure);
    if (((Figure) figure).getColor() == Color.BLACK) {
      blackFigures.remove(figure);
    } else {
      whiteFigures.remove(figure);
    }
    fieldToFigure.remove(((Figure) figure).getField());
    takenFields.remove(((Figure) figure).getField());
  }

  public List<Figure> getTypeOfFigures(Class clazz, Color color) {
    return getFiguresByColor(color).stream().filter(f -> f.getClass() == clazz).collect(Collectors.toList());
  }

  public King getKingByColor(Color color) {
    return (King) getFiguresByColor(color).stream().filter(f -> f.getClass() == King.class).collect(Collectors.toList()).get(0);
  }

  public List<Figure> getFiguresByColor(Color color) {
    return color == Color.WHITE ? whiteFigures : blackFigures;
  }
}