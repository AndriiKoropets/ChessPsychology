package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Knight;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.King;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class FillBoard {

  public List<Figure> createWhiteFigures(){
    List<Figure> whiteFigures = new ArrayList<>();
    whiteFigures.add(new Pawn(new Field(6, 0), Color.WHITE));
    whiteFigures.add(new Pawn(new Field(6, 1), Color.WHITE));
    whiteFigures.add(new Pawn(new Field(6, 2), Color.WHITE));
    whiteFigures.add(new Pawn(new Field(6, 3), Color.WHITE));
    whiteFigures.add(new Pawn(new Field(6, 4), Color.WHITE));
    whiteFigures.add(new Pawn(new Field(6, 5), Color.WHITE));
    whiteFigures.add(new Pawn(new Field(6, 6), Color.WHITE));
    whiteFigures.add(new Pawn(new Field(6, 7), Color.WHITE));
    whiteFigures.add(new Rock(new Field(7, 0), Color.WHITE));
    whiteFigures.add(new Rock(new Field(7, 7), Color.WHITE));
    whiteFigures.add(new Knight(new Field(7, 1), Color.WHITE));
    whiteFigures.add(new Knight(new Field(7, 6), Color.WHITE));
    whiteFigures.add(new Bishop(new Field(7, 2), Color.WHITE));
    whiteFigures.add(new Bishop(new Field(7, 5), Color.WHITE));
    whiteFigures.add(new Queen(new Field(7, 3), Color.WHITE));
    whiteFigures.add(new King(new Field(7, 4), Color.WHITE));
    return whiteFigures;
  }

  public List<Figure> createBlackFigures(){
    List<Figure> blackFigures = new ArrayList<>();
    blackFigures.add(new Pawn(new Field(1, 0), Color.BLACK));
    blackFigures.add(new Pawn(new Field(1, 1), Color.BLACK));
    blackFigures.add(new Pawn(new Field(1, 2), Color.BLACK));
    blackFigures.add(new Pawn(new Field(1, 3), Color.BLACK));
    blackFigures.add(new Pawn(new Field(1, 4), Color.BLACK));
    blackFigures.add(new Pawn(new Field(1, 5), Color.BLACK));
    blackFigures.add(new Pawn(new Field(1, 6), Color.BLACK));
    blackFigures.add(new Pawn(new Field(1, 7), Color.BLACK));
    blackFigures.add(new Rock(new Field(0, 0), Color.BLACK));
    blackFigures.add(new Rock(new Field(0, 7), Color.BLACK));
    blackFigures.add(new Knight(new Field(0, 1), Color.BLACK));
    blackFigures.add(new Knight(new Field(0, 6), Color.BLACK));
    blackFigures.add(new Bishop(new Field(0, 2), Color.BLACK));
    blackFigures.add(new Bishop(new Field(0, 5), Color.BLACK));
    blackFigures.add(new Queen(new Field(0, 3), Color.BLACK));
    blackFigures.add(new King(new Field(0, 4), Color.BLACK));
    return blackFigures;
  }
}
