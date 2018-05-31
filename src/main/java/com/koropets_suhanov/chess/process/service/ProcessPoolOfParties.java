package com.koropets_suhanov.chess.process.service;

import com.koropets_suhanov.chess.model.Board;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProcessPoolOfParties {

  private static final String PATH = "src/main/resources/parties/hou_yifan/black_lose.txt";
  private static final Pattern pattern = Pattern.compile("^(1)\\.\\s*(\\S+)\\s*(\\S+)*");


  public void processPoolOfParites() throws IOException {
    Board board = Board.getInstance();


    BufferedReader bf = new BufferedReader(new FileReader(PATH));
    List<String> parties = new ArrayList<>();
    String line;
    int counter = 0;
    int valid = 0;
    while ((line = bf.readLine()) != null) {
      counter++;
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()){
        valid++;
        parties.add(line);
      }
    }
    System.out.println("There are = " + counter + " parties");
    System.out.println("there are valid parites = " + valid);

    List<List<String>> allParsedParties = new ArrayList<>();
    for (String oneParty : parties) {
//      System.out.println(oneParty);
      String[] array = oneParty.split("(\\d)+\\.");
      List<String> finalParty = new ArrayList<>();

      for (String anArray : array) {
        String a = anArray.replaceFirst(" ", "");
        if (a.length() > 0) {
          a = a.substring(0, a.length() - 1);
        }
        if (a.contains("[")) {
          a = a.substring(0, a.indexOf("[") - 1);
        }
        if (!"".equals(a)) {
          finalParty.add(a);
        }
      }
      allParsedParties.add(finalParty);
//      System.out.println("Processing finished");
//      System.out.println(finalParty);
//      for (String str : finalParty) {
//        System.out.println(str);
//      }
    }

    System.out.println("Before = " + allParsedParties.size());
    allParsedParties = allParsedParties.stream().filter(p -> p.size() > 5).collect(Collectors.toList());
    System.out.println("After = " + allParsedParties.size());

    for (List<String> party: allParsedParties) {
      System.out.println(party);
    }
    System.out.println("Allparties = " + allParsedParties.size());

  }
}
