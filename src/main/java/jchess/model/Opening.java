package jchess.model;
public class Opening {
    private final String name;
    private final String[] fens;
    public Opening(String name, String[] fens) {
        this.name = name;
        this.fens = fens;
    }
    public String getName() {
        return name;
    }
    public String getFen(int step) {
        return fens[step];
    }
    public int getStepCount() {
        return fens.length;
    }
}