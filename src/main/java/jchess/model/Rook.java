package jchess.model;
import java.util.ArrayList;
import java.util.List;
public class Rook extends Piece {
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    public Rook(Color color) {
        super(color);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, Square currentSquare) {
        List<Move> possibleMoves = new ArrayList<>();
        for(int[] direction: DIRECTIONS){
            int currentRow = currentSquare.getRow();
            int currentCol = currentSquare.getCol();
            while (true) {
                currentRow += direction[0];
                currentCol += direction[1];
                Square newSquare = new Square(currentRow, currentCol);

                if (!newSquare.isValid()) {
                    break;
                }
                Piece targetPiece = board.getPiece(newSquare);

                if (targetPiece == null) {
                    possibleMoves.add(new Move(currentSquare, newSquare, this, null));
                } else {
                    if (targetPiece.getColor() != this.color) {
                        possibleMoves.add(new Move(currentSquare, newSquare, this, targetPiece));
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }
}



