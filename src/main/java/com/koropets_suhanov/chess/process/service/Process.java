package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.process.dto.FinalResult;
import com.koropets_suhanov.chess.process.dto.Parameter;
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
import com.koropets_suhanov.chess.process.dto.Turn;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;

@Slf4j
public class Process {

    //    private final static String PATH_TO_FILE = "src/main/resources/parties/enPassantBlack.txt";
//    private static final String PATH_TO_FILE = "src/main/resources/parties/tetsPartyPawn.txt";
//    private final static String PATH_TO_FILE = "src/main/resources/parties/enPassantWhite.txt";
    private final static String PATH_TO_FILE = "src/main/resources/parties/hou.txt";
    //1, 2, 3, 4, 5, 8, 9, 10, 11, 12, 14, 15, 16, 17 19, 20 21 are processed properly
    //Two figures could eat at the same time the same enemy:1, 10, 12, 17 - are processed properly
    //Transformation : 6, 7, 18
    //The party is wrong written 13
    private final static String PATH_TO_DIRECTORY = "src/main/resources/parties/";

    private static CurrentPosition game = new CurrentPosition();
    public static PositionInfluence positionInfluence = new PositionInfluence();
    private static EstimatePosition estimatePosition = new EstimatePosition();
    public static Color currentColor;
    public static String currentWrittenStyleTurn;
    public static int currentTurnNumber;

    private static final Pattern pattern = Pattern.compile("^(\\d+)\\.\\s*(\\S+)\\s*(\\S+)*$");
    private static Parameter whiteEstimationWholeParty;
    private static Parameter blackEstimationWholeParty;

    static FinalResult fullWhiteEstimation;
    static FinalResult fullBlackEstimation;

    public static final Board board = Board.getInstance();

    public void runProcess(){
        log.info("Process is starting");
        System.out.println("Process started");
        whiteEstimationWholeParty = Parameter.builder().build();
        blackEstimationWholeParty = Parameter.builder().build();
        fullWhiteEstimation = FinalResult.builder().build();
        fullBlackEstimation = FinalResult.builder().build();
        System.out.println("Board = " + board);
        printAllBoard();
        File file = null;
        try {
            file = new File(PATH_TO_FILE);
            Scanner scnr = new Scanner(file);
            String sCurrentLine;
            Set<Turn> whitePossibleTurns;
            Set<Turn> blackPossibleTurns;
            while (scnr.hasNextLine()) {
                sCurrentLine = scnr.nextLine();
                Matcher matcher = pattern.matcher(sCurrentLine);
                if (matcher.matches()) {
                    currentTurnNumber = Integer.valueOf(matcher.group(1));
                    String writtenWhiteTurn = matcher.group(2);
                    String writtenBlackTurn = matcher.group(3);
                    currentColor = Color.WHITE;
                    currentWrittenStyleTurn = writtenWhiteTurn;
                    Turn whiteTurn = ParseWrittenTurn.getActualTurn();
//                    System.out.println("White turn = " + whiteTurn);
                    whitePossibleTurns = game.getAllPossibleTurns();
//                    printAllPossibleTurns(whitePossibleTurns);
                    //TODO write logic which gets rid of makeTurn. It should be monolithic. Whole estimation could be defined in EstimatePosition class.
                    board.setCurrentTurn(whiteTurn);
                    positionInfluence.makeTurn(whiteTurn);
//                    System.out.println("After turn = " + whiteTurn);
                    printAllBoard();
//                    currentStateOfAllFigures();
                    whiteEstimationWholeParty = estimatePosition.estimate(whiteTurn, whitePossibleTurns);

                    fullWhiteEstimation = countFullEstimation(whiteEstimationWholeParty, Color.WHITE);
                    if (writtenBlackTurn != null) {
                        currentColor = Color.BLACK;
                        currentWrittenStyleTurn = writtenBlackTurn;
                        Turn blackTurn = ParseWrittenTurn.getActualTurn();
//                        System.out.println("Black turn = " + blackTurn);
                        blackPossibleTurns = game.getAllPossibleTurns();
//                        printAllPossibleTurns(blackPossibleTurns);
                        board.setCurrentTurn(blackTurn);
                        positionInfluence.makeTurn(blackTurn);
                        printAllBoard();
//                        System.out.println("After turn = " + blackTurn);
                        System.out.println("Figures = " + Board.getFigures());
                        System.out.println("White figures = " + Board.getFiguresByColor(Color.WHITE));
                        System.out.println("Black figures = " + Board.getFiguresByColor(Color.BLACK));
//                        System.out.println("Size = " + Board.getTakenFields().size() + "Taken fields = " + Board.getTakenFields());
                        blackEstimationWholeParty = estimatePosition.estimate(blackTurn, blackPossibleTurns);

                        fullBlackEstimation = countFullEstimation(blackEstimationWholeParty, Color.BLACK);
                    }
//                    printAllBoard();
//                    currentStateOfAllFigures();
                }
                System.out.println("White estimation = " + whiteEstimationWholeParty);
                System.out.println("Black estimation = " + blackEstimationWholeParty);
                System.out.println("White figures = " + Board.getFiguresByColor(Color.WHITE));
                System.out.println("Black figures = " + Board.getFiguresByColor(Color.BLACK));
            }
            printAllBoard();
//            System.out.println("White estimation = " + whiteEstimationWholeParty);
//            System.out.println("Black estimation = " + blackEstimationWholeParty);
            System.out.println("Full estimation");
            System.out.println("White = " + fullWhiteEstimation);
            System.out.println("Black = " + fullBlackEstimation);
        } catch (IOException e) {
            log.info("File {} was not found", file);
            throw new RuntimeException();
        }
    }

