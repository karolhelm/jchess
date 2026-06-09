package jchess.controller;

import jchess.config.UiConfig;
import jchess.model.FenParser;
import jchess.model.GameManager;
import jchess.model.Move;
import jchess.view.GameReviewView;

import java.util.ArrayList;
import java.util.List;

public class GameReviewController {

    private GameManager gameManager;
    private ChessController chessController;
    private final GameReviewView reviewView;
    private final Runnable refreshPositionUi;
    private final Runnable onClose;

    private final List<String> positions = new ArrayList<>();
    private final List<Move> leadingMoves = new ArrayList<>();
    private int positionIndex;
    private boolean active;

    public GameReviewController(
            UiConfig ui,
            GameManager gameManager,
            ChessController chessController,
            Runnable refreshPositionUi,
            Runnable onClose
    ) {
        this.gameManager = gameManager;
        this.chessController = chessController;
        this.refreshPositionUi = refreshPositionUi;
        this.onClose = onClose;
        this.reviewView = new GameReviewView(
                ui,
                this::goToFirst,
                this::goToPrev,
                this::goToNext,
                this::goToLast,
                this::close
        );
    }

    public GameReviewView getView() {
        return reviewView;
    }

    public void bindSession(GameManager gameManager, ChessController chessController) {
        this.gameManager = gameManager;
        this.chessController = chessController;
    }

    public boolean isActive() {
        return active;
    }

    public void recordPosition(String fen) {
        recordPosition(fen, null);
    }

    public void recordPosition(String fen, Move leadingMove) {
        if (active) {
            return;
        }
        positions.add(fen);
        leadingMoves.add(leadingMove);
    }

    public void resetPositions() {
        positions.clear();
        leadingMoves.clear();
        positionIndex = 0;
    }

    public void start() {
        if (positions.isEmpty()) {
            return;
        }
        active = true;
        chessController.setReviewMode(true);
        positionIndex = positions.size() - 1;
        showCurrent();
    }

    public void close() {
        active = false;
        chessController.setReviewMode(false);
        reviewView.hide();
        onClose.run();
    }

    private void goToFirst() {
        if (!active || positions.isEmpty()) {
            return;
        }
        positionIndex = 0;
        showCurrent();
    }

    private void goToLast() {
        if (!active || positions.isEmpty()) {
            return;
        }
        positionIndex = positions.size() - 1;
        showCurrent();
    }

    private void goToPrev() {
        if (!active || positionIndex <= 0) {
            return;
        }
        positionIndex--;
        showCurrent();
    }

    private void goToNext() {
        if (!active || positionIndex >= positions.size() - 1) {
            return;
        }
        positionIndex++;
        showCurrent();
    }

    private void showCurrent() {
        FenParser.loadFen(gameManager, positions.get(positionIndex));
        gameManager.getBoard().setLastMove(leadingMoves.get(positionIndex));
        refreshPositionUi.run();
        reviewView.show(positionIndex, positions.size());
    }
}
