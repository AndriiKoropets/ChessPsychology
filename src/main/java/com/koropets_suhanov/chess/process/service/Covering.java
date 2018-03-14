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
import com.koropets_suhanov.chess.utils.ProcessingUtils;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Covering {

    public Set<Turn> coveringIfRockAttacks(final King king, final Rock enemyRock) {
        Set<Turn> coveringTurns = new HashSet<>();
        Set<Field> fieldsBetween = fieldsBetweenRockAndKing(king, enemyRock.getField());
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Field> fieldsBetweenRockAndKing(final King king, final Field rockPosition) {
        Set<Field> fieldsBetween = new HashSet<>();
        if (king.getField().getX() == rockPosition.getX()) {
            if (king.getField().getX() > rockPosition.getX()) {
                for (int i = king.getField().getX() - 1; i > rockPosition.getX(); i--) {
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            } else {
                for (int i = king.getField().getX() + 1; i < rockPosition.getX(); i++) {
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            }
        }
        if (king.getField().getY() == rockPosition.getY()) {
            if (king.getField().getY() > rockPosition.getY()) {
                for (int j = king.getField().getY() - 1; j > rockPosition.getY(); j--) {
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            } else {
                for (int j = king.getField().getY() + 1; j < rockPosition.getY(); j++) {
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            }
        }
        return fieldsBetween;
    }

    public Set<Turn> coveringIfBishopAttacks(final King king, final Bishop bishop) {
        Set<Field> fieldsBetween = fieldsBetweenBishopAndKing(king, bishop.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Field> fieldsBetweenBishopAndKing(final King king, final Field bishopPosition) {
        Set<Field> fieldsBetween = new HashSet<>();
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()) {
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }
        }
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()) {
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()) {
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }

        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()) {
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        return fieldsBetween;
    }

    public Set<Turn> coveringIfQueenAttacks(final King king, final Queen queen) {
        Set<Field> fieldsBetween = fieldsBetweenQueenAndKing(king, queen.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFiguresByColor(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
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
}
