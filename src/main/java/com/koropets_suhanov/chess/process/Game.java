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

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
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
    private static final Field a1 = new Field(7, 0);
    private static final Field h1 = new Field(7, 7);
    private static final Field a8 = new Field(0, 0);
    private static final Field h8 = new Field(0, 7);
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
        King king = null;
        Set<Observer> figures = (color == Color.BLACK) ? Board.getBlackFigures() : Board.getWhiteFigures();
        for (Observer figure : figures){
            if (figure.getClass() == King.class){
                king = (King) figure;
                break;
            }
        }
        if (king.isUnderAttack() && king.getEnemiesAttackMe().size() == 1){
            Map<Figure, Field> kingMap = new HashMap<>();
            for (Figure enemy : king.getWhoCouldBeEaten()){
                if (enemy.getAliensProtectMe().size() == 0){
                    kingMap.put(king, enemy.getField());
                    possibleTurnsAndEating.add(new Turn.Builder().figureToDestinationField(kingMap)
                                                                    .eating(true)
                                                                    .targetedFigure(enemy)
                                                                    .numberOfTurn(numberOfTurn)
                                                                    .writtenStyle("")
                                                                    .build());
                }
            }
            Figure whoAttackKing = king.getEnemiesAttackMe().iterator().next();
            for (Observer observer : figures){
                if (((Figure)observer).getWhoCouldBeEaten().contains(whoAttackKing)){
                    Map<Figure, Field> alienToTargetField = new HashMap<>();
                    alienToTargetField.put((Figure)observer, whoAttackKing.getField());
                    possibleTurnsAndEating.add(new Turn.Builder().figureToDestinationField(alienToTargetField)
                                                                                            .eating(true)
                                                                                            .targetedFigure(whoAttackKing)
                                                                                            .numberOfTurn(numberOfTurn)
                                                                                            .writtenStyle("")
                                                                                            .build());
                }
            }
            //TODO add the case when other figures could cover king
            peacefulTurn(king);

            Figure figureAttacksKing = king.getEnemiesAttackMe().iterator().next();
            List<Turn> alienCovers = new ArrayList<>();
            if (figureAttacksKing instanceof Rock){
                alienCovers = coveringIfRockAttacks();
            }
            if (figureAttacksKing instanceof Bishop){
                alienCovers = coveringIfBishopAttacks();
            }
            if (figureAttacksKing instanceof Queen){
                alienCovers = coveringIfQueenAttacks();
            }

            if (alienCovers != null){
                possibleTurnsAndEating.addAll(alienCovers);
            }

        }
        if (king.isUnderAttack() && king.getEnemiesAttackMe().size() > 1){
            peacefulTurn(king);
        }
        if (!king.isUnderAttack()){
            for (Observer figure : figures){
                peacefulTurn((Figure) figure);
                for (Figure attackedFigure : ((Figure) figure).getWhoCouldBeEaten()){
                    Map<Figure, Field> figureFieldMap = new HashMap<>();
                    figureFieldMap.put((Figure)figure, attackedFigure.getField());
                    possibleTurnsAndEating.add(new Turn.Builder().figureToDestinationField(figureFieldMap)
                            .eating(true)
                            .numberOfTurn(numberOfTurn)
                            .writtenStyle("")
                            .build());
                }
            }
            possibleTurnsAndEating.addAll(castling(color));



//            possibleTurnsAndEating.add(new Turn.Builder().figureToDestinationField(castling(color))
//                                                            .eating(false)
//                                                            .writtenStyle("")
//                                                            .numberOfTurn(numberOfTurn)
//                                                            .build());
//            List<StringBuilder> turnsOnTheEndLine = pawnReachesEndLine(color);
//            for (StringBuilder stringBuilder : turnsOnTheEndLine){
//                possibleTurnsAndEating.add(stringBuilder.toString());
//            }
        }
    }

    private void peacefulTurn(Figure figure){
        for (Field field : figure.getPossibleFieldsToMove()){
            Map<Figure, Field> figureToFieldMap = new HashMap<>();
            figureToFieldMap.put(figure, field);
            possibleTurnsAndEating.add(new Turn.Builder().figureToDestinationField(figureToFieldMap)
                    .eating(false)
                    .targetedFigure(null)
                    .writtenStyle("")
                    .numberOfTurn(numberOfTurn)
                    .build());
        }
    }

    private List<Turn> coveringIfRockAttacks(){
        return null;
    }

    private List<Turn> coveringIfBishopAttacks(){
        return null;
    }

    private List<Turn> coveringIfQueenAttacks(){
        return null;
    }