    private static void currentStateOfAllFigures() {
        System.out.println("White figures");
        for (Observer observer : Board.getFiguresByColor(Color.WHITE)) {
            Figure currentFigure = (Figure) observer;
            printInfoAboutFigure(currentFigure);
        }
        System.out.println("Black figures");
        for (Observer observer : Board.getFiguresByColor(Color.BLACK)) {
            Figure currentFigure = (Figure) observer;
            printInfoAboutFigure(currentFigure);
        }
    }

    private static void printAllPossibleTurns(Set<Turn> allPossibleTurns) {
        System.out.println("Size = " + allPossibleTurns.size());
        for (Turn possibleTurn : allPossibleTurns) {
            System.out.println("Turn = " + possibleTurn.getFigureToDestinationField());
        }
    }

    private static void printInfoAboutFigure(Figure currentFigure) {
        System.out.println(currentFigure);
//        System.out.println("Possible fields to move = " + currentFigure.getPossibleFieldsToMove());
        System.out.println("Who could be eaten previous state = " + currentFigure.getWhoCouldBeEatenPreviousState());
        System.out.println("Who could be eaten now = " + currentFigure.getWhoCouldBeEaten());
        System.out.println("Get allies I protect = " + currentFigure.getAlliesIProtect());
        System.out.println("Get allies protect me = " + currentFigure.getAlliesProtectMe());
        System.out.println("Get figures attack me = " + currentFigure.getEnemiesAttackMe());
    }

    public static void printAllBoard() {
        System.out.println();
        int counter = 1;
        for (int i = 0; i < SIZE; i++) {
            System.out.print(Field.getVertical().get(i) + "  ");
            for (int j = 0; j < SIZE; j++) {
                Field currentPoint = new Field(i, j);
                if (currentPoint.isTaken()) {
                    System.out.print(" " + printFigure(Board.getFieldToFigure().get(currentPoint)) + " ");
                } else {
                    System.out.print("   ");
                }
            }
            if (counter == SIZE) {
                System.out.println();
                System.out.println();
                System.out.print("    ");
                for (int k = 0; k < SIZE; k++) {
                    System.out.print(Field.getHorizontal().get(k) + "  ");
                }
            }
            counter++;
            System.out.println();
        }
    }

    private static String printFigure(Figure figure) {
        if (figure.getClass() == Pawn.class) {
            return figure.getColor() == Color.WHITE ? "P" : "p";
        }
        if (figure.getClass() == Rock.class) {
            return figure.getColor() == Color.WHITE ? "R" : "r";
        }
        if (figure.getClass() == Knight.class) {
            return figure.getColor() == Color.WHITE ? "N" : "n";
        }
        if (figure.getClass() == Bishop.class) {
            return figure.getColor() == Color.WHITE ? "B" : "b";
        }
        if (figure.getClass() == King.class) {
            return figure.getColor() == Color.WHITE ? "K" : "k";
        }
        if (figure.getClass() == Queen.class) {
            return figure.getColor() == Color.WHITE ? "Q" : "q";
        }
        return null;
    }

    private static FinalResult countFullEstimation(Parameter parameter, Color color) {
        FinalResult globalEstimation = (color == Color.BLACK) ? fullBlackEstimation : fullWhiteEstimation;
        return FinalResult.builder().first(globalEstimation.getFirst() + parameter.getFirstAttackEnemy())
                .second(globalEstimation.getSecond() + parameter.getSecondBeUnderAttack())
                .third(globalEstimation.getThird() + parameter.getThirdWithdrawAttackOnEnemy())
                .fourth(globalEstimation.getFourth() + parameter.getFourthWithdrawAttackOnMe())
                .fifth(globalEstimation.getFifth() + parameter.getFifthDontTakeAChanceToAttack().getWeight())
                .sixth(globalEstimation.getSixth() + parameter.getSixthDontTakeAChanceToBeUnderAttack().getWeight())
                .seventh(globalEstimation.getSeventh() + parameter.getSeventhDontTakeAChanceToWithdrawAttackOnEnemy().getWeight())
                .eighth(globalEstimation.getEighth() + parameter.getEighthDontTakeAChanceToWithdrawAttackOnMe().getWeight())
                .build();
    }
}