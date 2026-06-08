package jchess.model;

public class BoardEvaluator {

    // Wartości bazowe figur
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;

    // Fazy gry dla płynnego przejścia (Tapered Evaluation)
    // Maksymalna faza to 24 (4x Skoczek=4, 4x Goniec=4, 4x Wieża=8, 2x Hetman=8)
    private static final int PHASE_WEIGHT_KNIGHT = 1;
    private static final int PHASE_WEIGHT_BISHOP = 1;
    private static final int PHASE_WEIGHT_ROOK = 2;
    private static final int PHASE_WEIGHT_QUEEN = 4;
    private static final int MAX_PHASE = 24;

    // --- TABLICE DLA GRY ŚRODKOWEJ (Midgame - MG) ---
    private static final int[][] PAWN_MG_PST = {
            {  0,  0,  0,  0,  0,  0,  0,  0},
            { 50, 50, 50, 50, 50, 50, 50, 50},
            { 10, 10, 20, 30, 30, 20, 10, 10},
            {  5,  5, 10, 25, 25, 10,  5,  5},
            {  0,  0,  0, 20, 20,  0,  0,  0},
            {  5, -5,-10,  0,  0,-10, -5,  5},
            {  5, 10, 10,-20,-20, 10, 10,  5},
            {  0,  0,  0,  0,  0,  0,  0,  0}
    };

