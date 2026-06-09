package jchess.model;

public class Move {
    private final Square start;
    private final Square end;
    private final Piece pieceMoved;
    private final Piece pieceCaptured;
    private boolean isCastling;
    private boolean isEnPassant;
    private Piece promotionPiece;
    private final Square rookStart;
    private final Square rookEnd;

    public Move(Square start, Square end, Piece pieceMoved, Piece pieceCaptured) {
        this.start = start;
        this.end = end;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
        this.isCastling = false;
        this.isEnPassant = false;
        this.promotionPiece = null;
        this.rookStart = null;
        this.rookEnd = null;
    }

    public Move(Square start, Square end, Piece pieceMoved, Piece pieceCaptured,
                Piece promotionPiece, boolean isEnPassant, boolean isCastling) {
        this(start, end, pieceMoved, pieceCaptured, promotionPiece, isEnPassant, isCastling, null, null);
    }

    public Move(Square start, Square end, Piece pieceMoved, Piece pieceCaptured,
                Piece promotionPiece, boolean isEnPassant, boolean isCastling,
                Square rookStart, Square rookEnd) {
        this.start = start;
        this.end = end;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
        this.promotionPiece = promotionPiece;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
        this.rookStart = rookStart;
        this.rookEnd = rookEnd;
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
    public Piece getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(Piece promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

    public Square getRookStart() {
        return rookStart;
    }

    public Square getRookEnd() {
        return rookEnd;
    }

}
