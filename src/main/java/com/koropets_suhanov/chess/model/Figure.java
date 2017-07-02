package com.koropets_suhanov.chess.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author AndriiKoroepts
 */
public abstract class Figure implements Observer {

    private Field field;
    private Color color;
    private Set<Figure> enemiesAttackMe = new LinkedHashSet<Figure>();
    private Set<Figure> aliensProtectMe = new LinkedHashSet<Figure>();
    private Set<Figure> whoCouldBeKilled = new LinkedHashSet<Figure>();
    private Set<Field> attackedFields = new LinkedHashSet<Field>();
    private Set<Field> fieldsUnderMyInfluence = new LinkedHashSet<Field>();
    private Set<Field> possibleFieldsToMove = new LinkedHashSet<Field>();
    private Set<Field> preyField = new HashSet<>();
    protected abstract void attackedFields();
    public abstract void possibleTurns();
    public abstract double getValue();
    public abstract int getPoint();

    public Figure(){}

    public Figure(Field field, Color color) {
        this.field = field;
        this.color = color;
    }

//    public boolean isAttack(int coordinate_X, int coordinate_Y){
//        boolean flag = false;
//        Point givenPointForCheck = new Point(coordinate_X, coordinate_Y);
//        if (attackedFields.contains(givenPointForCheck)){
//            flag = true;
//        }
//        return flag;
//    }

    public void update(Field field){
        this.field = field;
        this.enemiesAttackMe.clear();
        this.aliensProtectMe.clear();
        this.whoCouldBeKilled.clear();
        this.attackedFields.clear();
        this.possibleFieldsToMove.clear();
        this.fieldsUnderMyInfluence.clear();
        if (this.getClass() == King.class){
            ((King) this).looseOpportunityToCastling();
        }else {
            if (this.getClass() == Rock.class){
                ((Rock) this).looseOpportunityToCastling();
            }
        }
        attackedFields();
        possibleTurns();
    }

    public void update(){
        this.enemiesAttackMe.clear();
        this.aliensProtectMe.clear();
        this.whoCouldBeKilled.clear();
        this.attackedFields.clear();
        this.possibleFieldsToMove.clear();
        this.fieldsUnderMyInfluence.clear();
        attackedFields();
        possibleTurns();
    }

    public Field getField(){
        return this.field;
    }

    public Color getColor() {
        return color;
    }

    public Set<Figure> getWhoCouldBeKilled() {
        return whoCouldBeKilled;
    }

    public Set<Field> getAttackedFields() {
        return attackedFields;
    }

    public Set<Field> getFieldsUnderMyInfluence() {
        return fieldsUnderMyInfluence;
    }

    public void addEnemy(Figure figure){
        enemiesAttackMe.add(figure);
    }

    public Set<Figure> getEnemiesAttackMe() {
        return enemiesAttackMe;
    }

    public Set<Figure> getAliensProtectMe() {
        return aliensProtectMe;
    }

    public Set<Field> getPossibleFieldsToMove() {
        return possibleFieldsToMove;
    }

    public Set<Field> getPreyField() {
        return preyField;
    }

    public void addAlien(Figure figure){
        aliensProtectMe.add(figure);
    }

//    public void possibleTurns(){
//        for (Field field : attackedFields){
//            if (field.isTaken()){
//                if (this.getColor() == field.getFigureByField().getColor()){
//                    field.getFigureByField().addAlien(this);
//                }else {
//                    field.getFigureByField().addEnemy(this);
//                    this.getWhoCouldBeKilled().add(field.getFigureByField());
//                }
//            }else {
//                possibleFieldsToMove.add(field);
//            }
//        }
//    }

    protected boolean checkingFieldForTaken(Field field){
        if (!field.isTaken()){
            this.getPossibleFieldsToMove().add(field);
        }else {
            Figure tempFigure = Board.getFieldToFigure().get(field);
            if (tempFigure.getColor() == this.getColor()){
                tempFigure.addAlien(this);
                return true;
            }else {
                tempFigure.addEnemy(this);
                this.getWhoCouldBeKilled().add(tempFigure);
                this.getPreyField().add(tempFigure.getField());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o){
        return this.getClass() == o.getClass() && this.field.equals(((Figure)o).getField())
                && this.getColor() == ((Figure)o).getColor();
    }

    @Override
    public int hashCode(){
        return 31*this.getField().getX() + 97*this.getField().getY();
    }

    @Override
    public String toString(){
        if (this.getClass() == Pawn.class) {
            return "" + this.getField().toString();
        }
        if (this.getClass() == Rock.class) {
            return "R" + this.getField().toString();
        }
        if (this.getClass() == Knight.class) {
            return "N" + this.getField().toString();
        }
        if (this.getClass() == Bishop.class) {
            return "B" + this.getField().toString();
        }
        if (this.getClass() == Queen.class) {
            return "Q" + this.getField().toString();
        }
        if (this.getClass() == King.class) {
            return "K" + this.getField().toString();
        }
        return null;
    }
}