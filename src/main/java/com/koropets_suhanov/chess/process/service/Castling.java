package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.process.dto.FigureToField;
import com.koropets_suhanov.chess.process.dto.Turn;

import java.util.ArrayList;
import java.util.List;

import static com.koropets_suhanov.chess.process.constants.Constants.LONG_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.constants.Constants.SHORT_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.service.Process.currentColor;

public class Castling {

  private static final Field f1 = new Field(7, 5);
  private static final Field g1 = new Field(7, 6);
  private static final Field b1 = new Field(7, 1);
  private static final Field c1 = new Field(7, 2);
  private static final Field d1 = new Field(7, 3);
  private static final Field f8 = new Field(0, 5);
  private static final Field g8 = new Field(0, 6);
  private static final Field b8 = new Field(0, 1);
  private static final Field c8 = new Field(0, 2);
  private static final Field d8 = new Field(0, 3);
  public static final Field a1 = new Field(7, 0);
  public static final Field h1 = new Field(7, 7);
  public static final Field e1 = new Field(7, 4);
  public static final Field a8 = new Field(0, 0);
  public static final Field h8 = new Field(0, 7);
  public static final Field e8 = new Field(0, 4);

  public List<Turn> getCastlings() {
    List<Turn> castlings = new ArrayList<>();
    List<Figure> rocks = Board.getTypeOfFigures(Rock.class, currentColor);
    King king = (King) Board.getTypeOfFigures(King.class, currentColor).get(0);
    for (Figure figure : rocks) {
      Rock rock = (Rock) figure;
      if (isShortCastlingPossible(king, rock)) {
        castlings.add(shortCastling(rock, king));
      }
      if (isLongCastlingPossible(king, rock)) {
        castlings.add(longCastling(rock, king));
      }
    }
    return castlings;
  }

  private boolean isShortCastlingPossible(King king, Rock rock) {
    return ((currentColor == Color.BLACK && rock.getField().equals(h8) && king.getField().equals(e8))
            || (currentColor == Color.WHITE && rock.getField().equals(h1)) && king.getField().equals(e1))
            && rock.isOpportunityToCastling()
            && king.isOpportunityToCastling()
            && shortCastlingFieldsAreNotUnderInfluenceAndNotOccupied();
  }

  private boolean shortCastlingFieldsAreNotUnderInfluenceAndNotOccupied() {
    return (currentColor == Color.BLACK)
            ? !Board.getFieldsUnderWhiteInfluence().contains(f8)
            && !Board.getFieldsUnderWhiteInfluence().contains(g8)
            && Board.getFieldToFigure().get(f8) == null
            && Board.getFieldToFigure().get(g8) == null
            : !Board.getFieldsUnderBlackInfluence().contains(f1)
            && !Board.getFieldsUnderBlackInfluence().contains(g1)
            && Board.getFieldToFigure().get(f1) == null
            && Board.getFieldToFigure().get(g1) == null;
  }

  private Turn shortCastling(Rock rock, King king) {
    List<FigureToField> castlingDestinations = new ArrayList<>();
    if (currentColor == Color.BLACK) {
      castlingDestinations.add(FigureToField.builder().figure(king).field(g8).build());
      castlingDestinations.add(FigureToField.builder().figure(rock).field(f8).build());
    } else {
      castlingDestinations.add(FigureToField.builder().figure(king).field(g1).build());
      castlingDestinations.add(FigureToField.builder().figure(rock).field(f1).build());
    }
    return Turn.builder().figureToDestinationField(castlingDestinations).writtenStyle(SHORT_CASTLING_ZEROS).build();
  }

  private boolean isLongCastlingPossible(King king, Rock rock) {
    return ((currentColor == Color.BLACK && rock.getField().equals(a8) && king.getField().equals(e8))
            || (currentColor == Color.WHITE && rock.getField().equals(a1)) && king.getField().equals(e1))
            && rock.isOpportunityToCastling()
            && king.isOpportunityToCastling()
            && longCastlingFieldsAreNotUnderInfluenceAndNotOccupied();
  }

  private boolean longCastlingFieldsAreNotUnderInfluenceAndNotOccupied() {
    return (currentColor == Color.BLACK)
            ? !Board.getFieldsUnderWhiteInfluence().contains(b8)
            && !Board.getFieldsUnderWhiteInfluence().contains(c8)
            && !Board.getFieldsUnderWhiteInfluence().contains(d8)
            && Board.getFieldToFigure().get(b8) == null
            && Board.getFieldToFigure().get(c8) == null
            && Board.getFieldToFigure().get(d8) == null
            : !Board.getFieldsUnderBlackInfluence().contains(b1)
            && !Board.getFieldsUnderBlackInfluence().contains(c1)
            && !Board.getFieldsUnderBlackInfluence().contains(d1)
            && Board.getFieldToFigure().get(b1) == null
            && Board.getFieldToFigure().get(c1) == null
            && Board.getFieldToFigure().get(d1) == null;
  }

  private Turn longCastling(Rock rock, King king) {
    List<FigureToField> castlingDestination = new ArrayList<>();
    if (currentColor == Color.BLACK) {
      castlingDestination.add(FigureToField.builder().figure(king).field(c8).build());
      castlingDestination.add(FigureToField.builder().figure(rock).field(d8).build());
    } else {
      castlingDestination.add(FigureToField.builder().figure(king).field(c1).build());
      castlingDestination.add(FigureToField.builder().figure(rock).field(d1).build());
    }
    return Turn.builder().figureToDestinationField(castlingDestination).writtenStyle(LONG_CASTLING_ZEROS).build();
  }
}
