package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Knight;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.process.dto.FigureToField;
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.process.utils.ProcessUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import static com.koropets_suhanov.chess.process.constants.Constants.SHORT_CASTLING;
import static com.koropets_suhanov.chess.process.constants.Constants.SHORT_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.constants.Constants.LONG_CASTLING;
import static com.koropets_suhanov.chess.process.constants.Constants.LONG_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.constants.Constants.EATING_SYMBOL;
import static com.koropets_suhanov.chess.process.constants.Constants.PLUS;
import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;
import static com.koropets_suhanov.chess.process.service.Castling.e1;
import static com.koropets_suhanov.chess.process.service.Castling.e8;
import static com.koropets_suhanov.chess.process.service.Castling.h1;
import static com.koropets_suhanov.chess.process.service.Castling.h8;
import static com.koropets_suhanov.chess.process.service.Castling.a1;
import static com.koropets_suhanov.chess.process.service.Castling.a8;
import static com.koropets_suhanov.chess.process.service.Process.currentColor;
import static com.koropets_suhanov.chess.process.service.Process.currentTurnNumber;
import static com.koropets_suhanov.chess.process.service.Process.currentWrittenStyleTurn;

@UtilityClass
@Slf4j
public class ParseWrittenTurn {

  private List<Figure> candidateFiguresPeacefulTurn = new ArrayList<>();
  private List<Figure> eatTurnCandidateFigures = new ArrayList<>();
  private Field field;
  private List<FigureToField> figureToField = new ArrayList<>();
  private Figure figure;
  private boolean eating;
  private boolean transformation;
  private Figure targetedFigureToBeEaten;
  private final Field WHITE_KING_SHORT_CASTLING = new Field(7, 6);
  private final Field WHITE_KING_LONG_CASTLING = new Field(7, 2);
  private final Field BLACK_KING_SHORT_CASTLING = new Field(0, 6);
  private final Field BLACK_KING_LONG_CASTLING = new Field(0, 2);
  private final Field WHITE_ROCK_SHORT_CASTLING = new Field(7, 5);
  private final Field WHITE_ROCK_LONG_CASTLING = new Field(7, 3);
  private final Field BLACK_ROCK_SHORT_CASTLING = new Field(0, 5);
  private final Field BLACK_ROCK_LONG_CASTLING = new Field(0, 3);
  public final Set<Character> ALL_FIGURES_EXCEPT_PAWN = new HashSet<>(Arrays.asList('R', 'N', 'B', 'Q', 'K'));
  public final Set<String> ALL_FIGURES = new HashSet<>(Arrays.asList("R", "N", "B", "Q"));
  @Getter
  public static Figure figureBornFromTransformation;
  public static String figureInWrittenStyleToBorn;

  public Turn getActualTurn() {
    field = parseDestinationField(currentWrittenStyleTurn);
    return defineTurn();
  }

