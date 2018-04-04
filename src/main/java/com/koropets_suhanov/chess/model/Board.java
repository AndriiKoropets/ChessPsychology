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
  private static List<Observer> figures = new ArrayList<>();
  private static List<Observer> whiteFigures = new ArrayList<>();
  private static List<Observer> blackFigures = new ArrayList<>();
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
  private static Field field;
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
    figures.forEach(v -> ((Figure) v).possibleTurns());
    whiteFigures.forEach(white -> fieldsUnderWhiteInfluence.addAll(((Figure) white).getFieldsUnderMyInfluence()));
    blackFigures.forEach(black -> fieldsUnderBlackInfluence.addAll(((Figure) black).getFieldsUnderMyInfluence()));
    System.out.println("WhiteFigures size = " + whiteFigures.size());
    System.out.println("BlackFigures size = " + blackFigures.size());
  }

  @Override
  public void register(Observer figure) {
    figures.add(figure);
    if (((Figure) figure).getColor() == Color.WHITE) {
      whiteFigures.add(figure);
    } else {
      blackFigures.add(figure);
    }
    Field field = ((Figure) figure).getField();
    fieldToFigure.put(((Figure) figure).getField(), ((Figure) figure));
    takenFields.add(field);
  }

  private static void setTakenFields() {
    figures.forEach(f -> takenFields.add(((Figure) f).getField()));
  }

  private static void updateTakenFields(Observer figure) {
    takenFields.remove(((Figure) figure).getField());
    takenFields.add(field);
    fieldToFigure.put(field, (Figure) figure);
    fieldToFigure.replace(((Figure) figure).getField(), null);
  }

  private static void updateFieldsUnderWhiteInfluence() {
    fieldsUnderWhiteInfluence.clear();
    whiteFigures.forEach(w -> fieldsUnderWhiteInfluence.addAll(((Figure) w).getFieldsUnderMyInfluence()));
  }

  private static void updateFieldsUnderBlackInfluence() {
    fieldsUnderBlackInfluence.clear();
    blackFigures.forEach(b -> fieldsUnderBlackInfluence.addAll(((Figure) b).getFieldsUnderMyInfluence()));
  }

  public void setNewCoordinates(Turn turn, Figure updatedFigure, Field updatedField, boolean isUndoing) {
    Figure eatenFigure = turn.getTargetedFigure();
    boolean enPassant = turn.isEnPassant();
    if (eatenFigure != null) {
      removeFigure(eatenFigure);
    }
//        System.out.println("updatedFigure " + updatedFigure);
//        System.out.println("updatedField " + updatedField);
//        System.out.println("eatenFigure " + eatenFigure);
//        System.out.println("isUndoing " + isUndoing);
//        System.out.println("enPassant " + enPassant);
//        System.out.println("isTransformation " + turn.isTransformation());
//        for (Observer f : figures){
//            System.out.println(f);
//        }
//        Process.printAllBoard();
    if (turn.isTransformation() && isUndoing) {
      Figure pawnToReborn = turn.getFigureToDestinationField().get(0).getFigure();
      Figure transformedFigureToRemove = Board.getFieldToFigure().get(turn.getFigureToDestinationField().get(0).getField());
      this.field = updatedField;
      removeFigure(transformedFigureToRemove);
      register(pawnToReborn);
      notify(pawnToReborn);
      figures.forEach(f -> {
        ((Figure) f).possibleTurns();
        ((Figure) f).attackedFields();
      });
    } else {
      if (figures.contains(updatedFigure)) {
        if (!turn.isTransformation()) {
          this.field = updatedField;
          notify(updatedFigure);
          figures.forEach(f -> {
            ((Figure) f).possibleTurns();
            ((Figure) f).attackedFields();
          });
        } else {
          Figure newFigureForUpdate = ParseWrittenTurn.getFigureBornFromTransformation();
          register(newFigureForUpdate);
          removeFigure(turn.getFigureToDestinationField().get(0).getFigure());
          this.field = updatedField;
          notify(newFigureForUpdate);
          figures.forEach(f -> {
            ((Figure) f).possibleTurns();
            ((Figure) f).attackedFields();
          });
//                    Process.printAllBoard();
        }

      } else {
        throw new RuntimeException("There is no such figure on the Board: " + updatedFigure);
      }
      if (isUndoing) {
        if (turn.isTransformation()) {
          register(turn.getFigureToDestinationField().get(0).getFigure());
          Figure figureToDelete = Board.getFieldToFigure().get(turn.getFigureToDestinationField().get(0).getField());
          removeFigure(figureToDelete);
        }
        Figure figureToResurrect = UpdatePositionOnTheBoard.eatenFigureToResurrection;
        if (figureToResurrect != null) {
//                Process.printAllBoard();
          register(figureToResurrect);
        }
      }
      if (enPassant) {
        enPassantPrey = turn.getTargetedFigure();
      }
    }
//        Process.printAllBoard();
  }

  @Override
  public void notify(Observer figure) {
    updateTakenFields(figure);
    figure.update(field);
    figures.forEach(cf -> {
      if (!cf.equals(figure)) {
        ((Figure) cf).update();
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

  public static List<Figure> getExactTypeOfFiguresByColor(Class clazz, Color color) {
    List<Observer> observers = getFiguresByColor(color);
    List<Figure> figures = new ArrayList<>();
    observers = observers.stream().filter(f -> f.getClass() == clazz).collect(Collectors.toList());
    observers.forEach(observer -> figures.add((Figure) observer));
    return figures;
  }

  public static King getKingByColor(Color color) {
    return (King) getFiguresByColor(color).stream().filter(f -> f.getClass() == King.class).collect(Collectors.toList()).get(0);
  }

  public static List<Observer> getFiguresByColor(Color color) {
    return color == Color.WHITE ? whiteFigures : blackFigures;
  }
}