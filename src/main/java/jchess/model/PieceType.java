package jchess.model;

public enum PieceType {
    PAWN("pawn",'p', 1),
    KNIGHT("knight",'n', 3),
    BISHOP("bishop",'b', 3),
    ROOK("rook",'r', 5),
    QUEEN("queen",'q', 9),
    KING("king",'k', 0);
    private final String pieceName;
    private final char symbol;
    private final int value;

    PieceType(String pieceName,char symbol, int value){
        this.pieceName = pieceName;
        this.symbol = symbol;
        this.value = value;
    }

    public char getSymbol() {
        return symbol;
    }
    public int getValue() {
        return value;
    }
    public String getPieceName() { return pieceName; }
}