package main;

public class Coordinate {
    Integer currentRow;
    Integer currentColumn;
    Integer desiredRow;
    Integer desiredColumn;

    Coordinate(){}

    @Override
    public String toString() {
        return "Coordinate{" +
                "initialRow=" + currentRow +
                ", initialColumn=" + currentColumn +
                ", desiredRow=" + desiredRow +
                ", desiredColumn=" + desiredColumn +
                "}\n";
    }
}
