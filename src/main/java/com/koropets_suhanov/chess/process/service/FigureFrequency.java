package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.process.dto.FrequentFigure;
import com.koropets_suhanov.chess.process.dto.Frequency;

import static com.koropets_suhanov.chess.process.service.Process.currentColor;
import static com.koropets_suhanov.chess.process.service.Process.currentWrittenStyleTurn;

public class FigureFrequency {

  private final FrequentFigure WHITE_FREQUENCY = new FrequentFigure();
  private final FrequentFigure BLACK_FREQUENCY = new FrequentFigure();

  public Frequency countFrequency() {
    FrequentFigure frequent = (currentColor == Color.WHITE) ? WHITE_FREQUENCY : BLACK_FREQUENCY;
    char figure = currentWrittenStyleTurn.charAt(0);
    switch (figure) {
      case 'R':
        updateRock(frequent);
        break;
      case 'N':
        updateKnight(frequent);
        break;
      case 'B':
        updateBishop(frequent);
        break;
      case 'Q':
        updateQueen(frequent);
        break;
      case 'K':
        updateKing(frequent);
        break;
      case '0':
        updateKing(frequent);
        break;
      case 'O':
        updateKing(frequent);
        break;
      default:
        updatePawn(frequent);
        break;
    }
    return Frequency.builder().white(WHITE_FREQUENCY).black(BLACK_FREQUENCY).build();
  }


  private void updateKing(FrequentFigure frequency) {
    frequency.setKing(frequency.getKing() + 1);
  }

  private void updateQueen(FrequentFigure frequency) {
    frequency.setQueen(frequency.getQueen() + 1);
  }

  private void updateBishop(FrequentFigure frequency) {
    frequency.setBishop(frequency.getBishop() + 1);
  }

  private void updateKnight(FrequentFigure frequency) {
    frequency.setKnight(frequency.getKnight() + 1);
  }

  private void updateRock(FrequentFigure frequency) {
    frequency.setRock(frequency.getRock() + 1);
  }

  private void updatePawn(FrequentFigure frequency) {
    frequency.setPawn(frequency.getPawn() + 1);
  }
}
