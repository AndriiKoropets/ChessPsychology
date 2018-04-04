package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.process.service.Process;

public class ChessBootstrap {

  private static Process process = new Process();

  public static void main(String[] args) {
    process.runProcess();
  }
}