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
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.utils.ProcessingUtils;
import scala.Tuple2;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.koropets_suhanov.chess.process.constants.Constants.*;

public class Game {

    private static Set<Turn> possibleTurnsAndEating = new LinkedHashSet<Turn>();
    private static int numberOfTurn;

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

    public Set<Turn> getPossibleTurnsAndEatings(Color color, int numberOfTurn) {
        this.numberOfTurn = numberOfTurn;
        setPossibleTurnsAndEating(color);
        possibleTurnsAndEating = possibleTurnsAndEating.stream().filter(turn -> turn != null).collect(Collectors.toSet());
        return possibleTurnsAndEating;
    }

    private static void setPossibleTurnsAndEating(Color color){
        possibleTurnsAndEating.clear();
        King king = Board.getKing(color);
        List<Observer> allies = Board.getFigures(color).stream().filter(a -> a.getClass() != King.class).collect(Collectors.toList());

        if (king.isUnderAttack() && king.getEnemiesAttackMe().size() == 1){
            List<Tuple2<Figure, Field>> kingTuple2 = new ArrayList<>();
            for (Figure enemy : king.getWhoCouldBeEaten()){
                if (enemy.getAlliesProtectMe().size() == 0){
                    kingTuple2.add(new Tuple2<>(king, enemy.getField()));
                    possibleTurnsAndEating.add(ProcessingUtils.createTurn(kingTuple2, null, "", true, false, false, enemy, numberOfTurn));
                }
            }
            Figure whoAttackKing = king.getEnemiesAttackMe().iterator().next();
            for (Observer observer : allies){
                Figure ally = (Figure) observer;
                if (whoAttackKing.getClass() == Pawn.class && ally.getClass() == Pawn.class){
                    Pawn pawnAlly = (Pawn) ally;
                    if (enPassantCanSaveKing(pawnAlly, whoAttackKing)){
                        List<Tuple2<Figure, Field>> alienToTargetField = new ArrayList<>();
                        alienToTargetField.add(new Tuple2<>(pawnAlly, pawnAlly.getEnPassantField()));
                        possibleTurnsAndEating.add(ProcessingUtils.createTurn(alienToTargetField, null, "", true, false, true, whoAttackKing, numberOfTurn));
                    }
                }else if ((color == Color.WHITE && whoAttackKing.getField().getX() == LINE_H) || (color == Color.BLACK && whoAttackKing.getField().getX() == LINE_A)){
                    Pawn pawnAlly = (Pawn) ally;
                    if (pawnReachesLastLineCanSaveKing(pawnAlly, whoAttackKing)){
                        possibleTurnsAndEating.addAll(setTransformationFields(pawnAlly, whoAttackKing, color, true));

                    }
                }else if (ally.getWhoCouldBeEaten().contains(whoAttackKing)){
                    List<Tuple2<Figure, Field>> alienToTargetField = new ArrayList<>();
                    alienToTargetField.add(new Tuple2<>(ally, whoAttackKing.getField()));
                    possibleTurnsAndEating.add(ProcessingUtils.createTurn(alienToTargetField, null, "", true, false, false, whoAttackKing, numberOfTurn));
                }
            }
            peacefulTurn(king);

            Figure figureAttacksKing = king.getEnemiesAttackMe().iterator().next();
            Set<Turn> alienCovers = new HashSet<>();
            if (figureAttacksKing instanceof Rock){
                alienCovers = coveringIfRockAttacks(king, (Rock) figureAttacksKing);
            }
            if (figureAttacksKing instanceof Bishop){
                alienCovers = coveringIfBishopAttacks(king, (Bishop) figureAttacksKing);
            }
            if (figureAttacksKing instanceof Queen){
                alienCovers = coveringIfQueenAttacks(king, (Queen) figureAttacksKing);
            }

            if (alienCovers != null){
                possibleTurnsAndEating.addAll(alienCovers);
            }

        }
        if (king.isUnderAttack() && king.getEnemiesAttackMe().size() > 1){
            peacefulTurn(king);
        }

        if (!king.isUnderAttack()){
            for (Observer observer : allies){
                Figure ally = (Figure) observer;
                if (ally.getClass() == Pawn.class && ((Pawn)ally).isEnPassant()){
                    possibleTurnsAndEating.addAll(turnsInCaseEnPassant((Pawn) ally));

                }else if(ally.getClass() == Pawn.class && ((Pawn)ally).isOnThePenultimateLine()){
                    possibleTurnsAndEating.addAll(turnsInCaseTransformation(ally, color));

                }else{
                    peacefulTurn(ally);
                    for (Figure attackedFigure : ally.getWhoCouldBeEaten()){
                        List<Tuple2<Figure, Field>> figureFieldTuple = new ArrayList<>();
                        figureFieldTuple.add(new Tuple2<Figure, Field>(ally, attackedFigure.getField()));
                        possibleTurnsAndEating.add(ProcessingUtils.createTurn(figureFieldTuple, null, "", true, false, false,  attackedFigure, numberOfTurn));
                    }
                }
            }
            possibleTurnsAndEating.addAll(castling(color));
        }
    }

