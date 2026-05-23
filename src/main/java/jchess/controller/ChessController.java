package jchess.controller;

import jchess.model.*;
import jchess.view.ChessApp;

import java.util.List;

public class ChessController {

    private final GameManager gameManager;
    private final ChessApp view;
    private Square selectedSquare = null;

    public ChessController(GameManager gameManager, ChessApp view){
        this.gameManager = gameManager;
        this.view = view;
    }

        //handling mouse click from lambda
    public void handleSquareClick(int row, int col){
        Square clickedSquare = new Square(row, col);
        Piece clickedPiece = gameManager.getBoard().getPiece(clickedSquare);

            //logic of handling clicking: is there any piece, our piece, legal move...
        if (selectedSquare == null){
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()){
                selectedSquare = clickedSquare;
                view.drawBoard(selectedSquare);
            }
        }
                //we clicked on sth already
        else{
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()){
                selectedSquare = clickedSquare;
                view.drawBoard(selectedSquare);
                return;
            }
            List<Move> legalMoves = gameManager.getLegalMoves(selectedSquare);
            Move moveExecutor = null;
            //checking if is it legal
            for (Move move : legalMoves){
                if (move.getEnd().getRow() == clickedSquare.getRow() && move.getEnd().getCol() == clickedSquare.getCol()){
                    moveExecutor = move;
                    break;
                }
            }


            if (moveExecutor != null)
                gameManager.playMove(moveExecutor);


            selectedSquare = null;
            view.drawBoard(null);
        }
    }
}