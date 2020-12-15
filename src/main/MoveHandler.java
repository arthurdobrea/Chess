package main;

import static main.Main.board;
import static main.Main.emptyFigureCell;

public class MoveHandler {

    private static boolean checkIfAttackedFigureIsKing(Coordinate coordinate) {
        return !board[coordinate.desiredRow][coordinate.desiredColumn].name.equalsIgnoreCase("|k|");
    }

    public static boolean checkIfCheck() {
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

}
