package com.koropets_suhanov.chess.utils;

import com.koropets_suhanov.chess.model.Bishop;
import com.koropets_suhanov.chess.model.Observer;
import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import com.koropets_suhanov.chess.model.Board;
import com.koropets_suhanov.chess.model.Rock;
import com.koropets_suhanov.chess.model.Knight;
import com.koropets_suhanov.chess.model.Queen;
import com.koropets_suhanov.chess.model.King;
import com.koropets_suhanov.chess.model.Pawn;
import com.koropets_suhanov.chess.model.Color;
import com.koropets_suhanov.chess.process.dto.Turn;
import com.koropets_suhanov.chess.process.service.Process;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import scala.Tuple2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import static java.lang.Math.abs;
import static com.koropets_suhanov.chess.process.constants.Constants.longCastling;
import static com.koropets_suhanov.chess.process.constants.Constants.shortCastling;
import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;

@UtilityClass
@Slf4j
public class ProcessingUtils {


    public final Field f1 = new Field(7, 5);
    public final Field g1 = new Field(7, 6);
    public final Field b1 = new Field(7, 1);
    public final Field c1 = new Field(7, 2);
    public final Field d1 = new Field(7, 3);
    public final Field f8 = new Field(0, 5);
    public final Field g8 = new Field(0, 6);
    public final Field b8 = new Field(0, 1);
    public final Field c8 = new Field(0, 2);
    public final Field d8 = new Field(0, 3);
    public final Field a1 = new Field(7, 0);
    public final Field h1 = new Field(7, 7);
    public final Field e1 = new Field(7, 4);
    public final Field a8 = new Field(0, 0);
    public final Field h8 = new Field(0, 7);
    public final Field e8 = new Field(0, 4);

    private List<Observer> candidates;
    private String mainTurn;
    private Field field;
    private int number;
    private Set<Field> affectedFields;
    private List<Tuple2<Figure, Field>> figureToField = new ArrayList<>();
    private Figure figure;
    private boolean isEating;
    private boolean transformation;
    private Figure targetedFigure;
    private final Field WHITE_KING_SHORT_CASTLING = new Field(7, 6);
    private final Field WHITE_KING_LONG_CASTLING = new Field(7, 2);
    private final Field BLACK_KING_SHORT_CASTLING = new Field(0, 6);
    private final Field BLACK_KING_LONG_CASTLING = new Field(0, 2);
    private final Field WHITE_ROCK_SHORT_CASTLING = new Field(7, 5);
    private final Field WHITE_ROCK_LONG_CASTLING = new Field(7, 3);
    private final Field BLACK_ROCK_SHORT_CASTLING = new Field(0, 5);
    private final Field BLACK_ROCK_LONG_CASTLING = new Field(0, 3);
    private final FrequentFigure WHITE_FREQUENCY = new FrequentFigure();
    private final FrequentFigure BLACK_FREQUENCY = new FrequentFigure();
    private final String PLUS = "+";
    public final Set<String> FIGURES_IN_WRITTEN_STYLE = new HashSet<>(Arrays.asList("R", "N", "B", "Q"));
    private List<Tuple2<Figure, Field>> tuplesFigureToField;
    @Getter
    private Figure figureBornFromTransformation;
    private String figureInWrittenStyleToBorn;
    public Figure eatenFigureToResurrection;
    private Figure pawnFromTransformation;

    private static Board board = Process.board;

    public Turn getActualTurn(final String turnWrittenStyle, final Color color, int numberOfTurn){
        candidates = new ArrayList<>();
        mainTurn = turnWrittenStyle;
        number = numberOfTurn;
        System.out.println(turnWrittenStyle);
        field = parseTargetField(turnWrittenStyle);
        return setTurn(turnWrittenStyle, color);
    }

