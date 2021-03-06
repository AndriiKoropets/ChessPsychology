package com.koropets_suhanov.chess.process.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder
public class FinalResult implements Serializable {
  private int first;
  private int second;
  private int third;
  private int fourth;
  private int fifth;
  private int sixth;
  private int seventh;
  private int eighth;
}
