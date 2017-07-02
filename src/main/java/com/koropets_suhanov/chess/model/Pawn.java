package com.koropets_suhanov.chess.model;


import static com.koropets_suhanov.chess.model.Field.isValidField;

/**
 * @author AndriiKoropets
 */
public class Pawn extends Figure {

    private final static int PAWN_WEIGHT = 1;
    private final static int POINT = 1;
    private int numberOfDoneTurns;

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
        for (Field field : getAttackedFields()){
            Figure figure = Board.getFieldToFigure().get(field);
            if (figure != null){
                if (figure.getColor() == this.getColor()){
                    figure.addAlien(this);
                }else {
                    figure.addEnemy(this);
                    this.getWhoCouldBeKilled().add(figure);
                }
            }else {
                getFieldsUnderMyInfluence().add(field);
            }
        }
    }

    private void enPassant(){
        if (this.getColor() == Color.WHITE){
            if(this.getField().getX() == 3){
                Field leftField = new Field(3, this.getField().getY()-1);
                Figure leftEnemy = Board.getFieldToFigure().get(leftField);
                if (leftEnemy != null && leftEnemy.getColor() == Color.BLACK && leftEnemy.getClass() == Pawn.class && Board.getInstance().getPreviousTurn().equals(leftField)){
                    this.getWhoCouldBeKilled().add(leftEnemy);
                }
                Field rightField = new Field(3, this.getField().getY() + 1);
                Figure rightEnemy = Board.getFieldToFigure().get(rightField);
                if (rightEnemy != null && rightEnemy.getColor() == Color.BLACK && rightEnemy.getClass() == Pawn.class && Board.getInstance().getPreviousTurn().equals(rightField)){
                    this.getWhoCouldBeKilled().add(rightEnemy);
                }
            }
        }else {
            if (this.getField().getX() == 4){
                Field leftField = new Field(4, this.getField().getY() - 1);
                Figure leftEnemy = Board.getFieldToFigure().get(leftField);
                if (leftEnemy != null && leftEnemy.getColor() == Color.WHITE && leftEnemy.getClass() == Pawn.class && Board.getInstance().getPreviousTurn().equals(leftField)){
                    this.getWhoCouldBeKilled().add(leftEnemy);
                }
                Field rightField = new Field(4, this.getField().getY() + 1);
                Figure rightEnemy = Board.getFieldToFigure().get(rightField);
                if (rightEnemy != null && rightEnemy.getColor() == Color.WHITE && rightEnemy.getClass() == Pawn.class && Board.getInstance().getPreviousTurn().equals(rightField)){
                    this.getWhoCouldBeKilled().add(rightEnemy);
                }
            }
        }
    }
}