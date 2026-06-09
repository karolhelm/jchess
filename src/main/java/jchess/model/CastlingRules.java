package jchess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CastlingRules {
    private static final int KINGSIDE_KING_COL = 6;
    private static final int QUEENSIDE_KING_COL = 2;
    private static final int KINGSIDE_ROOK_COL = 5;
    private static final int QUEENSIDE_ROOK_COL = 3;

    private final Board board;
    private final Predicate<PieceColor> isKingInCheck;

    private boolean whiteCastleKingside;
    private boolean whiteCastleQueenside;
    private boolean blackCastleKingside;
    private boolean blackCastleQueenside;
    private int whiteKingStartCol = 4;
    private int whiteKingsideRookStartCol = 7;
    private int whiteQueensideRookStartCol = 0;
    private int blackKingStartCol = 4;
    private int blackKingsideRookStartCol = 7;
    private int blackQueensideRookStartCol = 0;

    public CastlingRules(Board board, Predicate<PieceColor> isKingInCheck) {
        this.board = board;
        this.isKingInCheck = isKingInCheck;
    }

    public void setWhiteCastleKingside(boolean value) { whiteCastleKingside = value; }
    public void setWhiteCastleQueenside(boolean value) { whiteCastleQueenside = value; }
    public void setBlackCastleKingside(boolean value) { blackCastleKingside = value; }
    public void setBlackCastleQueenside(boolean value) { blackCastleQueenside = value; }
    public boolean isWhiteCastleKingside() { return whiteCastleKingside; }
    public boolean isWhiteCastleQueenside() { return whiteCastleQueenside; }
    public boolean isBlackCastleKingside() { return blackCastleKingside; }
    public boolean isBlackCastleQueenside() { return blackCastleQueenside; }

    public void initFromBackRank() {
        initForColor(PieceColor.WHITE, 7);
        initForColor(PieceColor.BLACK, 0);
    }

    private void initForColor(PieceColor color, int row) {
        int kingCol = -1;
        int queensideRookCol = -1;
        int kingsideRookCol = -1;

        for (int col = 0; col < 8; col++) {
            Piece piece = board.getPiece(new Square(row, col));
            if (piece != null && piece.getColor() == color && piece.getType() == PieceType.KING) {
                kingCol = col;
            }
        }

        for (int col = 0; col < 8; col++) {
            Piece piece = board.getPiece(new Square(row, col));
            if (piece != null && piece.getColor() == color && piece.getType() == PieceType.ROOK) {
                if (col < kingCol) {
                    queensideRookCol = col;
                } else if (col > kingCol) {
                    kingsideRookCol = col;
                }
            }
        }

        if (color == PieceColor.WHITE) {
            whiteKingStartCol = kingCol;
            whiteQueensideRookStartCol = queensideRookCol;
            whiteKingsideRookStartCol = kingsideRookCol;
        } else {
            blackKingStartCol = kingCol;
            blackQueensideRookStartCol = queensideRookCol;
            blackKingsideRookStartCol = kingsideRookCol;
        }
    }

    public void syncRights() {
        syncRightsForColor(PieceColor.WHITE, 7);
        syncRightsForColor(PieceColor.BLACK, 0);
    }

    private void syncRightsForColor(PieceColor color, int row) {
        int kingStartCol = color == PieceColor.WHITE ? whiteKingStartCol : blackKingStartCol;
        int kingsideRookStartCol = color == PieceColor.WHITE ? whiteKingsideRookStartCol : blackKingsideRookStartCol;
        int queensideRookStartCol = color == PieceColor.WHITE ? whiteQueensideRookStartCol : blackQueensideRookStartCol;

        boolean kingOnStart = isPieceOnSquare(row, kingStartCol, color, PieceType.KING);
        boolean kingsideRookOnStart = isPieceOnSquare(row, kingsideRookStartCol, color, PieceType.ROOK);
        boolean queensideRookOnStart = isPieceOnSquare(row, queensideRookStartCol, color, PieceType.ROOK);

        if (color == PieceColor.WHITE) {
            if (!kingOnStart || !kingsideRookOnStart) {
                whiteCastleKingside = false;
            }
            if (!kingOnStart || !queensideRookOnStart) {
                whiteCastleQueenside = false;
            }
        } else {
            if (!kingOnStart || !kingsideRookOnStart) {
                blackCastleKingside = false;
            }
            if (!kingOnStart || !queensideRookOnStart) {
                blackCastleQueenside = false;
            }
        }
    }

    public List<Move> getMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        int row = color == PieceColor.WHITE ? 7 : 0;
        int kingStartCol = color == PieceColor.WHITE ? whiteKingStartCol : blackKingStartCol;
        int kingsideRookStartCol = color == PieceColor.WHITE ? whiteKingsideRookStartCol : blackKingsideRookStartCol;
        int queensideRookStartCol = color == PieceColor.WHITE ? whiteQueensideRookStartCol : blackQueensideRookStartCol;
        boolean canKingside = color == PieceColor.WHITE ? whiteCastleKingside : blackCastleKingside;
        boolean canQueenside = color == PieceColor.WHITE ? whiteCastleQueenside : blackCastleQueenside;

        Square kingStart = new Square(row, kingStartCol);
        Piece king = board.getPiece(kingStart);
        if (king == null || king.getType() != PieceType.KING || king.getColor() != color) {
            return moves;
        }

        if (canKingside && isRookReady(row, kingsideRookStartCol, color)
                && areSquaresEmpty(row, kingStartCol, kingsideRookStartCol, KINGSIDE_KING_COL, KINGSIDE_ROOK_COL)) {
            Square kingEnd = new Square(row, KINGSIDE_KING_COL);
            Square rookStart = new Square(row, kingsideRookStartCol);
            Square rookEnd = new Square(row, KINGSIDE_ROOK_COL);
            moves.add(new Move(kingStart, kingEnd, king, null, null, false, true, rookStart, rookEnd));
        }

        if (canQueenside && isRookReady(row, queensideRookStartCol, color)
                && areSquaresEmpty(row, kingStartCol, queensideRookStartCol, QUEENSIDE_KING_COL, QUEENSIDE_ROOK_COL)) {
            Square kingEnd = new Square(row, QUEENSIDE_KING_COL);
            Square rookStart = new Square(row, queensideRookStartCol);
            Square rookEnd = new Square(row, QUEENSIDE_ROOK_COL);
            moves.add(new Move(kingStart, kingEnd, king, null, null, false, true, rookStart, rookEnd));
        }

        return moves;
    }

    public boolean isMoveSafe(Move move) {
        if (isKingInCheck.test(move.getPieceMoved().getColor())) {
            return false;
        }

        Square start = move.getStart();
        Square end = move.getEnd();
        Square rookStart = move.getRookStart();
        Square rookEnd = move.getRookEnd();
        Piece king = move.getPieceMoved();
        int row = start.getRow();
        int step = end.getCol() > start.getCol() ? 1 : -1;

        for (int col = start.getCol() + step; col != end.getCol() + step; col += step) {
            Square crossedSquare = new Square(row, col);
            Piece savedPiece = board.getPiece(crossedSquare);
            board.setPiece(crossedSquare, king);
            board.setPiece(start, null);
            boolean isCrossedSafe = !isKingInCheck.test(king.getColor());
            board.setPiece(start, king);
            board.setPiece(crossedSquare, savedPiece);
            if (!isCrossedSafe) {
                return false;
            }
        }

        Piece savedKingStart = board.getPiece(start);
        Piece savedKingEnd = board.getPiece(end);
        Piece savedRookStart = board.getPiece(rookStart);
        Piece savedRookEnd = board.getPiece(rookEnd);

        board.setPiece(start, null);
        board.setPiece(rookStart, null);
        board.setPiece(end, king);
        board.setPiece(rookEnd, savedRookStart);

        boolean isSafe = !isKingInCheck.test(king.getColor());

        board.setPiece(start, savedKingStart);
        board.setPiece(end, savedKingEnd);
        board.setPiece(rookStart, savedRookStart);
        board.setPiece(rookEnd, savedRookEnd);

        return isSafe;
    }

    public List<Move> removeKingStepsCoveredByCastling(List<Move> moves) {
        List<Move> result = new ArrayList<>();
        for (Move move : moves) {
            if (move.isCastling()) {
                result.add(move);
            }
        }
        for (Move move : moves) {
            if (move.isCastling()) {
                continue;
            }
            if (move.getPieceMoved().getType() == PieceType.KING && isKingStepCoveredByCastling(move.getEnd(), result)) {
                continue;
            }
            result.add(move);
        }
        return result;
    }

    private boolean isKingStepCoveredByCastling(Square end, List<Move> castlingMoves) {
        for (Move castling : castlingMoves) {
            if (castling.getEnd().equals(end)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPieceOnSquare(int row, int col, PieceColor color, PieceType type) {
        Piece piece = board.getPiece(new Square(row, col));
        return piece != null && piece.getColor() == color && piece.getType() == type;
    }

    private boolean isRookReady(int row, int rookStartCol, PieceColor color) {
        Piece rook = board.getPiece(new Square(row, rookStartCol));
        return rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == color;
    }

    private boolean areSquaresEmpty(int row, int kingStartCol, int rookStartCol, int kingEndCol, int rookEndCol) {
        int min = Math.min(kingStartCol, rookStartCol);
        int max = Math.max(kingStartCol, rookStartCol);
        for (int col = min + 1; col < max; col++) {
            if (col == kingStartCol || col == rookStartCol) {
                continue;
            }
            if (!board.isEmpty(new Square(row, col))) {
                return false;
            }
        }
        return isKingLandingClear(row, kingEndCol, rookStartCol, kingStartCol)
                && isRookLandingClear(row, rookEndCol, rookStartCol, kingStartCol);
    }

    private boolean isRookLandingClear(int row, int rookEndCol, int rookStartCol, int kingStartCol) {
        Square landing = new Square(row, rookEndCol);
        if (board.isEmpty(landing)) {
            return true;
        }
        Piece piece = board.getPiece(landing);
        if (piece == null) {
            return false;
        }
        if (rookEndCol == rookStartCol && piece.getType() == PieceType.ROOK) {
            return true;
        }
        if (rookEndCol == kingStartCol && piece.getType() == PieceType.KING) {
            return true;
        }
        return false;
    }

    private boolean isKingLandingClear(int row, int kingEndCol, int rookStartCol, int kingStartCol) {
        Square landing = new Square(row, kingEndCol);
        if (board.isEmpty(landing)) {
            return true;
        }
        Piece piece = board.getPiece(landing);
        if (piece == null) {
            return false;
        }
        if (kingEndCol == kingStartCol && piece.getType() == PieceType.KING) {
            return true;
        }
        if (kingEndCol == rookStartCol && piece.getType() == PieceType.ROOK) {
            return true;
        }
        return false;
    }
}
