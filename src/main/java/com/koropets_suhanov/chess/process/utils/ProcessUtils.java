package com.koropets_suhanov.chess.process.utils;

import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Knight;
import com.koropets_suhanov.chess.model.Rock;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static com.koropets_suhanov.chess.process.service.ParseWrittenTurn.figureBornFromTransformation;
import static com.koropets_suhanov.chess.process.service.ParseWrittenTurn.figureInWrittenStyleToBorn;
import static com.koropets_suhanov.chess.process.service.Process.currentWrittenStyleTurn;

@Slf4j
@UtilityClass
public class ProcessUtils {

    public Figure createFigure(Field field, String writtenStyleOfTheFigure, Color color) {
        switch (writtenStyleOfTheFigure) {
            case "Q":
                return new Queen(field, color);
            case "B":
                return new Bishop(field, color);
            case "N":
                return new Knight(field, color);
            case "R":
                return new Rock(field, color);
        }
        throw new RuntimeException("Could not choose figure. Turn must be wrong written." + currentWrittenStyleTurn + " " + figureBornFromTransformation + " " + figureInWrittenStyleToBorn);
    }
}
