package utils;

import java.util.List;

/**
 * @author AndriiKoropets
 */
public class FigureTurn {

//    public static void main(String[] args) {
//        try {
//            List whiteTurns = new ArrayList();
//            List blackTurns = new ArrayList();
//            FileReader fileReader = new FileReader(PATH_TO_FILE);
//            BufferedReader bufferedInputStream = new BufferedReader(fileReader);
//            String nextLine = null;
//            while ((nextLine = bufferedInputStream.readLine()) != null){
//                Pattern pattern = Pattern.compile(REG_EX_TURNS);
//                Matcher matcher = pattern.matcher(nextLine);
//                if (matcher.find()){
//                    whiteTurns.add(matcher.group(1));
//                    blackTurns.add(matcher.group(2));
//                    numbersOfTurns++;
//                }
//            }
//            System.out.println("Number of turns: " + numbersOfTurns);
//            System.out.println("__________________WHITE___________________");
//            countTurns(whiteTurns);
//            System.out.println(whiteTurns);
//            System.out.println("_____________________BLACK___________________");
//            countTurns(blackTurns);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static final String PATH_TO_FILE = "/home/phoenix/Desktop/Proba1.txt";
    public static final String REG_EX_SURNAME = "";
    public static final String REG_EX_TURNS = "^\\d+\\.\\s(.*)\\s(.*)$";
    public static int numbersOfTurns;

    public static void countTurns(List givenList){
        int numberOfRocks = 0;
        int numberOfKnights = 0;
        int numberOfBishops = 0;
        int numberOfKings = 0;
        int numberOfQueens = 0;
        int numberOfPawns = 0;
        for (int i = 0; i < givenList.size(); i++){
            if (((String)givenList.get(i)).startsWith("R")){
                numberOfRocks++;
            }
            if (((String)givenList.get(i)).startsWith("N")){
                numberOfKnights++;
            }
            if (((String)givenList.get(i)).startsWith("B")){
                numberOfBishops++;
            }
            if (((String)givenList.get(i)).startsWith("K") || ((String) givenList.get(i)).startsWith("0")){
                numberOfKings++;
            }
            if (((String)givenList.get(i)).startsWith("Q")){
                numberOfQueens++;
            }
            if (((String)givenList.get(i)).startsWith("a") || ((String) givenList.get(i)).startsWith("b") || ((String)givenList.get(i)).startsWith("c") || ((String)givenList.get(i)).startsWith("d") || ((String)givenList.get(i)).startsWith("e") || ((String)givenList.get(i)).startsWith("f") || ((String)givenList.get(i)).startsWith("g") || ((String)givenList.get(i)).startsWith("h")){
                numberOfPawns++;
            }
        }
        if (numbersOfTurns - (numberOfRocks + numberOfKnights + numberOfBishops + numberOfKings + numberOfQueens + numberOfPawns) == 1){
            numberOfKings++;
        }
        System.out.println("Rocks:  " + numberOfRocks + "   in percentage: " + (numberOfRocks*100/numbersOfTurns) + "%");
        System.out.println("Knights:  " + numberOfKnights + "   in percentage: " + (numberOfKnights*100/numbersOfTurns) + "%");
        System.out.println("Bishops:  " + numberOfBishops + "   in percentage: " + (numberOfBishops*100/numbersOfTurns) + "%");
        System.out.println("Queens:  " + numberOfQueens + "   in percentage: " + (numberOfQueens*100/numbersOfTurns) + "%");
        System.out.println("Kings:  " + numberOfKings + "   in percentage: " + (numberOfKings*100/numbersOfTurns) + "%");
        System.out.println("Pawns:  " + numberOfPawns + "   in percentage: " + (numberOfPawns*100/numbersOfTurns) + "%");
    }
}