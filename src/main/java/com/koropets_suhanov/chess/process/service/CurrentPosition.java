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
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.process.dto.FigureToField;
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.utils.ProcessingUtils;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.koropets_suhanov.chess.process.constants.Constants.LINE_A;
import static com.koropets_suhanov.chess.process.constants.Constants.LINE_H;
import static com.koropets_suhanov.chess.process.service.Process.currentColor;

public class CurrentPosition {

    private Set<Turn> allPossibleTurns = new LinkedHashSet<>();

    private King king;
    private List<Figure> kingsAllies;
    private CastlingService castlingService = new CastlingService();

    public Set<Turn> getAllPossibleTurns() {
        defineAllPossibleTurns();
        return allPossibleTurns.stream().filter(turn -> turn != null).collect(Collectors.toSet());
    }

    private void defineAllPossibleTurns() {
        allPossibleTurns.clear();
        king = Board.getKingByColor(currentColor);

        List<Observer> allyObservers = Board.getFiguresByColor(currentColor).stream().filter(a -> a.getClass() != King.class).collect(Collectors.toList());
        kingsAllies = new ArrayList<>();
        allyObservers.forEach(o -> kingsAllies.add((Figure) o));

        define();
    }

    private void define(){
        if (oneEnemyAttacksKing()) {
            turnsWhenOneEnemyAttacksKing();
        } else if (manyEnemiesAttackKing()) {
            definePeacefulTurns(king);
        } else {
            noOneAttacksKing();
        }
    }

    private boolean manyEnemiesAttackKing(){
        return king.isUnderAttack() && king.getEnemiesAttackMe().size() > 1;
    }

    private boolean oneEnemyAttacksKing(){
        return king.isUnderAttack() && king.getEnemiesAttackMe().size() == 1;
    }

    private void turnsWhenOneEnemyAttacksKing() {
        kingEscapesByEatingEnemies();
        eatingTurns();
        definePeacefulTurns(king);
        coveringTurns();
    }

    private void kingEscapesByEatingEnemies(){
        List<FigureToField> kingToDestinationField = new ArrayList<>();
        for (Figure enemy : king.getWhoCouldBeEaten()) {
            if (enemy.getAlliesProtectMe().size() == 0) {
                kingToDestinationField.add(FigureToField.builder().figure(king).field(enemy.getField()).build());
                allPossibleTurns.add(Turn.builder().figureToDestinationField(kingToDestinationField).eating(true).targetedFigure(enemy).build());
            }
        }
    }

    private void eatingTurns(){
        Figure enemy = king.getEnemiesAttackMe().iterator().next();
        for (Figure ally : kingsAllies) {
            if (isEnemyAndAllyPawns(enemy, ally)) {
                pawnAttacksAndPawnProtectsKing((Pawn) enemy, (Pawn) ally);
            } else if (isEnemyOnTheLastLine(enemy) && ally.getClass() == Pawn.class) {
                Pawn pawnAlly = (Pawn) ally;
                if (pawnReachesLastLineCanSaveKing(pawnAlly, enemy)) {
                    allPossibleTurns.addAll(setTransformationFields(pawnAlly, enemy));

                }
            } else if (canKingProtectItself(ally, enemy)) {
                List<FigureToField> alienToTargetField = new ArrayList<>();
                alienToTargetField.add(FigureToField.builder().figure(ally).field(enemy.getField()).build());

                allPossibleTurns.add(Turn.builder().figureToDestinationField(alienToTargetField).eating(true).targetedFigure(enemy).build());
            }
        }
    }

    private void coveringTurns(){
        Figure figureAttacksKing = king.getEnemiesAttackMe().iterator().next();
        Set<Turn> alienCovers = new HashSet<>();
        if (figureAttacksKing instanceof Rock) {
            alienCovers = coveringIfRockAttacks(king, (Rock) figureAttacksKing);
        }
        if (figureAttacksKing instanceof Bishop) {
            alienCovers = coveringIfBishopAttacks(king, (Bishop) figureAttacksKing);
        }
        if (figureAttacksKing instanceof Queen) {
            alienCovers = coveringIfQueenAttacks(king, (Queen) figureAttacksKing);
        }

        if (alienCovers != null) {
            allPossibleTurns.addAll(alienCovers);
        }
    }

