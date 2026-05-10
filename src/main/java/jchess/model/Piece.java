package jchess.model;

import java.util.List;

abstract public class Piece {
    public enum Color {
        WHITE, BLACK
    }

    protected boolean hasMoved = false;

    protected final Color color;

    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public abstract List<Move> getPossibleMoves(Board board, Square currentSquare);
}
