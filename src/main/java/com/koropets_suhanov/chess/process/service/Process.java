package com.koropets_suhanov.chess.process.service;

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
import java.io.FileNotFoundException;
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
//    private final static String PATH_TO_FILE = "src/main/resources/parties/transformation.txt";
  private final static String PATH_TO_FILE = "src/main/resources/parties/transformation_eat.txt";
  //  private final static String PATH_TO_FILE = "src/main/resources/parties/hou.txt";
  //1, 2, 3, 4, 5, 8, 9, 10, 11, 12, 14, 15, 16, 17 19, 20 21 are processed properly
  //Two figures could eat at the same time the same enemy:1, 10, 12, 17 - are processed properly
  //Transformation : 6, 7, 18
  //The party is wrong written 13
  private final static String PATH_TO_DIRECTORY = "src/main/resources/parties/";

  private static CurrentPosition game = new CurrentPosition();
  public static UpdatePositionOnTheBoard updatePositionOnTheBoard = new UpdatePositionOnTheBoard();
  private static EstimatePosition estimatePosition = new EstimatePosition();
  public static Color currentColor;
  public static String currentWrittenStyleTurn;
  public static int currentTurnNumber;
  public static Turn currentWhiteTurn;
  public static Turn currentBlackTurn;
  public static Turn previousWhiteTurn;
  public static Turn previousBlackTurn;

  private static final Pattern pattern = Pattern.compile("^(\\d+)\\.\\s*(\\S+)\\s*(\\S+)*$");
  private static Parameter whiteEstimationWholeParty;
  private static Parameter blackEstimationWholeParty;
  private static Set<Turn> whitePossibleTurns;
  private static Set<Turn> blackPossibleTurns;

  static FinalResult fullWhiteEstimation;
  static FinalResult fullBlackEstimation;

  public static final Board board = Board.getInstance();

  public void runProcess() throws FileNotFoundException {
    initialize();
    Scanner scnr = new Scanner(new File(PATH_TO_FILE));
    String sCurrentLine;
    while (scnr.hasNextLine()) {
      sCurrentLine = scnr.nextLine();
      Matcher matcher = pattern.matcher(sCurrentLine);
      if (matcher.matches()) {

        currentTurnNumber = Integer.valueOf(matcher.group(1));
        String writtenWhiteTurn = matcher.group(2);
        String writtenBlackTurn = matcher.group(3);

        processWhiteTurn(writtenWhiteTurn);
        printBoardInformation();

        if (writtenBlackTurn != null) {
          processBlackTurn(writtenBlackTurn);
          printBoardInformation();
        }
      }
    }
    printFinalInformation();
  }

  private void initialize() {
    whiteEstimationWholeParty = Parameter.builder().build();
    blackEstimationWholeParty = Parameter.builder().build();
    fullWhiteEstimation = FinalResult.builder().build();
    fullBlackEstimation = FinalResult.builder().build();
    printAllBoard();
  }

  private void processWhiteTurn(String writtenWhiteTurn) {
    currentColor = Color.WHITE;
    currentWrittenStyleTurn = writtenWhiteTurn;
    previousWhiteTurn = currentWhiteTurn;
    Turn whiteTurn = ParseWrittenTurn.getActualTurn();
    currentWhiteTurn = whiteTurn;
    whitePossibleTurns = game.getAllPossibleTurns();
//    printAllPossibleTurns(whitePossibleTurns);
    updatePositionOnTheBoard.makeTurn(whiteTurn);
    printAllBoard();
//    whiteEstimationWholeParty = estimatePosition.estimate(whiteTurn, whitePossibleTurns);
    fullWhiteEstimation = countFullEstimation(whiteEstimationWholeParty, Color.WHITE);
  }

  private void processBlackTurn(String writtenBlackTurn) {
    currentColor = Color.BLACK;
    currentWrittenStyleTurn = writtenBlackTurn;
    previousBlackTurn = currentBlackTurn;
    Turn blackTurn = ParseWrittenTurn.getActualTurn();
    currentBlackTurn = blackTurn;
    blackPossibleTurns = game.getAllPossibleTurns();
    updatePositionOnTheBoard.makeTurn(blackTurn);
    printAllBoard();
//    blackEstimationWholeParty = estimatePosition.estimate(blackTurn, blackPossibleTurns);
    fullBlackEstimation = countFullEstimation(blackEstimationWholeParty, Color.BLACK);
  }

  private void printBoardInformation() {
    System.out.println("Figures = " + Board.getFigures());
    System.out.println("White figures = " + Board.getFiguresByColor(Color.WHITE));
    System.out.println("Black figures = " + Board.getFiguresByColor(Color.BLACK));
    System.out.println("White estimation = " + whiteEstimationWholeParty);
    System.out.println("Black estimation = " + blackEstimationWholeParty);
    System.out.println("Turn number " + currentTurnNumber + " = " + currentWrittenStyleTurn + " has just been successfully processed");
  }

  private void printFinalInformation() {
    System.out.println("Final board position is below!");
    printAllBoard();
    System.out.println("Full estimation");
    System.out.println("White = " + fullWhiteEstimation);
    System.out.println("Black = " + fullBlackEstimation);
  }

  private void printAllPossibleTurns(Set<Turn> allPossibleTurns) {
    System.out.println("Size = " + allPossibleTurns.size());
    for (Turn possibleTurn : allPossibleTurns) {
      System.out.println("Turn = " + possibleTurn.getFigureToDestinationField());
    }
  }

  private void printAllBoard() {
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

  private String printFigure(Figure figure) {
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

  private FinalResult countFullEstimation(Parameter parameter, Color color) {
    FinalResult globalEstimation = (color == Color.BLACK) ? fullBlackEstimation : fullWhiteEstimation;
    return FinalResult.builder().first(globalEstimation.getFirst() + parameter.getFirstAttackEnemy())
        .second(globalEstimation.getSecond() + parameter.getSecondBeUnderAttack())
        .third(globalEstimation.getThird() + parameter.getThirdWithdrawAttackOnEnemy())
        .fourth(globalEstimation.getFourth() + parameter.getFourthWithdrawAttackOnMe())
//            .fifth(globalEstimation.getFifth() + parameter.getFifthDontTakeAChanceToAttack().getWeight())
//            .sixth(globalEstimation.getSixth() + parameter.getSixthDontTakeAChanceToBeUnderAttack().getWeight())
//            .seventh(globalEstimation.getSeventh() + parameter.getSeventhDontTakeAChanceToWithdrawAttackOnEnemy().getWeight())
//            .eighth(globalEstimation.getEighth() + parameter.getEighthDontTakeAChanceToWithdrawAttackOnMe().getWeight())
        .build();
  }
}