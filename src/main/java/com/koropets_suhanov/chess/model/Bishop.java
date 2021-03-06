package com.koropets_suhanov.chess.model;

import static java.lang.Math.abs;

import java.util.HashSet;
import java.util.Set;

import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;

public class Bishop extends Figure {

  private final static int BISHOP_WEIGHT = 3;
  private final static int POINT = 2;

  public Bishop(Field field, Color color) {
    super(field, color);
    attackedFields();
  }

  @Override
  protected void attackedFields() {
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        if (abs(this.getField().getX() - i) == abs(this.getField().getY() - j) && abs(this.getField().getY() - j) != 0) {
          this.getAttackedFields().add(new Field(i, j));
        }
      }
    }
  }

  @Override
  public void possibleTurns() {
    aboveRightDiagonalPossibleTurnsBishopAndQueen();
    aboveLeftDiagonalPossibleTurnsBishopAndQueen();
    belowRightDiagonalPossibleTurnsBishopAndQueen();
    belowLeftDiagonalPossibleTurnsBishopAndQueen();
  }

  @Override
  public double getValue() {
    return BISHOP_WEIGHT;
  }

  @Override
  public int getPoint() {
    return POINT;
  }

  @Override
  public Set<Figure> pullAdditionalAlliesAndEnemies() {
    Set<Figure> chosenAllies = new HashSet<>();
    this.getAlliesIProtect().forEach(f -> {
      if (f.getClass() == Bishop.class || f.getClass() == Queen.class) {
        chosenAllies.add(f);
      }
    });
    return chosenAllies;
  }

  @Override
  public Figure createNewFigure() {
    return new Bishop(this.getField(), this.getColor());
  }

  @Override
  public String toString() {
    return "B" + this.getField().toString();
  }
}