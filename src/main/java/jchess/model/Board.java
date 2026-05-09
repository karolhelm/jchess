package jchess.model;

public class Board {
    private Piece[][] grid;

    public Board(){
        this.grid = new Piece[8][8];
        InitializeBoard();
    }

    private void InitializeBoard(){
     // will return board with pieces in starting postions waiting for all pieces to be implemented
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
        }
    }

    public boolean isEmpty(Square square) {
        if (square.isValid()) {
            return grid[square.getRow()][square.getCol()] == null;
        }
        return false;
    }
}
