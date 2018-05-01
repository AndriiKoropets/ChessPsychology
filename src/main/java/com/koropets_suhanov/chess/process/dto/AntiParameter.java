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
public class AntiParameter implements Serializable {
  private int fifthParam;
  private int sixthParam;
  private int seventhParam;
  private int eighthParam;
}
