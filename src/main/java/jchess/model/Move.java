package jchess.model;

public class Move {
    private final Square start;
    private final Square end;
    private final Piece pieceMoved;
    private final Piece pieceCaptured;
    private boolean isCastling;
    private boolean isEnPassant;
    private Piece promotionPiece;
    public Move(Square start, Square end, Piece pieceMoved, Piece pieceCaptured) {
        this.start = start;
        this.end = end;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;  //simple move
        this.isCastling = false;
        this.isEnPassant = false;
    }
    public Move(Square start, Square end, Piece pieceMoved, Piece pieceCaptured,
                Piece promotionPiece, boolean isEnPassant, boolean isCastling) {
        this.start = start;
        this.end = end;  //constructor for complicated moves
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
        this.promotionPiece = promotionPiece;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
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

}
