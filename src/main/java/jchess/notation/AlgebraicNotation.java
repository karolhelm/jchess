package jchess.notation;

import jchess.model.*;

import java.util.ArrayList;
import java.util.List;
public final class AlgebraicNotation {
    private static final char[] FILES = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private AlgebraicNotation() {
    }   //creates basic strign with square and piece
   public static String formatBase(Move move, GameManager game) {
        if (move.isCastling()) {
            return move.getEnd().getCol() > move.getStart().getCol() ? "O-O" : "O-O-O";
        }
        Piece piece = move.getPieceMoved();
        String dest = toSquare(move.getEnd());
        boolean capture = move.isCapture() || move.isEnPassant();
        if (piece.getType() == PieceType.PAWN) {
            StringBuilder sb = new StringBuilder();
            if (capture) {
                sb.append(FILES[move.getStart().getCol()]).append('x');
            }
            sb.append(dest);
            if (move.getPromotionPiece() != null) {
                sb.append('=').append(pieceLetter(move.getPromotionPiece().getType()));
            }
            return sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(pieceLetter(piece.getType()));
        sb.append(disambiguation(move, game));
        if (capture) {
            sb.append('x');
        }
        sb.append(dest);
        if (move.getPromotionPiece() != null) {
            sb.append('=').append(pieceLetter(move.getPromotionPiece().getType()));
        }
        return sb.toString();
    }
   //responsible for check and checkmate(+ and #)
    public static String withCheckSuffix(String base, GameManager game) {
        if (game.getStatus() != GameManager.GameStatus.ACTIVE) {
            if (game.isInCheck(game.getCurrentTurn())) {
                return base + "#";
            }
            return base;
        }
        if (game.isInCheck(game.getCurrentTurn())) {
            return base + "+";
        }
        return base;
    }
    private static char pieceLetter(PieceType type) {
        return switch (type) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            default -> throw new IllegalArgumentException("Pawn has no SAN letter");
        };
    }
    private static String toSquare(Square sq) {
        return "" + FILES[sq.getCol()] + (8 - sq.getRow());
    }// checks if theres possibility of other same piece moving to destination square if yes it adds a letter or a number to distinguish
    private static String disambiguation(Move move, GameManager game) {
        Piece piece = move.getPieceMoved();
        PieceType type = piece.getType();
        PieceColor color = piece.getColor();
        Square start = move.getStart();
        Square end = move.getEnd();
        if (type == PieceType.PAWN || type == PieceType.KING) {
            return "";
        }
        List<Square> candidates = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square sq = new Square(row, col);
                Piece p = game.getBoard().getPiece(sq);
                if (p == null || p.getColor() != color || p.getType() != type) {
                    continue;
                }
                for (Move legal : game.getLegalMoves(sq)) {
                    if (legal.getEnd().equals(end)) {
                        candidates.add(sq);
                        break;
                    }
                }
            }
        }
        if (candidates.size() <= 1) {
            return "";
        }
        boolean otherOnSameFile = false;
        boolean otherOnSameRank = false;
        for (Square sq : candidates) {
            if (sq.equals(start)) {
                continue;
            }
            if (sq.getCol() == start.getCol()) {
                otherOnSameFile = true;
            }
            if (sq.getRow() == start.getRow()) {
                otherOnSameRank = true;
            }
        }
        if (!otherOnSameFile) {
            return String.valueOf(FILES[start.getCol()]);
        }
        if (!otherOnSameRank) {
            return String.valueOf(8 - start.getRow());
        }

        return "";
    }
}