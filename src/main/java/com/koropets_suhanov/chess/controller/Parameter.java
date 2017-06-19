package com.koropets_suhanov.chess.controller;

import com.koropets_suhanov.chess.model.*;
import scala.Tuple8;

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
    private Color color;
    private final   int NUMBER_OF_THREADS = 10;

    public Parameter(Color color) {
        this.color = color;
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
}