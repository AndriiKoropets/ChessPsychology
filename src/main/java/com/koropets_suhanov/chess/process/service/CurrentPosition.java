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
import static com.koropets_suhanov.chess.process.constants.Constants.LONG_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.constants.Constants.SHORT_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.service.Process.currentColor;

public class CurrentPosition {

    private Set<Turn> possibleTurnsAndEating = new LinkedHashSet<>();

    public static final Field f1 = new Field(7, 5);
    public static final Field g1 = new Field(7, 6);
    public static final Field b1 = new Field(7, 1);
    public static final Field c1 = new Field(7, 2);
    public static final Field d1 = new Field(7, 3);
    public static final Field f8 = new Field(0, 5);
    public static final Field g8 = new Field(0, 6);
    public static final Field b8 = new Field(0, 1);
    public static final Field c8 = new Field(0, 2);
    public static final Field d8 = new Field(0, 3);
    public static final Field a1 = new Field(7, 0);
    public static final Field h1 = new Field(7, 7);
    public static final Field e1 = new Field(7, 4);
    public static final Field a8 = new Field(0, 0);
    public static final Field h8 = new Field(0, 7);
    public static final Field e8 = new Field(0, 4);

    private King king;
    private List<Figure> kingsAllies;

    public Set<Turn> getAllPossibleTurns() {
        defineAllPossibleTurns();
        return possibleTurnsAndEating.stream().filter(turn -> turn != null).collect(Collectors.toSet());
    }

    private void defineAllPossibleTurns() {
        possibleTurnsAndEating.clear();
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
            peacefulKingTurns();
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
        peacefulKingTurns();
        coveringTurns();
    }

    private void kingEscapesByEatingEnemies(){
        List<FigureToField> kingToDestinationField = new ArrayList<>();
        for (Figure enemy : king.getWhoCouldBeEaten()) {
            if (enemy.getAlliesProtectMe().size() == 0) {
                kingToDestinationField.add(FigureToField.builder().figure(king).field(enemy.getField()).build());
                possibleTurnsAndEating.add(Turn.builder().figureToDestinationField(kingToDestinationField).eating(true).targetedFigure(enemy).build());
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
                    possibleTurnsAndEating.addAll(setTransformationFields(pawnAlly, enemy));

                }
            } else if (canKingProtectItself(ally, enemy)) {
                List<FigureToField> alienToTargetField = new ArrayList<>();
                alienToTargetField.add(FigureToField.builder().figure(ally).field(enemy.getField()).build());

                possibleTurnsAndEating.add(Turn.builder().figureToDestinationField(alienToTargetField).eating(true).targetedFigure(enemy).build());
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
            possibleTurnsAndEating.addAll(alienCovers);
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
            possibleTurnsAndEating.add(Turn.builder().figureToDestinationField(alienToTargetField).eating(true).enPassant(true).targetedFigure(enemyAttacksKing).build());
        }
    }

    private boolean canEnPassantSaveKing(Pawn pawnAlly, Figure pawnEnemy) {
        return pawnAlly.isEnPassant() && pawnAlly.getEnPassantEnemy().equals(pawnEnemy);
    }

    private void noOneAttacksKing() {
        for (Observer observer : allies) {
            Figure ally = (Figure) observer;
            if (ally.getClass() == Pawn.class && ((Pawn) ally).isEnPassant()) {
                possibleTurnsAndEating.addAll(turnsInCaseEnPassant((Pawn) ally));

            } else if (ally.getClass() == Pawn.class && ((Pawn) ally).isOnThePenultimateLine()) {
                possibleTurnsAndEating.addAll(turnsInCaseTransformation(ally));

            } else {
                peacefulKingTurns(ally);
                for (Figure attackedFigure : ally.getWhoCouldBeEaten()) {
                    List<FigureToField> figureFieldTuple = new ArrayList<>();
                    figureFieldTuple.add(FigureToField.builder().figure(ally).field(attackedFigure.getField()).build());

                    possibleTurnsAndEating.add(Turn.builder().figureToDestinationField(figureFieldTuple).eating(true).targetedFigure(attackedFigure).build());
                }
            }
        }
        possibleTurnsAndEating.addAll(castling());
    }

