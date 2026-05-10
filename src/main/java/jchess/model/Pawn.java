package jchess.model;
import java.util.ArrayList;
import java.util.List;
public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, Square currentSquare) {
        List<Move> possibleMoves = new ArrayList<>();
        int currentRow = currentSquare.getRow();
        int currentCol = currentSquare.getCol();
        int direction;
        int startRow;
        if(this.color==Color.WHITE){
            direction=1;
            startRow=1;
        }
        else{
            direction=-1;
            startRow=6;
        }
        Square forwardOne = new Square(currentRow + direction, currentCol);
        if (forwardOne.isValid() && board.getPiece(forwardOne) == null) {
            possibleMoves.add(new Move(currentSquare, forwardOne, this, null));
            if (currentRow == startRow) {
                Square forwardTwo = new Square(currentRow + (direction * 2), currentCol);
                if (forwardTwo.isValid() && board.getPiece(forwardTwo) == null) {
                    possibleMoves.add(new Move(currentSquare, forwardTwo, this, null));
                }
            }
        }
        int[] captureCols = {currentCol - 1, currentCol + 1};
        for (int col : captureCols) {
            Square captureSquare = new Square(currentRow + direction, col);
            if (captureSquare.isValid()) {
                Piece targetPiece = board.getPiece(captureSquare);
                if (targetPiece != null && targetPiece.getColor() != this.color) {
                    possibleMoves.add(new Move(currentSquare, captureSquare, this, targetPiece));
                }
            }
        }
        Move lastMove = board.getLastMove();
        if (lastMove!=null&&lastMove.getPieceMoved() instanceof Pawn){
            int lastMoveStartRow=lastMove.getStart().getRow();
            int lastMoveEndRow=lastMove.getEnd().getRow();
            int lastMoveCol=lastMove.getEnd().getCol();
            if(Math.abs(lastMoveStartRow-lastMoveEndRow)==2){
                if (lastMoveEndRow == currentRow && Math.abs(lastMoveCol - currentCol) == 1){
                    Square enPassantTarget = new Square(currentRow + direction, lastMoveCol);
                    Move epMove = new Move(currentSquare, enPassantTarget, this, lastMove.getPieceMoved());
                    epMove.setEnPassant(true);
                    possibleMoves.add(epMove);
                }
            }
        }

        return possibleMoves;
    }
}
