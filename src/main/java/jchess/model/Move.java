package jchess.model;

public class Move {
    private final Square start;
    private final Square end;
    private final Piece pieceMoved;
    private final Piece pieceCaptured;
    private boolean isCastling;
    private boolean isEnPassant;

    public Move(Square start, Square end, Piece pieceMoved, Piece pieceCaptured) {
        this.start = start;
        this.end = end;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
        this.isCastling = false;
        this.isEnPassant = false;
    }

    public Square getStart() {
        return start;
    }

    public Square getEnd() {
        return end;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public Piece getPieceCaptured() {
        return pieceCaptured;
    }
    public boolean isCastling() {
        return isCastling;
    }

    public void setCastling(boolean castling) {
        isCastling = castling;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }
    public boolean isCapture() {
        return pieceCaptured != null;
    }

}
