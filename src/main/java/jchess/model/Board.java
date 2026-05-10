package jchess.model;

public class Board {
    private Piece[][] grid;
    private Move lastMove;
    public Move getLastMove() {
        return lastMove;
    }
    public void setLastMove(Move move) {
        this.lastMove = move;
    }
    public Board(){
        this.grid = new Piece[8][8];
        initializeBoard();
    }

    private void initializeBoard(){
        grid[0][0] = new Rook(Piece.Color.WHITE);
        grid[0][7] = new Rook(Piece.Color.WHITE);
        grid[0][1] = new Knight(Piece.Color.WHITE);
        grid[0][6] = new Knight(Piece.Color.WHITE);
        grid[0][2] = new Bishop(Piece.Color.WHITE);
        grid[0][5] = new Bishop(Piece.Color.WHITE);
        grid[0][3] = new Queen(Piece.Color.WHITE);
        grid[0][4] = new King(Piece.Color.WHITE);
        for(int col = 0; col < 8; col++){
            grid[1][col] = new Pawn(Piece.Color.WHITE);
        }
        grid[7][0] = new Rook(Piece.Color.BLACK);
        grid[7][7] = new Rook(Piece.Color.BLACK);
        grid[7][1] = new Knight(Piece.Color.BLACK);
        grid[7][6] = new Knight(Piece.Color.BLACK);
        grid[7][2] = new Bishop(Piece.Color.BLACK);
        grid[7][5] = new Bishop(Piece.Color.BLACK);
        grid[7][3] = new Queen(Piece.Color.BLACK);
        grid[7][4] = new King(Piece.Color.BLACK);
        for(int col = 0; col < 8; col++){
            grid[6][col] = new Pawn(Piece.Color.BLACK);
        }
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
        }
    }

    public boolean isEmpty(Square square) {
        if (square.isValid()) {
            return grid[square.getRow()][square.getCol()] == null;
        }
        return false;
    }
}
