package com.koropets_suhanov.chess.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;
import static java.lang.Math.abs;

@Getter
public abstract class Figure implements Observer {

    private Field field;
    private Color color;
    private Set<Figure> enemiesAttackMe = new LinkedHashSet<Figure>();
    private Set<Figure> alliesProtectMe = new LinkedHashSet<Figure>();
    private Set<Figure> whoCouldBeEaten = new LinkedHashSet<Figure>();
    private Set<Figure> whoCouldBeEatenPreviousState = new LinkedHashSet<Figure>();
    private Set<Figure> alliesIProtect = new LinkedHashSet<Figure>();
    private Set<Field> attackedFields = new LinkedHashSet<Field>();
    private Set<Field> fieldsUnderMyInfluence = new LinkedHashSet<Field>();
    private Set<Field> possibleFieldsToMove = new LinkedHashSet<Field>();
    private Set<Field> preyField = new HashSet<>();

    protected abstract void attackedFields();

    public abstract void possibleTurns();

    public abstract double getValue();

    public abstract int getPoint();

    public abstract Set<Figure> pullAdditionalAlliesAndEnemies();

    public abstract Figure createNewFigure();

    public Figure() {
    }

    public Figure(Field field, Color color) {
        this.field = field;
        this.color = color;
    }

    public void update(Field field) {
        this.field = field;
        this.enemiesAttackMe.clear();
        this.alliesProtectMe.clear();
        this.whoCouldBeEatenPreviousState.clear();
        this.whoCouldBeEatenPreviousState.addAll(this.whoCouldBeEaten);
        this.whoCouldBeEaten.clear();
        this.alliesIProtect.clear();
        this.attackedFields.clear();
        this.preyField.clear();
        this.possibleFieldsToMove.clear();
        this.fieldsUnderMyInfluence.clear();
        if (this.getClass() == King.class) {
            ((King) this).looseOpportunityToCastling();
        } else {
            if (this.getClass() == Rock.class) {
                ((Rock) this).looseOpportunityToCastling();
            }
        }
        attackedFields();
        possibleTurns();
    }

    public void update() {
        this.enemiesAttackMe.clear();
        this.alliesProtectMe.clear();
        this.whoCouldBeEatenPreviousState.clear();
        this.whoCouldBeEatenPreviousState.addAll(this.whoCouldBeEaten);
        this.whoCouldBeEaten.clear();
        this.alliesIProtect.clear();
        this.attackedFields.clear();
        this.preyField.clear();
        this.possibleFieldsToMove.clear();
        this.fieldsUnderMyInfluence.clear();
        attackedFields();
        possibleTurns();
    }

    public void addEnemy(Figure figure) {
        enemiesAttackMe.add(figure);
    }

    public void addAllyProtectMe(Figure figure) {
        alliesProtectMe.add(figure);
    }

    public void addAllyIProtect(Figure figure) {
        alliesIProtect.add(figure);
    }

    protected boolean checkingFieldForTaken(Field field) {
        if (!field.isTaken()) {
            this.getPossibleFieldsToMove().add(field);
        } else {
            Figure tempFigure = Board.getFieldToFigure().get(field);
            if (tempFigure != null) {
                if (tempFigure.getColor() == this.getColor()) {
                    tempFigure.addAllyProtectMe(this);
                    this.addAllyIProtect(tempFigure);
                    return true;
                } else {
                    tempFigure.addEnemy(this);
                    this.getWhoCouldBeEaten().add(tempFigure);
                    this.getPreyField().add(tempFigure.getField());
                    return true;
                }
            }
        }
        return false;
    }


    protected void abovePossibleTurnsRockAndQueen() {
        for (int i = this.getField().getX() + 1; i < SIZE; i++) {
            Field field = new Field(i, this.getField().getY());
            if (checkingFieldForTaken(field)) {
                break;
            } else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
    }

    protected void belowPossibleTurnsRockAndQueen() {
        for (int i = this.getField().getX() - 1; i >= 0; i--) {
            Field field = new Field(i, this.getField().getY());
            if (checkingFieldForTaken(field)) {
                break;
            } else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
    }

    protected void rightPossibleTurnsRockAndQueen() {
        for (int j = this.getField().getY() + 1; j < SIZE; j++) {
            Field field = new Field(this.getField().getX(), j);
            if (checkingFieldForTaken(field)) {
                break;
            } else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
    }

    protected void leftPossibleTurnsRockAndQueen() {
        for (int j = this.getField().getY() - 1; j >= 0; j--) {
            Field field = new Field(this.getField().getX(), j);
            if (checkingFieldForTaken(field)) {
                break;
            } else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
    }

    protected void aboveRightDiagonalPossibleTurnsBishopAndQueen() {
        for (int i = this.getField().getX() + 1; i < SIZE; i++) {
            boolean flag = false;
            for (int j = this.getField().getY() + 1; j < SIZE; j++) {
                if (i < SIZE && j < SIZE && abs(this.getField().getX() - i) == abs(this.getField().getY() - j)) {
                    Field field = new Field(i, j);
                    if (checkingFieldForTaken(field)) {
                        flag = true;
                        break;
                    } else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag) {
                break;
            }
        }
    }

    protected void aboveLeftDiagonalPossibleTurnsBishopAndQueen() {
        for (int i = this.getField().getX() + 1; i < SIZE; i++) {
            boolean flag = false;
            for (int j = this.getField().getY() - 1; j >= 0; j--) {
                if (i < SIZE && j >= 0 && abs(this.getField().getX() - i) == abs(this.getField().getY() - j)) {
                    Field field = new Field(i, j);
                    if (checkingFieldForTaken(field)) {
                        flag = true;
                        break;
                    } else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag) {
                break;
            }
        }
    }

    protected void belowRightDiagonalPossibleTurnsBishopAndQueen() {
        for (int i = this.getField().getX() - 1; i >= 0; i--) {
            boolean flag = false;
            for (int j = this.getField().getY() + 1; j < SIZE; j++) {
                if (i >= 0 && abs(this.getField().getX() - i) == abs(this.getField().getY() - j)) {
                    Field field = new Field(i, j);
                    if (checkingFieldForTaken(field)) {
                        flag = true;
                        break;
                    } else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag) {
                break;
            }
        }
    }

    protected void belowLeftDiagonalPossibleTurnsBishopAndQueen() {
        for (int i = this.getField().getX() - 1; i >= 0; i--) {
            boolean flag = false;
            for (int j = this.getField().getY() - 1; j >= 0; j--) {
                if (i >= 0 && j >= 0 && abs(this.getField().getX() - i) == abs(this.getField().getY() - j)) {
                    Field field = new Field(i, j);
                    if (checkingFieldForTaken(field)) {
                        flag = true;
                        break;
                    } else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag) {
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Figure figure = (Figure) o;
        return this.getField() == figure.getField()
                && this.getColor() == figure.getColor();
    }

    @Override
    public int hashCode() {
        return 31 * this.getField().getX() + 97 * this.getField().getY();
    }
}