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

    public Figure(){}

    public Figure(Field field, Color color) {
        this.field = field;
        this.color = color;
    }

    public void update(Field field){
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

    public Field getField(){
        return this.field;
    }

    public Color getColor() {
        return color;
    }

    public Set<Figure> getWhoCouldBeEaten() {
        return whoCouldBeEaten;
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

    public Set<Figure> getAlliesProtectMe() {
        return alliesProtectMe;
    }

    public Set<Figure> getAlliesIProtect(){
        return alliesIProtect;
    }

    public Set<Field> getPossibleFieldsToMove() {
        return possibleFieldsToMove;
    }

    public Set<Field> getPreyField() {
        return preyField;
    }

    public Set<Figure> getWhoCouldBeEatenPreviousState(){
        return whoCouldBeEatenPreviousState;
    }

    public void addAllyProtectMe(Figure figure){
        alliesProtectMe.add(figure);
    }

    public void addAllyIProtect(Figure figure){
        alliesIProtect.add(figure);
    }

    protected boolean checkingFieldForTaken(Field field){
        if (!field.isTaken()){
            this.getPossibleFieldsToMove().add(field);
        }else {
            Figure tempFigure = Board.getFieldToFigure().get(field);
            if (tempFigure.getColor() == this.getColor()){
                tempFigure.addAllyProtectMe(this);
                this.addAllyIProtect(tempFigure);
                return true;
            }else {
                tempFigure.addEnemy(this);
                this.getWhoCouldBeEaten().add(tempFigure);
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
}