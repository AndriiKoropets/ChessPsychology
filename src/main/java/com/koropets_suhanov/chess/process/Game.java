package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.process.pojo.Turn;
import com.koropets_suhanov.chess.utils.ProcessingUtils;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author AndriiKoropets
 */
public class Game {

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
    private Set<Turn> possibleTurnsAndEating = new LinkedHashSet<Turn>();
    private int numberOfTurn;

    public Set<Turn> getPossibleTurnsAndEatings(Color color, int numberOfTurn) {
        this.numberOfTurn = numberOfTurn;
        setPossibleTurnsAndEating(color);
        possibleTurnsAndEating = possibleTurnsAndEating.stream().filter(turn -> turn != null).collect(Collectors.toSet());
        return possibleTurnsAndEating;
    }

    private void setPossibleTurnsAndEating(Color color){
        possibleTurnsAndEating.clear();
        King king = (King) Board.getFiguresByClass(King.class, color).get(0);
        Set<Observer> allies = Board.getFigures(color);

        if (king.isUnderAttack() && king.getEnemiesAttackMe().size() == 1){
            Map<Figure, Field> kingMap = new HashMap<>();
            for (Figure enemy : king.getWhoCouldBeEaten()){
                if (enemy.getAlliesProtectMe().size() == 0){
                    kingMap.put(king, enemy.getField());
                    possibleTurnsAndEating.add(ProcessingUtils.createTurn(kingMap, "", true, enemy, numberOfTurn));
                }
            }
            Figure whoAttackKing = king.getEnemiesAttackMe().iterator().next();
            for (Observer observer : allies){
                if (((Figure)observer).getWhoCouldBeEaten().contains(whoAttackKing)){
                    Map<Figure, Field> alienToTargetField = new HashMap<>();
                    alienToTargetField.put((Figure)observer, whoAttackKing.getField());
                    possibleTurnsAndEating.add(ProcessingUtils.createTurn(alienToTargetField, "", true, whoAttackKing, numberOfTurn));
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
            for (Observer figure : allies){
                peacefulTurn((Figure) figure);
                for (Figure attackedFigure : ((Figure) figure).getWhoCouldBeEaten()){
                    Map<Figure, Field> figureFieldMap = new HashMap<>();
                    figureFieldMap.put((Figure)figure, attackedFigure.getField());
                    possibleTurnsAndEating.add(ProcessingUtils.createTurn(figureFieldMap, "", true, attackedFigure, numberOfTurn));
                }
            }
            possibleTurnsAndEating.addAll(castling(color));
        }
    }

    private void peacefulTurn(Figure figure){
        for (Field field : figure.getPossibleFieldsToMove()){
            Map<Figure, Field> figureToFieldMap = new HashMap<>();
            figureToFieldMap.put(figure, field);
            possibleTurnsAndEating.add(ProcessingUtils.createTurn(figureToFieldMap, "", false, null, numberOfTurn));
        }
    }

    private Set<Turn> coveringIfRockAttacks(final King king, final Rock enemyRock){
        Set<Turn> coveringTurns = new HashSet<>();
        Set<Field> fieldsBetween = ProcessingUtils.fieldsBetweenRockAndKing(king, enemyRock.getField());
        Set<Observer> alienFigures = Board.getFigures(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Turn> coveringIfBishopAttacks(final King king, final Bishop bishop){
        Set<Field> fieldsBetween = ProcessingUtils.fieldsBetweenBishopAndKing(king, bishop.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        Set<Observer> alienFigures = Board.getFigures(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private Set<Turn> coveringIfQueenAttacks(final King king, final Queen queen){
        Set<Field> fieldsBetween = ProcessingUtils.fieldsBetweenQueenAndKing(king, queen.getField());
        Set<Turn> coveringTurns = new HashSet<>();
        Set<Observer> alienFigures = Board.getFigures(king.getColor());
        setCoveringTurns(alienFigures, coveringTurns, fieldsBetween);
        return coveringTurns;
    }

    private void setCoveringTurns(final Set<Observer> alienFigures, final Set<Turn> coveringTurns, final Set<Field> fieldsBetween){
        alienFigures.stream().filter(v -> v.getClass() != King.class).forEach(f ->{
            ((Figure)f).getPossibleFieldsToMove().forEach(k -> {
                if (fieldsBetween.contains(k)){
                    Map<Figure, Field> covering = new HashMap<>();
                    covering.put( (Figure) f, k);
                    coveringTurns.add(ProcessingUtils.createTurn(covering, "", false, null, numberOfTurn));
                }
            });
        });
    }

    private List<Turn> castling(Color color){
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

    private Turn shortCastling(Rock rock, King king, Color color){
        Turn shortCastling = null;
        Map<Figure, Field> castlingMap = new HashMap<>();
        if (rock.isOpportunityToCastling() && king.isOpportunityToCastling()){
            if (color == Color.BLACK){
                if (!Board.getFieldsUnderWhiteInfluence().contains(f8) && !Board.getFieldsUnderWhiteInfluence().contains(g8) &&
                        Board.getFieldToFigure().get(f8) == null && Board.getFieldToFigure().get(g8) == null){
                    castlingMap.put(king, g8);
                    castlingMap.put(rock, f8);
                    shortCastling = ProcessingUtils.createTurn(castlingMap, ProcessingUtils.shortCastling, false, null, numberOfTurn);
                }
            }else{
                if (!Board.getFieldsUnderBlackInfluence().contains(f1) && !Board.getFieldsUnderBlackInfluence().contains(g1) &&
                        Board.getFieldToFigure().get(f1) == null && Board.getFieldToFigure().get(g1) == null){
                    castlingMap.put(king, g1);
                    castlingMap.put(rock, f1);
                    shortCastling = ProcessingUtils.createTurn(castlingMap, ProcessingUtils.shortCastling, false, null, numberOfTurn);
                }
            }
        }
        return shortCastling;
    }

    private Turn longCastling(Rock rock, King king, Color color){
        Turn longCastling = null;
        Map<Figure, Field> castlingMap = new HashMap<>();
        if (rock.isOpportunityToCastling() && king.isOpportunityToCastling()){
            if (color == Color.BLACK){
                if (!Board.getFieldsUnderWhiteInfluence().contains(b8) && !Board.getFieldsUnderWhiteInfluence().contains(c8) &&
                        !Board.getFieldsUnderWhiteInfluence().contains(d8) && Board.getFieldToFigure().get(b8) == null &&
                        Board.getFieldToFigure().get(c8) == null && Board.getFieldToFigure().get(d8) == null){
                    castlingMap.put(king, c8);
                    castlingMap.put(rock, d8);
                    longCastling = ProcessingUtils.createTurn(castlingMap, ProcessingUtils.longCastling, false, null, numberOfTurn);
                }
            }else {
                if (!Board.getFieldsUnderBlackInfluence().contains(b1) && !Board.getFieldsUnderBlackInfluence().contains(c1) &&
                        !Board.getFieldsUnderBlackInfluence().contains(d1) && Board.getFieldToFigure().get(b1) == null &&
                        Board.getFieldToFigure().get(c1) == null && Board.getFieldToFigure().get(d1) == null){
                    castlingMap.put(king, c1);
                    castlingMap.put(rock, d1);
                    longCastling = ProcessingUtils.createTurn(castlingMap, ProcessingUtils.longCastling, false, null, numberOfTurn);
                }
            }
        }
        return longCastling;
    }
}