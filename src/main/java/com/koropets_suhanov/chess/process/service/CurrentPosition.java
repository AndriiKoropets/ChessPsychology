package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.process.dto.FigureToField;
import com.koropets_suhanov.chess.process.dto.Turn;

import java.util.*;
import java.util.stream.Collectors;

import static com.koropets_suhanov.chess.process.constants.Constants.LINE_A;
import static com.koropets_suhanov.chess.process.constants.Constants.LINE_H;
import static com.koropets_suhanov.chess.process.service.Process.currentColor;

public class CurrentPosition {

  private Set<Turn> allPossibleTurns = new LinkedHashSet<>();

  private King king;
  private List<Figure> kingsAllies;
  private Castling castlingService = new Castling();
  private EnPassantAndTransformation enPassantAndTransformation = new EnPassantAndTransformation();
  private Covering covering = new Covering();

  public Set<Turn> getAllPossibleTurns() {
    defineAllPossibleTurns();
    return allPossibleTurns.stream().filter(Objects::nonNull).collect(Collectors.toSet());
  }

  private void defineAllPossibleTurns() {
    allPossibleTurns.clear();
    king = Board.getKingByColor(currentColor);

    List<Figure> allies = Board.getFiguresByColor(currentColor).stream()
        .filter(a -> a.getClass() != King.class)
        .collect(Collectors.toList());
    kingsAllies = new ArrayList<>();
    kingsAllies.addAll(allies);

    define();
  }

  private void define() {
    if (oneEnemyAttacksKing()) {
      turnsWhenOneEnemyAttacksKing();
    } else if (manyEnemiesAttackKing()) {
      definePeacefulTurns(king);
    } else {
      noOneAttacksKing();
    }
  }

  private boolean manyEnemiesAttackKing() {
    return king.isUnderAttack() && king.getEnemiesAttackMe().size() > 1;
  }

  private boolean oneEnemyAttacksKing() {
    return king.isUnderAttack() && king.getEnemiesAttackMe().size() == 1;
  }

  private void turnsWhenOneEnemyAttacksKing() {
    kingEscapesByEatingEnemies();
    eatingTurns();
    definePeacefulTurns(king);
    coveringTurns();
  }

  private void kingEscapesByEatingEnemies() {
    List<FigureToField> kingToDestinationField = new ArrayList<>();
    for (Figure enemy : king.getWhoCouldBeEaten()) {
      if (enemy.getAlliesProtectMe().size() == 0) {
        kingToDestinationField.add(FigureToField.builder().figure(king).field(enemy.getField()).build());
        allPossibleTurns.add(Turn.builder().figureToDestinationField(kingToDestinationField).eating(true).targetedFigure(enemy).build());
      }
    }
  }

  private void eatingTurns() {
    Figure enemy = king.getEnemiesAttackMe().iterator().next();
    for (Figure ally : kingsAllies) {
      if (isEnemyAndAllyPawns(enemy, ally)) {
        pawnAttacksAndPawnProtectsKing((Pawn) enemy, (Pawn) ally);
      } else if (isEnemyOnTheLastLine(enemy) && ally.getClass() == Pawn.class) {
        Pawn pawnAlly = (Pawn) ally;
        if (pawnReachesLastLineCanSaveKing(pawnAlly, enemy)) {
          allPossibleTurns.addAll(enPassantAndTransformation.setTransformationFields(pawnAlly, enemy));

        }
      } else if (canKingProtectItself(ally, enemy)) {
        List<FigureToField> alienToTargetField = new ArrayList<>();
        alienToTargetField.add(FigureToField.builder().figure(ally).field(enemy.getField()).build());

        allPossibleTurns.add(Turn.builder().figureToDestinationField(alienToTargetField).eating(true).targetedFigure(enemy).build());
      }
    }
  }

