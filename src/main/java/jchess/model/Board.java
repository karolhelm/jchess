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
            grid[start.getRow()][start.getCol()] = null;
            grid[end.getRow()][end.getCol()] = movingPiece;
            if(move.isCastling()){
                int row = start.getRow();
                if (end.getCol() == 6) {
                    Piece rook = grid[row][7];
                    grid[row][7] = null;
                    grid[row][5] = rook;
                }
                else if (end.getCol() == 2) {
                    Piece rook = grid[row][0];
                    grid[row][0] = null;
                    grid[row][3] = rook;
                }
            } else if(move.isEnPassant()){
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

    public boolean isEmpty(Square square) {
        if (square.isValid()) {
            return grid[square.getRow()][square.getCol()] == null;
        }
        return false;
    }
}
