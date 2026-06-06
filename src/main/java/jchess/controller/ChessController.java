package jchess.controller;

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
    public ChessController(GameManager gameManager, ChessApp view){
        this.gameManager = gameManager;
        this.view = view;
    }

        //handling mouse click from lambda
    public void handleSquareClick(int row, int col){
        if (gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            return;
        }

        Square clickedSquare = new Square(row, col);
        Piece clickedPiece = gameManager.getBoard().getPiece(clickedSquare);

            //logic of handling clicking: is there any piece, our piece, legal move...
        if (selectedSquare == null){
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()){
                selectedSquare = clickedSquare;
                currentLegalMoves = gameManager.getLegalMoves(selectedSquare);
                view.drawBoard(selectedSquare, currentLegalMoves);
            }
        }
                //we clicked on sth already
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
                //checking if is it legal
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
                        final Move finalMove = moveExecutor;  //lambda for handling promotion
                        view.showPromotionDialog(gameManager.getCurrentTurn(), chosenPiece -> {
                            finalMove.setPromotionPiece(chosenPiece);
                            executeMove(finalMove);
                            selectedSquare = null;
                            view.drawBoard(null, null);
                            view.updateGraveyards();
                            showGameOverIfNeeded();
                        });
                        return;
                    } else
                        executeMove(moveExecutor);
                }


                selectedSquare = null;
                view.drawBoard(null, null);
                view.updateGraveyards();
                showGameOverIfNeeded();
            }
        }
    }
    private void executeMove(Move move) { //playMove with notation handling
        PieceColor mover = move.getPieceMoved().getColor(); //info who played so we know if it is new row or black's moves
        String base = AlgebraicNotation.formatBase(move, gameManager);
        gameManager.playMove(move);
        String san = AlgebraicNotation.withCheckSuffix(base, gameManager); //suffix
        view.recordMove(san, mover);
    }
    private void showGameOverIfNeeded() {
        if (gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            view.showGameOverDialog();
        }
    }
}
