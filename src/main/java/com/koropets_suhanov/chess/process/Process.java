package com.koropets_suhanov.chess.process;

import com.koropets_suhanov.chess.model.Observer;
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
import com.koropets_suhanov.chess.utils.Turn;
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
    private final static String PATH_TO_FILE = "src/main/resources/parties/childsMat";
    private final static String PATH_TO_DIRECTORY = "src/main/resources/parties/";
    static boolean run = true;
    private static final Board board = Board.getInstance();
    private static final Pattern pattern = Pattern.compile("^(\\d+)\\.\\s*(\\S+)\\s*(\\S+)*$");
    private static Game game = new Game();
    private static Parameter whiteEstimationWholeParty;
    private static Parameter blackEstimationWholeParty;

    public static void main(String[] args){
        process();
    }

    private static void process(){
        LOG.info("Process is starting");
        printFigures();
        try{
            File text = new File(PATH_TO_FILE);
            Scanner scnr = new Scanner(text);
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
                    whitePossibleTurns = game.getPossibleTurnsAndKillings(Color.WHITE);
                    EstimatePosition.makeTurn(whiteTurn);
                    whiteEstimationWholeParty = EstimatePosition.estimate(whiteTurn, whitePossibleTurns, Color.WHITE);
                    if (writtenBlackTurn != null){
                        Turn blackTurn = ProcessingUtils.getActualTurn(writtenBlackTurn, false, numberOfTurn);
                        blackPossibleTurns = game.getPossibleTurnsAndKillings(Color.BLACK);
                        EstimatePosition.makeTurn(blackTurn);
                        blackEstimationWholeParty = EstimatePosition.estimate(blackTurn, blackPossibleTurns, Color.BLACK);
                    }
                    printFigures();
//                    currentStateOfTheBoard();
                }
            }
            System.out.println("White estimation = " + whiteEstimationWholeParty);
            System.out.println("Black estimation = " + blackEstimationWholeParty);
        } catch (IOException e) {
            LOG.info("Error during processing file ", e);
        }
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

    private void updateEstimationParameter(Parameter parameter, Color color){
        Parameter globalEstimation = (color == Color.BLACK) ? blackEstimationWholeParty : whiteEstimationWholeParty;
        globalEstimation = new Parameter.Builder().first(globalEstimation.getFirstAttackEnemy() + parameter.getFirstAttackEnemy())
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