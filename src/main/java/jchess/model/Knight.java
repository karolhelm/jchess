package jchess.model;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    private static final int[][] MOVE_OFFSETS = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    public Knight(Color color) {
        super(color);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, Square currentSquare) {
        List<Move> possibleMoves = new ArrayList<>();
        int currentRow = currentSquare.getRow();
        int currentCol = currentSquare.getCol();
        for (int[] offset : MOVE_OFFSETS) {
            int newRow = currentRow + offset[0];
            int newCol = currentCol + offset[1];
            Square newSquare = new Square(newRow, newCol);
            if(newSquare.isValid()){
                Piece targetPiece = board.getPiece(newSquare);
                if(targetPiece == null){
                    possibleMoves.add(new Move(currentSquare, newSquare, this, null));
                } else if (targetPiece.getColor() != this.color) {
                    possibleMoves.add(new Move(currentSquare, newSquare, this, targetPiece));
                }
            }
        }
        return possibleMoves;
    }
}
