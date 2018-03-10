package com.koropets_suhanov.chess.process.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.Builder;
import scala.Tuple2;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Parameter {

    private int firstAttackEnemy;
    private int secondBeUnderAttack;
    private int thirdWithdrawAttackOnEnemy;
    private int fourthWithdrawAttackOnMe;
    private Tuple2<Integer, List<FigureToField>> fifthDontTakeAChanceToAttack;
    private Tuple2<Integer, List<FigureToField>> sixthDontTakeAChanceToBeUnderAttack;
    private Tuple2<Integer, List<FigureToField>> seventhDontTakeAChanceToWithdrawAttackOnEnemy;
    private Tuple2<Integer, List<FigureToField>> eighthDontTakeAChanceToWithdrawAttackOnMe;
}