package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.process.service.Process;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;

@Slf4j
public class ChessBootstrap {

  private static Process process = new Process();

  public static void main(String[] args) throws FileNotFoundException {
    log.info("Process is starting");
    process.runProcess();
  }
}