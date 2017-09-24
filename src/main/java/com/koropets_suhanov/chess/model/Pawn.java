package com.koropets_suhanov.chess.model;


import com.koropets_suhanov.chess.process.Process;

import static com.koropets_suhanov.chess.model.Field.isValidField;

/**
 * @author AndriiKoropets
 */
public class Pawn extends Figure {

    private final static int PAWN_WEIGHT = 1;
    private final static int POINT = 1;
    private int numberOfMadeTurns;

    public Pawn(Field field, Color color) {
        super(field, color);
        attackedFields();
    }

    @Override
    public void possibleTurns() {
        if (this.getColor() == Color.WHITE){
            Field firstPossibleTurn = new Field(this.getField().getX() - 1, this.getField().getY());
            Field secondPossibleTurn = new Field(this.getField().getX() - 2, this.getField().getY());
            if (this.getField().getX() == 6){
                if (!firstPossibleTurn.isTaken()){
                    this.getPossibleFieldsToMove().add(firstPossibleTurn);
                }else {
                    return;
                }
                if (!secondPossibleTurn.isTaken()){
                    this.getPossibleFieldsToMove().add(secondPossibleTurn);
                }
            }else {
                if (!firstPossibleTurn.isTaken()){
                    this.getPossibleFieldsToMove().add(firstPossibleTurn);
                }
            }
        }else {
            Field firstPossibleTurn = new Field(this.getField().getX() + 1, this.getField().getY());
            Field secondPossibleTurn = new Field(this.getField().getX() + 2, this.getField().getY());
            if (this.getField().getX() == 1){
                if (!firstPossibleTurn.isTaken()){
                    this.getPossibleFieldsToMove().add(firstPossibleTurn);
                }else {
                    return;
                }
                if (!secondPossibleTurn.isTaken()){
                    this.getPossibleFieldsToMove().add(secondPossibleTurn);
                }
            }else {
                if (!firstPossibleTurn.isTaken()){
                    this.getPossibleFieldsToMove().add(firstPossibleTurn);
                }
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
    public void attackedFields() {
        int left;
        int right;
        if (this.getColor() == Color.WHITE){
            left = this.getField().getX() - 1;
            right = this.getField().getY() - 1;
            if (isValidField(left, right)){
                getAttackedFields().add(new Field(left, right));
            }
            left = this.getField().getX() - 1;
            right = this.getField().getY() + 1;
            if (isValidField(left,right)){
                getAttackedFields().add(new Field(left, right));
            }
        }else {
            left = this.getField().getX() + 1;
            right = this.getField().getY() - 1;
            if (isValidField(left, right)){
                getAttackedFields().add(new Field(left, right));
            }
            left = this.getField().getX() + 1;
            right = this.getField().getY() + 1;
            if (isValidField(left, right)){
                getAttackedFields().add(new Field(left, right));
            }
        }
        enPassant();
        fillAttackedAndProtectedFigures();
    }

    private void fillAttackedAndProtectedFigures(){
        getAttackedFields().forEach(f -> {
            Figure figure = Board.getFieldToFigure().get(f);
            if (figure != null){
                if (figure.getColor() == this.getColor()){
                    figure.addAllyProtectMe(this);
                    this.addAllyIProtect(figure);
                }else {
                    figure.addEnemy(this);
                    this.getWhoCouldBeEaten().add(figure);
                }
            }else {
                getFieldsUnderMyInfluence().add(f);
            }
        });
    }

    private void enPassant(){
        if (this.getColor() == Color.WHITE){
            if(this.getField().getX() == 3){
                Field leftField = new Field(3, this.getField().getY()-1);
                Figure leftEnemy = Board.getFieldToFigure().get(leftField);
                if (leftEnemy != null && leftEnemy.getColor() == Color.BLACK && leftEnemy.getClass() == Pawn.class && Process.BOARD.getPreviousTurn().equals(leftField)){
                    this.getWhoCouldBeEaten().add(leftEnemy);
                }
                Field rightField = new Field(3, this.getField().getY() + 1);
                Figure rightEnemy = Board.getFieldToFigure().get(rightField);
                if (rightEnemy != null && rightEnemy.getColor() == Color.BLACK && rightEnemy.getClass() == Pawn.class && Process.BOARD.getPreviousTurn().equals(rightField)){
                    this.getWhoCouldBeEaten().add(rightEnemy);
                }
            }
        }else {
            if (this.getField().getX() == 4){
                Field leftField = new Field(4, this.getField().getY() - 1);
                Figure leftEnemy = Board.getFieldToFigure().get(leftField);
                if (leftEnemy != null && leftEnemy.getColor() == Color.WHITE && leftEnemy.getClass() == Pawn.class && Process.BOARD.getPreviousTurn().equals(leftField)){
                    this.getWhoCouldBeEaten().add(leftEnemy);
                }
                Field rightField = new Field(4, this.getField().getY() + 1);
                Figure rightEnemy = Board.getFieldToFigure().get(rightField);
                if (rightEnemy != null && rightEnemy.getColor() == Color.WHITE && rightEnemy.getClass() == Pawn.class && Process.BOARD.getPreviousTurn().equals(rightField)){
                    this.getWhoCouldBeEaten().add(rightEnemy);
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.getField().toString();
    }
}