  private Field parseDestinationField(String turn) {
    int x, y;
    if (!turn.equalsIgnoreCase(SHORT_CASTLING_ZEROS) && !turn.equalsIgnoreCase(LONG_CASTLING_ZEROS)) {
      if (!whetherWrittenTurnIsTransformation()) {
        if (turn.contains(PLUS)) {
          x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 2)));
          y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 3));
        } else {
          x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 1)));
          y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 2));
        }
      } else {
        if (turn.contains(PLUS)) {
          x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 3)));
          y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 4));
        } else {
          x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 2)));
          y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 3));
        }
      }
      return new Field(x, y);
    } else {
      log.info("Target field is null. Castling");
      return null;
    }
  }

  private Turn defineTurn() {
    initialize();
    return isCastling() ? setCastlingTurn() : setNonCastlingTurn();
  }

  private void initialize() {
    candidateFiguresPeacefulTurn.clear();
    eatTurnCandidateFigures.clear();
    figureToField.clear();
    figure = null;
    targetedFigureToBeEaten = null;
    eating = isEating();
    figureBornFromTransformation = null;
    figureInWrittenStyleToBorn = writtenFigureToBorn(currentWrittenStyleTurn);
    transformation = whetherWrittenTurnIsTransformation();
  }

  private String writtenFigureToBorn(String turn) {
    if (turn.contains(PLUS)) {
      return "" + turn.charAt(turn.length() - 2);
    } else {
      return "" + turn.charAt(turn.length() - 1);
    }
  }

  private boolean whetherWrittenTurnIsTransformation() {
    int lengthOfTheWrittenTurn = currentWrittenStyleTurn.length();
    if (currentWrittenStyleTurn.contains(PLUS)) {
      char previousBeforeTheLast = currentWrittenStyleTurn.charAt(lengthOfTheWrittenTurn - 2);
      return ALL_FIGURES.contains(Character.toString(previousBeforeTheLast));
    } else {
      char theLast = currentWrittenStyleTurn.charAt(lengthOfTheWrittenTurn - 1);
      return ALL_FIGURES.contains(Character.toString(theLast));
    }
  }

  private boolean isEating() {
    return currentWrittenStyleTurn.contains(EATING_SYMBOL);
  }

  private boolean isCastling() {
    return SHORT_CASTLING_ZEROS.equals(currentWrittenStyleTurn)
            || LONG_CASTLING_ZEROS.equals(currentWrittenStyleTurn)
            || SHORT_CASTLING.equals(currentWrittenStyleTurn)
            || LONG_CASTLING.equals(currentWrittenStyleTurn);
  }

  private Turn setCastlingTurn() {
    return SHORT_CASTLING_ZEROS.equals(currentWrittenStyleTurn) ? shortCastlingTurn() : longCastlingTurn();
  }

  private Turn shortCastlingTurn() {
    List<FigureToField> figureToField = (currentColor == Color.WHITE)
            ? createFigureToFieldCastling(Board.getFieldToFigure().get(e1), Board.getFieldToFigure().get(h1), WHITE_KING_SHORT_CASTLING, WHITE_ROCK_SHORT_CASTLING)
            : createFigureToFieldCastling(Board.getFieldToFigure().get(e8), Board.getFieldToFigure().get(h8), BLACK_KING_SHORT_CASTLING, BLACK_ROCK_SHORT_CASTLING);
    return Turn.builder()
            .figureToDestinationField(figureToField)
            .writtenStyle(currentWrittenStyleTurn)
            .numberOfTurn(currentTurnNumber)
            .build();
  }

  private Turn longCastlingTurn() {
    List<FigureToField> figureToField = (currentColor == Color.WHITE)
            ? createFigureToFieldCastling(Board.getFieldToFigure().get(e1), Board.getFieldToFigure().get(a1), WHITE_KING_LONG_CASTLING, WHITE_ROCK_LONG_CASTLING)
            : createFigureToFieldCastling(Board.getFieldToFigure().get(e8), Board.getFieldToFigure().get(a8), BLACK_KING_LONG_CASTLING, BLACK_ROCK_LONG_CASTLING);
    return Turn.builder()
            .figureToDestinationField(figureToField)
            .writtenStyle(currentWrittenStyleTurn)
            .numberOfTurn(currentTurnNumber)
            .build();
  }

  private List<FigureToField> createFigureToFieldCastling(Figure king, Figure rock, Field kingFieldDestination, Field rockFieldDestination) {
    List<FigureToField> figureToField = new ArrayList<>();
    FigureToField kingDestination = FigureToField.builder()
            .figure(king)
            .field(kingFieldDestination)
            .build();
    FigureToField rockDestination = FigureToField.builder()
            .figure(rock)
            .field(rockFieldDestination)
            .build();
    figureToField.add(kingDestination);
    figureToField.add(rockDestination);
    return figureToField;
  }

  private Turn setNonCastlingTurn() {
    Turn.TurnBuilder curTurnBuilder = Turn.builder();
    char firstCharacter = currentWrittenStyleTurn.charAt(0);

    if (isNotPawn(firstCharacter)) {
      notPawnDefineFigureToField(firstCharacter);
    } else {
      definePawnsToDestinationFields();
      curTurnBuilder.enPassant(isEnPassantScenario(figureToField));
    }

    return curTurnBuilder
            .figureToDestinationField(figureToField)
            .figureFromTransformation(figureBornFromTransformation)
            .writtenStyle(currentWrittenStyleTurn)
            .eating(eating)
            .transformation(transformation)
            .targetedFigure(targetedFigureToBeEaten)
            .numberOfTurn(currentTurnNumber)
            .build();
  }

  private boolean isNotPawn(Character firstCharacter) {
    return ALL_FIGURES_EXCEPT_PAWN.contains(firstCharacter);
  }

  private void notPawnDefineFigureToField(Character firstCharacter) {
    switch (firstCharacter) {
      case 'R':
        defineNowPawnFiguresToDestinationFields(Rock.class);
        break;
      case 'N':
        defineNowPawnFiguresToDestinationFields(Knight.class);
        break;
      case 'B':
        defineNowPawnFiguresToDestinationFields(Bishop.class);
        break;
      case 'Q':
        defineNowPawnFiguresToDestinationFields(Queen.class);
        break;
      case 'K':
        defineNowPawnFiguresToDestinationFields(King.class);
        break;
      default:
        throw new RuntimeException("Undefined first character in turn " + currentWrittenStyleTurn);
    }
  }

  private void defineNowPawnFiguresToDestinationFields(Class figureType) {
    definePossibleCandidatesFromWrittenTurn(figureType);
    if (!eatTurnCandidateFigures.isEmpty()) {
      electOneFromEatingCandidates(figureType);
    }
    if (!candidateFiguresPeacefulTurn.isEmpty()) {
      electOneFromCandidates();
    }
    if (figure == null) {
      throw new RuntimeException("Could not define figure. Turn must be wrong written. Turn = " + currentWrittenStyleTurn);
    }
    figureToField.add(FigureToField.builder().figure(figure).field(field).build());
  }

  private void definePossibleCandidatesFromWrittenTurn(Class figureType) {
    List<Figure> figures = Board.getTypeOfFigures(figureType, currentColor);
    for (Figure curFigure : figures) {
      if (eating) {
        if (curFigure.getPreyField().contains(field)) {
          eatTurnCandidateFigures.add(curFigure);
          targetedFigureToBeEaten = Board.getFieldToFigure().get(field);
        }
      } else {
        if (curFigure.getPossibleFieldsToMove().contains(field)) {
          candidateFiguresPeacefulTurn.add(curFigure);
        }
      }
    }
  }

  private void electOneFromEatingCandidates(Class figureType) {
    if (eatTurnCandidateFigures.size() == 1) {
      figure = eatTurnCandidateFigures.get(0);
    } else {
      figure = choseFigureWhichAttack(eatTurnCandidateFigures, figureType);
    }
  }

  private void electOneFromCandidates() {
    if (candidateFiguresPeacefulTurn.size() > 1) {
      figure = choseExactFigure(candidateFiguresPeacefulTurn);
    } else {
      figure = candidateFiguresPeacefulTurn.get(0);
    }
  }

  private void definePawnsToDestinationFields() {
    definePossiblePawnsCandidates();
    if (!eatTurnCandidateFigures.isEmpty()) {
      electOneFromEatingCandidates(Pawn.class);
    }
    if (!candidateFiguresPeacefulTurn.isEmpty()) {
      electOneFromCandidates();
    }
    if (figure == null) {
      throw new RuntimeException("Could not define actual pawn. Turn must be wrong written. Turn = " + currentWrittenStyleTurn);
    }
    figureToField.add(FigureToField.builder().figure(figure).field(field).build());
  }

  private void definePossiblePawnsCandidates() {
    List<Figure> figures = Board.getTypeOfFigures(Pawn.class, currentColor);
    for (Figure curFigure : figures) {
      Pawn pawn = (Pawn) curFigure;
      pawn.printAllInformation();
      if (eating) {
        if (transformation) {
          if (pawn.getPreyField().contains(field)) {
            eatTurnCandidateFigures.add(pawn);
            targetedFigureToBeEaten = Board.getFieldToFigure().get(field);
            figureBornFromTransformation = ProcessUtils.createFigure(field, figureInWrittenStyleToBorn, currentColor);
          }
        } else if (pawn.isEnPassant()) {
          if (pawn.getEnPassantField().equals(field)) {
            eatTurnCandidateFigures.add(pawn);
            targetedFigureToBeEaten = pawn.getEnPassantEnemy();
          }
        } else {
          if (pawn.getPreyField().contains(field)) {
            eatTurnCandidateFigures.add(pawn);
            targetedFigureToBeEaten = Board.getFieldToFigure().get(field);
          }
        }
      } else {
        if (transformation) {
          if (pawn.getPossibleFieldsToMove().contains(field)) {
            candidateFiguresPeacefulTurn.add(pawn);
            figureBornFromTransformation = ProcessUtils.createFigure(field, figureInWrittenStyleToBorn, currentColor);
          }
        } else {
          if (pawn.getPossibleFieldsToMove().contains(field)) {
            candidateFiguresPeacefulTurn.add(curFigure);
          }
        }
      }
    }
  }

  private Figure choseFigureWhichAttack(List<Figure> targets, Class clazz) {
    if (clazz == Pawn.class) {
      char verticalPawn = currentWrittenStyleTurn.charAt(0);
      for (Figure currentFigure : targets) {
        if (currentFigure.getField().getY() == Field.getInvertedHorizontal().get(verticalPawn)) {
          return currentFigure;
        }
      }
    } else {
      char secondPosition = currentWrittenStyleTurn.charAt(1);
      int integer = Character.getNumericValue(secondPosition);
      return chose(integer, secondPosition, targets);
    }
    return null;
  }

  private Figure choseExactFigure(List<Figure> targets) {
    char secondPosition = currentWrittenStyleTurn.charAt(1);
    int integer = Character.getNumericValue(secondPosition);
    return chose(integer, secondPosition, targets);
  }

  private Figure chose(int integer, char secondPosition, List<Figure> candidatesForBeingTheOne) {
    for (Figure candidate : candidatesForBeingTheOne) {
      if (integer > SIZE) {
        if (candidate.getField().getY() == Field.getInvertedHorizontal().get(secondPosition)) {
          return candidate;
        }
      } else {
        if (candidate.getField().getX() == Field.getInvertedVertical().get(integer)) {
          return candidate;
        }
      }
    }
    throw new RuntimeException("Could not choose exact figure. Turn must be wrong written. Turn = " + currentWrittenStyleTurn);
  }

  private boolean isEnPassantScenario(List<FigureToField> figureToField) {
    Pawn pawn = (Pawn) figureToField.get(0).getFigure();
    return pawn.isEnPassant();
  }
}