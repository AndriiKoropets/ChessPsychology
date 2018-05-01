package com.koropets_suhanov.chess.process.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TurnAntiParameter implements Serializable {
  private AntiParameter antiParameter;
  private Turn turn;
}
