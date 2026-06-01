package jchess.model;

public enum PieceType {
    PAWN('p', 1),
    KNIGHT('n', 3),
    BISHOP('b', 3),
    ROOK('r', 5),
    QUEEN('q', 9),
    KING('k', 0);

    private final char symbol;
    private final int value;

    PieceType(char symbol, int value){
        this.symbol = symbol;
        this.value = value;
    }

    public char getSymbol() {
        return symbol;
    }
    public int getValue() {
        return value;
    }
}