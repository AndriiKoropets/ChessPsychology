package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.utils.Turn;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * @author AndriiKoropets
 */
public class Game {

    private Set<Turn> possibleTurnsAndKillings = new LinkedHashSet<Turn>();
    private int numberOfTurn;

    public Set<Turn> getPossibleTurnsAndKillings(Color color, int numberOfTurn) {
        this.numberOfTurn = numberOfTurn;
        setPossibleTurnsAndKillings(color);
        return possibleTurnsAndKillings;
    }

    private void setPossibleTurnsAndKillings(Color color){
        possibleTurnsAndKillings.clear();
        King king = null;
        Set<Observer> figures = (color == Color.BLACK) ? Board.getBlackFigures() : Board.getWhiteFigures();
        for (Observer figure : figures){
            if (figure.getClass() == King.class){
                king = (King) figure;
                break;
            }
        }
        if (king.isUnderAttack()){
            if (king.getPossibleFieldsToMove().isEmpty()){
                Map<Figure, Field> kingMap = new HashMap<>();
                for (Figure enemy : king.getWhoCouldBeKilled()){
                    if (enemy.getAliensProtectMe().size() == 0){
                        kingMap.put(king, enemy.getField());
                        possibleTurnsAndKillings.add(new Turn.Builder().figureToDestinationField(kingMap)
                                                                        .killing(true)
                                                                        .numberOfTurn(numberOfTurn)
                                                                        .writtenStyle("")
                                                                        .build());
                    }
                }
                //TODO add case when other figures could kill enemy which attacks king or cover king
            }
            for (Field field : king.getPossibleFieldsToMove()){
                Map<Figure, Field> figureToFieldMap = new HashMap<>();
                figureToFieldMap.put(king, field);
                possibleTurnsAndKillings.add(new Turn.Builder().figureToDestinationField(figureToFieldMap)
                                                                .killing(false)
                                                                .writtenStyle("")
                                                                .numberOfTurn(numberOfTurn)
                                                                .build());
            }
            for (Figure enemy : king.getWhoCouldBeKilled()){
                Map<Figure, Field> figureToFieldMap = new HashMap<>();
                figureToFieldMap.put(king, enemy.getField());
                possibleTurnsAndKillings.add(new Turn.Builder().figureToDestinationField(figureToFieldMap)
                        .killing(true)
                        .writtenStyle("")
                        .numberOfTurn(numberOfTurn)
                        .build());
            }
        }else {
            for (Observer figure : figures){
                for (Field field : ((Figure)figure).getPossibleFieldsToMove()){
                    Map<Figure, Field> figureFieldMap = new HashMap<>();
                    figureFieldMap.put((Figure)figure, field);
                    possibleTurnsAndKillings.add(new Turn.Builder().figureToDestinationField(figureFieldMap)
                                                                    .killing(false)
                                                                    .numberOfTurn(numberOfTurn)
                                                                    .writtenStyle("")
                                                                    .build());
                }
                for (Figure attackedFigure : ((Figure) figure).getWhoCouldBeKilled()){
                    Map<Figure, Field> figureFieldMap = new HashMap<>();
                    figureFieldMap.put((Figure)figure, attackedFigure.getField());
                    possibleTurnsAndKillings.add(new Turn.Builder().figureToDestinationField(figureFieldMap)
                            .killing(true)
                            .numberOfTurn(numberOfTurn)
                            .writtenStyle("")
                            .build());
                }
            }
            possibleTurnsAndKillings.add(new Turn.Builder().figureToDestinationField(castling(color))
                                                            .killing(false)
                                                            .writtenStyle("")
                                                            .numberOfTurn(numberOfTurn)
                                                            .build());
            List<StringBuilder> turnsOnTheEndLine = pawnReachesEndLine(color);
            for (StringBuilder stringBuilder : turnsOnTheEndLine){
                possibleTurnsAndKillings.add(stringBuilder.toString());
            }
        }
    }

    private Map<Figure, Field> pawnReachesEndLine(Color color){
        Set<Observer> figures = (color == Color.BLACK) ? Board.getBlackFigures() : Board.getWhiteFigures();
        Map<Figure, Field> pawnAndField = new HashMap<>();
        for (Observer figure : figures){
            if (figure.getClass() == Pawn.class){
                Pawn pawn = (Pawn) figure;
                if (color == Color.BLACK){
                    if (pawn.getField().getX() == 6){
                        for (Field field : pawn.getPossibleFieldsToMove()){
                            pawnAndField.put(pawn, field);
                            turnsIfReachedEndLine(false, pawnAndField);
                        }
                        for (Field field : pawn.getAttackedFields()){
                            pawnAndField.put()
                            turnsIfReachedEndLine(pawn, (Field) field, true, pawnAndField);
                        }
                    }
                }else {
                    if (pawn.getField().getX() == 1){
                        for (Object field : pawn.getPossibleFieldsToMove()){
                            turnsIfReachedEndLine(pawn, (Field) field, false,pawnAndField);
                        }
                        for (Object field : pawn.getAttackedFields()){
                            turnsIfReachedEndLine(pawn, (Field) field, true, pawnAndField);
                        }
                    }
                }
            }
        }
        return pawnAndField;
    }

