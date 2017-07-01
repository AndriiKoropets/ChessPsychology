package com.koropets_suhanov.chess.controller;

import com.koropets_suhanov.chess.model.*;

import java.util.*;

/**
 * @author AndriiKoropets
 */
public class Game {

    private Set<StringBuilder> possibleTurnsAndKillings = new LinkedHashSet<StringBuilder>();


    public Set<StringBuilder> getPossibleTurnsAndKillings() {
        return possibleTurnsAndKillings;
    }

    public void setPossibleTurnsAndKillings(Color color){
        possibleTurnsAndKillings.clear();
        King king = null;
        List kings = Board.getInstance().getFiguresByClass(King.class);
        for (int i = 0; i < kings.size(); i++){
            if (((King)kings.get(0)).getColor() == color){
                king = (King)kings.get(0);
            }else {
                king = (King)kings.get(1);
            }
        }
        if (king.isUnderAttack()){
            if (king.getPossibleFieldsToMove().isEmpty()){
                for (Figure enemy : king.getWhoCouldBeKilled()){
                    if (enemy.getAliensProtectMe().size() >= 1){
                        //TODO other alien figures could protect me
                        System.out.println("Mat");
                        return;
                    }
                }
            }
            StringBuilder turn = new StringBuilder(king.toString());
            for (Object field : king.getPossibleFieldsToMove()){
                turn.append("-").append(field.toString());
                possibleTurnsAndKillings.add(turn);
            }
            for (Object figure : king.getWhoCouldBeKilled()){
                turn.append("x").append(((Figure)figure).getField());
                possibleTurnsAndKillings.add(turn);
            }
        }else {
            Set figures;
            if (color == Color.BLACK){
                figures = Board.getInstance().getBlackFigures();
            }else {
                figures = Board.getInstance().getWhiteFigures();
            }
            StringBuilder turn;
            for (Object figure : figures){
//                Set<Turn> turns = new LinkedHashSet<Turn>();
                for (Object field : ((Figure)figure).getPossibleFieldsToMove()){
                    turn = new StringBuilder(figure.toString());
                    turn.append("-").append(field.toString());
                    possibleTurnsAndKillings.add(turn);
                }
                for (Object attackedFigure : ((Figure) figure).getWhoCouldBeKilled()){
                    turn = new StringBuilder(figure.toString());
                    turn.append("x").append(((Figure)attackedFigure).getField().toString());
                    possibleTurnsAndKillings.add(turn);
                }
            }
            List<String> castles = castling(color);
            for (String castle : castles){
                StringBuilder castleTurn = new StringBuilder(castle);
                possibleTurnsAndKillings.add(castleTurn);
            }
            List<StringBuilder> turnsOnTheEndLine = pawnReachesEndLine(color);
            for (StringBuilder stringBuilder : turnsOnTheEndLine){
                possibleTurnsAndKillings.add(stringBuilder);
            }
        }
    }

    public List<StringBuilder> pawnReachesEndLine(Color color){
        Set figures;
        boolean isBlack;
        List<StringBuilder> turns = new ArrayList<StringBuilder>();
        if (color == Color.BLACK){
            figures = Board.getInstance().getBlackFigures();
            isBlack = true;
        }else {
            figures = Board.getInstance().getWhiteFigures();
            isBlack = false;
        }
        for (Object figure : figures){
            if (figure.getClass() == Pawn.class && ((Figure)figure).getColor() == color){
                Pawn pawn = (Pawn) figure;
                if (isBlack){
                    if (pawn.getField().getX() == 6){
                        for (Object field : pawn.getPossibleFieldsToMove()){
                            turnsIfReachedEndLine(pawn, (Field) field, false, turns);
                        }
                        for (Object field : pawn.getAttackedFields()){
                            turnsIfReachedEndLine(pawn, (Field) field, true, turns);
                        }
                    }
                }else {
                    if (pawn.getField().getX() == 1){
                        for (Object field : pawn.getPossibleFieldsToMove()){
                            turnsIfReachedEndLine(pawn, (Field) field, false,turns);
                        }
                        for (Object field : pawn.getAttackedFields()){
                            turnsIfReachedEndLine(pawn, (Field) field, true, turns);
                        }
                    }
                }
            }
        }
        return turns;
    }

    private void turnsIfReachedEndLine(Pawn pawn, Field aimedField, boolean isKilling, List<StringBuilder> storage){
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

    public static List<String> castling(Color color){
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

    private void makeTurn(){
        //TODO make a turn actually
    }

    private void undo(){
        //TODO undo a turn that made
    }
}