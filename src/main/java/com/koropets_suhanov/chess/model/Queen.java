package com.koropets_suhanov.chess.model;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;

/**
 * @author AndriiKoropets
 */
public class Queen extends Figure {

    private final static int QUEEN_WEIGHT = 9;
    private final static int POINT = 4;

    public Queen(Field field, Color color) {
        super(field, color);
        attackedFields();
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
        for (int i = this.getField().getX() + 1; i < Board.SIZE; i++){
            boolean flag = false;
            for (int j = this.getField().getY() + 1; j < Board.SIZE; j++){
                if (i < Board.SIZE && j < Board.SIZE &&  abs(this.getField().getX() - i) == abs(this.getField().getY() - j)){
                    Field field = new Field(i, j);
                    if (checkingFieldForTaken(field)){
                        flag = true;
                        break;
                    }else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag){
                break;
            }
        }
        for (int i = this.getField().getX() + 1; i < Board.SIZE; i++){
            boolean flag = false;
            for (int j = this.getField().getY() - 1; j >= 0; j--){
                if (i < Board.SIZE && j >= 0 && abs(this.getField().getX() - i) == abs(this.getField().getY() - j)){
                    Field field = new Field(i,j);
                    if (checkingFieldForTaken(field)){
                        flag = true;
                        break;
                    }else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag){
                break;
            }
        }
        for (int i = this.getField().getX() - 1; i >= 0; i--){
            boolean flag = false;
            for (int j = this.getField().getY() + 1; j < Board.SIZE; j++){
                if (i >= 0 && abs(this.getField().getX() - i) == abs(this.getField().getY() - j)){
                    Field field = new Field(i,j);
                    if (checkingFieldForTaken(field)){
                        flag = true;
                        break;
                    }else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag){
                break;
            }
        }
        for (int i = this.getField().getX() - 1; i >= 0; i--){
            boolean flag = false;
            for (int j = this.getField().getY() - 1; j >= 0; j--){
                if (i >= 0 && j >= 0 && abs(this.getField().getX() - i) == abs(this.getField().getY() - j)){
                    Field field = new Field(i,j);
                    if (checkingFieldForTaken(field)){
                        flag = true;
                        break;
                    }else {
                        this.getFieldsUnderMyInfluence().add(field);
                    }
                }
            }
            if (flag){
                break;
            }
        }
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
            if (f.getClass() == Bishop.class || f.getClass() == Rock.class || f.getClass() == Queen.class){
                chosenAllies.add(f);
            }
        });
        return chosenAllies;
    }

    @Override
    protected void attackedFields() {
        for (int i = 0; i < Board.SIZE; i++){
            for (int j = 0; j < Board.SIZE; j++){
                if ((i == this.getField().getX() || j == this.getField().getY()) || (abs(this.getField().getX() - i) == abs(this.getField().getY() - j)) ){
                    if (this.getField().getX() == i && this.getField().getY() == j){
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