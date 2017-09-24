package com.koropets_suhanov.chess.model;

/**
 * @author AndriiKoropets
 */
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
        for (int i = this.getField().getX() + 1; i < Board.SIZE; i++){
            Field field = new Field(i, this.getField().getY());
            if (checkingFieldForTaken(field)){
                break;
            }else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
        for (int i = this.getField().getX() - 1; i >= 0; i--){
            Field field = new Field(i, this.getField().getY());
            if (checkingFieldForTaken(field)){
                break;
            }else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
        for (int j = this.getField().getY() + 1; j < Board.SIZE; j++){
            Field field = new Field(this.getField().getX(), j);
            if (checkingFieldForTaken(field)){
                break;
            }else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
        for (int j = this.getField().getY() - 1; j >= 0; j--){
            Field field = new Field(this.getField().getX(), j);
            if (checkingFieldForTaken(field)){
                break;
            }else {
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
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
    protected void attackedFields() {
        for (int i = 0; i < Board.SIZE; i++){
            for (int j = 0; j < Board.SIZE; j++){
                if (i == this.getField().getX() || j == this.getField().getY()){
                    if (i == this.getField().getX() && j == this.getField().getY()){
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