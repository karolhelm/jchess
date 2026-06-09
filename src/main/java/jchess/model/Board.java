package jchess.model;

public class Board {
    private Piece[][] grid;
    private Move lastMove;
    public Board(){
        this.grid = new Piece[8][8];
    }

    public Piece getPiece(Square square){
        if(square.isValid()) {
            return grid[square.getRow()][square.getCol()];
        }
        return null;
    }

    public void setPiece(Square square, Piece piece) {
        if (square.isValid()) {
            grid[square.getRow()][square.getCol()] = piece;
        }
    }
    public Move getLastMove() {
        return lastMove;
    }
    public void setLastMove(Move move) {
        this.lastMove = move;
    }

    public void movePiece(Move move){
        Square start = move.getStart();
        Square end = move.getEnd();
        Piece movingPiece = move.getPieceMoved();
        if(start.isValid() && end.isValid()){
            if (move.isCastling()) {
                Square rookStart = move.getRookStart();
                Square rookEnd = move.getRookEnd();
                Piece rook = grid[rookStart.getRow()][rookStart.getCol()];
                grid[start.getRow()][start.getCol()] = null;
                grid[rookStart.getRow()][rookStart.getCol()] = null;
                grid[end.getRow()][end.getCol()] = movingPiece;
                grid[rookEnd.getRow()][rookEnd.getCol()] = rook;
            } else {
            grid[start.getRow()][start.getCol()] = null;
            grid[end.getRow()][end.getCol()] = movingPiece;
            if(move.isEnPassant()){
                int capturedPawnRow = start.getRow();
                int capturedPawnCol = end.getCol();
                grid[capturedPawnRow][capturedPawnCol] = null;
            }
            if (movingPiece.getType() ==PieceType.PAWN) {
                int endRow = end.getRow();
                int endCol = end.getCol();
                if (endRow == 0 || endRow == 7) {
                    if (move.getPromotionPiece() != null) {
                        grid[endRow][endCol] = move.getPromotionPiece();
                    } else {
                        Piece defaultQueen = (movingPiece.getColor() == PieceColor.WHITE) ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
                        grid[endRow][end.getCol()] = defaultQueen;
                    }
                }
            }
            }
        }
    }
    public void undoMovePiece(Move move) {
        Square start = move.getStart();
        Square end = move.getEnd();
        Piece movingPiece = move.getPieceMoved();
        Piece capturedPiece = move.getPieceCaptured();
        grid[start.getRow()][start.getCol()] = movingPiece;

        if (move.isEnPassant()) {
            grid[end.getRow()][end.getCol()] = null;
            int capturedPawnRow = start.getRow();
            int capturedPawnCol = end.getCol();
            grid[capturedPawnRow][capturedPawnCol] = capturedPiece;
        } else {
            grid[end.getRow()][end.getCol()] = capturedPiece;
        }
        if (move.isCastling()) {
            Square rookStart = move.getRookStart();
            Square rookEnd = move.getRookEnd();
            Piece rook = grid[rookEnd.getRow()][rookEnd.getCol()];
            grid[rookEnd.getRow()][rookEnd.getCol()] = null;
            grid[rookStart.getRow()][rookStart.getCol()] = rook;
        }
    }
    public boolean isEmpty(Square square) {
        if (square.isValid()) {
            return grid[square.getRow()][square.getCol()] == null;
        }
        return false;
    }
}
