package jchess.model;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator{

    // Main dispatcher
    public static List<Move> getPossibleMoves(Board board, Square currentSquare){
        Piece piece = board.getPiece(currentSquare);

        if (piece == null){
            return new ArrayList<>();
        }

        switch (piece.getType()){
            case PAWN:
                return getPawnPossibleMoves(board, currentSquare, piece);
            case KNIGHT:
                return getKnightPossibleMoves(board, currentSquare, piece);
            case ROOK:
                return getRookPossibleMoves(board, currentSquare, piece);
            case BISHOP:
                return getBishopPossibleMoves(board, currentSquare, piece);
            case QUEEN:
                return getQueenPossibleMoves(board, currentSquare, piece);
            case KING:
                return getKingPossibleMoves(board, currentSquare, piece);
            default:
                return new ArrayList<>();
        }
    }
 //our old logic except castling and en passant, those are in a game mangaer because movegenerator only knows physical shape of a board without knowing context
    private static List<Move> getPawnPossibleMoves(Board board, Square currentSquare, Piece piece){
        List<Move> possibleMoves = new ArrayList<>();
        int currentRow = currentSquare.getRow();
        int currentCol = currentSquare.getCol();
        int direction;
        int startRow;

        if (piece.getColor() == PieceColor.WHITE){
            direction = -1;
            startRow = 6;
        }else{
            direction = 1;
            startRow = 1;
        }

        Square forwardOne = new Square(currentRow + direction, currentCol);
        if (forwardOne.isValid() && board.getPiece(forwardOne) == null){
            possibleMoves.add(new Move(currentSquare, forwardOne, piece, null));
            if (currentRow == startRow){
                Square forwardTwo = new Square(currentRow + (direction * 2), currentCol);
                if (forwardTwo.isValid() && board.getPiece(forwardTwo) == null){
                    possibleMoves.add(new Move(currentSquare, forwardTwo, piece, null));
                }
            }
        }

        int[] captureCols = {currentCol - 1, currentCol + 1};
        for (int col : captureCols) {
            Square captureSquare = new Square(currentRow + direction, col);
            if (captureSquare.isValid()) {
                Piece targetPiece = board.getPiece(captureSquare);
                if (targetPiece != null && targetPiece.getColor() != piece.getColor()){
                    possibleMoves.add(new Move(currentSquare, captureSquare, piece, targetPiece));
                }
            }
        }
        return possibleMoves;
    }

    private static List<Move> getRookPossibleMoves(Board board, Square currentSquare, Piece piece){
        List<Move> possibleMoves = new ArrayList<>();
        int[][] DIRECTIONS = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (int[] direction : DIRECTIONS) {
            int currentRow = currentSquare.getRow();
            int currentCol = currentSquare.getCol();
            while (true) {
                currentRow += direction[0];
                currentCol += direction[1];
                Square newSquare = new Square(currentRow, currentCol);

                if (!newSquare.isValid()){
                    break;
                }
                Piece targetPiece = board.getPiece(newSquare);

                if (targetPiece == null)
                    possibleMoves.add(new Move(currentSquare, newSquare, piece, null));
                else{
                    if (targetPiece.getColor() != piece.getColor())
                        possibleMoves.add(new Move(currentSquare, newSquare, piece, targetPiece));
                    break;
                }
            }
        }
        return possibleMoves;
    }

    private static List<Move> getKnightPossibleMoves(Board board, Square currentSquare, Piece piece){
        List<Move> possibleMoves = new ArrayList<>();
        int[][] MOVE_OFFSETS = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        int currentRow = currentSquare.getRow();
        int currentCol = currentSquare.getCol();

        for (int[] offset : MOVE_OFFSETS){
            int newRow = currentRow + offset[0];
            int newCol = currentCol + offset[1];
            Square newSquare = new Square(newRow, newCol);

            if (newSquare.isValid()){
                Piece targetPiece = board.getPiece(newSquare);
                if (targetPiece == null)
                    possibleMoves.add(new Move(currentSquare, newSquare, piece, null));
                else if (targetPiece.getColor() != piece.getColor())
                    possibleMoves.add(new Move(currentSquare, newSquare, piece, targetPiece));

            }
        }
        return possibleMoves;
    }

    private static List<Move> getBishopPossibleMoves(Board board, Square currentSquare, Piece piece){
        List<Move> possibleMoves = new ArrayList<>();
        int[][] DIRECTIONS = {
                {-1, -1}, {1, 1}, {1, -1}, {-1, 1}
        };

        for (int[] direction : DIRECTIONS){
            int currentRow = currentSquare.getRow();
            int currentCol = currentSquare.getCol();
            while (true){
                currentRow += direction[0];
                currentCol += direction[1];
                Square newSquare = new Square(currentRow, currentCol);

                if (!newSquare.isValid())
                    break;

                Piece targetPiece = board.getPiece(newSquare);

                if (targetPiece == null)
                    possibleMoves.add(new Move(currentSquare, newSquare, piece, null));
                else{
                    if (targetPiece.getColor() != piece.getColor()) {
                        possibleMoves.add(new Move(currentSquare, newSquare, piece, targetPiece));
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }

    private static List<Move> getQueenPossibleMoves(Board board, Square currentSquare, Piece piece) {
        List<Move> possibleMoves = new ArrayList<>();
        int[][] DIRECTIONS = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {1, 1}, {1, -1}, {-1, 1}
        };

        for (int[] direction : DIRECTIONS) {
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
                    possibleMoves.add(new Move(currentSquare, newSquare, piece, null));
                } else {
                    if (targetPiece.getColor() != piece.getColor()) {
                        possibleMoves.add(new Move(currentSquare, newSquare, piece, targetPiece));
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }

    private static List<Move> getKingPossibleMoves(Board board, Square currentSquare, Piece piece) {
        List<Move> possibleMoves = new ArrayList<>();
        int[][] DIRECTIONS = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {1, 1}, {1, -1}, {-1, 1}
        };

        for (int[] direction : DIRECTIONS) {
            int newRow = currentSquare.getRow() + direction[0];
            int newCol = currentSquare.getCol() + direction[1];
            Square newSquare = new Square(newRow, newCol);

            if (newSquare.isValid()) {
                Piece targetPiece = board.getPiece(newSquare);
                if (targetPiece == null) {
                    possibleMoves.add(new Move(currentSquare, newSquare, piece, null));
                } else if (targetPiece.getColor() != piece.getColor()) {
                    possibleMoves.add(new Move(currentSquare, newSquare, piece, targetPiece));
                }
            }
        }
        return possibleMoves;
    }
}