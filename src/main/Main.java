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
    static String emptyCell = "|-|";

    public static void main(String[] args) {
        initMap();
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
            turnCounter++;
        }
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
        if ((Math.abs(coordinate.initialRow - coordinate.desiredRow) <= 1) && (Math.abs(coordinate.initialColumn - coordinate.desiredColumn) <= 1)) {
            return true;
        }
        return false;
    }

    public static boolean checkKnightMovementPattern(Coordinate coordinate) {
        return ((Math.abs(coordinate.initialRow - coordinate.desiredRow) == 2) && ((Math.abs(coordinate.initialColumn - coordinate.desiredColumn) == 1))) ||
                ((Math.abs(coordinate.initialRow - coordinate.desiredRow) == 1) && ((Math.abs(coordinate.initialColumn - coordinate.desiredColumn) == 2)));
    }

    public static boolean checkPawnMovementPattern(Coordinate coordinate) {
        if (coordinate.initialRow == 1 || coordinate.initialRow == 6) {
            if ((Math.abs(coordinate.initialRow - coordinate.desiredRow) <= 2 && (coordinate.initialColumn == coordinate.desiredColumn))) {
                return true;
            }
        }
        if ((Math.abs(coordinate.initialRow - coordinate.desiredRow) == 1 && (coordinate.initialColumn == coordinate.desiredColumn))) {
            return true;
        }
        return false;
    }

    public static boolean checkBishopMovementPattern(Coordinate coordinate) {
        if (Math.abs(coordinate.initialRow - coordinate.desiredRow) == Math.abs(coordinate.initialColumn - coordinate.desiredColumn)) {

            //Лево вверх
            if ((coordinate.desiredRow < coordinate.initialRow) && (coordinate.desiredColumn < coordinate.initialColumn)) {
                for (int i = 1; i <= coordinate.initialRow - coordinate.desiredRow; ++i) {
                    if (board[coordinate.initialRow - i][coordinate.initialColumn - i].name != emptyCell) {
                        return false;
                    }
                }
                return true;
            }

            //Лево вниз
            if ((coordinate.desiredRow > coordinate.initialRow) && (coordinate.desiredColumn < coordinate.initialColumn)) {
                for (int i = 1; i <= Math.abs(coordinate.initialRow - coordinate.desiredRow); ++i) {
                    if (board[coordinate.initialRow + i][coordinate.initialColumn - i].name != emptyCell) {
                        return false;
                    }
                }
            }

            //Право вверх
            if ((coordinate.desiredRow < coordinate.initialRow) && (coordinate.desiredColumn > coordinate.initialColumn)) {
                for (int i = 1; i <= Math.abs(coordinate.initialRow - coordinate.desiredRow); ++i) {
                    if (board[coordinate.initialRow - i][coordinate.initialColumn + i].name != emptyCell) {
                        return false;
                    }
                }
            }

            //Право вниз
            if ((coordinate.desiredRow > coordinate.initialRow) && (coordinate.desiredColumn > coordinate.initialColumn)) {
                for (int i = 1; i <= coordinate.initialRow - coordinate.desiredRow; ++i) {
                    if (board[coordinate.initialRow + i][coordinate.initialColumn + i].name != emptyCell) {
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

        //horizontal can be optimized
        if (((coordinate.initialRow - coordinate.desiredRow == 0) && Math.abs(coordinate.initialColumn - coordinate.desiredColumn) > 0)) {

            //horizontal лево
            if (coordinate.initialColumn > coordinate.desiredColumn) {
                for (int i = 1; i < Math.abs(coordinate.desiredColumn - coordinate.desiredColumn); ++i) {
                    if (board[coordinate.initialRow][coordinate.initialColumn - i].name != emptyCell) {
                        return false;
                    }
                }
                return true;
            }

            //horizontal право
            if (coordinate.initialColumn < coordinate.desiredColumn) {
                for (int i = 1; i < coordinate.desiredColumn - coordinate.desiredColumn; ++i) {
                    if (board[coordinate.initialRow][coordinate.initialColumn + i].name != emptyCell) {
                        return false;
                    }
                }

                return true;
            }
        }

        //vertical can be optimized
        if (((Math.abs(coordinate.initialRow - coordinate.desiredRow) > 0) && (coordinate.initialColumn - coordinate.desiredColumn) == 0)) {

            //vertical вверх
            if (coordinate.initialRow > coordinate.desiredRow) {
                for (int i = 1; i < Math.abs(coordinate.desiredRow - coordinate.initialRow); ++i) {
                    if (board[coordinate.initialRow - i][coordinate.initialColumn].name != emptyCell) {
                        return false;
                    }
                }
                return true;
            }

            //vertical вниз
            if (coordinate.initialRow < coordinate.desiredRow) {
                for (int i = 1; i < coordinate.desiredRow - coordinate.initialRow; ++i) {
                    if (board[coordinate.initialRow + i][coordinate.initialColumn].name != emptyCell) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public static boolean moveCanBeDone(Coordinate coordinate) {
        String figure = board[coordinate.initialRow][coordinate.initialColumn].name;

        return (checkForMovementPattern(figure, coordinate) &&
                checkIfFigureExistsOnThisPosition(coordinate) &&
                checkIfThereIsNoAllyFigureOnThatPosition(coordinate));
    }

    public static boolean checkIfFigureExistsOnThisPosition(Coordinate coordinate) {
        return !board[coordinate.initialRow][coordinate.initialColumn].equals(emptyCell);
    }

    public static boolean checkIfThereIsNoAllyFigureOnThatPosition(Coordinate coordinate) {
        if (!board[coordinate.desiredRow][coordinate.desiredColumn].equals(emptyCell)) {
            if (board[coordinate.initialRow][coordinate.initialColumn].team.equals(board[coordinate.desiredRow][coordinate.desiredColumn].team)) {
                return false;
            }else{
                return true;
            }
        }
        return true;
    }

    public static String scanFromKeyboard() {
        System.out.println("Press enter for next move >>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        return line;
    }

    public static void move(Coordinate it) {
        ChessFigure figure = board[it.initialRow][it.initialColumn];
        board[it.initialRow][it.initialColumn] = emptyFigureCell;
        board[it.desiredRow][it.desiredColumn] = figure;

    }

    public static void readMovesFromFile() {
        List<String> listOfMoves = new ArrayList<>();

//        Path path = Paths.get("sample-moves.txt");
//        Path path = Paths.get("sample-moves-invalid.txt");
//        Path path = Paths.get("checkmate.txt");
        Path path = Paths.get("custom.txt");

        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(listOfMoves::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String it : listOfMoves) {
            Coordinate coordinate = new Coordinate();

            coordinate.initialRow = Integer.parseInt(it.substring(1, 2)) - 1;
            coordinate.initialColumn = charsToPosition.get(it.substring(0, 1));

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

    public static void initMap() {
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

        List<ChessFigure> P = new ArrayList<ChessFigure>(8);
        for (int i = 0; i < 8; i++) {
            P.add(new ChessFigure("|" + "P" + "|", "white"));
        }

        ChessFigure k = new ChessFigure("|" + "k" + "|", "black");
        ChessFigure q = new ChessFigure("|" + "q" + "|", "black");
        ChessFigure b = new ChessFigure("|" + "b" + "|", "black");
        ChessFigure n = new ChessFigure("|" + "n" + "|", "black");
        ChessFigure r = new ChessFigure("|" + "r" + "|", "black");

        List<ChessFigure> p = new ArrayList<ChessFigure>(8);
        for (int i = 0; i < 8; i++) {
            p.add(new ChessFigure("|" + "p" + "|", "black"));
        }


//        board = new String[][]{
//                {R.name, N.name, B.name, Q.name, K.name, B.name, N.name, R.name},
//                {P.get(0).name, P.get(1).name, P.get(2).name, P.get(3).name, P.get(4).name, P.get(5).name, P.get(6).name, P.get(7).name},
//                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
//                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
//                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
//                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
//                {p.get(0).name, p.get(1).name, p.get(2).name, p.get(3).name, p.get(4).name, p.get(5).name, p.get(6).name, p.get(7).name},
//                {r.name, n.name, b.name, q.name, k.name, b.name, n.name, r.name}};

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
