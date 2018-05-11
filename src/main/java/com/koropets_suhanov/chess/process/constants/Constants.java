package com.koropets_suhanov.chess.process.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
  public static final int LINE_H = 0;
  public static final int LINE_A = 7;
  public static final String SHORT_CASTLING_ZEROS = "0-0";
  public static final String SHORT_CASTLING = "O-O";
  public static final String LONG_CASTLING_ZEROS = "0-0-0";
  public static final String LONG_CASTLING = "O-O-O";
  public static final String EATING_SYMBOL = "x";
  public static final byte SIZE = 8;
  public static final String CHECK_SYMBOL = "+";
  public static final String CHECKMAT_SYMBOL = "#";
}