  private void coveringTurns() {
    Figure figureAttacksKing = king.getEnemiesAttackMe().iterator().next();
    Set<Turn> alienCovers = new HashSet<>();
    if (figureAttacksKing instanceof Rock) {
      alienCovers = covering.coveringIfRockAttacks(king, (Rock) figureAttacksKing);
    }
    if (figureAttacksKing instanceof Bishop) {
      alienCovers = covering.coveringIfBishopAttacks(king, (Bishop) figureAttacksKing);
    }
    if (figureAttacksKing instanceof Queen) {
      alienCovers = covering.coveringIfQueenAttacks(king, (Queen) figureAttacksKing);
    }

    if (alienCovers != null) {
      allPossibleTurns.addAll(alienCovers);
    }
  }

  private boolean isEnemyAndAllyPawns(Figure enemy, Figure ally) {
    return enemy.getClass() == Pawn.class && ally.getClass() == Pawn.class;
  }

  private boolean isEnemyOnTheLastLine(Figure enemy) {
    return (currentColor == Color.WHITE && enemy.getField().getX() == LINE_H)
            || (currentColor == Color.BLACK && enemy.getField().getX() == LINE_A);
  }

  private boolean canKingProtectItself(Figure ally, Figure enemy) {
    return ally.getWhoCouldBeEaten().contains(enemy) && enemy.getAlliesProtectMe().size() == 1;
  }

  private void pawnAttacksAndPawnProtectsKing(Pawn enemy, Pawn ally) {
    if (canEnPassantSaveKing(ally, enemy)) {
      List<FigureToField> alienToTargetField = new ArrayList<>();
      alienToTargetField.add(FigureToField.builder().figure(ally).field(ally.getEnPassantField()).build());
      allPossibleTurns.add(Turn.builder().figureToDestinationField(alienToTargetField).eating(true).enPassant(true).targetedFigure(enemy).build());
    }
  }

  private boolean pawnReachesLastLineCanSaveKing(Pawn pawnAlly, Figure enemy) {
    return pawnAlly.isOnThePenultimateLine() && pawnAlly.getWhoCouldBeEaten().contains(enemy);
  }

  private boolean canEnPassantSaveKing(Pawn pawnAlly, Figure pawnEnemy) {
    return pawnAlly.isEnPassant() && pawnAlly.getEnPassantEnemy().equals(pawnEnemy);
  }

  private void noOneAttacksKing() {
    for (Figure ally : kingsAllies) {
      if (isEnPassantCase(ally)) {
        allPossibleTurns.addAll(enPassantAndTransformation.turnsInCaseEnPassant((Pawn) ally));

      } else if (isTransformationCase(ally)) {
        allPossibleTurns.addAll(enPassantAndTransformation.turnsInCaseTransformation(ally));

      } else {
        definePeacefulTurns(ally);
        for (Figure attackedFigure : ally.getWhoCouldBeEaten()) {
          List<FigureToField> figureField = new ArrayList<>();
          figureField.add(FigureToField.builder().figure(ally).field(attackedFigure.getField()).build());

          allPossibleTurns.add(Turn.builder().figureToDestinationField(figureField).eating(true).targetedFigure(attackedFigure).build());
        }
      }
    }
    allPossibleTurns.addAll(castlingService.getCastlings());
  }

  private boolean isEnPassantCase(Figure ally) {
    return ally.getClass() == Pawn.class && ((Pawn) ally).isEnPassant();
  }

  private boolean isTransformationCase(Figure ally) {
    return ally.getClass() == Pawn.class && ((Pawn) ally).isOnThePenultimateLine();
  }

  private void definePeacefulTurns(Figure figure) {
    for (Field field : figure.getPossibleFieldsToMove()) {
      List<FigureToField> figureToField = new ArrayList<>();
      figureToField.add(FigureToField.builder().figure(figure).field(field).build());
      allPossibleTurns.add(Turn.builder().figureToDestinationField(figureToField).build());
    }
  }
}