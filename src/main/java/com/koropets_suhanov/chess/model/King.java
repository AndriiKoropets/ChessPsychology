package com.koropets_suhanov.chess.model;

import java.util.Set;
import static java.lang.Math.abs;

/**
 * @author AndriiKoropets
 */
public class King extends Figure {

    private boolean opportunityToCastling = true;
    private static final int KING_WEIGHT = Integer.MAX_VALUE;

    public King(Field field, Color color) {
        super(field, color);
        attackedFields();
    }

    @Override
    public void possibleTurns(){
        Set<Field> enemyInfluence;
        for (Field field : this.getAttackedFields()){
            Figure figure = Board.getFieldToFigure().get(field);
            if (this.getColor() == Color.BLACK){
                enemyInfluence = Board.getFieldsUnderWhiteInfluence();
            }else {
                enemyInfluence = Board.getFieldsUnderBlackInfluence();
            }
            if (!enemyInfluence.contains(field)){
                if (figure != null){
                    if (this.getColor() == figure.getColor()){
                        figure.addAlien(this);
                    }else {
                        figure.addEnemy(this);
                        this.getWhoCouldBeKilled().add(figure);
                    }
                }else {
                    this.getPossibleFieldsToMove().add(field);
                    this.getFieldsUnderMyInfluence().add(field);
                }
            }
        }
    }

    @Override
    public double getValue() {
        return KING_WEIGHT;
    }

    public boolean isOpportunityToCastling() {
        return opportunityToCastling;
    }

    public void looseOpportunityToCastling() {
        this.opportunityToCastling = false;
    }

    public boolean isUnderAttack(){
        Set<Field> enemyInfluence;
        if (this.getColor() == Color.WHITE){
            enemyInfluence = Board.getFieldsUnderBlackInfluence();
        }else {
            enemyInfluence = Board.getFieldsUnderWhiteInfluence();
        }
        return enemyInfluence.contains(this.getField());
    }

    @Override
    protected void attackedFields() {
        for (int  i = 0; i < Board.SIZE; i++){
            for (int j = 0; j < Board.SIZE; j++){
                if ((abs(this.getField().getX() - i) <= 1) && (abs(this.getField().getY() - j) <= 1)) {
                    if (this.getField().getX() == i && this.getField().getY() == j){
                        continue;
                    }
                    Field field = new Field(i, j);
                    this.getAttackedFields().add(field);
                }
            }
        }
    }
}