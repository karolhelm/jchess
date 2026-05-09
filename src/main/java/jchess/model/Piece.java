package jchess.model;

import java.util.List;

abstract public class Piece {
    public enum Color {
        WHITE, BLACK
    }

    protected final Color color;

    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    //Lista Wszystkich ruchow, (tez tych nielegalnych)
    public abstract List<Move> getPossibleMoves(Board board, Square currentSquare);
}
