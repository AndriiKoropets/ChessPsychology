package com.koropets_suhanov.chess.controller;

import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.utils.ProcessingUtils;
import com.koropets_suhanov.chess.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AndriiKoropets
 */
public class Process {

    private static final Logger LOG = LoggerFactory.getLogger(Process.class);
    private final static String REG_EX_TURN = "^(\\d+)\\.\\s*(\\S+)\\s*(\\S+)*$";
    private final static String REG_EX_SURNAMES = "";
    private final static String PATH_TO_FILE = "src/main/resources/parties/childsMat";
    private final static String PATH_TO_DIRECTORY = "src/main/resources/parties/";
    private static List<String> whiteTurns = new ArrayList<String>();
    private static List<String> blackTurns = new ArrayList<String>();
    static boolean run = true;
    private static int numberOfTurn;


    public static void main(String[] args) throws InterruptedException{
        Board board = Board.getInstance();
//       printAllPossibleTurns();
        printFigures();
//        printFile();
        System.out.println(Board.getTakenFields());
        System.out.println("White figures");
        for (Observer figure : board.getWhiteFigures()){
            Set<Field> set = ((Figure)figure).getPossibleFieldsToMove();
            if (figure.getClass() == Knight.class){
                for (Field field : ((Figure) figure).getAttackedFields()){
                    System.out.println(field + "   " + field.isTaken());
                }
            }
            System.out.println(figure.toString() + ", attacked fields = " + ((Figure)figure).getAttackedFields() + ", possible turns : " + set + "   aliens  = " + ((Figure)figure).getAliensProtectMe() + "   enemies = " + ((Figure)figure).getEnemiesAttackMe());
        }
        System.out.println("Black figures");
        for (Observer figure : board.getBlackFigures()){
            System.out.println(figure.toString() + ", attacked fields = " + ((Figure) figure).getAttackedFields() + ", possible turns : " + ((Figure)figure).getPossibleFieldsToMove()  + "   aliens  = " + ((Figure)figure).getAliensProtectMe() + "   enemies = " + ((Figure)figure).getEnemiesAttackMe());
        }

//        printFile();
    }

    public static void printFile(){
        Pattern pattern = Pattern.compile(REG_EX_TURN);
        try{
            File text = new File(PATH_TO_FILE);
            Scanner scnr = new Scanner(text);
            String sCurrentLine;
            while (scnr.hasNextLine()) {
                sCurrentLine = scnr.nextLine();
                Matcher matcher = pattern.matcher(sCurrentLine);
                if (matcher.matches()){
                    printAllPossibleTurns();
                    numberOfTurn = Integer.valueOf(matcher.group(1));
                    whiteTurns.add(matcher.group(2));
                    ProcessingUtils.getActualTurn(matcher.group(2), true, numberOfTurn);
                    System.out.println(sCurrentLine + " ==== after turn ==== " +  matcher.group(1));
                    printAllPossibleTurns();
                    printFigures();
                    currentStateOfTheBoard();

                    blackTurns.add(matcher.group(3));
                    if (matcher.group(2) != null){
                        ProcessingUtils.getActualTurn(matcher.group(3), false, numberOfTurn);
                    }
                    printAllPossibleTurns();
                    System.out.println(sCurrentLine + " ==== after turn ====" + matcher.group(2));
                    printFigures();
                    currentStateOfTheBoard();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void estimateEachTurn(){

    }

    public static void printAllPossibleTurns(){
        Game game = new Game();
        game.setPossibleTurnsAndKillings(Color.WHITE);
        System.out.println(game.getPossibleTurnsAndKillings().size());
        System.out.println(game.getPossibleTurnsAndKillings());
        game.setPossibleTurnsAndKillings(Color.BLACK);
        System.out.println(game.getPossibleTurnsAndKillings().size());
        System.out.println(game.getPossibleTurnsAndKillings());
    }

    private static void currentStateOfTheBoard(){
        System.out.println("White figures");
        for (Observer observer : Board.getWhiteFigures()){
            Figure currentFigure = (Figure) observer;
            System.out.println(currentFigure.toString() + currentFigure.getPossibleFieldsToMove() + currentFigure.getFieldsUnderMyInfluence() + currentFigure.getWhoCouldBeKilled() + currentFigure.getAliensProtectMe());
        }
        System.out.println("Black figures");
        for (Observer observer : Board.getBlackFigures()){
            Figure currentFigure = (Figure) observer;
            System.out.println(currentFigure.toString() + currentFigure.getPossibleFieldsToMove() + currentFigure.getFieldsUnderMyInfluence() + currentFigure.getWhoCouldBeKilled() + currentFigure.getAliensProtectMe());
        }
    }

    private static void printFigures(){
        System.out.println();
        int counter = 1;
        for (int i = 0; i < Board.SIZE; i++){
            System.out.print(Field.getVertical().get(i) + "  ");
            for (int j = 0; j < Board.SIZE; j++){
                Field currentPoint = new Field(i, j);
                if (currentPoint.isTaken()){
                    System.out.print(" " + print(Board.getFieldToFigure().get(currentPoint)) + " ");
                }else {
                    System.out.print("   ");
                }
            }
            if (counter == Board.SIZE){
                System.out.println();
                System.out.println();
                System.out.print("    ");
                for (int k = 0; k < Board.SIZE; k++){
                    System.out.print(Field.getHorizontal().get(k) + "  ");
                }
            }
            counter++;
            System.out.println();
        }
    }

    private static String print(Figure figure){
        if (figure.getClass() == King.class){
            if (figure.getColor() == Color.WHITE){
                return "K";
            }else {
                return "k";
            }
        }
        if (figure.getClass() == Queen.class){
            if (figure.getColor() == Color.WHITE){
                return "Q";
            }else {
                return "q";
            }
        }
        if (figure.getClass() == Rock.class){
            if (figure.getColor() == Color.WHITE){
                return "R";
            }else {
                return "r";
            }
        }
        if (figure.getClass() == Knight.class){
            if (figure.getColor() == Color.WHITE){
                return "N";
            }else {
                return "n";
            }
        }
        if (figure.getClass() == Bishop.class){
            if (figure.getColor() == Color.WHITE){
                return "B";
            }else {
                return "b";
            }
        }
        if (figure.getClass() == Pawn.class){
            if (figure.getColor() == Color.WHITE){
                return "P";
            }else {
                return "p";
            }
        }
        return null;
    }
}