    private void peacefulKingTurns() {
        for (Field field : figure.getPossibleFieldsToMove()) {
            List<FigureToField> figureToField = new ArrayList<>();
            figureToField.add(FigureToField.builder().figure(figure).field(field).build());
            possibleTurnsAndEating.add(Turn.builder().figureToDestinationField(figureToField).build());
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

    private List<Turn> castling() {
        List<Turn> castlings = new ArrayList<>();
        List<Figure> rocks = Board.getExactTypeOfFiguresByColor(Rock.class, currentColor);
        King king = (King) Board.getExactTypeOfFiguresByColor(King.class, currentColor).get(0);
        for (Figure figure : rocks) {
            Rock rock = (Rock) figure;
            if (isShortCastlingPossible(rock)) {
                castlings.add(shortCastling(rock, king));
            }
            if (isLongCastlingPossible(rock)) {
                castlings.add(longCastling(rock, king));
            }
        }
        return castlings;
    }

    private boolean isShortCastlingPossible(Rock rock){
        return ((currentColor == Color.BLACK && rock.getField().equals(h8))
                || (currentColor == Color.WHITE && rock.getField().equals(h1)))
                && rock.isOpportunityToCastling()
                && king.isOpportunityToCastling();
    }

    private boolean isLongCastlingPossible(Rock rock){
        return ((currentColor == Color.BLACK && rock.getField().equals(a8))
                || (currentColor == Color.WHITE && rock.getField().equals(a1)))
                && rock.isOpportunityToCastling()
                && king.isOpportunityToCastling();
    }

    private Turn shortCastling(Rock rock, King king) {
        Turn shortCastlingTurn = null;
        List<FigureToField> castlingTuple = new ArrayList<>();
        if (rock.isOpportunityToCastling() && king.isOpportunityToCastling()) {
            if (currentColor == Color.BLACK) {
                if (!Board.getFieldsUnderWhiteInfluence().contains(f8) && !Board.getFieldsUnderWhiteInfluence().contains(g8) &&
                        Board.getFieldToFigure().get(f8) == null && Board.getFieldToFigure().get(g8) == null) {
                    castlingTuple.add(FigureToField.builder().figure(king).field(g8).build());
                    castlingTuple.add(FigureToField.builder().figure(rock).field(f8).build());

                    shortCastlingTurn = Turn.builder().figureToDestinationField(castlingTuple).writtenStyle(SHORT_CASTLING_ZEROS).build();
                }
            } else {
                if (!Board.getFieldsUnderBlackInfluence().contains(f1) && !Board.getFieldsUnderBlackInfluence().contains(g1) &&
                        Board.getFieldToFigure().get(f1) == null && Board.getFieldToFigure().get(g1) == null) {
                    castlingTuple.add(FigureToField.builder().figure(king).field(g1).build());
                    castlingTuple.add(FigureToField.builder().figure(rock).field(f1).build());
                    shortCastlingTurn = Turn.builder().figureToDestinationField(castlingTuple).writtenStyle(SHORT_CASTLING_ZEROS).build();
                }
            }
        }
        return shortCastlingTurn;
    }

    private Turn longCastling(Rock rock, King king) {
        Turn longCastlingTurn = null;
        List<FigureToField> castlingTuple = new ArrayList<>();
        if (rock.isOpportunityToCastling() && king.isOpportunityToCastling()) {
            if (currentColor == Color.BLACK) {
                if (!Board.getFieldsUnderWhiteInfluence().contains(b8) && !Board.getFieldsUnderWhiteInfluence().contains(c8) &&
                        !Board.getFieldsUnderWhiteInfluence().contains(d8) && Board.getFieldToFigure().get(b8) == null &&
                        Board.getFieldToFigure().get(c8) == null && Board.getFieldToFigure().get(d8) == null) {
                    castlingTuple.add(FigureToField.builder().figure(king).field(c8).build());
                    castlingTuple.add(FigureToField.builder().figure(rock).field(d8).build());
                    longCastlingTurn = Turn.builder().figureToDestinationField(castlingTuple).writtenStyle(LONG_CASTLING_ZEROS).build();
                }
            } else {
                if (!Board.getFieldsUnderBlackInfluence().contains(b1) && !Board.getFieldsUnderBlackInfluence().contains(c1) &&
                        !Board.getFieldsUnderBlackInfluence().contains(d1) && Board.getFieldToFigure().get(b1) == null &&
                        Board.getFieldToFigure().get(c1) == null && Board.getFieldToFigure().get(d1) == null) {
                    castlingTuple.add(FigureToField.builder().figure(king).field(c1).build());
                    castlingTuple.add(FigureToField.builder().figure(rock).field(d1).build());
                    longCastlingTurn = Turn.builder().figureToDestinationField(castlingTuple).writtenStyle(LONG_CASTLING_ZEROS).build();
                }
            }
        }
        return longCastlingTurn;
    }

    private boolean pawnReachesLastLineCanSaveKing(Pawn pawnAlly, Figure enemy) {
        return pawnAlly.isOnThePenultimateLine() && pawnAlly.getWhoCouldBeEaten().contains(enemy);
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