package jchess.model;

import java.util.ArrayList;
import java.util.List;

public class ChessBot {

    private static final int MAX_DEPTH = 4;

    public Move findBestMove(GameManager gameManager) {
        Move bookMove = OpeningBook.findBookMove(gameManager);
        if (bookMove != null) {
            return bookMove;
        }

        boolean isWhiteTurn = gameManager.getCurrentTurn() == PieceColor.WHITE;

        int bestValue = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move bestMove = null;

        List<Move> legalMoves = getAllLegalMoves(gameManager, isWhiteTurn ? PieceColor.WHITE : PieceColor.BLACK);
        orderMoves(legalMoves);

        if (legalMoves.isEmpty()) return null;

        for (Move move : legalMoves) {
            gameManager.playMove(move);
            int boardValue = minimax(gameManager, MAX_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !isWhiteTurn);
            gameManager.undoMove(move);

            if (isWhiteTurn) {
                if (boardValue > bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
            } else {
                if (boardValue < bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    private int minimax(GameManager gameManager, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0 || gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
           // Quiescence Search
            return quiescenceSearch(gameManager, alpha, beta, isMaximizingPlayer);
        }

        List<Move> legalMoves = getAllLegalMoves(gameManager, isMaximizingPlayer ? PieceColor.WHITE : PieceColor.BLACK);
        orderMoves(legalMoves); // Move Ordering

        if (legalMoves.isEmpty()) {
            return BoardEvaluator.evaluate(gameManager);
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                gameManager.playMove(move);
                int eval = minimax(gameManager, depth - 1, alpha, beta, false);
                gameManager.undoMove(move);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : legalMoves) {
                gameManager.playMove(move);
                int eval = minimax(gameManager, depth - 1, alpha, beta, true);
                gameManager.undoMove(move);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }
            return minEval;
        }
    }

    // Quiescence search
    private int quiescenceSearch(GameManager gameManager, int alpha, int beta, boolean isMaximizingPlayer) {
        int standPat = BoardEvaluator.evaluate(gameManager);

        // Fail-hard beta cutoff
        if (isMaximizingPlayer) {
            if (standPat >= beta) return beta;
            if (alpha < standPat) alpha = standPat;
        } else {
            if (standPat <= alpha) return alpha;
            if (beta > standPat) beta = standPat;
        }
        //we only look at captures
        List<Move> captures = getCaptureMoves(gameManager, isMaximizingPlayer ? PieceColor.WHITE : PieceColor.BLACK);
        orderMoves(captures);

        if (isMaximizingPlayer) {
            int maxEval = standPat;
            for (Move move : captures) {
                gameManager.playMove(move);
                int eval = quiescenceSearch(gameManager, alpha, beta, false);
                gameManager.undoMove(move);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = standPat;
            for (Move move : captures) {
                gameManager.playMove(move);
                int eval = quiescenceSearch(gameManager, alpha, beta, true);
                gameManager.undoMove(move);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private List<Move> getCaptureMoves(GameManager gameManager, PieceColor color) {
        List<Move> allMoves = getAllLegalMoves(gameManager, color);
        List<Move> captures = new ArrayList<>();
        for (Move move : allMoves) {
            if (move.isCapture() || move.isEnPassant()) {
                captures.add(move);
            }
        }
        return captures;
    }

    private List<Move> getAllLegalMoves(GameManager gameManager, PieceColor color) {
        List<Move> allMoves = new ArrayList<>();
        Board board = gameManager.getBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = board.getPiece(square);
                if (piece != null && piece.getColor() == color) {
                    allMoves.addAll(gameManager.getLegalMoves(square));
                }
            }
        }
        return allMoves;
    }

    private void orderMoves(List<Move> moves) {
        moves.sort((m1, m2) -> Integer.compare(scoreMove(m2), scoreMove(m1))); // Descending order
    }
    // (Most Valuable Victim - Least Valuable Attacker)
    private int scoreMove(Move move) {
        int score = 0;

        if (move.getPieceCaptured() != null) {
            int victimValue = move.getPieceCaptured().getValue() * 100;
            int attackerValue = move.getPieceMoved().getValue() * 100;
            score = 10 * victimValue - attackerValue;
        }

        if (move.getPromotionPiece() != null) {
            score += move.getPromotionPiece().getValue() * 100; // promotion
        }

        return score;
    }
}