    private boolean isEnemyAndAllyPawns(Figure enemy, Figure ally){
        return enemy.getClass() == Pawn.class && ally.getClass() == Pawn.class;
    }

    private boolean isEnemyOnTheLastLine(Figure enemy){
        return  (currentColor == Color.WHITE && enemy.getField().getX() == LINE_H)
                || (currentColor == Color.BLACK && enemy.getField().getX() == LINE_A);
    }

    private boolean canKingProtectItself(Figure ally, Figure enemy){
        return ally.getWhoCouldBeEaten().contains(enemy) && enemy.getAlliesProtectMe().size() == 1;
    }

    private void pawnAttacksAndPawnProtectsKing(Pawn enemy, Pawn ally){
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
                allPossibleTurns.addAll(turnsInCaseEnPassant((Pawn) ally));

            } else if (isTransformationCase(ally)) {
                allPossibleTurns.addAll(turnsInCaseTransformation(ally));

            } else {
                definePeacefulTurns(ally);
                for (Figure attackedFigure : ally.getWhoCouldBeEaten()) {
                    List<FigureToField> figureFieldTuple = new ArrayList<>();
                    figureFieldTuple.add(FigureToField.builder().figure(ally).field(attackedFigure.getField()).build());

                    allPossibleTurns.add(Turn.builder().figureToDestinationField(figureFieldTuple).eating(true).targetedFigure(attackedFigure).build());
                }
            }
        }
        allPossibleTurns.addAll(castlingService.getCastlings());
    }

    private boolean isEnPassantCase(Figure ally){
        return ally.getClass() == Pawn.class && ((Pawn) ally).isEnPassant();
    }

    private boolean isTransformationCase(Figure ally){
        return ally.getClass() == Pawn.class && ((Pawn) ally).isOnThePenultimateLine();
    }

    private void definePeacefulTurns(Figure figure) {
        for (Field field : figure.getPossibleFieldsToMove()) {
            List<FigureToField> figureToField = new ArrayList<>();
            figureToField.add(FigureToField.builder().figure(figure).field(field).build());
            allPossibleTurns.add(Turn.builder().figureToDestinationField(figureToField).build());
        }
    }

    private Set<Turn> coveringIfRockAttacks(final King king, final Rock enemyRock) {
        Set<Turn> coveringTurns = new HashSet<>();
        Set<Field> fieldsBetween = CurrentPositionUtils.fieldsBetweenRockAndKing(king, enemyRock.getField());
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Turn> coveringIfBishopAttacks(final King king, final Bishop bishop) {
        Set<Field> fieldsBetween = CurrentPositionUtils.fieldsBetweenBishopAndKing(king, bishop.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Turn> coveringIfQueenAttacks(final King king, final Queen queen) {
        Set<Field> fieldsBetween = CurrentPositionUtils.fieldsBetweenQueenAndKing(king, queen.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private void setCoveringTurns(final List<Observer> alienFigures, final Set<Turn> coveringTurns, final Set<Field> fieldsBetween) {
        alienFigures.stream().filter(v -> v.getClass() != King.class).forEach(f -> {
            ((Figure) f).getPossibleFieldsToMove().forEach(k -> {
                if (fieldsBetween.contains(k)) {
                    if (f.getClass() == Pawn.class && ((Pawn) f).isOnThePenultimateLine()) {
                        for (String writtenStyleTurn : ProcessingUtils.FIGURES_IN_WRITTEN_STYLE) {
                            List<FigureToField> covering = new ArrayList<>();
                            covering.add(FigureToField.builder().figure((Figure) f).field(k).build());
                            coveringTurns.add(Turn.builder().figureToDestinationField(covering).figureFromTransformation(ProcessingUtils.createFigure(k, writtenStyleTurn, ((Figure) f).getColor())).transformation(true).build());
                        }
                    }
                    List<FigureToField> covering = new ArrayList<>();
                    covering.add(FigureToField.builder().figure((Figure) f).field(k).build());
                    coveringTurns.add(Turn.builder().figureToDestinationField(covering).build());
                }
            });
        });
    }

    private Set<Turn> turnsInCaseEnPassant(Pawn ally) {
        Set<Turn> possibleTurns = new HashSet<>();
        Figure enPassantEnemy = ally.getEnPassantEnemy();
        for (Field field : ally.getPossibleFieldsToMove()) {
            List<FigureToField> figureToFieldTupleList = new ArrayList<>();
            figureToFieldTupleList.add(FigureToField.builder().figure(ally).field(field).build());
            possibleTurns.add(Turn.builder().figureToDestinationField(figureToFieldTupleList).build());

        }
        for (Figure enemy : ally.getWhoCouldBeEaten()) {
            if (!enemy.equals(enPassantEnemy)) {
                List<FigureToField> figureToFieldTupleList = new ArrayList<>();
                figureToFieldTupleList.add(FigureToField.builder().figure(ally).field(enemy.getField()).build());
                possibleTurns.add(Turn.builder().figureToDestinationField(figureToFieldTupleList).eating(true).targetedFigure(enemy).build());
            }
        }
        if (enPassantEnemy != null) {
            List<FigureToField> figureToFieldList = new ArrayList<>();
            figureToFieldList.add(FigureToField.builder().figure(ally).field(ally.getEnPassantField()).build());
            possibleTurns.add(Turn.builder().figureToDestinationField(figureToFieldList).eating(true).targetedFigure(enPassantEnemy).build());
        }
        return possibleTurns;
    }

    private Set<Turn> turnsInCaseTransformation(Figure ally) {
        Set<Turn> possibleTurns = new HashSet<>();
        for (Field possibleFieldToMove : ally.getPossibleFieldsToMove()) {
            for (String writtenStyle : ProcessingUtils.FIGURES_IN_WRITTEN_STYLE) {
                List<FigureToField> figureToFieldTupleList = new ArrayList<>();
                figureToFieldTupleList.add(FigureToField.builder().figure(ally).field(possibleFieldToMove).build());
                possibleTurns.add(Turn.builder().figureToDestinationField(figureToFieldTupleList).transformation(true).figureFromTransformation(ProcessingUtils.createFigure(possibleFieldToMove, writtenStyle, currentColor)).build());

            }
        }
        for (Figure enemy : ally.getWhoCouldBeEaten()) {
            for (String writtenStyle : ProcessingUtils.FIGURES_IN_WRITTEN_STYLE) {
                List<FigureToField> figureToFieldTupleList = new ArrayList<>();
                figureToFieldTupleList.add(FigureToField.builder().figure(ally).field(enemy.getField()).build());
                possibleTurns.add(Turn.builder().figureToDestinationField(figureToFieldTupleList).transformation(true).figureFromTransformation(ProcessingUtils.createFigure(enemy.getField(), writtenStyle, currentColor)).eating(true).targetedFigure(enemy).build());

            }
        }
        return possibleTurns;
    }

    private Set<Turn> setTransformationFields(Pawn pawn, Figure enemy) {
        Set<Turn> transformationSet = new HashSet<>();
        for (String writtenStyleOfFigure : ProcessingUtils.FIGURES_IN_WRITTEN_STYLE) {
            List<FigureToField> allyToFieldList = new ArrayList<>();
            allyToFieldList.add(FigureToField.builder().figure(pawn).field(enemy.getField()).build());

            Turn newTransformationTurn = Turn.builder().figureToDestinationField(allyToFieldList).transformation(true). eating(true).targetedFigure(enemy).figureFromTransformation(ProcessingUtils.createFigure(enemy.getField(), writtenStyleOfFigure, currentColor)).build();
            transformationSet.add(newTransformationTurn);
        }
        return transformationSet;
    }
}