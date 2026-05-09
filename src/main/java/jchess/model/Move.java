package jchess.model;

public class Move {
    private final Square start;
    private final Square end;
    private final Piece pieceMoved;
    private final Piece pieceCaptured;

    public Move(Square start, Square end, Piece pieceMoved, Piece pieceCaptured) {
        this.start = start;
        this.end = end;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
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

    public boolean isCapture() {
        return pieceCaptured != null;
    }

}
