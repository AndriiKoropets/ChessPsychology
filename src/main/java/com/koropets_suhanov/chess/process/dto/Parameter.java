package com.koropets_suhanov.chess.process.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.Builder;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Parameter implements Serializable {
  private int firstAttackEnemy;
  private int secondBeUnderAttack;
  private int thirdWithdrawAttackOnEnemy;
  private int fourthWithdrawAttackOnMe;
  private WeightAndDestinations fifthDontTakeAChanceToAttack;
  private WeightAndDestinations sixthDontTakeAChanceToBeUnderAttack;
  private WeightAndDestinations seventhDontTakeAChanceToWithdrawAttackOnEnemy;
  private WeightAndDestinations eighthDontTakeAChanceToWithdrawAttackOnMe;
}