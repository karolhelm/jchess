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
    private volatile boolean gameTerminated = false;

    public ChessController(GameManager gameManager, ChessApp view){
        this.gameManager = gameManager;
        this.view = view;
    }

    public void setBotMode(boolean isBotMode) {
        this.isBotMode = isBotMode;
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
        if (isBotThinking) {
            return;
        }

        if (gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            return;
        }

        Square clickedSquare = new Square(row, col);
        Piece clickedPiece = gameManager.getBoard().getPiece(clickedSquare);

        if (selectedSquare == null){
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()){
                selectedSquare = clickedSquare;
                currentLegalMoves = gameManager.getLegalMoves(selectedSquare);
                view.drawBoard(selectedSquare, currentLegalMoves);
            }
        }
        else {
            if (selectedSquare.equals(clickedSquare)) {
                selectedSquare = null;
                currentLegalMoves.clear();
                view.drawBoard(null, currentLegalMoves);
            } else if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()) {
                selectedSquare = clickedSquare;
                currentLegalMoves = gameManager.getLegalMoves(selectedSquare);
                view.drawBoard(selectedSquare, currentLegalMoves);
            } else {
                Move moveExecutor = null;

                for (Move move : currentLegalMoves) {
                    if (move.getEnd().getRow() == clickedSquare.getRow() && move.getEnd().getCol() == clickedSquare.getCol()) {
                        moveExecutor = move;
                        break;
                    }
                }

                if (moveExecutor != null) {
                    Piece movingPiece = moveExecutor.getPieceMoved();
                    boolean isPawn = (movingPiece.getType() == PieceType.PAWN);
                    int targetRow = moveExecutor.getEnd().getRow();
                    boolean isPromotion = isPawn && (targetRow == 0 || targetRow == 7);

                    if (isPromotion) {
                        final Move finalMove = moveExecutor;
                        view.showPromotionDialog(gameManager.getCurrentTurn(), chosenPiece -> {
                            finalMove.setPromotionPiece(chosenPiece);
                            executeMove(finalMove);
                            selectedSquare = null;
                            view.drawBoard(null, null);
                            view.updateGraveyards();
                            showGameOverIfNeeded();
                            makeBotMove();
                        });
                        return;
                    } else {
                        executeMove(moveExecutor);
                    }
                }

                selectedSquare = null;
                view.drawBoard(null, null);
                view.updateGraveyards();
                showGameOverIfNeeded();
                makeBotMove();
            }
        }
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
                System.err.println("Bot napotkał krytyczny błąd w trakcie obliczeń!");
                botTask.getException().printStackTrace();
            });

            Thread thread = new Thread(botTask);
            thread.setDaemon(true);
            thread.start();
        }
    }
}