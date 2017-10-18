package com.koropets_suhanov.chess.process.pojo;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import scala.Tuple2;

import java.util.List;

/**
 * @author AndriiKoropets
 */
public class Parameter {

    private int firstAttackEnemy;
    private int secondBeUnderAttack;
    private int thirdWithdrawAttackOnEnemy;
    private int fourthWithdrawAttackOnMe;
    private Tuple2<Integer, List<Tuple2<Figure, Field>>> fifthDontTakeAChanceToAttack;
    private Tuple2<Integer, List<Tuple2<Figure, Field>>> sixthDontTakeAChanceToBeUnderAttack;
    private Tuple2<Integer, List<Tuple2<Figure, Field>>> seventhDontTakeAChanceToWithdrawAttackOnEnemy;
    private Tuple2<Integer, List<Tuple2<Figure, Field>>> eighthDontTakeAChanceToWithdrawAttackOnMe;


    private Parameter(int firstAttackEnemy, int secondBeUnderAttack, int thirdWithdrawAttackOnEnemy,
                     int fourthWithdrawAttackOnMe, Tuple2<Integer, List<Tuple2<Figure, Field>>> fifthDontTakeAChanceToAttack,
                      Tuple2<Integer, List<Tuple2<Figure, Field>>> sixthDontTakeAChanceToBeUnderAttack,
                      Tuple2<Integer, List<Tuple2<Figure, Field>>> seventhDontTakeAChanceToWithdrawAttackOnEnemy,
                      Tuple2<Integer, List<Tuple2<Figure, Field>>> eighthDontTakeAChanceToWithdrawAttackOnMe) {
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

    public int getSecondBeUnderAttack() {
        return secondBeUnderAttack;
    }

    public int getThirdWithdrawAttackOnEnemy() {
        return thirdWithdrawAttackOnEnemy;
    }

    public int getFourthWithdrawAttackOnMe() {
        return fourthWithdrawAttackOnMe;
    }

    public Tuple2<Integer, List<Tuple2<Figure, Field>>> getFifthDontTakeAChanceToAttack() {
        return fifthDontTakeAChanceToAttack;
    }

    public Tuple2<Integer, List<Tuple2<Figure, Field>>> getSixthDontTakeAChanceToBeUnderAttack() {
        return sixthDontTakeAChanceToBeUnderAttack;
    }

    public Tuple2<Integer, List<Tuple2<Figure, Field>>> getSeventhDontTakeAChanceToWithdrawAttackOnEnemy() {
        return seventhDontTakeAChanceToWithdrawAttackOnEnemy;
    }

    public Tuple2<Integer, List<Tuple2<Figure, Field>>> getEighthDontTakeAChanceToWithdrawAttackOnMe() {
        return eighthDontTakeAChanceToWithdrawAttackOnMe;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "firstAttackEnemy=" + firstAttackEnemy +
                ", secondBeUnderAttack=" + secondBeUnderAttack +
                ", thirdWithdrawAttackOnEnemy=" + thirdWithdrawAttackOnEnemy +
                ", fourthWithdrawAttackOnMe=" + fourthWithdrawAttackOnMe +
                ", fifthDontTakeAChanceToAttack=" + fifthDontTakeAChanceToAttack +
                ", sixthDontTakeAChanceToBeUnderAttack=" + sixthDontTakeAChanceToBeUnderAttack +
                ", seventhDontTakeAChanceToWithdrawAttackOnEnemy=" + seventhDontTakeAChanceToWithdrawAttackOnEnemy +
                ", eighthDontTakeAChanceToWithdrawAttackOnMe=" + eighthDontTakeAChanceToWithdrawAttackOnMe +
                '}';
    }

    public static final class Builder{

        private int first;
        private int second;
        private int third;
        private int fourth;
        private Tuple2<Integer, List<Tuple2<Figure, Field>>> fifth;
        private Tuple2<Integer, List<Tuple2<Figure, Field>>> sixth;
        private Tuple2<Integer, List<Tuple2<Figure, Field>>> seventh;
        private Tuple2<Integer, List<Tuple2<Figure, Field>>> eighth;

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

        public Builder fifth(final Tuple2<Integer, List<Tuple2<Figure, Field>>> fifth){
            this.fifth = fifth;
            return this;
        }

        public Builder sixth(final Tuple2<Integer, List<Tuple2<Figure, Field>>> sixth){
            this.sixth = sixth;
            return this;
        }

        public Builder seventh(final Tuple2<Integer, List<Tuple2<Figure, Field>>> seventh){
            this.seventh = seventh;
            return this;
        }

        public Builder eighth(final Tuple2<Integer, List<Tuple2<Figure, Field>>> eighth){
            this.eighth = eighth;
            return this;
        }

        public Parameter build(){
            return new Parameter(first, second, third, fourth, fifth, sixth, seventh, eighth);
        }
    }
}