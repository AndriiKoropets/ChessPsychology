package com.koropets_suhanov.chess.model;

import com.koropets_suhanov.chess.process.service.Process;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import static com.koropets_suhanov.chess.model.Field.isValidField;

public class Pawn extends Figure {

  private final static int PAWN_WEIGHT = 1;
  private final static int POINT = 1;
  private boolean enPassant;
  private Field enPassantField;
  @Getter
  @Setter
  private Field previousPosition;
  private Figure enPassantEnemy;

  public Pawn(Field field, Color color) {
    super(field, color);
    previousPosition = field;
    attackedFields();
  }

  @Override
  public void possibleTurns() {
    if (this.getColor() == Color.WHITE) {
      whitePossibleTurns();
    } else {
      blackPossibleTurns();
    }
  }

  private void whitePossibleTurns() {
    Field firstPossibleTurn = new Field(this.getField().getX() - 1, this.getField().getY());
    if (this.getField().getX() == 6) {
      whiteInitialPosition(firstPossibleTurn);
    } else {
      whiteNotInitialPosition(firstPossibleTurn);
    }
  }

  private void whiteInitialPosition(Field firstPossibleTurn) {
    Field secondPossibleTurn = new Field(this.getField().getX() - 2, this.getField().getY());
    if (!firstPossibleTurn.isTaken()) {
      this.getPossibleFieldsToMove().add(firstPossibleTurn);
    } else {
      return;
    }
    if (!secondPossibleTurn.isTaken()) {
      this.getPossibleFieldsToMove().add(secondPossibleTurn);
    }
  }

  private void whiteNotInitialPosition(Field firstPossibleTurn) {
    if (!firstPossibleTurn.isTaken()) {
      this.getPossibleFieldsToMove().add(firstPossibleTurn);
    }
  }

  private void blackPossibleTurns() {
    Field firstPossibleTurn = new Field(this.getField().getX() + 1, this.getField().getY());
    if (this.getField().getX() == 1) {
      Field secondPossibleTurn = new Field(this.getField().getX() + 2, this.getField().getY());
      if (!firstPossibleTurn.isTaken()) {
        this.getPossibleFieldsToMove().add(firstPossibleTurn);
      } else {
        return;
      }
      if (!secondPossibleTurn.isTaken()) {
        this.getPossibleFieldsToMove().add(secondPossibleTurn);
      }
    } else {
      if (!firstPossibleTurn.isTaken()) {
        this.getPossibleFieldsToMove().add(firstPossibleTurn);
      }
    }
  }

  @Override
  public double getValue() {
    return PAWN_WEIGHT;
  }

  @Override
  public int getPoint() {
    return POINT;
  }

  @Override
  public Set<Figure> pullAdditionalAlliesAndEnemies() {
    return null;
  }

  @Override
  public Figure createNewFigure() {
    return new Pawn(this.getField(), this.getColor());
  }

  @Override
  public void attackedFields() {
    int left;
    int right;
    enPassant = false;
    enPassantField = null;
    enPassantEnemy = null;
    if (this.getColor() == Color.WHITE) {
      left = this.getField().getX() - 1;
      right = this.getField().getY() - 1;
      if (isValidField(left, right)) {
        getAttackedFields().add(new Field(left, right));
      }
      left = this.getField().getX() - 1;
      right = this.getField().getY() + 1;
      if (isValidField(left, right)) {
        getAttackedFields().add(new Field(left, right));
      }
    } else {
      left = this.getField().getX() + 1;
      right = this.getField().getY() - 1;
      if (isValidField(left, right)) {
        getAttackedFields().add(new Field(left, right));
      }
      left = this.getField().getX() + 1;
      right = this.getField().getY() + 1;
      if (isValidField(left, right)) {
        getAttackedFields().add(new Field(left, right));
      }
    }
    enPassant();
    fillAttackedAndProtectedFigures();
  }

  private void fillAttackedAndProtectedFigures() {
    getAttackedFields().forEach(f -> {
      Figure figure = Board.getFieldToFigure().get(f);
      if (figure != null) {
        if (figure.getColor() == this.getColor()) {
          figure.addAllyProtectMe(this);
          this.addAllyIProtect(figure);
        } else {
          figure.addEnemy(this);
          this.getWhoCouldBeEaten().add(figure);
          this.getPreyField().add(figure.getField());
        }
      } else {
        getFieldsUnderMyInfluence().add(f);
      }
    });
  }

  public boolean isEnPassant() {
    return enPassant;
  }

  public Field getEnPassantField() {
    return enPassantField;
  }

  public Figure getEnPassantEnemy() {
    return enPassantEnemy;
  }

