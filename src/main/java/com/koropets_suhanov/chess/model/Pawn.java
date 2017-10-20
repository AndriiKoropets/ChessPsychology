package com.koropets_suhanov.chess.model;

import java.util.Set;

import static com.koropets_suhanov.chess.model.Field.isValidField;

/**
 * @author AndriiKoropets
 */
public class Pawn extends Figure {

    private final static int PAWN_WEIGHT = 1;
    private final static int POINT = 1;
    private boolean enPassant;

    public Pawn(Field field, Color color) {
        super(field, color);
        attackedFields();
    }

    @Override
    public void possibleTurns() {
        if (this.getColor() == Color.WHITE){
            Field firstPossibleTurn = new Field(this.getField().getX() - 1, this.getField().getY());
            if (this.getField().getX() == 6){

                Field secondPossibleTurn = new Field(this.getField().getX() - 2, this.getField().getY());
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
            if (this.getField().getX() == 1){
                Field secondPossibleTurn = new Field(this.getField().getX() + 2, this.getField().getY());
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
                    this.getPreyField().add(figure.getField());
                }
            }else {
                getFieldsUnderMyInfluence().add(f);
            }
        });
    }

    public boolean getEnPassant(){
        return enPassant;
    }

    private void enPassant(){
        if (Board.getPreviousTurn() != null && Board.getPreviousTurn().getFigureToDestinationField().size() == 1
                && Board.getPreviousTurn().getFigureToDestinationField().get(0)._1.getClass() == this.getClass()
                && Board.getPreviousTurn().getFigureToDestinationField().get(0)._1.getColor() != this.getColor()){
            if (this.getColor() == Color.WHITE){
                if(this.getField().getX() == 3){
                    Field leftField = new Field(3, this.getField().getY()-1);
                    Figure leftEnemy = Board.getFieldToFigure().get(leftField);
                    if (leftEnemy != null && leftEnemy.getColor() == Color.BLACK && leftEnemy.getClass() == Pawn.class
                            && Board.getPreviousTurn().getFigureToDestinationField().get(0)._1.equals(leftEnemy)){
                        this.getWhoCouldBeEaten().add(leftEnemy);
                        this.getPreyField().add(leftField);
                        enPassant = true;
                    }
                    Field rightField = new Field(3, this.getField().getY() + 1);
                    Figure rightEnemy = Board.getFieldToFigure().get(rightField);
                    if (rightEnemy != null && rightEnemy.getColor() == Color.BLACK && rightEnemy.getClass() == Pawn.class
                            && Board.getPreviousTurn().getFigureToDestinationField().get(0)._1.equals(rightEnemy)){
                        this.getWhoCouldBeEaten().add(rightEnemy);
                        this.getPreyField().add(rightField);
                        enPassant = true;
                    }
                }
            }else {
                if (this.getField().getX() == 4){
                    Field leftField = new Field(4, this.getField().getY() - 1);
                    Figure leftEnemy = Board.getFieldToFigure().get(leftField);
                    System.out.println("Pawn = " + this);
                    System.out.println("Left field = " + leftField);
                    System.out.println("Left enemy = " + leftEnemy);
                    System.out.println("Previous turn = " + Board.getPreviousTurn());
                    if (leftEnemy != null && leftEnemy.getColor() == Color.WHITE && leftEnemy.getClass() == Pawn.class
                            && Board.getPreviousTurn().getFigureToDestinationField().get(0)._1.equals(leftEnemy)){
                        this.getWhoCouldBeEaten().add(leftEnemy);
                        this.getPreyField().add(leftField);
                        enPassant = true;
                    }
                    Field rightField = new Field(4, this.getField().getY() + 1);
                    Figure rightEnemy = Board.getFieldToFigure().get(rightField);

                    System.out.println("Pawn = " + this);
                    System.out.println("Right field = " + rightField);
                    System.out.println("Right enemy = " + rightEnemy);
                    System.out.println("Previous turn = " + Board.getPreviousTurn());
                    if (rightEnemy != null && rightEnemy.getColor() == Color.WHITE && rightEnemy.getClass() == Pawn.class
                            && Board.getPreviousTurn().getFigureToDestinationField().get(0)._1.equals(rightEnemy)){
                        this.getWhoCouldBeEaten().add(rightEnemy);
                        this.getPreyField().add(rightField);
                        enPassant = true;
                    }
                    System.out.println(this + " " + this.getWhoCouldBeEaten());
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.getField().toString();
    }
}