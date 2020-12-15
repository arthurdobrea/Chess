package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static main.MoveHandler.checkIfCheck;
import static main.MoveHandler.moveCanBeDone;

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
                showBoard();
                System.out.println("CHECK");
                return;
            }
            turnCounter++;
        }
    }


    public static void scanFromKeyboard() {
        System.out.println("Press enter for next move >>");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static Integer showStartGameInterface(){
        System.out.println("Choose file from above:");
        System.out.println("1 sample-moves.txt");
        System.out.println("2 sample-moves-invalid.txt");
        System.out.println("3 checkmate.txt");
        Scanner scanner = new Scanner(System.in);
        return  Integer.valueOf(scanner.nextLine());
    }

    public static void move(Coordinate it) {
        ChessFigure figure = board[it.currentRow][it.currentColumn];
        board[it.currentRow][it.currentColumn] = emptyFigureCell;
        board[it.desiredRow][it.desiredColumn] = figure;

    }

    public static void readMovesFromFile() {

        Path fileToPlay = Paths.get("sample-moves.txt");
        boolean uiFlag = true;
        while (uiFlag){
            int s = showStartGameInterface();
            switch (s){
                case 1 : fileToPlay = Paths.get("sample-moves.txt"); uiFlag = false; break;
                case 2 : fileToPlay = Paths.get("sample-moves-invalid.txt"); uiFlag = false; break;
                case 3 : fileToPlay = Paths.get("checkmate.txt"); uiFlag = false; break;
                default: System.out.println("pls choose correct number <3");
            }
        }

        List<String> listOfMoves = new ArrayList<>();

        try (Stream<String> stream = Files.lines(fileToPlay)) {
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