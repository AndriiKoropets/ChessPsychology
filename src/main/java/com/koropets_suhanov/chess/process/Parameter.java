package com.koropets_suhanov.chess.process;

/**
 * @author AndriiKoropets
 */
public class Parameter {

    private int firstAttackEnemy;
    private int secondBeUnderAttack;
    private int thirdWithdrawAttackOnEnemy;
    private int fourthWithdrawAttackOnMe;
    private int fifthDontTakeAChanceToAttack;
    private int sixthDontTakeAChanceToBeUnderAttack;
    private int seventhDontTakeAChanceToWithdrawAttackOnEnemy;
    private int eighthDontTakeAChanceToWithdrawAttackOnMe;


    private Parameter(int firstAttackEnemy, int secondBeUnderAttack, int thirdWithdrawAttackOnEnemy,
                     int fourthWithdrawAttackOnMe, int fifthDontTakeAChanceToAttack, int sixthDontTakeAChanceToBeUnderAttack,
                     int seventhDontTakeAChanceToWithdrawAttackOnEnemy, int eighthDontTakeAChanceToWithdrawAttackOnMe) {
        this.firstAttackEnemy = firstAttackEnemy;
        this.secondBeUnderAttack = secondBeUnderAttack;
        this.thirdWithdrawAttackOnEnemy = thirdWithdrawAttackOnEnemy;
        this.fourthWithdrawAttackOnMe = fourthWithdrawAttackOnMe;
        this.fifthDontTakeAChanceToAttack = fifthDontTakeAChanceToAttack;
        this.sixthDontTakeAChanceToBeUnderAttack = sixthDontTakeAChanceToBeUnderAttack;
        this.seventhDontTakeAChanceToWithdrawAttackOnEnemy = seventhDontTakeAChanceToWithdrawAttackOnEnemy;
        this.eighthDontTakeAChanceToWithdrawAttackOnMe = eighthDontTakeAChanceToWithdrawAttackOnMe;
    }

    public   int getFirstAttackEnemy() {
        return firstAttackEnemy;
    }

    public void setFirstAttackEnemy(int firstAttackEnemy) {
        this.firstAttackEnemy = firstAttackEnemy;
    }

    public int getSecondBeUnderAttack() {
        return secondBeUnderAttack;
    }

    public void setSecondBeUnderAttack(int secondBeUnderAttack) {
        this.secondBeUnderAttack = secondBeUnderAttack;
    }

    public int getThirdWithdrawAttackOnEnemy() {
        return thirdWithdrawAttackOnEnemy;
    }

    public void setThirdWithdrawAttackOnEnemy(int thirdWithdrawAttackOnEnemy) {
        this.thirdWithdrawAttackOnEnemy = thirdWithdrawAttackOnEnemy;
    }

    public int getFourthWithdrawAttackOnMe() {
        return fourthWithdrawAttackOnMe;
    }

    public void setFourthWithdrawAttackOnMe(int fourthWithdrawAttackOnMe) {
        this.fourthWithdrawAttackOnMe = fourthWithdrawAttackOnMe;
    }

    public int getFifthDontTakeAChanceToAttack() {
        return fifthDontTakeAChanceToAttack;
    }

    public void setFifthDontTakeAChanceToAttack(int fifthDontTakeAChanceToAttack) {
        this.fifthDontTakeAChanceToAttack = fifthDontTakeAChanceToAttack;
    }

    public int getSixthDontTakeAChanceToBeUnderAttack() {
        return sixthDontTakeAChanceToBeUnderAttack;
    }

    public void setSixthDontTakeAChanceToBeUnderAttack(int sixthDontTakeAChanceToBeUnderAttack) {
        this.sixthDontTakeAChanceToBeUnderAttack = sixthDontTakeAChanceToBeUnderAttack;
    }

    public int getSeventhDontTakeAChanceToWithdrawAttackOnEnemy() {
        return seventhDontTakeAChanceToWithdrawAttackOnEnemy;
    }

    public void setSeventhDontTakeAChanceToWithdrawAttackOnEnemy(int seventhDontTakeAChanceToWithdrawAttackOnEnemy) {
        this.seventhDontTakeAChanceToWithdrawAttackOnEnemy = seventhDontTakeAChanceToWithdrawAttackOnEnemy;
    }

    public int getEighthDontTakeAChanceToWithdrawAttackOnMe() {
        return eighthDontTakeAChanceToWithdrawAttackOnMe;
    }

    public void setEighthDontTakeAChanceToWithdrawAttackOnMe(int eighthDontTakeAChanceToWithdrawAttackOnMe) {
        this.eighthDontTakeAChanceToWithdrawAttackOnMe = eighthDontTakeAChanceToWithdrawAttackOnMe;
    }

    public static final class Builder{

        private int first;
        private int second;
        private int third;
        private int fourth;
        private int fifth;
        private int sixth;
        private int seventh;
        private int eighth;

        public Builder first(final int first){
            this.first = first;
            return this;
        }

        public Builder second(final int second){
            this.second = second;
            return this;
        }

        public Builder third(final int third){
            this.third = third;
            return this;
        }

        public Builder fourth(final int fourth){
            this.fourth = fourth;
            return this;
        }

        public Builder fifth(final int fifth){
            this.fifth = fifth;
            return this;
        }

        public Builder sixth(final int sixth){
            this.sixth = sixth;
            return this;
        }

        public Builder seventh(final int seventh){
            this.seventh = seventh;
            return this;
        }

        public Builder eighth(final int eighth){
            this.eighth = eighth;
            return this;
        }

        public Parameter build(){
            return new Parameter(first, second, third, fourth, fifth, sixth, seventh, eighth);
        }
    }
}