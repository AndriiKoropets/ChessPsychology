package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.process.pojo.Parameter;
import com.koropets_suhanov.chess.utils.ProcessingUtils;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Knight;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.process.pojo.Turn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

/**
 * @author AndriiKoropets
 */
public class Process {

    private static final Logger LOG = LoggerFactory.getLogger(Process.class);
    private final static String PATH_TO_FILE = "src/main/resources/parties/testParty.txt";
//    private final static String PATH_TO_FILE = "src/main/resources/parties/testFirstParameter.txt";
    private final static String PATH_TO_DIRECTORY = "src/main/resources/parties/";
    public static final Board BOARD = Board.getInstance();
    private static final Pattern pattern = Pattern.compile("^(\\d+)\\.\\s*(\\S+)\\s*(\\S+)*$");
    private static Game game = new Game();
    private static Parameter whiteEstimationWholeParty;
    private static Parameter blackEstimationWholeParty;

    static Parameter fullWhiteEstimation;
    static Parameter fullBlackEstimation;

    public static void main(String[] args){
        process();
    }

    private static void process(){
        LOG.info("Process is starting");
        whiteEstimationWholeParty = new Parameter.Builder().build();
        blackEstimationWholeParty = new Parameter.Builder().build();
        fullWhiteEstimation = new Parameter.Builder().build();
        fullBlackEstimation = new Parameter.Builder().build();
        printAllBoard();
        File file = null;
        try{
            file = new File(PATH_TO_FILE);
            Scanner scnr = new Scanner(file);
            String sCurrentLine;
            Set<Turn> whitePossibleTurns;
            Set<Turn> blackPossibleTurns;
            while (scnr.hasNextLine()) {
                sCurrentLine = scnr.nextLine();
                Matcher matcher = pattern.matcher(sCurrentLine);
                if (matcher.matches()){
                    int numberOfTurn = Integer.valueOf(matcher.group(1));
                    String writtenWhiteTurn = matcher.group(2);
                    String writtenBlackTurn = matcher.group(3);
                    Turn whiteTurn = ProcessingUtils.getActualTurn(writtenWhiteTurn, true, numberOfTurn);
                    System.out.println("White turn = " + whiteTurn);
                    whitePossibleTurns = game.getPossibleTurnsAndEatings(Color.WHITE, numberOfTurn);
                    //TODO write logic which gets rid of makeTurn. It should be monolithic. Whole estimation could be defined in EstimatePosition class.
                    ProcessingUtils.makeTurn(whiteTurn);
                    System.out.println("After turn = " + whiteTurn);
                    printAllBoard();
//                    currentStateOfAllFigures();
                    whiteEstimationWholeParty = EstimatePosition.estimate(whiteTurn, whitePossibleTurns, Color.WHITE);
                    fullWhiteEstimation = countFullEstimation(whiteEstimationWholeParty, Color.WHITE);
                    ProcessingUtils.makeTurn(whiteTurn);
                    if (writtenBlackTurn != null){
                        Turn blackTurn = ProcessingUtils.getActualTurn(writtenBlackTurn, false, numberOfTurn);
                        System.out.println("Black turn = " + blackTurn);
                        blackPossibleTurns = game.getPossibleTurnsAndEatings(Color.BLACK, numberOfTurn);
                        ProcessingUtils.makeTurn(blackTurn);
                        System.out.println("After turn = " + blackTurn);
                        printAllBoard();
//                        currentStateOfAllFigures();
                        blackEstimationWholeParty = EstimatePosition.estimate(blackTurn, blackPossibleTurns, Color.BLACK);
                        fullBlackEstimation = countFullEstimation(blackEstimationWholeParty, Color.BLACK);
                        ProcessingUtils.makeTurn(blackTurn);
                    }
//                    printAllBoard();
//                    currentStateOfAllFigures();
                }
                System.out.println("White estimation = " + whiteEstimationWholeParty);
                System.out.println("Black estimation = " + blackEstimationWholeParty);
                System.out.println("White figures = " + Board.getFigures(Color.WHITE));
                System.out.println("Black figures = " + Board.getFigures(Color.BLACK));
            }
//            System.out.println("White estimation = " + whiteEstimationWholeParty);
//            System.out.println("Black estimation = " + blackEstimationWholeParty);
            System.out.println("Full estimation");
            System.out.println("White = " + fullWhiteEstimation);
            System.out.println("Black = " + fullBlackEstimation);
        } catch (IOException e) {
            LOG.info("File {} was not found", file);
            throw new RuntimeException();
        }
    }

