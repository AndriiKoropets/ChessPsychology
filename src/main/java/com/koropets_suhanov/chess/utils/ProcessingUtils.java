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
import com.koropets_suhanov.chess.process.dto.FigureToField;
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

import static com.koropets_suhanov.chess.process.constants.Constants.SHORT_CASTLING;
import static com.koropets_suhanov.chess.process.constants.Constants.SHORT_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.constants.Constants.LONG_CASTLING;
import static com.koropets_suhanov.chess.process.constants.Constants.LONG_CASTLING_ZEROS;
import static com.koropets_suhanov.chess.process.constants.Constants.PLUS;
import static com.koropets_suhanov.chess.process.constants.Constants.SIZE;
import static com.koropets_suhanov.chess.process.service.Process.currentColor;
import static com.koropets_suhanov.chess.process.service.Process.currentTurnNumber;
import static com.koropets_suhanov.chess.process.service.Process.currentWrittenStyleTurn;
import static java.lang.Math.abs;

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
    private Field field;
    private Set<Field> affectedFields;
    private List<FigureToField> figureToField = new ArrayList<>();
    private Figure figure;
    private boolean eating;
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
    public final Set<Character> FIGURES_IN_WRITTEN_STYLE_EXCEPT_PAWN = new HashSet<>(Arrays.asList('R', 'N', 'B', 'Q', 'K'));
    public final Set<String> FIGURES_IN_WRITTEN_STYLE = new HashSet<>(Arrays.asList("R", "N", "B", "Q"));
    private List<FigureToField> tuplesFigureToField;
    @Getter
    private Figure figureBornFromTransformation;
    private String figureInWrittenStyleToBorn;
    public Figure eatenFigureToResurrection;
    private Figure pawnFromTransformation;

    private static Board board = Process.board;

    public Turn getActualTurn() {
        candidates = new ArrayList<>();
        field = parseTargetField(currentWrittenStyleTurn);
        return setTurn();
    }

    public Tuple2<FrequentFigure, FrequentFigure> countFrequency(boolean isWhite, String writtenTurn) {
        FrequentFigure frequent = isWhite ? WHITE_FREQUENCY : BLACK_FREQUENCY;
        char figure = writtenTurn.charAt(0);
        switch (figure) {
            case 'R':
                frequent.updateRock();
            case 'N':
                frequent.updateKnight();
            case 'B':
                frequent.updateBishop();
            case 'Q':
                frequent.updateQueen();
            case 'K':
                frequent.updateKing();
            case '0':
                frequent.updateKing();
            case 'O':
                frequent.updateKing();
            default:
                frequent.updatePawn();
        }
        return new Tuple2<>(WHITE_FREQUENCY, BLACK_FREQUENCY);
    }

    private Turn setTurn() {
        initialize();
        return isCastling() ? setCastlingTurn() : setNonCastlingTurn();
    }

    private void initialize() {
        candidates.clear();
        figureToField.clear();
        figure = null;
        targetedFigure = null;
        eating = isEating();
        figureBornFromTransformation = null;
        figureInWrittenStyleToBorn = writtenFigureToBorn(currentWrittenStyleTurn);
        transformation = whetherWrittenTurnIsTransformation();
    }

    private boolean isEating() {
        return currentWrittenStyleTurn.contains("x");
    }

    private boolean isCastling() {
        return SHORT_CASTLING_ZEROS.equals(currentWrittenStyleTurn)
                || LONG_CASTLING_ZEROS.equals(currentWrittenStyleTurn)
                || SHORT_CASTLING.equals(currentWrittenStyleTurn)
                || LONG_CASTLING.equals(currentWrittenStyleTurn);
    }

    private Turn setCastlingTurn() {
        return SHORT_CASTLING_ZEROS.equals(currentWrittenStyleTurn) ? shortCastlingTurn() : longCastlingTurn();
    }

    private Turn shortCastlingTurn() {
        List<FigureToField> figureToField = (currentColor == Color.WHITE)
                ? createFigureToFieldCastling(Board.getFieldToFigure().get(e1), Board.getFieldToFigure().get(h1), WHITE_KING_SHORT_CASTLING, WHITE_ROCK_SHORT_CASTLING)
                : createFigureToFieldCastling(Board.getFieldToFigure().get(e8), Board.getFieldToFigure().get(h8), BLACK_KING_SHORT_CASTLING, BLACK_ROCK_SHORT_CASTLING);
        return Turn.builder()
                .figureToDestinationField(figureToField)
                .writtenStyle(currentWrittenStyleTurn)
                .numberOfTurn(currentTurnNumber)
                .build();
    }

    private Turn longCastlingTurn() {
        List<FigureToField> figureToField = (currentColor == Color.WHITE)
                ? createFigureToFieldCastling(Board.getFieldToFigure().get(e1), Board.getFieldToFigure().get(a1), WHITE_KING_LONG_CASTLING, WHITE_ROCK_LONG_CASTLING)
                : createFigureToFieldCastling(Board.getFieldToFigure().get(e8), Board.getFieldToFigure().get(a8), BLACK_KING_LONG_CASTLING, BLACK_ROCK_LONG_CASTLING);
        return Turn.builder()
                .figureToDestinationField(figureToField)
                .writtenStyle(currentWrittenStyleTurn)
                .numberOfTurn(currentTurnNumber)
                .build();
    }

    private List<FigureToField> createFigureToFieldCastling(Figure king, Figure rock, Field kingFieldDestination, Field rockFieldDestination) {
        List<FigureToField> figureToField = new ArrayList<>();
        FigureToField kingDestination = FigureToField.builder()
                .figure(king)
                .field(kingFieldDestination)
                .build();
        FigureToField rockDestination = FigureToField.builder()
                .figure(rock)
                .field(rockFieldDestination)
                .build();
        figureToField.add(kingDestination);
        figureToField.add(rockDestination);
        return figureToField;
    }

    private Turn setNonCastlingTurn() {
        Turn.TurnBuilder curTurnBuilder = Turn.builder();
        char firstCharacter = currentWrittenStyleTurn.charAt(0);

        if (!isPawn(firstCharacter)) {
            switch (firstCharacter) {
                case 'R':
                    figureToField = createFigureToFieldDependsOnFigureType(Rock.class);
                case 'N':
                    figureToField = createFigureToFieldDependsOnFigureType(Knight.class);
                case 'B':
                    figureToField = createFigureToFieldDependsOnFigureType(Bishop.class);
                case 'Q':
                    figureToField = createFigureToFieldDependsOnFigureType(Queen.class);
                case 'K':
                    figureToField = createFigureToFieldDependsOnFigureType(King.class);
                default:
                    figureToField = createFigureToFieldDependsOnFigureType(Pawn.class);
            }
        } else {
            figureToField = createFigureToFieldDependsOnFigureType(Pawn.class);
            curTurnBuilder.enPassant(isEnPassantScenario(figureToField));
        }

        return curTurnBuilder
                .figureToDestinationField(figureToField)
                .figureFromTransformation(figureBornFromTransformation)
                .writtenStyle(currentWrittenStyleTurn)
                .eating(eating)
                .transformation(transformation)
                .targetedFigure(targetedFigure)
                .numberOfTurn(currentTurnNumber)
                .build();
    }

    private boolean isPawn(Character firstCharacter) {
        return FIGURES_IN_WRITTEN_STYLE_EXCEPT_PAWN.contains(firstCharacter);
    }

    private List<FigureToField> createFigureToFieldDependsOnFigureType(Class figure) {
        return currentWrittenStyleTurn.contains("x")
                ? fetchFigureToTargetField(figure)
                : fetchFigureToTargetField(figure);
    }

    private List<FigureToField> fetchFigureToTargetField(Class clazz) {
        List<Observer> targets = new ArrayList<>();
        List<Figure> figures = Board.getFiguresByClass(clazz, currentColor);
        for (Observer curFigure : figures) {
            if (eating) {
                if (transformation && clazz == Pawn.class) {
                    Pawn pawn = (Pawn) curFigure;
                    if (pawn.getPreyField().contains(field)) {
                        targets.add(curFigure);
                        targetedFigure = Board.getFieldToFigure().get(field);
                        figureBornFromTransformation = createFigure(field, figureInWrittenStyleToBorn, pawn.getColor());
                    }
                } else {
                    if (clazz == Pawn.class && ((Pawn) curFigure).isEnPassant()) {
                        Pawn pawn = (Pawn) curFigure;
                        if (pawn.getEnPassantField().equals(field)) {
//                        System.out.println("Field = " + field);
                            targets.add(pawn);
                            targetedFigure = pawn.getEnPassantEnemy();
//                        System.out.println("Here... passed " + targetedFigure + " " + targets);
//                        System.out.println("who could be eaten = " + pawn.getWhoCouldBeEaten() + " aleis I protect = "
//                                + pawn.getAlliesIProtect() + " enemy fields = " + pawn.getPreyField() + " enPassant enemy = "
//                        + pawn.getEnPassantEnemy());
                        }
                    } else {
//                    System.out.println("Class = "  + clazz + " color = " + color + " eating = " + eating);
                        if (((Figure) curFigure).getPreyField().contains(field)) {
                            targets.add(curFigure);
                            targetedFigure = Board.getFieldToFigure().get(field);
//                        System.out.println("targeted figure = " + targetedFigure);
                        }
                    }
                }
            } else {
                if (transformation && clazz == Pawn.class) {
                    Pawn pawn = (Pawn) curFigure;
                    if (pawn.getPossibleFieldsToMove().contains(field)) {
                        candidates.add(pawn);
                        figureBornFromTransformation = createFigure(field, figureInWrittenStyleToBorn, currentColor);
                    }
                } else {
                    if (((Figure) curFigure).getPossibleFieldsToMove().contains(field)) {
                        candidates.add(curFigure);
                    }
                }
            }
        }
        if (!targets.isEmpty()) {
            System.out.println("targets = " + targets);
            if (targets.size() == 1) {
                figure = (Figure) targets.get(0);
                System.out.println("Figure = " + figure);
            } else {
                figure = choseFigureWhichAttack(targets, clazz);
            }
        }
        if (!candidates.isEmpty()) {
            if (candidates.size() > 1) {
                figure = choseExactFigure(candidates);
            } else {
                figure = (Figure) candidates.get(0);
            }
        }
        if (figure != null) {

            figureToField.add(FigureToField.builder().figure(figure).field(field).build());
        }
        if (figureToField.size() == 0) {
            throw new RuntimeException("Could not fetch figure. Turn must be wrong written. Turn = " + currentWrittenStyleTurn);
        }
        return figureToField;
    }

    private Figure choseFigureWhichAttack(List<Observer> targets, Class clazz) {
        if (clazz == Pawn.class) {
            char verticalPawn = currentWrittenStyleTurn.charAt(0);
            for (Object currentFigure : targets) {
                if (((Figure) currentFigure).getField().getY() == Field.getInvertedHorizontal().get(verticalPawn)) {
                    return (Figure) currentFigure;
                }
            }
        } else {
            char secondPosition = currentWrittenStyleTurn.charAt(1);
            System.out.println("SecondPosition = " + secondPosition);
            int integer = Character.getNumericValue(secondPosition);
            System.out.println("integer = " + integer);
            return chose(integer, secondPosition, targets);
        }
        return null;
    }

    private Figure choseExactFigure(List<Observer> targets) {
        char secondPosition = currentWrittenStyleTurn.charAt(1);
        int integer = Character.getNumericValue(secondPosition);
        return chose(integer, secondPosition, targets);
    }

    private Figure chose(int integer, char secondPosition, List<Observer> candidatesForBeingTheOne) {
        System.out.println("candidates = " + candidatesForBeingTheOne);
        for (Observer observer : candidatesForBeingTheOne) {
            if (integer > SIZE) {
                System.out.println("Passed = " + integer);
                if (((Figure) observer).getField().getY() == Field.getInvertedHorizontal().get(secondPosition)) {
                    return (Figure) observer;
                }
            } else {
                if (((Figure) observer).getField().getX() == Field.getInvertedVertical().get(integer)) {
                    return (Figure) observer;
                }
            }
        }
        throw new RuntimeException("Could not choose exact figure. Turn must be wrong written. Turn = " + currentWrittenStyleTurn);
    }

    private Field parseTargetField(String turn) {
        int x;
        int y;
        if (!turn.equalsIgnoreCase(SHORT_CASTLING_ZEROS) && !turn.equalsIgnoreCase(LONG_CASTLING_ZEROS)) {
            if (!whetherWrittenTurnIsTransformation()) {
                if (turn.contains(PLUS)) {
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 2)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 3));
                } else {
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 1)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 2));
                }
            } else {
                if (turn.contains(PLUS)) {
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 3)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 4));
                } else {
                    x = Field.getInvertedVertical().get(Character.getNumericValue(turn.charAt(turn.length() - 2)));
                    y = Field.getInvertedHorizontal().get(turn.charAt(turn.length() - 3));
                }
            }
            return new Field(x, y);
        } else {
            log.debug("Target field is null. Castling");
            return null;
        }
    }

    private String writtenFigureToBorn(String turn) {
        if (turn.contains(PLUS)) {
            return "" + turn.charAt(turn.length() - 2);
        } else {
            return "" + turn.charAt(turn.length() - 1);
        }
    }

    private boolean whetherWrittenTurnIsTransformation() {
        int lengthOfTheWrittenTurn = currentWrittenStyleTurn.length();
        if (currentWrittenStyleTurn.contains(PLUS)) {
            char previousBeforeTheLast = currentWrittenStyleTurn.charAt(lengthOfTheWrittenTurn - 2);
            if (FIGURES_IN_WRITTEN_STYLE.contains(Character.toString(previousBeforeTheLast))) {
                return true;
            }
        } else {
            char theLast = currentWrittenStyleTurn.charAt(lengthOfTheWrittenTurn - 1);
            if (FIGURES_IN_WRITTEN_STYLE.contains(Character.toString(theLast))) {
                return true;
            }
        }
        return false;
    }

    public Turn createTurn(List<FigureToField> figureToField, Figure figureFromTransformation,
                           String writtenStyle, boolean isEating, boolean transformation, boolean enPassant,
                           Figure targetedFigure, int numberOfTurn) {
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

    public Set<Field> fieldsBetweenRockAndKing(final King king, final Field rockPosition) {
        Set<Field> fieldsBetween = new HashSet<>();
        if (king.getField().getX() == rockPosition.getX()) {
            if (king.getField().getX() > rockPosition.getX()) {
                for (int i = king.getField().getX() - 1; i > rockPosition.getX(); i--) {
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            } else {
                for (int i = king.getField().getX() + 1; i < rockPosition.getX(); i++) {
                    fieldsBetween.add(new Field(i, king.getField().getY()));
                }
            }
        }
        if (king.getField().getY() == rockPosition.getY()) {
            if (king.getField().getY() > rockPosition.getY()) {
                for (int j = king.getField().getY() - 1; j > rockPosition.getY(); j--) {
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            } else {
                for (int j = king.getField().getY() + 1; j < rockPosition.getY(); j++) {
                    fieldsBetween.add(new Field(king.getField().getX(), j));
                }
            }
        }
        return fieldsBetween;
    }

    public Set<Field> fieldsBetweenBishopAndKing(final King king, final Field bishopPosition) {
        Set<Field> fieldsBetween = new HashSet<>();
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()) {
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }
        }
        if (king.getField().getX() > bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()) {
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() - 1; i > bishopPosition.getX(); i--) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() > bishopPosition.getY()) {
            int yPosition = king.getField().getY() - 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition--;
            }

        }
        if (king.getField().getX() < bishopPosition.getX() && king.getField().getY() < bishopPosition.getY()) {
            int yPosition = king.getField().getY() + 1;
            for (int i = king.getField().getX() + 1; i < bishopPosition.getX(); i++) {
                fieldsBetween.add(new Field(i, yPosition));
                yPosition++;
            }
        }
        return fieldsBetween;
    }

    public Set<Field> fieldsBetweenQueenAndKing(final King king, final Field queenPosition) {
        Set<Field> fieldsBetweenQueenAndKing = new HashSet<>();
        if (king.getField().getX() == queenPosition.getX() || king.getField().getY() == queenPosition.getY()) {
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenRockAndKing(king, queenPosition));
        } else {
            fieldsBetweenQueenAndKing.addAll(fieldsBetweenBishopAndKing(king, queenPosition));
        }
        return fieldsBetweenQueenAndKing;
    }

    public Set<Figure> getAffectedFigures(Color color) {
        Set<Figure> acceptedFigures = new HashSet<>();
        List<Observer> observers = Board.getFigures(color);
        affectedFields.forEach(f -> {
            observers.forEach(o -> {
                if (((Figure) o).getAttackedFields().contains(f)) {
                    acceptedFigures.add((Figure) o);
                }
            });
        });
        return acceptedFigures;
    }

    private void getAffectedFields(Turn turn) {
        affectedFields = new HashSet<>();
        for (FigureToField figureToDestinationField : turn.getFigureToDestinationField()) {
            affectedFields.add(figureToDestinationField.getFigure().getField());
            affectedFields.add(figureToDestinationField.getField());
        }
    }

    public void makeTurn(Turn turn) {
        getAffectedFields(turn);
        System.out.println(affectedFields);
        System.out.println("Turn = " + turn);
        setTurnForUndoing(turn);
        for (FigureToField figuresToFields : turn.getFigureToDestinationField()) {
            board.setNewCoordinates(turn, figuresToFields.getFigure(), figuresToFields.getField(), turn.getTargetedFigure(), false, turn.isEnPassant());
        }
        makePullAdditionalAlliesAndEnemies();
    }

    public void undoTurn(Turn turn) {
        Turn undoTurn = Turn.builder()
                .figureToDestinationField(tuplesFigureToField)
                .eating(false)
                .writtenStyle("")
                .numberOfTurn(turn.getNumberOfTurn())
                .build();
        for (FigureToField figuresToFields : undoTurn.getFigureToDestinationField()) {
            board.setNewCoordinates(turn, figuresToFields.getFigure(), figuresToFields.getField(), undoTurn.getTargetedFigure(), true, turn.isEnPassant());
        }
        ProcessingUtils.eatenFigureToResurrection = null;
        makePullAdditionalAlliesAndEnemies();
    }

    private void setTurnForUndoing(Turn turn) {
        System.out.println("Turn = " + turn);
        tuplesFigureToField = new ArrayList<>();
        eatenFigureToResurrection = null;
        for (FigureToField figureToField : turn.getFigureToDestinationField()) {
            tuplesFigureToField.add(FigureToField.builder().figure(figureToField.getFigure()).field(figureToField.getFigure().getField()).build());
        }
        if (turn.isEating()) {
            if (turn.getFigureToDestinationField().size() == 1
                    && turn.getFigureToDestinationField().get(0).getFigure().getClass() == Pawn.class
                    && ((Pawn) turn.getFigureToDestinationField().get(0).getFigure()).isEnPassant()) {

                eatenFigureToResurrection = turn.getTargetedFigure().createNewFigure();
            } else {
                Figure tempFigure = Board.getFieldToFigure().get(turn.getFigureToDestinationField().get(0).getField());
                if (!turn.isEnPassant()) {
                    eatenFigureToResurrection = tempFigure.createNewFigure();
                }
            }
        }
        if (turn.isTransformation()) {
            pawnFromTransformation = turn.getFigureToDestinationField().get(0).getFigure();
        }
        figureBornFromTransformation = turn.getFigureFromTransformation();
    }

    private void makePullAdditionalAlliesAndEnemies() {
        Map<Figure, Set<Figure>> figureToChosenAllies = new HashMap<>();
        Board.getFigures().forEach(f -> {
            Set<Figure> chosenAllies = ((Figure) f).pullAdditionalAlliesAndEnemies();
            if (!isEmpty(chosenAllies)) {
                figureToChosenAllies.put((Figure) f, chosenAllies);
            }
        });
        for (int i = 0; i < SIZE; i++) {
            for (Figure curFigure : figureToChosenAllies.keySet()) {
                for (Figure ally : figureToChosenAllies.get(curFigure)) {
                    if (ally != null) {
                        doUpdate(curFigure, ally);
                    }
                }
            }
        }
    }

    private void doUpdate(Figure curFigure, Figure ally) {
        for (Figure undefendedAlly : ally.getAlliesIProtect()) {
            updateProtectionOfUndefendedAllies(curFigure, ally, undefendedAlly);
        }
        for (Figure prey : ally.getWhoCouldBeEaten()) {
            updateWhoCouldBeEaten(curFigure, ally, prey);
        }
    }

    private void updateWhoCouldBeEaten(Figure curFigure, Figure ally, Figure prey) {
        if (!curFigure.getWhoCouldBeEaten().contains(prey) && ally.getWhoCouldBeEaten().contains(prey)
                && curFigure.getAttackedFields().contains(prey.getField()) && isOnTheSameLine(curFigure, ally, prey)) {
            curFigure.getWhoCouldBeEaten().add(prey);
            prey.addEnemy(curFigure);
        }
    }

    private void updateProtectionOfUndefendedAllies(Figure curFigure, Figure ally, Figure undefendedAlly) {
        if (!curFigure.getAlliesIProtect().contains(undefendedAlly) && ally.getAlliesIProtect().contains(undefendedAlly)
                && curFigure.getAttackedFields().contains(undefendedAlly.getField()) && isOnTheSameLine(curFigure, ally, undefendedAlly)
                && !curFigure.equals(undefendedAlly) && !ally.equals(undefendedAlly)) {
            curFigure.addAllyIProtect(undefendedAlly);
            undefendedAlly.addAllyProtectMe(curFigure);
        }
    }

    private boolean isOnTheSameLine(Figure f1, Figure f2, Figure f3) {
        if (f1.getClass() == Bishop.class || f2.getClass() == Bishop.class || f3.getClass() == Bishop.class) {
            return (abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
                    && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY()))
                    && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY()));
        }
        if (f1.getClass() == Rock.class || f2.getClass() == Rock.class || f3.getClass() == Rock.class) {
            return ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX())) ||
                    ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY()));
        }
        return ((f1.getField().getX() == f2.getField().getX()) && (f2.getField().getX() == f3.getField().getX())) ||
                ((f1.getField().getY() == f2.getField().getY()) && (f2.getField().getY() == f3.getField().getY())) ||
                (((abs(f1.getField().getX() - f2.getField().getX()) == abs(f1.getField().getY() - f2.getField().getY()))
                        && (abs(f2.getField().getX() - f3.getField().getX()) == abs(f2.getField().getY() - f3.getField().getY())))
                        && (abs(f1.getField().getX() - f3.getField().getX()) == abs(f1.getField().getY() - f3.getField().getY())));
    }

    private boolean isEnPassantScenario(List<FigureToField> figureToField) {
        Pawn pawn = (Pawn) figureToField.get(0).getFigure();
        return pawn.isEnPassant();
    }

    public boolean isEmpty(Set<?> set) {
        return set == null || set.isEmpty();
    }
}