package com.koropets_suhanov.chess.model;

import java.util.HashSet;
import java.util.Set;

import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;

public class Rock extends Figure {

  private final static double ROCK_WEIGHT = 4.5;
  private final static int POINT = 3;
  private boolean opportunityToCastling = true;

  public Rock(Field field, Color color) {
    super(field, color);
    attackedFields();
  }

  public boolean isOpportunityToCastling() {
    return opportunityToCastling;
  }

  public void looseOpportunityToCastling() {
    this.opportunityToCastling = false;
  }

  @Override
  public void possibleTurns() {
    abovePossibleTurnsRockAndQueen();
    belowPossibleTurnsRockAndQueen();
    rightPossibleTurnsRockAndQueen();
    leftPossibleTurnsRockAndQueen();
  }

  @Override
  public double getValue() {
    return ROCK_WEIGHT;
  }

  @Override
  public int getPoint() {
    return POINT;
  }

  @Override
  public Set<Figure> pullAdditionalAlliesAndEnemies() {
    Set<Figure> chosen = new HashSet<>();
    this.getAlliesIProtect().forEach(f -> {
      if (f.getClass() == Rock.class || f.getClass() == Queen.class) {
        chosen.add(f);
      }
    });
    return chosen;
  }

  @Override
  public Figure createNewFigure() {
    return new Rock(this.getField(), this.getColor());
  }

  @Override
  protected void attackedFields() {
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        if (i == this.getField().getX() || j == this.getField().getY()) {
          if (i == this.getField().getX() && j == this.getField().getY()) {
            continue;
          }
          this.getAttackedFields().add(new Field(i, j));
        }
      }
    }
  }

  @Override
  public String toString() {
    return "R" + this.getField();
  }
}