package jchess.controller;

import jchess.config.UiConfig;
import jchess.model.FenParser;
import jchess.model.GameManager;
import jchess.model.Opening;
import jchess.view.GameTimer;
import jchess.view.OpeningView;

public class OpeningPreviewController {

    private GameManager gameManager;
    private ChessController chessController;
    private final GameTimer gameTimer;
    private final OpeningView openingView;
    private final Runnable refreshPositionUi;
    private final Runnable closeStartMenu;
    private final Runnable onPositionEnded;

    private Opening currentOpening;
    private int stepIndex;

    public OpeningPreviewController(
            UiConfig ui,
            GameManager gameManager,
            ChessController chessController,
            GameTimer gameTimer,
            Runnable refreshPositionUi,
            Runnable closeStartMenu,
            Runnable onPositionEnded
    ) {
        this.gameManager = gameManager;
        this.chessController = chessController;
        this.gameTimer = gameTimer;
        this.refreshPositionUi = refreshPositionUi;
        this.closeStartMenu = closeStartMenu;
        this.onPositionEnded = onPositionEnded;
        this.openingView = new OpeningView(ui, this::prevStep, this::nextStep);
    }

    public OpeningView getView() {
        return openingView;
    }

    public void bindSession(GameManager gameManager, ChessController chessController) {
        this.gameManager = gameManager;
        this.chessController = chessController;
    }

    public boolean isPreviewActive() {
        return currentOpening != null;
    }

    public void start(Opening opening) {
        currentOpening = opening;
        stepIndex = 0;
        chessController.setBotMode(false);
        chessController.setOpeningPreviewMode(true);
        gameTimer.stop();
        closeStartMenu.run();
        showStep();
    }

    public void reset() {
        currentOpening = null;
        stepIndex = 0;
        chessController.setOpeningPreviewMode(false);
        openingView.hide();
    }

    private void prevStep() {
        if (currentOpening != null && stepIndex > 0) {
            stepIndex--;
            showStep();
        }
    }

    private void nextStep() {
        if (currentOpening != null && stepIndex < currentOpening.getStepCount() - 1) {
            stepIndex++;
            showStep();
        }
    }

    private void showStep() {
        FenParser.loadFen(gameManager, currentOpening.getFen(stepIndex));
        refreshPositionUi.run();
        openingView.show(currentOpening.getName(), stepIndex, currentOpening.getStepCount());
        onPositionEnded.run();
    }
}