    public Tuple2<FrequentFigure, FrequentFigure> countFrequency(boolean isWhite, String writtenTurn){
        FrequentFigure frequent = isWhite ? WHITE_FREQUENCY : BLACK_FREQUENCY;
        char figure = writtenTurn.charAt(0);
        switch (figure){
            case 'R' :  frequent.updateRock();
            case 'N' : frequent.updateKnight();
            case 'B' : frequent.updateBishop();
            case 'Q' : frequent.updateQueen();
            case 'K' : frequent.updateKing();
            case '0' : frequent.updateKing();
            default: frequent.updatePawn();
        }
        return new Tuple2<>(WHITE_FREQUENCY, BLACK_FREQUENCY);
    }

    private Turn setTurn(final String writtenStyle, final Color color){
        initialize();
        return isCastling(writtenStyle) ? setCastlingTurn(writtenStyle, color) : setNonCastlingTurn(writtenStyle, color);
    }

    private void initialize(){
        candidates.clear();
        figureToField.clear();
        figure = null;
        targetedFigure = null;
        isEating = false;
        figureBornFromTransformation = null;
        figureInWrittenStyleToBorn = writtenFigureToBorn(mainTurn);
        transformation = whetherWrittenTurnIsTransformation();
    }

    private boolean isCastling(final String writtenStyle){
        return shortCastling.equals(writtenStyle) || longCastling.equals(writtenStyle);
    }

    private Turn setCastlingTurn(final String writtenStyle, final Color color){
        return shortCastling.equals(writtenStyle) ? shortCastlingTurn(writtenStyle, color) : longCastlingTurn(writtenStyle, color);
    }

    private Turn shortCastlingTurn(final String writtenStyle, final Color color){
        List<Tuple2<Figure, Field>> figureToField = (color == Color.WHITE)
                ? createFigureToFieldCastling(Board.getFieldToFigure().get(e1), Board.getFieldToFigure().get(h1), WHITE_KING_SHORT_CASTLING, WHITE_ROCK_SHORT_CASTLING)
                : createFigureToFieldCastling(Board.getFieldToFigure().get(e8), Board.getFieldToFigure().get(h8), BLACK_KING_SHORT_CASTLING, BLACK_ROCK_SHORT_CASTLING);
        return createTurn(figureToField, null, writtenStyle, false, false, false, null, number);
    }

    private Turn longCastlingTurn(final String writtenStyle, final Color color){
        List<Tuple2<Figure, Field>> figureToField = (color == Color.WHITE)
                ? createFigureToFieldCastling(Board.getFieldToFigure().get(e1), Board.getFieldToFigure().get(a1), WHITE_KING_LONG_CASTLING, WHITE_ROCK_LONG_CASTLING)
                : createFigureToFieldCastling(Board.getFieldToFigure().get(e8), Board.getFieldToFigure().get(a8), BLACK_KING_LONG_CASTLING, BLACK_ROCK_LONG_CASTLING);
        return createTurn(figureToField, null, writtenStyle, false, false, false, null, number);
    }

    private List<Tuple2<Figure, Field>> createFigureToFieldCastling(Figure king, Figure rock, Field kingFieldDestination, Field rockFieldDestination){
        List<Tuple2<Figure, Field>> figureToField = new ArrayList<>();
        figureToField.add(new Tuple2<>(king, kingFieldDestination));
        figureToField.add(new Tuple2<>(rock, rockFieldDestination));
        return figureToField;
    }

