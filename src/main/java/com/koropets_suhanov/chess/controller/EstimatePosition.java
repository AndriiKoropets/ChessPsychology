package com.koropets_suhanov.chess.controller;

import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Observer;

import java.util.HashMap;
import java.util.Map;

import com.koropets_suhanov.chess.model.Turn;
import scala.Tuple2;

/**
 * @author AndriiKoropets
 */
public class EstimatePosition {

    Map<String, Tuple2<Observer, Parameter>> turnToReflection = new HashMap<>();

    public Parameter estimateParameters(Turn turn){
        Parameter parameter = new Parameter(Color.BLACK);
        parameter.setFirstAttackEnemy(estimateFirstParameter(turn));
        parameter.setSecondBeUnderAttack(estimateSecondParameter(turn));
        parameter.setThirdWithdrawAttackOnEnemy(estimateThirdParameter(turn));
        parameter.setFourthWithdrawAttackOnMe(estimateFourthParameter(turn));
        parameter.setFifthDontTakeAChanceToAttack(estimateFifthParameter(turn));
        parameter.setSixthDontTakeAChanceToBeUnderAttack(estimateSixthParameter(turn));
        parameter.setSeventhDontTakeAChanceToWithdrawAttackOnEnemy(estimateSeventhParameter(turn));
        parameter.setEighthDontTakeAChanceToWithdrawAttackOnMe(estimateEightParameter(turn));
        return parameter;
    }

    private int estimateEightParameter(Turn turn) {
        return 0;
    }

    private int estimateSeventhParameter(Turn turn) {
        return 0;
    }

    private int estimateSixthParameter(Turn turn) {
        return 0;
    }

    private int estimateFifthParameter(Turn turn) {
        return 0;
    }

    private int estimateFourthParameter(Turn turn) {
        return 0;
    }

    private int estimateSecondParameter(Turn turn) {
        return 0;
    }

    private int estimateThirdParameter(Turn turn) {
        return 0;
    }

    public static int estimateFirstParameter(Turn turn){
//        Turn turn = Board.getInstance().getPossibleTurnsAndKillings().get(turnsNumber);
        int numberOfFiguresIAttack = turn.getFigure().getWhoCouldBeKilled().size();
        return 0;
    }

}