    private static final int[][] KNIGHT_MG_PST = {
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-30,  0, 10, 15, 15, 10,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  0, 15, 20, 20, 15,  0,-30},
            {-30,  5, 10, 15, 15, 10,  5,-30},
            {-40,-20,  0,  5,  5,  0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50}
    };

    private static final int[][] BISHOP_MG_PST = {
            {-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10},
            {-10,  0, 10, 10, 10, 10,  0,-10},
            {-10, 10, 10, 10, 10, 10, 10,-10},
            {-10,  5,  0,  0,  0,  0,  5,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20}
    };

    private static final int[][] ROOK_MG_PST = {
            {  0,  0,  0,  0,  0,  0,  0,  0},
            {  5, 10, 10, 10, 10, 10, 10,  5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            {  0,  0,  0,  5,  5,  0,  0,  0}
    };

    private static final int[][] QUEEN_MG_PST = {
            {-20,-10,-10, -5, -5,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5,  5,  5,  5,  0,-10},
            { -5,  0,  5,  5,  5,  5,  0, -5},
            {  0,  0,  5,  5,  5,  5,  0, -5},
            {-10,  5,  5,  5,  5,  5,  0,-10},
            {-10,  0,  5,  0,  0,  0,  0,-10},
            {-20,-10,-10, -5, -5,-10,-10,-20}
    };

    private static final int[][] KING_MG_PST = {
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            { 20, 20,  0,  0,  0,  0, 20, 20},
            { 20, 30, 10,  0,  0, 10, 30, 20}
    };

    private static final int[][] PAWN_EG_PST = {
            {  0,  0,  0,  0,  0,  0,  0,  0},
            { 80, 80, 80, 80, 80, 80, 80, 80},
            { 50, 50, 50, 50, 50, 50, 50, 50},
            { 30, 30, 30, 30, 30, 30, 30, 30},
            { 20, 20, 20, 20, 20, 20, 20, 20},
            { 10, 10, 10, 10, 10, 10, 10, 10},
            {  0,  0,  0,  0,  0,  0,  0,  0},
            {  0,  0,  0,  0,  0,  0,  0,  0}
    };

    // Król w końcówce absolutnie MUSI iść do centrum planszy
    private static final int[][] KING_EG_PST = {
            {-50,-40,-30,-20,-20,-30,-40,-50},
            {-30,-20,-10,  0,  0,-10,-20,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-30,  0,  0,  0,  0,-30,-30},
            {-50,-30,-30,-30,-30,-30,-30,-50}
    };

    public static int evaluate(GameManager gameManager) {
        GameManager.GameStatus status = gameManager.getStatus();
        if (status == GameManager.GameStatus.WHITE_WINS) return 100000;
        if (status == GameManager.GameStatus.BLACK_WINS) return -100000;
        if (status == GameManager.GameStatus.STALEMATE) return 0;

        Board board = gameManager.getBoard();

        int mgEval = 0; // Ocena gry środkowej
        int egEval = 0; // Ocena końcówki
        int phase = 0;  // Faza gry (im wyższa, tym bardziej "midgame")

        Square whiteKingSq = null;
        Square blackKingSq = null;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Square(row, col));
                if (piece != null) {
                    PieceColor color = piece.getColor();
                    PieceType type = piece.getType();

                    int baseVal = getBaseValue(type);
                    int mgPstVal = getPstValue(getMgPst(type), color, row, col);
                    int egPstVal = getPstValue(getEgPst(type), color, row, col);

                    int pieceMgVal = baseVal + mgPstVal;
                    int pieceEgVal = baseVal + egPstVal;

                    if (color == PieceColor.WHITE) {
                        mgEval += pieceMgVal;
                        egEval += pieceEgVal;
                        if (type == PieceType.KING) whiteKingSq = new Square(row, col);
                    } else {
                        mgEval -= pieceMgVal;
                        egEval -= pieceEgVal;
                        if (type == PieceType.KING) blackKingSq = new Square(row, col);
                    }

                    phase += getPiecePhase(type);
                }
            }
        }

        phase = Math.min(phase, MAX_PHASE);

        int evaluation = (mgEval * phase + egEval * (MAX_PHASE - phase)) / MAX_PHASE;

        if (whiteKingSq != null && blackKingSq != null) {
            int endgameWeight = MAX_PHASE - phase;
            if (endgameWeight > 0) {
                if (evaluation > 400) {
                    evaluation += (forceKingToCorner(whiteKingSq, blackKingSq) * endgameWeight) / MAX_PHASE;
                } else if (evaluation < -400) {
                    evaluation -= (forceKingToCorner(blackKingSq, whiteKingSq) * endgameWeight) / MAX_PHASE;
                }
            }
        }

        return evaluation;
    }

    private static int getBaseValue(PieceType type) {
        return switch (type) {
            case PAWN -> PAWN_VALUE;
            case KNIGHT -> KNIGHT_VALUE;
            case BISHOP -> BISHOP_VALUE;
            case ROOK -> ROOK_VALUE;
            case QUEEN -> QUEEN_VALUE;
            case KING -> 0;
        };
    }

    private static int[][] getMgPst(PieceType type) {
        return switch (type) {
            case PAWN -> PAWN_MG_PST;
            case KNIGHT -> KNIGHT_MG_PST;
            case BISHOP -> BISHOP_MG_PST;
            case ROOK -> ROOK_MG_PST;
            case QUEEN -> QUEEN_MG_PST;
            case KING -> KING_MG_PST;
        };
    }

    private static int[][] getEgPst(PieceType type) {
        return switch (type) {
            case PAWN -> PAWN_EG_PST;
            case KNIGHT -> KNIGHT_MG_PST;
            case BISHOP -> BISHOP_MG_PST;
            case ROOK -> ROOK_MG_PST;
            case QUEEN -> QUEEN_MG_PST;
            case KING -> KING_EG_PST;
        };
    }

    private static int getPstValue(int[][] pst, PieceColor color, int row, int col) {
        int pstRow = color == PieceColor.WHITE ? row : 7 - row;
        return pst[pstRow][col];
    }

    private static int getPiecePhase(PieceType type) {
        return switch (type) {
            case KNIGHT -> PHASE_WEIGHT_KNIGHT;
            case BISHOP -> PHASE_WEIGHT_BISHOP;
            case ROOK -> PHASE_WEIGHT_ROOK;
            case QUEEN -> PHASE_WEIGHT_QUEEN;
            default -> 0;
        };
    }

    private static int forceKingToCorner(Square winningKing, Square losingKing) {
        int eval = 0;
        int losingKingRankDist = Math.max(3 - losingKing.getRow(), losingKing.getRow() - 4);
        int losingKingFileDist = Math.max(3 - losingKing.getCol(), losingKing.getCol() - 4);
        int losingKingDistFromCenter = losingKingRankDist + losingKingFileDist;

        eval += losingKingDistFromCenter * 10;
        int distBetweenKings = Math.abs(winningKing.getRow() - losingKing.getRow()) +
                Math.abs(winningKing.getCol() - losingKing.getCol());

        eval += (14 - distBetweenKings) * 4;

        return eval;
    }
}