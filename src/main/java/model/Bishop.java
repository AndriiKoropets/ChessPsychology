package model;

import static java.lang.Math.abs;

/**
 * @author AndriiKoropets
 */
public class Bishop extends Figure {

    private final static int BISHOP_WEIGHT = 3;
//    private final static Set<Integer> set = new LinkedHashSet<Integer>(8);

//    static {
//        set.add(1);
//        set.add(2);
//        set.add(3);
//        set.add(4);
//        set.add(5);
//        set.add(6);
//        set.add(7);
//    }

    public Bishop(Field field, Color color) {
        super(field, color);
        attackedFields();
    }

    @Override
    protected void attackedFields() {
        for (int i = 0; i < Board.SIZE; i++){
            for (int j = 0; j < Board.SIZE; j++){
                if (abs(this.getField().getX() - i) == abs(this.getField().getY() - j) && abs(this.getField().getY() - j) != 0){
                    this.getAttackedFields().add(new Field(i, j));
                }
            }
        }
    }

    @Override
    public void possibleTurns() {
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
}