    private Set<Turn> turnsIfReachedEndLine(boolean isKilling, Map<Figure, Field> storage){
        if (isKilling){
            StringBuilder turn1 = new StringBuilder(pawn.getField().toString());
            turn1.append("-").append(aimedField.toString()).append("(").append("Q").append(")");
            storage.add(turn1);
            StringBuilder turn2 = new StringBuilder(pawn.getField().toString());
            turn2.append("-").append(aimedField.toString()).append("(").append("R").append(")");
            storage.add(turn2);
            StringBuilder turn3 = new StringBuilder(pawn.getField().toString());
            turn3.append("-").append(aimedField.toString()).append("(").append("K").append(")");
            storage.add(turn3);
            StringBuilder turn4 = new StringBuilder(pawn.getField().toString());
            turn4.append("-").append(aimedField.toString()).append("(").append("B").append(")");
            storage.add(turn4);
        }else {
            StringBuilder turn1 = new StringBuilder(pawn.getField().toString());
            turn1.append("x").append(aimedField.toString()).append("(").append("Q").append(")");
            storage.add(turn1);
            StringBuilder turn2 = new StringBuilder(pawn.getField().toString());
            turn2.append("x").append(aimedField.toString()).append("(").append("R").append(")");
            storage.add(turn2);
            StringBuilder turn3 = new StringBuilder(pawn.getField().toString());
            turn3.append("x").append(aimedField.toString()).append("(").append("K").append(")");
            storage.add(turn3);
            StringBuilder turn4 = new StringBuilder(pawn.getField().toString());
            turn4.append("x").append(aimedField.toString()).append("(").append("B").append(")");
            storage.add(turn4);
        }
    }

    private static Map<Figure, Field> castling(Color color){
        Board board = Board.getInstance();
        List<String> list = new ArrayList<String>();
        List<Figure> rocks = Board.getInstance().getFiguresByClass(Rock.class);
        Set figures;
        if (color == Color.BLACK){
            figures = board.getBlackFigures();
        }else {
            figures = board.getWhiteFigures();
        }
        for (Object figure : figures){
            if (figure.getClass() == King.class){
                if (((King) figure).getColor() == Color.WHITE){
                    King king = (King) figure;
                    if (!king.isUnderAttack() && king.isOpportunityToCastling()){
                        for (Figure rock : rocks){
                            if (rock.getColor() == Color.WHITE){
                                if (rock.getField().equals(new Field(7, 0)) && ((Rock) rock).isOpportunityToCastling()){
                                    if (!new Field(7, 1).isUnderInfluence(Color.BLACK) && !new Field(7, 2).isUnderInfluence(Color.BLACK) &&
                                            !new Field(7, 3).isUnderInfluence(Color.BLACK) && !new Field(7, 1).isTaken() && !new Field(7, 2).isTaken() &&
                                            !new Field(7, 3).isTaken()){
                                        list.add("0-0-0");
                                    }
                                }
                                if (rock.getField().equals(new Field(7, 7)) && ((Rock) rock).isOpportunityToCastling()){
                                    if (!new Field(7, 6).isUnderInfluence(Color.BLACK) && !new Field(7, 5).isUnderInfluence(Color.BLACK) &&
                                            !new Field(7, 6).isTaken() && !new Field(7, 5).isTaken()){
                                        list.add("0-0");
                                    }
                                }
                            }
                        }
                    }
                    return list;
                }else {
                    King king = (King) figure;
                    if (!king.isUnderAttack() && king.isOpportunityToCastling()){
                        for (Figure rock : rocks){
                            if (rock.getColor() == Color.BLACK){
                                if (rock.getField().equals(new Field(0, 0)) && ((Rock) rock).isOpportunityToCastling()){
                                    if (!new Field(0, 1).isUnderInfluence(Color.WHITE) && !new Field(0, 2).isUnderInfluence(Color.WHITE) &&
                                            !new Field(0, 3).isUnderInfluence(Color.WHITE) && !new Field(0, 1).isTaken() && !new Field(0, 2).isTaken() &&
                                            !new Field(0, 3).isTaken()){
                                        list.add("0-0-0");
                                    }
                                }
                                if (rock.getField().equals(new Field(0, 7)) && ((Rock) rock).isOpportunityToCastling()){
                                    if (!new Field(0, 6).isUnderInfluence(Color.WHITE) && !new Field(0, 5).isUnderInfluence(Color.WHITE) &&
                                            !new Field(0, 6).isTaken() && !new Field(0, 5).isTaken()){
                                        list.add("0-0");
                                    }
                                }
                            }
                        }
                    }
                    return list;
                }
            }
        }
        return null;
    }

    private void makeTurn(Turn turn){
        //TODO make a turn actually
    }

    private void undo(Turn turn){
        //TODO undo a turn that made
    }
}