    private static void peacefulTurn(Figure figure){
        for (Field field : figure.getPossibleFieldsToMove()){
            List<Tuple2<Figure, Field>> figureToFieldTuple = new ArrayList<>();
            figureToFieldTuple.add(new Tuple2<>(figure, field));
            possibleTurnsAndEating.add(ProcessingUtils.createTurn(figureToFieldTuple, null, "", false, false, false, null, numberOfTurn));
        }
    }

    private static Set<Turn> coveringIfRockAttacks(final King king, final Rock enemyRock){
        Set<Turn> coveringTurns = new HashSet<>();
        Set<Field> fieldsBetween = ProcessingUtils.fieldsBetweenRockAndKing(king, enemyRock.getField());
        List<Observer> alienFigures = Board.getFigures(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private static Set<Turn> coveringIfBishopAttacks(final King king, final Bishop bishop){
        Set<Field> fieldsBetween = ProcessingUtils.fieldsBetweenBishopAndKing(king, bishop.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFigures(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private static Set<Turn> coveringIfQueenAttacks(final King king, final Queen queen){
        Set<Field> fieldsBetween = ProcessingUtils.fieldsBetweenQueenAndKing(king, queen.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        List<Observer> alienFigures = Board.getFigures(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private static void setCoveringTurns(final List<Observer> alienFigures, final Set<Turn> coveringTurns, final Set<Field> fieldsBetween){
        alienFigures.stream().filter(v -> v.getClass() != King.class).forEach(f ->{
            ((Figure)f).getPossibleFieldsToMove().forEach(k -> {
                if (fieldsBetween.contains(k)){
                    if (f.getClass() == Pawn.class && ((Pawn)f).isOnThePenultimateLine()){
                        for (String writtenStypeTurn : ProcessingUtils.FIGURES_IN_WRITTEN_STYLE){
                            List<Tuple2<Figure, Field>> covering = new ArrayList<>();
                            covering.add(new Tuple2<>((Figure)f, k));
                            coveringTurns.add(ProcessingUtils.createTurn(covering, ProcessingUtils.createFigure(k, writtenStypeTurn, ((Figure)f).getColor()),
                                    "", false, true, false, null, numberOfTurn));
                        }
                    }
                    List<Tuple2<Figure, Field>> covering = new ArrayList<>();
                    covering.add(new Tuple2<>((Figure)f, k));
                    coveringTurns.add(ProcessingUtils.createTurn(covering, null, "", false, false, false, null, numberOfTurn));
                }
            });
        });
    }

    private static List<Turn> castling(Color color){
        List<Turn> castlings = new ArrayList<>();
        List<Figure> rocks = Board.getFiguresByClass(Rock.class, color);
        King king = (King) Board.getFiguresByClass(King.class, color).get(0);
        for (Figure rock : rocks){
            if ((color == Color.BLACK && rock.getField().equals(h8)) || (color == Color.WHITE && rock.getField().equals(h1))){
                castlings.add(shortCastling((Rock)rock, king, color));
            }
            if ((color == Color.BLACK && rock.getField().equals(a8)) || (color == Color.WHITE && rock.getField().equals(a1))){
                castlings.add(longCastling((Rock) rock, king, color));
            }
        }
        return castlings;
    }

    private static Turn shortCastling(Rock rock, King king, Color color){
        Turn shortCastlingTurn = null;
        List<Tuple2<Figure, Field>> castlingTuple = new ArrayList<>();
        if (rock.isOpportunityToCastling() && king.isOpportunityToCastling()){
            if (color == Color.BLACK){
                if (!Board.getFieldsUnderWhiteInfluence().contains(f8) && !Board.getFieldsUnderWhiteInfluence().contains(g8) &&
                        Board.getFieldToFigure().get(f8) == null && Board.getFieldToFigure().get(g8) == null){
                    castlingTuple.add(new Tuple2<>(king, g8));
                    castlingTuple.add(new Tuple2<>(rock, f8));
                    shortCastlingTurn = ProcessingUtils.createTurn(castlingTuple, null, shortCastling, false, false, false, null, numberOfTurn);
                }
            }else{
                if (!Board.getFieldsUnderBlackInfluence().contains(f1) && !Board.getFieldsUnderBlackInfluence().contains(g1) &&
                        Board.getFieldToFigure().get(f1) == null && Board.getFieldToFigure().get(g1) == null){
                    castlingTuple.add(new Tuple2<>(king, g1));
                    castlingTuple.add(new Tuple2<>(rock, f1));
                    shortCastlingTurn = ProcessingUtils.createTurn(castlingTuple, null, shortCastling, false, false, false, null, numberOfTurn);
                }
            }
        }
        return shortCastlingTurn;
    }

    private static Turn longCastling(Rock rock, King king, Color color){
        Turn longCastlingTurn = null;
        List<Tuple2<Figure, Field>> castlingTuple = new ArrayList<>();
        if (rock.isOpportunityToCastling() && king.isOpportunityToCastling()){
            if (color == Color.BLACK){
                if (!Board.getFieldsUnderWhiteInfluence().contains(b8) && !Board.getFieldsUnderWhiteInfluence().contains(c8) &&
                        !Board.getFieldsUnderWhiteInfluence().contains(d8) && Board.getFieldToFigure().get(b8) == null &&
                        Board.getFieldToFigure().get(c8) == null && Board.getFieldToFigure().get(d8) == null){
                    castlingTuple.add(new Tuple2<>(king, c8));
                    castlingTuple.add(new Tuple2<>(rock, d8));
                    longCastlingTurn = ProcessingUtils.createTurn(castlingTuple, null, longCastling, false, false, false,  null, numberOfTurn);
                }
            }else {
                if (!Board.getFieldsUnderBlackInfluence().contains(b1) && !Board.getFieldsUnderBlackInfluence().contains(c1) &&
                        !Board.getFieldsUnderBlackInfluence().contains(d1) && Board.getFieldToFigure().get(b1) == null &&
                        Board.getFieldToFigure().get(c1) == null && Board.getFieldToFigure().get(d1) == null){
                    castlingTuple.add(new Tuple2<>(king, c1));
                    castlingTuple.add(new Tuple2<>(rock, d1));
                    longCastlingTurn = ProcessingUtils.createTurn(castlingTuple, null, longCastling, false, false, false, null, numberOfTurn);
                }
            }
        }
        return longCastlingTurn;
    }

    private static boolean enPassantCanSaveKing(Pawn pawnAlly, Figure pawnEnemy){
        return pawnAlly.isEnPassant() && pawnAlly.getEnPassantEnemy().equals(pawnEnemy);
    }

    private static boolean pawnReachesLastLineCanSaveKing(Pawn pawnAlly, Figure enemy){
        return pawnAlly.isOnThePenultimateLine() && pawnAlly.getWhoCouldBeEaten().contains(enemy);
    }

    private static Set<Turn> turnsInCaseEnPassant(Pawn ally){
        Set<Turn> possibleTurns = new HashSet<>();
        Figure enPassantEnemy = ally.getEnPassantEnemy();
        for (Field field : ally.getPossibleFieldsToMove()){
            List<Tuple2<Figure, Field>> figureToFieldTupleList = new ArrayList<>();
            figureToFieldTupleList.add(new Tuple2<>(ally, field));
            possibleTurns.add(ProcessingUtils.createTurn(figureToFieldTupleList, null, "",
                    false, false, false, null, numberOfTurn));

        }
        for (Figure enemy : ally.getWhoCouldBeEaten()){
            if (!enemy.equals(enPassantEnemy)){
                List<Tuple2<Figure, Field>> figureToFieldTupleList = new ArrayList<>();
                figureToFieldTupleList.add(new Tuple2<>(ally, enemy.getField()));
                possibleTurns.add(ProcessingUtils.createTurn(figureToFieldTupleList, null, "",
                        true, false, false,  enemy, numberOfTurn));
            }
        }
        if (enPassantEnemy != null){
            List<Tuple2<Figure, Field>> figureToFieldList = new ArrayList<>();
            figureToFieldList.add(new Tuple2<>(ally, ally.getEnPassantField()));
            possibleTurns.add(ProcessingUtils.createTurn(figureToFieldList, null, "", true,
                    false, true,  enPassantEnemy, numberOfTurn));
        }
        return possibleTurns;
    }

    private static Set<Turn> turnsInCaseTransformation(Figure ally, Color color){
        Set<Turn> possibleTurns = new HashSet<>();
        for (Field possibleFieldToMove : ally.getPreyField()){
            for (String writtenStyle: ProcessingUtils.FIGURES_IN_WRITTEN_STYLE){
                List<Tuple2<Figure, Field>> figureToFieldTupleList = new ArrayList<>();
                figureToFieldTupleList.add(new Tuple2<>(ally, possibleFieldToMove));
                possibleTurns.add(ProcessingUtils.createTurn(figureToFieldTupleList, ProcessingUtils.createFigure(possibleFieldToMove, writtenStyle, color),
                        "", false, true, false, null, numberOfTurn));

            }
        }
        for (Figure enemy : ally.getWhoCouldBeEaten()){
            for (String writtenStyle : ProcessingUtils.FIGURES_IN_WRITTEN_STYLE){
                List<Tuple2<Figure, Field>> figureToFieldTupleList = new ArrayList<>();
                figureToFieldTupleList.add(new Tuple2<>(ally, enemy.getField()));
                possibleTurns.add(ProcessingUtils.createTurn(figureToFieldTupleList, ProcessingUtils.createFigure(enemy.getField(), writtenStyle, color),
                        "", true, true, false, enemy, numberOfTurn));

            }
        }
        return possibleTurns;
    }

    private static Set<Turn> setTransformationFields(Pawn pawn, Figure enemy, Color color, boolean eating){
        Set<Turn> transformationSet = new HashSet<>();
        for (String writtenStyleOfFigure : ProcessingUtils.FIGURES_IN_WRITTEN_STYLE){
            List<Tuple2<Figure, Field>> allyToFieldList = new ArrayList<>();
            allyToFieldList.add(new Tuple2<Figure, Field>(pawn, enemy.getField()));
            Turn newTransformationTurn = ProcessingUtils.createTurn(allyToFieldList, ProcessingUtils.createFigure(enemy.getField(), writtenStyleOfFigure, color),
                    "", eating, true, false,  enemy, numberOfTurn);
            transformationSet.add(newTransformationTurn);
        }
        return transformationSet;
    }
}