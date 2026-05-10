package jchess.model;
import java.util.ArrayList;
import java.util.List;
public class King extends Piece {
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {1, 1}, {1, -1}, {-1, 1}
    };
    public King(Color color) {
        super(color);
    }
    @Override
    public List<Move> getPossibleMoves(Board board, Square currentSquare) {
        List<Move> possibleMoves = new ArrayList<>();

        for (int[] direction : DIRECTIONS) {
            int newRow = currentSquare.getRow() + direction[0];
            int newCol = currentSquare.getCol() + direction[1];
            Square newSquare = new Square(newRow, newCol);
            if (newSquare.isValid()) {
                Piece targetPiece = board.getPiece(newSquare);
                if (targetPiece == null) {
                    possibleMoves.add(new Move(currentSquare, newSquare, this, null));
                }
                else if (targetPiece.getColor() != this.color) {
                    possibleMoves.add(new Move(currentSquare, newSquare, this, targetPiece));
                }
            }
        }
        return possibleMoves;
    }
}