    private static void currentStateOfAllFigures(){
        System.out.println("White figures");
        for (Observer observer : Board.getFigures(Color.WHITE)){
            Figure currentFigure = (Figure) observer;
            printInfoAboutFigure(currentFigure);
        }
        System.out.println("Black figures");
        for (Observer observer : Board.getFigures(Color.BLACK)){
            Figure currentFigure = (Figure) observer;
            printInfoAboutFigure(currentFigure);
        }
    }

    private static void printInfoAboutFigure(Figure currentFigure){
        System.out.println(currentFigure);
//        System.out.println("Possible fields to move = " + currentFigure.getPossibleFieldsToMove());
        System.out.println("Who could be eaten previous state = " + currentFigure.getWhoCouldBeEatenPreviousState());
        System.out.println("Who could be eaten now = " + currentFigure.getWhoCouldBeEaten());
        System.out.println("Get allies I protect = " + currentFigure.getAlliesIProtect());
        System.out.println("Get allies protect me = " + currentFigure.getAlliesProtectMe());
        System.out.println("Get figures attack me = " + currentFigure.getEnemiesAttackMe());
    }

    private static void printAllBoard(){
        System.out.println();
        int counter = 1;
        for (int i = 0; i < Board.SIZE; i++){
            System.out.print(Field.getVertical().get(i) + "  ");
            for (int j = 0; j < Board.SIZE; j++){
                Field currentPoint = new Field(i, j);
                if (currentPoint.isTaken()){
                    System.out.print(" " + printFigure(Board.getFieldToFigure().get(currentPoint)) + " ");
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

    private static String printFigure(Figure figure){
        if (figure.getClass() == Pawn.class){
            return figure.getColor() == Color.WHITE ? "P" : "p";
        }
        if (figure.getClass() == Rock.class){
            return figure.getColor() == Color.WHITE ? "R" : "r";
        }
        if (figure.getClass() == Knight.class){
            return figure.getColor() == Color.WHITE ? "N" : "n";
        }
        if (figure.getClass() == Bishop.class){
            return figure.getColor() == Color.WHITE ? "B" : "b";
        }
        if (figure.getClass() == King.class){
            return figure.getColor() == Color.WHITE ? "K" : "k";
        }
        if (figure.getClass() == Queen.class){
            return figure.getColor() == Color.WHITE ? "Q" : "q";
        }
        return null;
    }

    private static Parameter countFullEstimation(Parameter parameter, Color color){
        Parameter globalEstimation = (color == Color.BLACK) ? fullBlackEstimation : fullWhiteEstimation;
        return new Parameter.Builder().first(globalEstimation.getFirstAttackEnemy() + parameter.getFirstAttackEnemy())
                .second(globalEstimation.getSecondBeUnderAttack() + parameter.getSecondBeUnderAttack())
                .third(globalEstimation.getThirdWithdrawAttackOnEnemy() + parameter.getThirdWithdrawAttackOnEnemy())
                .fourth(globalEstimation.getFourthWithdrawAttackOnMe() + parameter.getFourthWithdrawAttackOnMe())
                .fifth(globalEstimation.getFifthDontTakeAChanceToAttack() + parameter.getFifthDontTakeAChanceToAttack())
                .sixth(globalEstimation.getSixthDontTakeAChanceToBeUnderAttack() + parameter.getSixthDontTakeAChanceToBeUnderAttack())
                .seventh(globalEstimation.getSeventhDontTakeAChanceToWithdrawAttackOnEnemy() + parameter.getSeventhDontTakeAChanceToWithdrawAttackOnEnemy())
                .eighth(globalEstimation.getEighthDontTakeAChanceToWithdrawAttackOnMe() + parameter.getEighthDontTakeAChanceToWithdrawAttackOnMe())
                .build();
    }
}