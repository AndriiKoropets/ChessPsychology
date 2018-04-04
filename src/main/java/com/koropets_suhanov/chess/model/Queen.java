package com.koropets_suhanov.chess.model;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;
import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;

public class Queen extends Figure {

  private final static int QUEEN_WEIGHT = 9;
  private final static int POINT = 4;

  public Queen(Field field, Color color) {
    super(field, color);
    attackedFields();
  }

  @Override
  public void possibleTurns() {
    abovePossibleTurnsRockAndQueen();
    belowPossibleTurnsRockAndQueen();
    rightPossibleTurnsRockAndQueen();
    leftPossibleTurnsRockAndQueen();
    aboveRightDiagonalPossibleTurnsBishopAndQueen();
    aboveLeftDiagonalPossibleTurnsBishopAndQueen();
    belowRightDiagonalPossibleTurnsBishopAndQueen();
    belowLeftDiagonalPossibleTurnsBishopAndQueen();
  }

  @Override
  public double getValue() {
    return QUEEN_WEIGHT;
  }

  @Override
  public int getPoint() {
    return POINT;
  }

  @Override
  public Set<Figure> pullAdditionalAlliesAndEnemies() {
    Set<Figure> chosenAllies = new HashSet<>();
    this.getAlliesIProtect().forEach(f -> {
      if (f.getClass() == Bishop.class || f.getClass() == Rock.class || f.getClass() == Queen.class) {
        chosenAllies.add(f);
      }
    });
    return chosenAllies;
  }

  @Override
  public Figure createNewFigure() {
    return new Queen(this.getField(), this.getColor());
  }

  @Override
  protected void attackedFields() {
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        if ((i == this.getField().getX() || j == this.getField().getY()) || (abs(this.getField().getX() - i) == abs(this.getField().getY() - j))) {
          if (this.getField().getX() == i && this.getField().getY() == j) {
            continue;
          }
          this.getAttackedFields().add(new Field(i, j));
        }
      }
    }
  }

  @Override
  public String toString() {
    return "Q" + this.getField();
  }
}