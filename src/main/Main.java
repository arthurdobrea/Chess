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
    static String[][] board;
    static boolean nextTurn = true;

    public static void main(String[] args) {
        initMap();
        createBoard();
        readMovesFromFile();
        int i = 0;
        while (nextTurn) {
            move(listOfCoordinates.get(i));
            showBoard();
            i++;
            Scanner scanner = new Scanner(System.in);
            String inputString = scanner.nextLine();
            if (i == listOfCoordinates.size()){
                return;
            }
            if (inputString == null) {
                nextTurn = false;
            }else{
                nextTurn = true;
            }
        }

    }

    public static void move(Coordinate it) {
        String figure = board[it.initialRow][it.initialColumn];
        board[it.initialRow][it.initialColumn] = "|-|";
        board[it.desiredRow][it.desiredColumn] = figure;

    }

    public static void readMovesFromFile() {
        List<String> listOfMoves = new ArrayList<>();

        Path path = Paths.get("sample-moves.txt");

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
        ChessFigure K = new ChessFigure("|" + "K" + "|");
        ChessFigure Q = new ChessFigure("|" + "Q" + "|");
        ChessFigure B = new ChessFigure("|" + "B" + "|");
        ChessFigure N = new ChessFigure("|" + "N" + "|");
        ChessFigure R = new ChessFigure("|" + "R" + "|");
        List<ChessFigure> P = new ArrayList<ChessFigure>(8);

        for (int i = 0; i < 8; i++) {
            P.add(new ChessFigure("|" + "P" + "|"));
        }

        ChessFigure k = new ChessFigure("|" + "k" + "|");
        ChessFigure q = new ChessFigure("|" + "q" + "|");
        ChessFigure b = new ChessFigure("|" + "b" + "|");
        ChessFigure n = new ChessFigure("|" + "n" + "|");
        ChessFigure r = new ChessFigure("|" + "r" + "|");
        List<ChessFigure> p = new ArrayList<ChessFigure>(8);

        for (int i = 0; i < 8; i++) {
            p.add(new ChessFigure("|" + "p" + "|"));
        }


        board = new String[][]{
                {R.name, N.name, B.name, Q.name, K.name, B.name, N.name, R.name},
                {P.get(0).name, P.get(1).name, P.get(2).name, P.get(3).name, P.get(4).name, P.get(5).name, P.get(6).name, P.get(7).name},
                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
                {"|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|", "|-|"},
                {p.get(0).name, p.get(1).name, p.get(2).name, p.get(3).name, p.get(4).name, p.get(5).name, p.get(6).name, p.get(7).name},
                {r.name, n.name, b.name, q.name, k.name, b.name, n.name, r.name},
                {"a", "b", "c", "d", "e", "f", "g", "h"}};
    }

}
