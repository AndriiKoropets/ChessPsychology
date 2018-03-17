package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.process.dto.FigureToField;
import com.koropets_suhanov.chess.process.utils.ProcessUtils;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Covering {

    public Set<Turn> coveringIfRockAttacks(final King king, final Rock enemyRock) {
        Set<Turn> coveringTurns = new HashSet<>();
        Set<Field> fieldsBetween = fieldsBetweenRockAndKing(king, enemyRock.getField());
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        defineCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Field> fieldsBetweenRockAndKing(final King king, final Field rockPosition) {
        Set<Field> fieldsBetween = new HashSet<>();
        rockAndKingVerticalCase(fieldsBetween, king, rockPosition);
        rockAndKingHorizontalCase(fieldsBetween, king, rockPosition);
        return fieldsBetween;
    }

    private void rockAndKingVerticalCase(Set<Field> fieldsBetween, King king, Field rockPosition){
        if (isRockAndKingOnTheSameVerticalLine(king, rockPosition)) {
            if (isKingHigherOnVerticalLine(king, rockPosition)) {
                for (int i = king.getField().getX() - 1; i > rockPosition.getX(); i--) {
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            } else {
                for (int i = king.getField().getX() + 1; i < rockPosition.getX(); i++) {
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            }
        }
    }

    private boolean isRockAndKingOnTheSameVerticalLine(King king, Field rockPosition){
        return king.getField().getX() == rockPosition.getX();
    }

    private boolean isKingHigherOnVerticalLine(King king, Field rockPosition){
        return king.getField().getX() > rockPosition.getX();
    }

    private void rockAndKingHorizontalCase(Set<Field> fieldsBetween, King king, Field rockPosition){
        if (isRockAndKingOnTheSameHorizontalLine(king, rockPosition)) {
            if (isKingOnTheRightFromRock(king, rockPosition)) {
                for (int j = king.getField().getY() - 1; j > rockPosition.getY(); j--) {
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            } else {
                for (int j = king.getField().getY() + 1; j < rockPosition.getY(); j++) {
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            }
        }
    }

    private boolean isRockAndKingOnTheSameHorizontalLine(King king, Field rockPosition){
        return king.getField().getY() == rockPosition.getY();
    }

    private boolean isKingOnTheRightFromRock(King king, Field rockPosition){
        return king.getField().getY() > rockPosition.getY();
    }

    public Set<Turn> coveringIfBishopAttacks(final King king, final Bishop bishop) {
        Set<Field> fieldsBetween = fieldsBetweenBishopAndKing(king, bishop.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        defineCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Field> fieldsBetweenBishopAndKing(final King king, final Field bishopPosition) {
        Set<Field> fieldsBetween = new HashSet<>();
        rightUpperCornerFromBishop(fieldsBetween, king, bishopPosition);
        leftUpperCornerFromBishop(fieldsBetween, king, bishopPosition);
        rightLowerCornerFromBishop(fieldsBetween, king, bishopPosition);
        leftLowerCornerFromBishop(fieldsBetween, king, bishopPosition);
        return fieldsBetween;
    }

    private void leftUpperCornerFromBishop(Set<Field> fieldsBetween, King king, Field bishopPosition){
        if (isKingOnTheLeftUpperCornerFromBishop(king, bishopPosition)) {
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
    }

    private boolean isKingOnTheLeftUpperCornerFromBishop(King king, Field bishopPosition){
        return king.getField().getX() > bishopPosition.getX() && king.getField().getY() < bishopPosition.getY();
    }

    private void rightUpperCornerFromBishop(Set<Field> fieldsBetween, King king, Field bishopPosition){
        if (isKingOnTheRightUpperCornerFromBishop(king, bishopPosition)) {
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }
        }
    }

    private boolean isKingOnTheRightUpperCornerFromBishop(King king, Field bishopPosition){
        return king.getField().getX() > bishopPosition.getX() && king.getField().getY() > bishopPosition.getY();
    }

    private void rightLowerCornerFromBishop(Set<Field> fieldsBetween, King king, Field bishopPosition){
        if (isKingOnTheRightLowerCornerFromBishop(king, bishopPosition)) {
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }
        }
    }

    private boolean isKingOnTheRightLowerCornerFromBishop(King king, Field bishopPosition){
        return king.getField().getX() < bishopPosition.getX() && king.getField().getY() > bishopPosition.getY();
    }

    private void leftLowerCornerFromBishop(Set<Field> fieldsBetween, King king, Field bishopPosition){
        if (isKingOnTheLeftLowerCornerFromBishop(king, bishopPosition)) {
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
    }

    private boolean isKingOnTheLeftLowerCornerFromBishop(King king, Field bishopPosition){
        return king.getField().getX() < bishopPosition.getX() && king.getField().getY() < bishopPosition.getY();
    }

    public Set<Turn> coveringIfQueenAttacks(final King king, final Queen queen) {
        Set<Field> fieldsBetween = fieldsBetweenQueenAndKing(king, queen.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        defineCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Field> fieldsBetweenQueenAndKing(final King king, final Field queenPosition) {
        Set<Field> fieldsBetweenQueenAndKing = new HashSet<>();
        if (king.getField().getX() == queenPosition.getX() || king.getField().getY() == queenPosition.getY()) {
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenRockAndKing(king, queenPosition));
        } else {
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenBishopAndKing(king, queenPosition));
        }
        return fieldsBetweenQueenAndKing;
    }

    private void defineCoveringTurns(final List<Observer> alienFigures, final Set<Turn> coveringTurns, final Set<Field> fieldsBetween) {
        alienFigures.stream().filter(v -> v.getClass() != King.class).forEach(f -> {
            ((Figure) f).getPossibleFieldsToMove().forEach(k -> {
                if (fieldsBetween.contains(k)) {
                    if (f.getClass() == Pawn.class && ((Pawn) f).isOnThePenultimateLine()) {
                        for (String writtenStyleTurn : ParseWrittenTurn.FIGURES_IN_WRITTEN_STYLE) {
                            List<FigureToField> covering = new ArrayList<>();
                            covering.add(FigureToField.builder().figure((Figure) f).field(k).build());
                            coveringTurns.add(Turn.builder()
                                    .figureToDestinationField(covering)
                                    .figureFromTransformation(
                                            ProcessUtils.createFigure(k, writtenStyleTurn, ((Figure) f).getColor()))
                                    .transformation(true)
                                    .build());
                        }
                    }
                    List<FigureToField> covering = new ArrayList<>();
                    covering.add(FigureToField.builder().figure((Figure) f).field(k).build());
                    coveringTurns.add(Turn.builder().figureToDestinationField(covering).build());
                }
            });
        });
    }
}