//    private Map<Figure, Field> pawnReachesEndLine(Color color){
//        Set<Observer> figures = (color == Color.BLACK) ? Board.getBlackFigures() : Board.getWhiteFigures();
//        Map<Figure, Field> pawnAndField = new HashMap<>();
//        for (Observer figure : figures){
//            if (figure.getClass() == Pawn.class){
//                Pawn pawn = (Pawn) figure;
//                if (color == Color.BLACK){
//                    if (pawn.getField().getX() == 6){
//                        for (Field field : pawn.getPossibleFieldsToMove()){
//                            pawnAndField.put(pawn, field);
//                            turnsIfReachedEndLine(false, pawnAndField);
//                        }
//                        for (Field field : pawn.getAttackedFields()){
//                            pawnAndField.put();
//                            turnsIfReachedEndLine(pawn, (Field) field, true, pawnAndField);
//                        }
//                    }
//                }else {
//                    if (pawn.getField().getX() == 1){
//                        for (Object field : pawn.getPossibleFieldsToMove()){
//                            turnsIfReachedEndLine(pawn, (Field) field, false,pawnAndField);
//                        }
//                        for (Object field : pawn.getAttackedFields()){
//                            turnsIfReachedEndLine(pawn, (Field) field, true, pawnAndField);
//                        }
//                    }
//                }
//            }
//        }
//        return pawnAndField;
//    }

//    private Set<Turn> turnsIfReachedEndLine(boolean isEating, Map<Figure, Field> storage){
//        if (isEating){
//            StringBuilder turn1 = new StringBuilder(pawn.getField().toString());
//            turn1.append("-").append(aimedField.toString()).append("(").append("Q").append(")");
//            storage.add(turn1);
//            StringBuilder turn2 = new StringBuilder(pawn.getField().toString());
//            turn2.append("-").append(aimedField.toString()).append("(").append("R").append(")");
//            storage.add(turn2);
//            StringBuilder turn3 = new StringBuilder(pawn.getField().toString());
//            turn3.append("-").append(aimedField.toString()).append("(").append("K").append(")");
//            storage.add(turn3);
//            StringBuilder turn4 = new StringBuilder(pawn.getField().toString());
//            turn4.append("-").append(aimedField.toString()).append("(").append("B").append(")");
//            storage.add(turn4);
//        }else {
//            StringBuilder turn1 = new StringBuilder(pawn.getField().toString());
//            turn1.append("x").append(aimedField.toString()).append("(").append("Q").append(")");
//            storage.add(turn1);
//            StringBuilder turn2 = new StringBuilder(pawn.getField().toString());
//            turn2.append("x").append(aimedField.toString()).append("(").append("R").append(")");
//            storage.add(turn2);
//            StringBuilder turn3 = new StringBuilder(pawn.getField().toString());
//            turn3.append("x").append(aimedField.toString()).append("(").append("K").append(")");
//            storage.add(turn3);
//            StringBuilder turn4 = new StringBuilder(pawn.getField().toString());
//            turn4.append("x").append(aimedField.toString()).append("(").append("B").append(")");
//            storage.add(turn4);
//        }
//    }

    private List<Turn> castling(Color color){
        List<Turn> castlings = new ArrayList<>();
        List<Figure> rocks = Board.getInstance().getFiguresByClass(Rock.class, color);
        King king = (King) Board.getInstance().getFiguresByClass(King.class, color).get(0);
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
                    shortCastling = new Turn.Builder()
                                        .eating(false)
                                        .writtenStyle("0-0")
                                        .targetedFigure(null)
                                        .numberOfTurn(numberOfTurn)
                                        .figureToDestinationField(castlingMap)
                                        .build();
                }
            }else{
                if (!Board.getFieldsUnderBlackInfluence().contains(f1) && !Board.getFieldsUnderBlackInfluence().contains(g1) &&
                        Board.getFieldToFigure().get(f1) == null && Board.getFieldToFigure().get(g1) == null){
                    castlingMap.put(king, g1);
                    castlingMap.put(rock, f1);
                    shortCastling = new Turn.Builder()
                                        .eating(false)
                                        .writtenStyle("0-0")
                                        .targetedFigure(null)
                                        .numberOfTurn(numberOfTurn)
                                        .figureToDestinationField(castlingMap)
                                        .build();
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
                    longCastling = new Turn.Builder()
                                        .eating(false)
                                        .writtenStyle("0-0-0")
                                        .targetedFigure(null)
                                        .numberOfTurn(numberOfTurn)
                                        .figureToDestinationField(castlingMap)
                                        .build();
                }
            }else {
                if (!Board.getFieldsUnderBlackInfluence().contains(b1) && !Board.getFieldsUnderBlackInfluence().contains(c1) &&
                        !Board.getFieldsUnderBlackInfluence().contains(d1) && Board.getFieldToFigure().get(b1) == null &&
                        Board.getFieldToFigure().get(c1) == null && Board.getFieldToFigure().get(d1) == null){
                    castlingMap.put(king, c1);
                    castlingMap.put(rock, d1);
                    longCastling = new Turn.Builder()
                                        .eating(false)
                                        .writtenStyle("0-0-0")
                                        .targetedFigure(null)
                                        .numberOfTurn(numberOfTurn)
                                        .figureToDestinationField(castlingMap)
                                        .build();
                }
            }
        }
        return longCastling;
    }

    private void makeTurn(Turn turn){
        //TODO make a turn actually
    }

    private void undo(Turn turn){
        //TODO undo a turn that made
    }
}