    private Turn setNonCastlingTurn(final String writtenStyle, final Color color){
        char firstCharacter = writtenStyle.charAt(0);
        switch (firstCharacter){
            case 'R' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Rock.class, color, true, false) : fetchFigureToTargetField(Rock.class, color, false, false);
                return createTurn(figureToField, null, writtenStyle, isEating, false, false, targetedFigure, number);
            case 'N' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Knight.class, color, true, false) : fetchFigureToTargetField(Knight.class, color, false, false);
                return createTurn(figureToField, null, writtenStyle, isEating, false, false, targetedFigure, number);
            case 'B' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Bishop.class, color, true, false) : fetchFigureToTargetField(Bishop.class, color, false, false);
                return createTurn(figureToField, null, writtenStyle, isEating, false, false, targetedFigure, number);
            case 'Q' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Queen.class, color, true, false) : fetchFigureToTargetField(Queen.class, color, false, false);
                return createTurn(figureToField, null, writtenStyle, isEating, false, false, targetedFigure, number);
            case 'K' :  figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(King.class, color, true, false) : fetchFigureToTargetField(King.class, color, false, false);
                return createTurn(figureToField, null, writtenStyle, isEating, false, false, targetedFigure, number);
            default :   figureToField = writtenStyle.contains("x") ? fetchFigureToTargetField(Pawn.class, color, true, transformation) : fetchFigureToTargetField(Pawn.class, color, false, transformation);
                return isEnPassantScenario(figureToField) ? createTurn(figureToField, figureBornFromTransformation, writtenStyle, isEating, transformation, true, targetedFigure, number) :
                        createTurn(figureToField, figureBornFromTransformation, writtenStyle, isEating, transformation, false, targetedFigure, number);
        }
    }

    private List<Tuple2<Figure, Field>> fetchFigureToTargetField(Class clazz, Color color, boolean eating, boolean transformation){
        isEating = eating;
        List<Observer> targets = new ArrayList<Observer>();
        List<Figure> figures = Board.getFiguresByClass(clazz, color);
        for (Observer curFigure : figures){
            if (eating){
                if (transformation && clazz == Pawn.class){
                    Pawn pawn = (Pawn) curFigure;
                    if (pawn.getPreyField().contains(field)){
                        targets.add(curFigure);
                        targetedFigure = Board.getFieldToFigure().get(field);
                        figureBornFromTransformation = createFigure(field, figureInWrittenStyleToBorn, pawn.getColor());
                    }
                }else {
                    if (clazz == Pawn.class && ((Pawn) curFigure).isEnPassant()){
                        Pawn pawn = (Pawn) curFigure;
                        if (pawn.getEnPassantField().equals(field)){
//                        System.out.println("Field = " + field);
                            targets.add(pawn);
                            targetedFigure = pawn.getEnPassantEnemy();
//                        System.out.println("Here... passed " + targetedFigure + " " + targets);
//                        System.out.println("who could be eaten = " + pawn.getWhoCouldBeEaten() + " aleis I protect = "
//                                + pawn.getAlliesIProtect() + " enemy fields = " + pawn.getPreyField() + " enPassant enemy = "
//                        + pawn.getEnPassantEnemy());
                        }
                    }else {
//                    System.out.println("Class = "  + clazz + " color = " + color + " eating = " + eating);
                        if (((Figure) curFigure).getPreyField().contains(field)){
                            targets.add(curFigure);
                            targetedFigure = Board.getFieldToFigure().get(field);
//                        System.out.println("targeted figure = " + targetedFigure);
                        }
                    }
                }
            }else {
                if (transformation && clazz == Pawn.class){
                    Pawn pawn = (Pawn) curFigure;
                    if (pawn.getPossibleFieldsToMove().contains(field)){
                        candidates.add(pawn);
                        figureBornFromTransformation = createFigure(field, figureInWrittenStyleToBorn, color);
                    }
                }else {
                    if (((Figure) curFigure).getPossibleFieldsToMove().contains(field)){
                        candidates.add(curFigure);
                    }
                }
            }
        }
        if (!targets.isEmpty()){
            System.out.println("targets = " + targets);
            if (targets.size() == 1){
                figure = (Figure)targets.get(0);
                System.out.println("Figure = " + figure);
            }else{
                figure = choseFigureWhichAttack(targets, clazz);
            }
        }
        if (!candidates.isEmpty()){
            if (candidates.size() > 1){
                figure = choseExactFigure(candidates);
            }else {
                figure = (Figure) candidates.get(0);
            }
        }
        if (figure != null){
            figureToField.add(new Tuple2<>(figure, field));
        }
        if (figureToField.size() == 0){
            throw new RuntimeException("Could not fetch figure. Turn must be wrong written. Turn = " + mainTurn);
        }
        return figureToField;
    }

    private Figure choseFigureWhichAttack(List<Observer> targets, Class clazz){
        if (clazz == Pawn.class){
            char verticalPawn = mainTurn.charAt(0);
            for (Object currentFigure : targets){
                if (((Figure) currentFigure).getField().getY() == Field.getInvertedHorizontal().get(verticalPawn)){
                    return (Figure) currentFigure;
                }
            }
        }else {
            char secondPosition = mainTurn.charAt(1);
            System.out.println("SecondPosition = " + secondPosition);
            int integer = Character.getNumericValue(secondPosition);
            System.out.println("integer = " + integer);
            return chose(integer, secondPosition, targets);
        }
        return null;
    }

    private Figure choseExactFigure(List<Observer> targets){
        char secondPosition = mainTurn.charAt(1);
        int integer = Character.getNumericValue(secondPosition);
        return chose(integer, secondPosition, targets);
    }

    private Figure chose(int integer, char secondPosition, List<Observer> candidatesForBeingTheOne){
        System.out.println("candidates = " + candidatesForBeingTheOne);
        for (Observer observer : candidatesForBeingTheOne){
            if (integer > SIZE){
                System.out.println("Passed = " + integer);
                if (((Figure) observer).getField().getY() == Field.getInvertedHorizontal().get(secondPosition)){
                    return (Figure) observer;
                }
            }else {
                if (((Figure) observer).getField().getX() == Field.getInvertedVertical().get(integer)){
                    return (Figure) observer;
                }
            }
        }
        throw new RuntimeException("Could not choose exact figure. Turn must be wrong written. Turn = " + mainTurn);
    }

    private Field parseTargetField(String turn){
        int x;
        int y;
        if (!turn.equalsIgnoreCase(shortCastling) && !turn.equalsIgnoreCase(longCastling)){
            if (!whetherWrittenTurnIsTransformation()){
                if (turn.contains(PLUS)){
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length()-2)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length()-3));
                }else {
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length()-1)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length()-2));
                }
            }else {
                if (turn.contains(PLUS)){
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 3)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 4));
                }else {
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 2)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 3));
                }
            }
            return new Field(x,y);
        }else {
            log.debug("Target field is null. Castling");
            return null;
        }
    }

    private String writtenFigureToBorn(String turn){
        if (turn.contains(PLUS)){
            return "" + turn.charAt(turn.length() - 2);
        }else {
            return "" + turn.charAt(turn.length() - 1);
        }
    }

    private boolean whetherWrittenTurnIsTransformation(){
        int lengthOfTheWrittenTurn = mainTurn.length();
        if (mainTurn.contains(PLUS)){
            char previousBeforeTheLast = mainTurn.charAt(lengthOfTheWrittenTurn - 2);
            if (FIGURES_IN_WRITTEN_STYLE.contains(Character.toString(previousBeforeTheLast))){
                return true;
            }
        }else {
            char theLast = mainTurn.charAt(lengthOfTheWrittenTurn - 1);
            if (FIGURES_IN_WRITTEN_STYLE.contains(Character.toString(theLast))){
                return true;
            }
        }
        return false;
    }

    public Turn createTurn(List<Tuple2<Figure, Field>> figureToField, Figure figureFromTransformation,
                                  String writtenStyle, boolean isEating, boolean transformation, boolean enPassant,
                                  Figure targetedFigure, int numberOfTurn){
        return Turn.builder().figureToDestinationField(figureToField)
                .figureFromTransformation(figureFromTransformation)
                .writtenStyle(writtenStyle)
                .eating(isEating)
                .transformation(transformation)
                .enPassant(enPassant)
                .targetedFigure(targetedFigure)
                .numberOfTurn(numberOfTurn)
                .build();
    }

    public Figure createFigure(Field field, String writtenStyleOfTheFigure, Color color){
        switch (writtenStyleOfTheFigure){
            case "Q" : return new Queen(field, color);
            case "B" : return new Bishop(field, color);
            case "N" : return new Knight(field, color);
            case "R" : return new Rock(field, color);
        }
        throw new RuntimeException("Could not choose figure. Turn must be wrong written." + mainTurn + " " + figureBornFromTransformation + " " + figureInWrittenStyleToBorn);
    }

    public Set<Field> fieldsBetweenRockAndKing(final King king, final Field rockPosition){
        Set<Field> fieldsBetween = new HashSet<>();
        if (king.getField().getX() == rockPosition.getX()){
            if (king.getField().getX() > rockPosition.getX()){
                for (int i = king.getField().getX() - 1; i > rockPosition.getX(); i--){
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            }else {
                for (int i = king.getField().getX() + 1; i < rockPosition.getX(); i++){
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            }
        }
        if (king.getField().getY() == rockPosition.getY()){
            if (king.getField().getY() > rockPosition.getY()){
                for (int j = king.getField().getY() - 1; j > rockPosition.getY(); j--){
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            }else {
                for (int j = king.getField().getY() + 1; j < rockPosition.getY(); j++){
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            }
        }
        return fieldsBetween;
    }

    public Set<Field> fieldsBetweenBishopAndKing(final King king, final Field bishopPosition){
        Set<Field> fieldsBetween =  new HashSet<>();
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()){
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }
        }
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()){
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()){
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }

        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()){
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++){
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        return fieldsBetween;
    }

    public Set<Field> fieldsBetweenQueenAndKing(final King king, final Field queenPosition){
        Set<Field> fieldsBetweenQueenAndKing = new HashSet<>();
        if (king.getField().getX() == queenPosition.getX() || king.getField().getY() == queenPosition.getY()){
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenRockAndKing(king, queenPosition));
        }else {
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenBishopAndKing(king, queenPosition));
        }
        return fieldsBetweenQueenAndKing;
    }

    public Set<Figure> getAffectedFigures(Color color){
        Set<Figure> acceptedFigures = new HashSet<>();
        List<Observer> observers = Board.getFigures(color);
        affectedFields.forEach(f -> {
            observers.forEach(o -> {
                if (((Figure)o).getAttackedFields().contains(f)){
                    acceptedFigures.add((Figure)o);
                }
            });
        });
        return acceptedFigures;
    }

    private void getAffectedFields(Turn turn){
        affectedFields = new HashSet<>();
        for (Tuple2<Figure, Field> tuple2 : turn.getFigureToDestinationField()){
            affectedFields.add(tuple2._1.getField());
            affectedFields.add(tuple2._2);
        }
    }

    public void makeTurn(Turn turn){
        getAffectedFields(turn);
        System.out.println(affectedFields);
        System.out.println("Turn = " + turn);
        setTurnForUndoing(turn);
        for (Tuple2<Figure, Field> tuple2 : turn.getFigureToDestinationField()){
            board.setNewCoordinates(turn, tuple2._1, tuple2._2, turn.getTargetedFigure(), false, turn.isEnPassant());
        }
        makePullAdditionalAlliesAndEnemies();
    }

    public void undoTurn(Turn turn){
        Turn undoTurn = Turn.builder()
                .figureToDestinationField(tuplesFigureToField)
                .eating(false)
                .writtenStyle("")
                .numberOfTurn(turn.getNumberOfTurn())
                .build();
        for (Tuple2<Figure, Field> tuple2 : undoTurn.getFigureToDestinationField()){
            board.setNewCoordinates(turn, tuple2._1, tuple2._2, undoTurn.getTargetedFigure(), true, turn.isEnPassant());
        }
        ProcessingUtils.eatenFigureToResurrection = null;
        makePullAdditionalAlliesAndEnemies();
    }

    private void setTurnForUndoing(Turn turn){
        System.out.println("Turn = " + turn);
        tuplesFigureToField = new ArrayList<>();
        eatenFigureToResurrection = null;
        for (Tuple2<Figure, Field> tuple2 : turn.getFigureToDestinationField()){
            tuplesFigureToField.add(new Tuple2<>(tuple2._1, tuple2._1.getField()));
        }
        if (turn.isEating()){
            if (turn.getFigureToDestinationField().size() == 1
                    && turn.getFigureToDestinationField().get(0)._1.getClass() == Pawn.class
                    && ((Pawn) turn.getFigureToDestinationField().get(0)._1).isEnPassant()){

                eatenFigureToResurrection = turn.getTargetedFigure().createNewFigure();
            }else {
                Figure tempFigure = Board.getFieldToFigure().get(turn.getFigureToDestinationField().get(0)._2);
                if (!turn.isEnPassant()){
                    eatenFigureToResurrection = tempFigure.createNewFigure();
                }
            }
        }
        if (turn.isTransformation()){
            pawnFromTransformation = turn.getFigureToDestinationField().get(0)._1;
        }
        figureBornFromTransformation = turn.getFigureFromTransformation();
    }

    private void makePullAdditionalAlliesAndEnemies(){
        Map<Figure, Set<Figure>> figureToChosenAllies = new HashMap<>();
        Board.getFigures().forEach(f -> {
            Set<Figure> chosenAllies = ((Figure)f).pullAdditionalAlliesAndEnemies();
            if (!isEmpty(chosenAllies)){
                figureToChosenAllies.put((Figure)f, chosenAllies);
            }
        });
        for (int i = 0; i < SIZE; i++){
            for (Figure curFigure : figureToChosenAllies.keySet()){
                for (Figure ally : figureToChosenAllies.get(curFigure)){
                    if (ally != null){
                        doUpdate(curFigure, ally);
                    }
                }
            }
        }
    }

    private void doUpdate(Figure curFigure, Figure ally){
        for (Figure undefendedAlly : ally.getAlliesIProtect()){
            updateProtectionOfUndefendedAllies(curFigure, ally, undefendedAlly);
        }
        for (Figure prey : ally.getWhoCouldBeEaten()){
            updateWhoCouldBeEaten(curFigure, ally, prey);
        }
    }

    private void updateWhoCouldBeEaten(Figure curFigure, Figure ally, Figure prey){
        if (!curFigure.getWhoCouldBeEaten().contains(prey) && ally.getWhoCouldBeEaten().contains(prey)
                && curFigure.getAttackedFields().contains(prey.getField()) && isOnTheSameLine(curFigure, ally, prey)){
            curFigure.getWhoCouldBeEaten().add(prey);
            prey.addEnemy(curFigure);
        }
    }

    private void updateProtectionOfUndefendedAllies(Figure curFigure, Figure ally, Figure undefendedAlly){
        if (!curFigure.getAlliesIProtect().contains(undefendedAlly) && ally.getAlliesIProtect().contains(undefendedAlly)
                && curFigure.getAttackedFields().contains(undefendedAlly.getField()) && isOnTheSameLine(curFigure, ally, undefendedAlly)
                && !curFigure.equals(undefendedAlly) && !ally.equals(undefendedAlly)){
            curFigure.addAllyIProtect(undefendedAlly);
            undefendedAlly.addAllyProtectMe(curFigure);
        }
    }

    private boolean isOnTheSameLine(Figure f1, Figure f2, Figure f3){
        if (f1.getClass() == Bishop.class || f2.getClass() == Bishop.class  || f3.getClass() == Bishop.class){
            return (abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
                    && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY()))
                    && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY()));
        }
        if (f1.getClass() == Rock.class || f2.getClass() == Rock.class  || f3.getClass() == Rock.class){
            return  ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX())) ||
                    ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY()));
        }
        return ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX())) ||
                ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY())) ||
                (((abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
                        && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY())))
                        && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY())));
    }

    private boolean isEnPassantScenario(List<Tuple2<Figure, Field>> figureToField){
        Pawn pawn = (Pawn) figureToField.get(0)._1;
        return pawn.isEnPassant();
    }

    public boolean isEmpty(Set<?> set){
        return set == null || set.isEmpty();
    }
}