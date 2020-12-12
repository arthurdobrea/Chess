package main;

public class Coordinate {
    Integer initialRow;
    Integer initialColumn;
    Integer desiredRow;
    Integer desiredColumn;

    public Coordinate(Integer initialRow,
                       Integer initialColumn,
                       Integer desiredRow,
                       Integer desiredColumn) {
        this.initialRow = initialRow;
        this.initialColumn = initialColumn;
        this.desiredRow = desiredRow;
        this.desiredColumn = desiredColumn;

    }

    Coordinate(){};

    public Integer getInitialRow() {
        return initialRow;
    }

    public void setInitialRow(Integer initialRow) {
        this.initialRow = initialRow;
    }

    public Integer getInitialColumn() {
        return initialColumn;
    }

    public void setInitialColumn(Integer initialColumn) {
        this.initialColumn = initialColumn;
    }

    public Integer getDesiredRow() {
        return desiredRow;
    }

    public void setDesiredRow(Integer desiredRow) {
        this.desiredRow = desiredRow;
    }

    public Integer getDesiredColumn() {
        return desiredColumn;
    }

    public void setDesiredColumn(Integer desiredColumn) {
        this.desiredColumn = desiredColumn;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "initialRow=" + initialRow +
                ", initialColumn=" + initialColumn +
                ", desiredRow=" + desiredRow +
                ", desiredColumn=" + desiredColumn +
                "}\n";
    }
}