  private void enPassant() {
    if (Process.currentTurnNumber >= 2) {
      if (this.getColor() == Color.WHITE) {
        if (this.getField().getX() == 3
            && Process.currentBlackTurn.getFigureToDestinationField().size() == 1
            && Process.currentBlackTurn.getFigureToDestinationField().get(0).getFigure().getClass() == this.getClass()
            && Process.currentBlackTurn.getFigureToDestinationField().get(0).getFigure().getColor() != this.getColor()
            && !this.getFieldsUnderMyInfluence().contains(((Pawn) Process.currentBlackTurn.getFigureToDestinationField().get(0).getFigure()).getPreviousPosition())
            && enemyStaysLeftOrRight((Pawn) Process.currentBlackTurn.getFigureToDestinationField().get(0).getFigure())) {
//          this.printAllInformation();
          Field leftField = null;
          Figure leftEnemy = null;
          if (this.getField().getY() != 0) {
            leftField = new Field(3, this.getField().getY() - 1);
            leftEnemy = Board.getFieldToFigure().get(leftField);
          }
          if (leftEnemy != null && leftEnemy.getColor() == Color.BLACK && leftEnemy.getClass() == Pawn.class
              && Process.currentBlackTurn.getFigureToDestinationField().get(0).getFigure().equals(leftEnemy)) {
            initializeEnPassant(leftField, leftEnemy, Color.WHITE);
          }
          Field rightField = null;
          Figure rightEnemy = null;
          if (this.getField().getY() != 7) {
            rightField = new Field(3, this.getField().getY() + 1);
            rightEnemy = Board.getFieldToFigure().get(rightField);
          }
          if (rightEnemy != null && rightEnemy.getColor() == Color.BLACK && rightEnemy.getClass() == Pawn.class
              && Process.currentBlackTurn.getFigureToDestinationField().get(0).getFigure().equals(rightEnemy)) {
            initializeEnPassant(rightField, rightEnemy, Color.WHITE);
          }
        }
      } else {
        if (this.getField().getX() == 4
            && Process.currentWhiteTurn.getFigureToDestinationField().size() == 1
            && Process.currentWhiteTurn.getFigureToDestinationField().get(0).getFigure().getClass() == this.getClass()
            && Process.currentWhiteTurn.getFigureToDestinationField().get(0).getFigure().getColor() != this.getColor()
            && !this.getFieldsUnderMyInfluence().contains(((Pawn) Process.currentWhiteTurn.getFigureToDestinationField().get(0).getFigure()).getPreviousPosition())
            && enemyStaysLeftOrRight((Pawn) Process.currentWhiteTurn.getFigureToDestinationField().get(0).getFigure())) {
//          System.out.println("EnPassant turn previous = " + Process.currentWhiteTurn + " Field = " + Process.currentWhiteTurn.getFigureToDestinationField().get(0).getField() + " x= " + + Process.currentWhiteTurn.getFigureToDestinationField().get(0).getField().getX());
//          this.printAllInformation();
          Field leftField = null;
          Figure leftEnemy = null;
          if (this.getField().getY() != 0) {
            leftField = new Field(4, this.getField().getY() - 1);
            leftEnemy = Board.getFieldToFigure().get(leftField);
          }
          if (leftEnemy != null && leftEnemy.getColor() == Color.WHITE && leftEnemy.getClass() == Pawn.class
              && Process.currentWhiteTurn.getFigureToDestinationField().get(0).getFigure().equals(leftEnemy)) {
            initializeEnPassant(leftField, leftEnemy, Color.BLACK);
          }
          Field rightField = null;
          Figure rightEnemy = null;
          if (this.getField().getY() != 7) {
            rightField = new Field(4, this.getField().getY() + 1);
            rightEnemy = Board.getFieldToFigure().get(rightField);
          }
          if (rightEnemy != null && rightEnemy.getColor() == Color.WHITE && rightEnemy.getClass() == Pawn.class
              && Process.currentWhiteTurn.getFigureToDestinationField().get(0).getFigure().equals(rightEnemy)) {
            initializeEnPassant(rightField, rightEnemy, Color.BLACK);
          }
        }
      }
    }
  }

  private boolean enemyStaysLeftOrRight(Pawn enemy) {
    return this.getField().getY() + 1 == enemy.getField().getY() || this.getField().getY() - 1 == enemy.getField().getY();
  }

  private void initializeEnPassant(Field enemyField, Figure enemy, Color color) {
    this.getWhoCouldBeEaten().add(enemy);
    this.getPreyField().add(enemyField);
    enPassantEnemy = Board.getFieldToFigure().get(enemyField);
    enPassant = true;
    enPassantField = (color == Color.WHITE) ? new Field(enemyField.getX() - 1, enemyField.getY())
        : new Field(enemyField.getX() + 1, enemyField.getY());
  }

  public boolean isOnThePenultimateLine() {
    return (this.getColor() == Color.BLACK && this.getField().getX() == 6)
        || (this.getColor() == Color.WHITE && this.getField().getX() == 1);
  }

  @Override
  public String toString() {
    return this.getField().toString();
  }
}