package main;

public class ChessFigure {
    public String name;
    public String team;

    public ChessFigure(String name, String team) {
        this.team = team;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
