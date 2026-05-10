package jchess.model;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final Board board;
    private Piece.Color currentTurn;

    public enum GameStatus {
        ACTIVE, WHITE_WINS, BLACK_WINS, STALEMATE
    }

    private GameStatus status = GameStatus.ACTIVE;

    public GameManager() {
        this.board = new Board();
        this.currentTurn = Piece.Color.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public Piece.Color getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void playMove(Move move) {
        if (status != GameStatus.ACTIVE) {
            return;
        }
        board.movePiece(move);
        move.getPieceMoved().setHasMoved(true);
        board.setLastMove(move);
        switchTurn();
        updateGameStatus();
    }

    private void switchTurn() {
        if (currentTurn == Piece.Color.WHITE) {
            currentTurn = Piece.Color.BLACK;
        } else {
            currentTurn = Piece.Color.WHITE;
        }
    }

    public List<Move> getLegalMoves(Square square) {
        Piece piece = board.getPiece(square);
        List<Move> legalMoves = new ArrayList<>();

        if (piece == null || piece.getColor() != currentTurn) {
            return legalMoves;
        }

        List<Move> pseudoLegalMoves = piece.getPossibleMoves(board, square);
        for (Move move : pseudoLegalMoves) {
            if (isMoveSafe(move)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    private boolean isMoveSafe(Move move) {
        Square start = move.getStart();
        Square end = move.getEnd();
        Piece movingPiece = move.getPieceMoved();
        Piece capturedPiece = move.getPieceCaptured();
        // check logic when castling
        if (move.isCastling()) {
            if (isKingInCheck(movingPiece.getColor())) {
                return false;
            }
            int row = start.getRow();
            int crossedCol;
            if (end.getCol() == 6) {
                crossedCol = 5;
            } else {
                crossedCol = 3;
            }
            Square crossedSquare = new Square(row, crossedCol);
            board.setPiece(crossedSquare, movingPiece);
            board.setPiece(start, null);
            boolean isCrossedSafe = !isKingInCheck(movingPiece.getColor());
            board.setPiece(start, movingPiece);
            board.setPiece(crossedSquare, null);
            if (!isCrossedSafe) {
                return false;
            }

        }

        board.setPiece(end, movingPiece);
        board.setPiece(start, null);

        boolean isSafe = !isKingInCheck(movingPiece.getColor());

        board.setPiece(start, movingPiece);
        board.setPiece(end, capturedPiece);

        return isSafe;
    }

    private boolean isKingInCheck(Piece.Color color) {
        Square kingSquare = null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = board.getPiece(square);
                if (piece instanceof King && piece.getColor() == color) {
                    kingSquare = square;
                    break;
                }
            }
            if (kingSquare != null) {
                break;
            }
        }

        if (kingSquare == null) {
            return false;
        }

        Piece.Color enemyColor;
        if (color == Piece.Color.WHITE) {
            enemyColor = Piece.Color.BLACK;
        } else {
            enemyColor = Piece.Color.WHITE;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = board.getPiece(square);
                if (piece != null && piece.getColor() == enemyColor) {
                    List<Move> enemyMoves = piece.getPossibleMoves(board, square);
                    for (Move move : enemyMoves) {
                        if (move.getEnd().equals(kingSquare)) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }
    // checkmate and stalemate
    private void updateGameStatus() {
        boolean hasAnySafeMove = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = board.getPiece(square);
                if (piece != null && piece.getColor() == currentTurn) {
                    List<Move> possibleMoves = piece.getPossibleMoves(board, square);
                    for (Move move : possibleMoves) {
                        if (isMoveSafe(move)) {
                            hasAnySafeMove = true;
                            break;
                        }
                    }
                }
                if (hasAnySafeMove) break;
            }
            if (hasAnySafeMove) break;
        }
        if (!(hasAnySafeMove)) {
            if (isKingInCheck(currentTurn)) {
                if (currentTurn == Piece.Color.WHITE) {
                    status = GameStatus.BLACK_WINS;
                } else {
                    status = GameStatus.WHITE_WINS;
                }
            } else {
                status = GameStatus.STALEMATE;

            }
        }
    }
}
