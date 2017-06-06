package model;

import java.util.Iterator;

import static java.lang.Math.abs;

/**
 * @author AndriiKoropets
 */
public class Knight extends Figure {

    private final static int KNIGHT_WEIGHT = 3;

    public Knight(Field field, Color color) {
        super(field, color);
        attackedFields();
    }

    @Override
    public void possibleTurns(){
        for (Field field : getAttackedFields()){
            if(!checkingFieldForTaken(field)){
                this.getFieldsUnderMyInfluence().add(field);
            }
        }
    }

    @Override
    protected void attackedFields() {
        for (int i = 0; i < Board.SIZE; i++){
            for (int j = 0; j < Board.SIZE; j++){
                if (abs(this.getField().getX() - i) + abs(this.getField().getY() - j) == 3){
                    if (this.getField().getX()== i || this.getField().getY() == j){
                        continue;
                    }
                    Field field = new Field(i, j);
                    this.getAttackedFields().add(field);
                }
            }
        }
    }
}
