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
        if(!this.hasMoved){
            int row = currentSquare.getRow();
            Square kingsideRookSquare = new Square(row, 7);
            Piece kingsideRook = board.getPiece(kingsideRookSquare);
            if(kingsideRook instanceof Rook && !kingsideRook.hasMoved){
                if (board.isEmpty(new Square(row, 5)) && board.isEmpty(new Square(row, 6))) {
                    Square castlingTarget = new Square(row, 6);
                    Move castlingMove = new Move(currentSquare, castlingTarget, this, null);
                    castlingMove.setCastling(true);
                    possibleMoves.add(castlingMove);
                }
            }

            Square queensideRookSquare = new Square(row, 0);
            Piece queensideRook = board.getPiece(queensideRookSquare);

            if (queensideRook instanceof Rook && !queensideRook.hasMoved) {
                if (board.isEmpty(new Square(row, 1)) &&
                        board.isEmpty(new Square(row, 2)) &&
                        board.isEmpty(new Square(row, 3))) {

                    Square castlingTarget = new Square(row, 2);
                    Move castlingMove = new Move(currentSquare, castlingTarget, this, null);
                    castlingMove.setCastling(true);
                    possibleMoves.add(castlingMove);
                }
            }
        }
        return possibleMoves;
    }
}
