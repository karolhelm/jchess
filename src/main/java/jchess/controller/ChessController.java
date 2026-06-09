package jchess.controller;

import javafx.concurrent.Task;
import jchess.model.*;
import jchess.view.ChessApp;
import jchess.notation.AlgebraicNotation;
import java.util.ArrayList;
import java.util.List;

public class ChessController {

    private final GameManager gameManager;
    private final ChessApp view;
    private Square selectedSquare = null;
    private List<Move> currentLegalMoves = new ArrayList<>();
    private PieceColor botColor = PieceColor.BLACK;

    private final ChessBot chessBot = new ChessBot();
    private boolean isBotMode = false;
    private boolean isBotThinking = false;
    private boolean openingPreviewMode = false;
    private volatile boolean gameTerminated = false;

    public ChessController(GameManager gameManager, ChessApp view){
        this.gameManager = gameManager;
        this.view = view;
    }

    public void setBotMode(boolean isBotMode) {
        this.isBotMode = isBotMode;
    }

    public void setOpeningPreviewMode(boolean openingPreviewMode) {
        this.openingPreviewMode = openingPreviewMode;
    }

    public PieceColor getActiveClock() {
        if (isBotThinking) {
            return botColor;
        }
        return gameManager.getCurrentTurn();
    }

    public void markGameTerminated() {
        gameTerminated = true;
    }

    public void handleSquareClick(int row, int col){
        if (openingPreviewMode || isBotThinking) {
            return;
        }

        if (gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            return;
        }

        Square clickedSquare = new Square(row, col);
        Piece clickedPiece = gameManager.getBoard().getPiece(clickedSquare);

        if (selectedSquare == null){
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()){
                if (clickedPiece.getType() == PieceType.ROOK) {
                    Square kingSquare = findKingSquare(gameManager.getCurrentTurn());
                    if (kingSquare != null) {
                        Move castling = findCastlingViaRook(gameManager.getLegalMoves(kingSquare), clickedSquare);
                        if (castling != null) {
                            executeMove(castling);
                            view.drawBoard(null, null);
                            view.updateGraveyards();
                            showGameOverIfNeeded();
                            makeBotMove();
                            return;
                        }
                    }
                }
                selectedSquare = clickedSquare;
                currentLegalMoves = gameManager.getLegalMoves(selectedSquare);
                view.drawBoard(selectedSquare, currentLegalMoves);
            }
        }
        else {
            if (selectedSquare.equals(clickedSquare)) {
                Move castlingInPlace = findCastlingInPlace(currentLegalMoves, clickedSquare);
                if (castlingInPlace != null) {
                    executeMove(castlingInPlace);
                    selectedSquare = null;
                    currentLegalMoves.clear();
                    view.drawBoard(null, null);
                    view.updateGraveyards();
                    showGameOverIfNeeded();
                    makeBotMove();
                    return;
                }
                selectedSquare = null;
                currentLegalMoves.clear();
                view.drawBoard(null, currentLegalMoves);
            } else if (tryExecuteMoveToSquare(clickedSquare)) {
                return;
            } else if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()) {
                selectedSquare = clickedSquare;
                currentLegalMoves = gameManager.getLegalMoves(selectedSquare);
                view.drawBoard(selectedSquare, currentLegalMoves);
            } else {
                selectedSquare = null;
                currentLegalMoves.clear();
                view.drawBoard(null, null);
            }
        }
    }

    private Square findKingSquare(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = gameManager.getBoard().getPiece(square);
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return square;
                }
            }
        }
        return null;
    }

    private Move findCastlingViaRook(List<Move> legalMoves, Square square) {
        for (Move move : legalMoves) {
            if (move.isCastling() && move.getRookStart().equals(square)) {
                return move;
            }
        }
        return null;
    }

    private Move findCastlingInPlace(List<Move> legalMoves, Square square) {
        for (Move move : legalMoves) {
            if (move.isCastling() && move.getStart().equals(square) && move.getEnd().equals(square)) {
                return move;
            }
        }
        return null;
    }

    private boolean tryExecuteMoveToSquare(Square clickedSquare) {
        Move moveExecutor = findCastlingViaRook(currentLegalMoves, clickedSquare);

        if (moveExecutor == null) {
            for (Move move : currentLegalMoves) {
                if (move.getEnd().equals(clickedSquare)) {
                    if (move.isCastling()) {
                        moveExecutor = move;
                        break;
                    }
                    if (moveExecutor == null) {
                        moveExecutor = move;
                    }
                }
            }
        }

        if (moveExecutor == null) {
            return false;
        }

        Piece movingPiece = moveExecutor.getPieceMoved();
        boolean isPawn = movingPiece.getType() == PieceType.PAWN;
        int targetRow = moveExecutor.getEnd().getRow();
        boolean isPromotion = isPawn && (targetRow == 0 || targetRow == 7);

        if (isPromotion) {
            final Move finalMove = moveExecutor;
            view.showPromotionDialog(gameManager.getCurrentTurn(), chosenPiece -> {
                finalMove.setPromotionPiece(chosenPiece);
                executeMove(finalMove);
                selectedSquare = null;
                currentLegalMoves.clear();
                view.drawBoard(null, null);
                view.updateGraveyards();
                showGameOverIfNeeded();
                makeBotMove();
            });
            return true;
        }

        executeMove(moveExecutor);
        selectedSquare = null;
        currentLegalMoves.clear();
        view.drawBoard(null, null);
        view.updateGraveyards();
        showGameOverIfNeeded();
        makeBotMove();
        return true;
    }

    private void executeMove(Move move) {
        PieceColor mover = move.getPieceMoved().getColor();
        String base = AlgebraicNotation.formatBase(move, gameManager);
        gameManager.playMove(move);
        String san = AlgebraicNotation.withCheckSuffix(base, gameManager);
        view.recordMove(san, mover);
    }

    private void showGameOverIfNeeded() {
        if (gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            view.showGameOverDialog();
        }
    }

    private void makeBotMove() {
        if (!isBotMode || gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            return;
        }

        if (gameManager.getCurrentTurn() == botColor) {
            isBotThinking = true;

            Task<Move> botTask = new Task<>() {
                @Override
                protected Move call() {
                    return chessBot.findBestMove(gameManager);
                }
            };

            botTask.setOnSucceeded(event -> {
                isBotThinking = false;

                if (gameTerminated) {
                    return;
                }

                Move bestMove = botTask.getValue();

                if (bestMove != null && gameManager.getStatus() == GameManager.GameStatus.ACTIVE) {
                    executeMove(bestMove);
                    view.drawBoard(null, null);
                    view.updateGraveyards();
                    showGameOverIfNeeded();
                }
            });

            botTask.setOnFailed(event -> {
                isBotThinking = false;
                System.err.println("Bot encountered a critical error during calculation!");
                botTask.getException().printStackTrace();
            });

            Thread thread = new Thread(botTask);
            thread.setDaemon(true);
            thread.start();
        }
    }
}