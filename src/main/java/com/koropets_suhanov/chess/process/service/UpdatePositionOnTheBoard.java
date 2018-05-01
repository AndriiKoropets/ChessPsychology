package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.process.dto.FigureToField;
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.process.utils.ProcessUtils;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;
import static com.koropets_suhanov.chess.process.service.Process.board;
import static com.koropets_suhanov.chess.process.service.ParseWrittenTurn.figureBornFromTransformation;
import static java.lang.Math.abs;

public class UpdatePositionOnTheBoard {
  private Set<Field> affectedFields;
  private List<FigureToField> figureToFieldList;
  private Figure pawnFromTransformation;
  public static Figure eatenFigureToResurrection;

  public void makeTurn(Turn turn) {
    getAffectedFields(turn);
//    System.out.println("Affected fields on the current turn = " + affectedFields);
//        System.out.println("Making turn = " + turn);
    prepareTurnForUndoing(turn);
    for (FigureToField figuresToFields : turn.getFigureToDestinationField()) {
      board.setNewCoordinates(turn, figuresToFields.getFigure(), figuresToFields.getField(), false);
    }
//    System.out.println(Board.getFieldToFigure());
    makePullAdditionalAlliesAndEnemies();
  }

  private void getAffectedFields(Turn turn) {
    affectedFields = new HashSet<>();
    for (FigureToField figureToDestinationField : turn.getFigureToDestinationField()) {
      affectedFields.add(figureToDestinationField.getFigure().getField());
      affectedFields.add(figureToDestinationField.getField());
    }
  }

  private void prepareTurnForUndoing(Turn turn) {
    figureToFieldList = new ArrayList<>();
    eatenFigureToResurrection = null;
    for (FigureToField figureToField : turn.getFigureToDestinationField()) {
      figureToFieldList.add(FigureToField.builder().figure(figureToField.getFigure()).field(figureToField.getFigure().getField()).build());
    }
    if (turn.isEating()) {
      if (turn.getFigureToDestinationField().size() == 1
          && turn.getFigureToDestinationField().get(0).getFigure().getClass() == Pawn.class
          && ((Pawn) turn.getFigureToDestinationField().get(0).getFigure()).isEnPassant()) {
        eatenFigureToResurrection = turn.getTargetedFigure().createNewFigure();
      } else {
        Figure targetEnemyWillBeEaten = Board.getFieldToFigure().get(turn.getFigureToDestinationField().get(0).getField());
        if (!turn.isEnPassant()) {
          eatenFigureToResurrection = targetEnemyWillBeEaten.createNewFigure();
        }
      }
    }
    if (turn.isTransformation()) {
      pawnFromTransformation = turn.getFigureToDestinationField().get(0).getFigure();
    }
    figureBornFromTransformation = turn.getFigureFromTransformation();
  }

  private void makePullAdditionalAlliesAndEnemies() {
    Map<Figure, Set<Figure>> figureToChosenAllies = new HashMap<>();
    Board.getFigures().forEach(f -> {
      Set<Figure> chosenAllies = (f).pullAdditionalAlliesAndEnemies();
      if (!ProcessUtils.isEmpty(chosenAllies)) {
        figureToChosenAllies.put(f, chosenAllies);
      }
    });
    for (int i = 0; i < SIZE; i++) {
      for (Figure curFigure : figureToChosenAllies.keySet()) {
        for (Figure ally : figureToChosenAllies.get(curFigure)) {
          if (ally != null) {
            doUpdate(curFigure, ally);
          }
        }
      }
    }
  }

  private void doUpdate(Figure curFigure, Figure ally) {
    for (Figure undefendedAlly : ally.getAlliesIProtect()) {
      updateProtectionOfUndefendedAllies(curFigure, ally, undefendedAlly);
    }
    for (Figure prey : ally.getWhoCouldBeEaten()) {
      updateWhoCouldBeEaten(curFigure, ally, prey);
    }
  }


  private void updateWhoCouldBeEaten(Figure curFigure, Figure ally, Figure prey) {
    if (!curFigure.getWhoCouldBeEaten().contains(prey) && ally.getWhoCouldBeEaten().contains(prey)
            && curFigure.getAttackedFields().contains(prey.getField()) && isOnTheSameLine(curFigure, ally, prey)) {
      curFigure.getWhoCouldBeEaten().add(prey);
      prey.addEnemy(curFigure);
    }
  }

  private void updateProtectionOfUndefendedAllies(Figure curFigure, Figure ally, Figure undefendedAlly) {
    if (!curFigure.getAlliesIProtect().contains(undefendedAlly) && ally.getAlliesIProtect().contains(undefendedAlly)
            && curFigure.getAttackedFields().contains(undefendedAlly.getField()) && isOnTheSameLine(curFigure, ally, undefendedAlly)
            && !curFigure.equals(undefendedAlly) && !ally.equals(undefendedAlly)) {
      curFigure.addAllyIProtect(undefendedAlly);
      undefendedAlly.addAllyProtectMe(curFigure);
    }
  }

  private boolean isOnTheSameLine(Figure f1, Figure f2, Figure f3) {
    if (isSomeoneBishop(f1, f2, f3)) {
      return (abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
              && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY()))
              && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY()));
    }
    if (isSomeoneRock(f1, f2, f3)) {
      return ((f1.getField().getX() == f2.getField().getX())
              && (f2.getField().getX() == f3.getField().getX()))
              || ((f1.getField().getY() == f2.getField().getY())
              && (f2.getField().getY() == f3.getField().getY()));
    }
    return ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX()))
            || ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY()))
            || (((abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
            && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY())))
            && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY())));
  }

  private boolean isSomeoneBishop(Figure f1, Figure f2, Figure f3) {
    return f1.getClass() == Bishop.class || f2.getClass() == Bishop.class || f3.getClass() == Bishop.class;
  }

  private boolean isSomeoneRock(Figure f1, Figure f2, Figure f3) {
    return f1.getClass() == Rock.class || f2.getClass() == Rock.class || f3.getClass() == Rock.class;
  }

  public void undoTurn(Turn turn) {
    Turn undoTurn = Turn.builder()
            .figureToDestinationField(figureToFieldList)
            .eating(false)
            .writtenStyle("")
            .numberOfTurn(turn.getNumberOfTurn())
            .build();
    for (FigureToField figuresToFields : undoTurn.getFigureToDestinationField()) {
      board.setNewCoordinates(turn, figuresToFields.getFigure(), figuresToFields.getField(), true);
    }
    eatenFigureToResurrection = null;
    makePullAdditionalAlliesAndEnemies();
  }

  public Set<Figure> getAffectedFigures(Color color) {
    Set<Figure> acceptedFigures = new HashSet<>();
    List<Figure> observers = Board.getFiguresByColor(color);
    affectedFields.forEach(f -> {
      observers.forEach(o -> {
        if ((o).getAttackedFields().contains(f)) {
          acceptedFigures.add(o);
        }
      });
    });
    return acceptedFigures;
  }
}
