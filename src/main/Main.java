package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    static Map<String, Integer> charsToPosition = new HashMap<>();
    static List<Coordinate> listOfCoordinates = new ArrayList<>();

    static ChessFigure[][] board;
    static ChessFigure emptyFigureCell = new ChessFigure("|-|", null);
    static int turnCounter = 0;

    public static void main(String[] args) {
        initCoordinateMap();
        createBoard();
        readMovesFromFile();
        handleRounds();
    }

    public static void handleRounds() {
        while (true) {
            showBoard();
            if (turnCounter == listOfCoordinates.size()) break;
            scanFromKeyboard();
            if (!moveCanBeDone(listOfCoordinates.get(turnCounter))) {
                System.out.println("You can not make this move");
                turnCounter++;
                continue;
            }
            move(listOfCoordinates.get(turnCounter));
            if (checkIfCheck()) {
                System.out.println("CHECK");
                showBoard();
                return;
            }
            turnCounter++;
        }
    }

    private static boolean checkIfAttackedFigureIsKing(Coordinate coordinate) {
        return !board[coordinate.desiredRow][coordinate.desiredColumn].name.equalsIgnoreCase("|k|");
    }

    private static boolean checkIfCheck() {
        Coordinate coordinateForWhiteKing = new Coordinate();
        Coordinate coordinateForBlackKing = new Coordinate();

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board[i][j].name.equals("|K|")) {
                    coordinateForWhiteKing.desiredRow = i;
                    coordinateForWhiteKing.desiredColumn = j;
                }
                if (board[i][j].name.equals("|k|")) {
                    coordinateForBlackKing.desiredRow = i;
                    coordinateForBlackKing.desiredColumn = j;
                }
            }
        }

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board[i][j] != emptyFigureCell) {
                    if (!board[i][j].team.equals(board[coordinateForWhiteKing.desiredRow][coordinateForWhiteKing.desiredColumn].team)) {
                        String figure = board[i][j].name;
                        coordinateForWhiteKing.currentRow = i;
                        coordinateForWhiteKing.currentColumn = j;
                        if (checkForMovementPattern(figure, coordinateForWhiteKing)) {
                            return true;
                        }
                    }
                    if (!board[i][j].team.equals(board[coordinateForBlackKing.desiredRow][coordinateForBlackKing.desiredColumn].team)) {
                        String figure = board[i][j].name;
                        coordinateForBlackKing.currentRow = i;
                        coordinateForBlackKing.currentColumn = j;
                        if (checkForMovementPattern(figure, coordinateForBlackKing)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkForMovementPattern(String figure, Coordinate coordinate) {
        if (figure.equalsIgnoreCase("|p|")) {
            return checkPawnMovementPattern(coordinate);
        }
        if (figure.equalsIgnoreCase("|n|")) {
            return checkKnightMovementPattern(coordinate);
        }
        if (figure.equalsIgnoreCase("|r|")) {
            return checkRookMovementPattern(coordinate);
        }
        if (figure.equalsIgnoreCase("|b|")) {
            return checkBishopMovementPattern(coordinate);
        }
        if (figure.equalsIgnoreCase("|q|")) {
            return checkRookMovementPattern(coordinate) || checkBishopMovementPattern(coordinate);
        }
        if (figure.equalsIgnoreCase("|k|")) {
            return checkKingMovementPattern(coordinate);
        }
        return false;
    }

    public static boolean checkKingMovementPattern(Coordinate coordinate) {
        return (Math.abs(coordinate.currentRow - coordinate.desiredRow) <= 1) && (Math.abs(coordinate.currentColumn - coordinate.desiredColumn) <= 1);
    }

    public static boolean checkKnightMovementPattern(Coordinate coordinate) {
        return ((Math.abs(coordinate.currentRow - coordinate.desiredRow) == 2) && ((Math.abs(coordinate.currentColumn - coordinate.desiredColumn) == 1))) ||
                ((Math.abs(coordinate.currentRow - coordinate.desiredRow) == 1) && ((Math.abs(coordinate.currentColumn - coordinate.desiredColumn) == 2)));
    }

    public static boolean checkPawnMovementPattern(Coordinate coordinate) {
        if (coordinate.currentRow == 1 || coordinate.currentRow == 6) {
            if ((Math.abs(coordinate.currentRow - coordinate.desiredRow) <= 2 && (coordinate.currentColumn.equals(coordinate.desiredColumn)))) {
                return true;
            }
        }
        if ((Math.abs(coordinate.currentRow - coordinate.desiredRow) == 1 && (coordinate.currentColumn.equals(coordinate.desiredColumn)))) {
            return true;
        }
        if ((Math.abs(coordinate.currentRow - coordinate.desiredRow) == 1 && (Math.abs(coordinate.currentColumn - coordinate.desiredColumn)) != 0)) {
            return (!board[coordinate.currentRow][coordinate.currentColumn].team.equals(board[coordinate.desiredRow][coordinate.desiredColumn].team)) &&
                    (board[coordinate.desiredRow][coordinate.desiredColumn].team != null);
        }
        return false;
    }

    public static boolean checkBishopMovementPattern(Coordinate coordinate) {
        if (Math.abs(coordinate.currentRow - coordinate.desiredRow) == Math.abs(coordinate.currentColumn - coordinate.desiredColumn)) {

            //left up
            if ((coordinate.desiredRow < coordinate.currentRow) && (coordinate.desiredColumn < coordinate.currentColumn)) {
                for (int i = 1; i < coordinate.currentRow - coordinate.desiredRow; ++i) {
                    if (board[coordinate.currentRow - i][coordinate.currentColumn - i] != emptyFigureCell) {
                        return false;
                    }
                }
                return true;
            }

            //left down
            if ((coordinate.desiredRow > coordinate.currentRow) && (coordinate.desiredColumn < coordinate.currentColumn)) {
                for (int i = 1; i < Math.abs(coordinate.currentRow - coordinate.desiredRow); ++i) {
                    if (board[coordinate.currentRow + i][coordinate.currentColumn - i] != emptyFigureCell) {
                        return false;
                    }
                }
            }

            //right up
            if ((coordinate.desiredRow < coordinate.currentRow) && (coordinate.desiredColumn > coordinate.currentColumn)) {
                for (int i = 1; i < Math.abs(coordinate.currentRow - coordinate.desiredRow); ++i) {
                    if (!board[coordinate.currentRow - i][coordinate.currentColumn + i].equals(emptyFigureCell)) {
                        return false;
                    }
                }
            }

            //right down
            if ((coordinate.desiredRow > coordinate.currentRow) && (coordinate.desiredColumn > coordinate.currentColumn)) {
                for (int i = 1; i < coordinate.currentRow - coordinate.desiredRow; ++i) {
                    if (!board[coordinate.currentRow + i][coordinate.currentColumn + i].equals(emptyFigureCell)) {
                        return false;
                    }
                }
                return true;
            }

            return true;
        }
        return false;
    }

    public static boolean checkRookMovementPattern(Coordinate coordinate) {

        if (((coordinate.currentRow - coordinate.desiredRow == 0) && Math.abs(coordinate.currentColumn - coordinate.desiredColumn) > 0)) {

            //horizontal left direction
            if (coordinate.currentColumn > coordinate.desiredColumn) {
                for (int i = 1; i < Math.abs(coordinate.currentColumn - coordinate.desiredColumn); ++i) {
                    if (!board[coordinate.currentRow][coordinate.currentColumn - i].equals(emptyFigureCell)) {
                        return false;
                    }
                }
                return true;
            }

            //horizontal right direction
            if (coordinate.currentColumn < coordinate.desiredColumn) {
                for (int i = 1; i < coordinate.currentColumn - coordinate.desiredColumn; ++i) {
                    if (!board[coordinate.currentRow][coordinate.currentColumn + i].equals(emptyFigureCell)) {
                        return false;
                    }
                }

                return true;
            }
        }

        if (((Math.abs(coordinate.currentRow - coordinate.desiredRow) > 0) && (coordinate.currentColumn - coordinate.desiredColumn) == 0)) {

            //vertical up
            if (coordinate.currentRow > coordinate.desiredRow) {
                for (int i = 1; i < Math.abs(coordinate.desiredRow - coordinate.currentRow); ++i) {
                    if (board[coordinate.currentRow - i][coordinate.currentColumn] != emptyFigureCell) {
                        return false;
                    }
                }
                return true;
            }

            //vertical down
            if (coordinate.currentRow < coordinate.desiredRow) {
                for (int i = 1; i < coordinate.desiredRow - coordinate.currentRow; ++i) {
                    if (board[coordinate.currentRow + i][coordinate.currentColumn] != emptyFigureCell) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public static boolean moveCanBeDone(Coordinate coordinate) {
        String figure = board[coordinate.currentRow][coordinate.currentColumn].name;

        return (checkForMovementPattern(figure, coordinate) &&
                checkIfFigureExistsOnThisPosition(coordinate) &&
                checkIfThereIsNoAllyFigureOnThatPosition(coordinate)) &&
                checkIfAttackedFigureIsKing(coordinate);
    }

    public static boolean checkIfFigureExistsOnThisPosition(Coordinate coordinate) {
        return !board[coordinate.currentRow][coordinate.currentColumn].equals(emptyFigureCell);
    }

    public static boolean checkIfThereIsNoAllyFigureOnThatPosition(Coordinate coordinate) {
        if (!board[coordinate.desiredRow][coordinate.desiredColumn].equals(emptyFigureCell)) {
            return !board[coordinate.currentRow][coordinate.currentColumn].team.equals(board[coordinate.desiredRow][coordinate.desiredColumn].team);
        }
        return true;
    }

    public static void scanFromKeyboard() {
        System.out.println("Press enter for next move >>");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static void move(Coordinate it) {
        ChessFigure figure = board[it.currentRow][it.currentColumn];
        board[it.currentRow][it.currentColumn] = emptyFigureCell;
        board[it.desiredRow][it.desiredColumn] = figure;

    }

    public static void readMovesFromFile() {
        List<String> listOfMoves = new ArrayList<>();

//        Path path = Paths.get("sample-moves.txt");
        Path path = Paths.get("sample-moves-invalid.txt");
//        Path path = Paths.get("checkmate.txt");

        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(listOfMoves::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String it : listOfMoves) {
            Coordinate coordinate = new Coordinate();

            coordinate.currentRow = Integer.parseInt(it.substring(1, 2)) - 1;
            coordinate.currentColumn = charsToPosition.get(it.substring(0, 1));

            coordinate.desiredRow = Integer.parseInt(it.substring(3, 4)) - 1;
            coordinate.desiredColumn = charsToPosition.get(it.substring(2, 3));

            listOfCoordinates.add(coordinate);
        }

    }

    public static void showBoard() {
        String space = " ";
        String[] positions = {space + "  " + "a" + space, space + "b" + space, space + "c" + space, space + "d" + space, space + "e" + space, space + "f" + space, space + "g" + space, space + "h" + space};
        for (int i = 0; i < 8; i++) {
            System.out.print(positions[i]);
        }
        System.out.print('\n');

        for (int i = 0; i < 8; i++) {
            System.out.print(i + 1 + space);
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
            }
            System.out.print('\n');
        }
    }

    public static void initCoordinateMap() {
        charsToPosition.put("a", 0);
        charsToPosition.put("b", 1);
        charsToPosition.put("c", 2);
        charsToPosition.put("d", 3);
        charsToPosition.put("e", 4);
        charsToPosition.put("f", 5);
        charsToPosition.put("g", 6);
        charsToPosition.put("h", 7);
    }

    public static void createBoard() {
        ChessFigure K = new ChessFigure("|" + "K" + "|", "white");
        ChessFigure Q = new ChessFigure("|" + "Q" + "|", "white");
        ChessFigure B = new ChessFigure("|" + "B" + "|", "white");
        ChessFigure N = new ChessFigure("|" + "N" + "|", "white");
        ChessFigure R = new ChessFigure("|" + "R" + "|", "white");

        List<ChessFigure> P = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            P.add(new ChessFigure("|" + "P" + "|", "white"));
        }

        ChessFigure k = new ChessFigure("|" + "k" + "|", "black");
        ChessFigure q = new ChessFigure("|" + "q" + "|", "black");
        ChessFigure b = new ChessFigure("|" + "b" + "|", "black");
        ChessFigure n = new ChessFigure("|" + "n" + "|", "black");
        ChessFigure r = new ChessFigure("|" + "r" + "|", "black");

        List<ChessFigure> p = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            p.add(new ChessFigure("|" + "p" + "|", "black"));
        }

        board = new ChessFigure[][]{
                {R, N, B, Q, K, B, N, R},
                {P.get(0), P.get(1), P.get(2), P.get(3), P.get(4), P.get(5), P.get(6), P.get(7)},
                {emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell},
                {emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell},
                {emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell},
                {emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell, emptyFigureCell},
                {p.get(0), p.get(1), p.get(2), p.get(3), p.get(4), p.get(5), p.get(6), p.get(7)},
                {r, n, b, q, k, b, n, r}};
    }

}