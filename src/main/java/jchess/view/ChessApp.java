package jchess.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jchess.config.AppConfig;
import jchess.config.AppConfigLoader;
import jchess.config.UiConfig;
import jchess.controller.ChessController;
import jchess.controller.GameReviewController;
import jchess.controller.OpeningPreviewController;
import jchess.model.BoardEvaluator;
import jchess.model.GameManager;
import jchess.model.Move;
import jchess.model.Piece;
import jchess.model.PieceColor;
import jchess.model.Square;
import jchess.model.FenParser;
import jchess.model.GameMode;
import jchess.model.Opening;
import java.util.List;
import java.util.function.Consumer;

public class ChessApp extends Application {
    private static final double BASE_DPI = 96.0;
    private static final double WINDOW_SHRINK = 0.9;
    private static final int TILE_SIZE = 75;
    private static final int OFFSET_SIZE = 30;
    private static final int RIGHT_PANEL_WIDTH = 240;
    private static final int CHROME_HEIGHT = 170;

    private GameManager gameManager;
    private ChessController controller;
    private StackPane appRoot;
    private BoardView boardView;
    private GameOverDialog gameOverOverlay;
    private PromotionDialog promotionOverlay;
    private StartMenuView startMenuOverlay;
    private GraveyardView blackGraveyard;
    private GraveyardView whiteGraveyard;
    private AppConfig appConfig;
    private PieceImageFactory pieceImageFactory;
    private GameTimer gameTimer;
    private String selectedThemeId;
    private MoveHistoryView moveHistoryView;
    private OpeningPreviewController openingPreviewController;
    private GameReviewController gameReviewController;
    private EndGameButtonView endGameButtonView;
    private EvaluationBarView evaluationBarView;
    private boolean fenLoadedGame = false;
    @Override
    public void start(Stage primaryStage) {
        loadConfig();
        selectedThemeId = appConfig.getDefaultBoardThemeId();
        createGameSession();

        pieceImageFactory = new PieceImageFactory(appConfig, ChessApp.class, TILE_SIZE - 10);
        boardView = new BoardView(
                appConfig,
                pieceImageFactory,
                (row, col) -> controller.handleSquareClick(row, col),
                TILE_SIZE,
                OFFSET_SIZE
        );
        gameTimer = new GameTimer(ui(), () -> controller.getActiveClock(), this::endGameByTime);
        openingPreviewController = new OpeningPreviewController(
                ui(),
                gameManager,
                controller,
                gameTimer,
                this::refreshAfterPositionLoad,
                this::removeStartMenuOverlay,
                this::showGameOverIfPositionEnded
        );
        gameReviewController = new GameReviewController(
                ui(),
                gameManager,
                controller,
                this::refreshAfterReviewStep,
                this::onReviewClosed
        );

        double displayScale = (BASE_DPI / Screen.getPrimary().getDpi()) * WINDOW_SHRINK;
        int windowWidth = (TILE_SIZE * 8) + OFFSET_SIZE + (int) EvaluationBarView.CONTAINER_WIDTH + RIGHT_PANEL_WIDTH;
        int windowHeight = (TILE_SIZE * 8) + OFFSET_SIZE + CHROME_HEIGHT;

        BorderPane root = new BorderPane();
        appRoot = new StackPane(root);
        evaluationBarView = new EvaluationBarView(TILE_SIZE * 8);
        HBox centerArea = new HBox(boardView, evaluationBarView);
        centerArea.setAlignment(Pos.CENTER_LEFT);
        centerArea.setStyle("-fx-background-color: " + ui().getBackground() + ";");
        root.setCenter(centerArea);
        moveHistoryView = new MoveHistoryView(ui());
        endGameButtonView = new EndGameButtonView(ui(), this::endGameManually);
        FenCopyButtonView fenCopyButtonView = new FenCopyButtonView(ui(), () -> FenParser.toFen(gameManager));
        VBox rightSide = new VBox(moveHistoryView, openingPreviewController.getView(), gameReviewController.getView(), endGameButtonView, fenCopyButtonView);
        rightSide.setPrefWidth(RIGHT_PANEL_WIDTH);
        rightSide.setStyle("-fx-background-color: " + ui().getBackground() + ";");
        VBox.setVgrow(moveHistoryView, Priority.ALWAYS);
        root.setRight(rightSide);
        blackGraveyard = new GraveyardView(pieceImageFactory::create);
        whiteGraveyard = new GraveyardView(pieceImageFactory::create);
        root.setTop(createTopBar());
        root.setBottom(createBottomBar());

        showStartMenu();
        drawBoard(null, null);

        Group scaledUi = new Group(appRoot);
        scaledUi.setScaleX(displayScale);
        scaledUi.setScaleY(displayScale);

        StackPane sceneRoot = new StackPane(scaledUi);
        sceneRoot.setStyle("-fx-background-color: " + ui().getBackground() + ";");

        Scene scene = new Scene(
                sceneRoot,
                Math.max(1, (int) Math.round(windowWidth * displayScale)),
                Math.max(1, (int) Math.round(windowHeight * displayScale))
        );
        primaryStage.setTitle("JChess");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void loadConfig() {
        try {
            appConfig = AppConfigLoader.load();
        } catch (Exception e) {
            throw new RuntimeException("Json crashed", e);
        }
    }

    private void createGameSession() {
        gameManager = new GameManager();
        controller = new ChessController(gameManager, this);
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(5, blackGraveyard, gameTimer.getBlackTimerLabel());
        topBar.setStyle("-fx-background-color: " + ui().getBackground() + "; -fx-padding: 10; -fx-alignment: center;");
        return topBar;
    }

    private VBox createBottomBar() {
        VBox bottomBar = new VBox(5, gameTimer.getWhiteTimerLabel(), whiteGraveyard);
        bottomBar.setStyle("-fx-background-color: " + ui().getBackground() + "; -fx-padding: 10; -fx-alignment: center;");
        return bottomBar;
    }

    private void showStartMenu() {
        if (startMenuOverlay != null) {
            return;
        }

        startMenuOverlay = new StartMenuView(
                appConfig, this::selectTheme, this::startGameWithTime, this::loadFenPosition, this::startOpeningPreview
        );
        appRoot.getChildren().add(startMenuOverlay);
    }

    private void selectTheme(String themeId) {
        selectedThemeId = themeId;
        drawBoard(null, null);
    }

    private void startOpeningPreview(Opening opening) {
        openingPreviewController.start(opening);
        endGameButtonView.setLabel("End study");
    }

    private void startGameWithTime(int timeInSeconds, boolean isBot, GameMode gameMode, String fenOrNull) {
        openingPreviewController.reset();
        endGameButtonView.setLabel("End game");
        controller.setBotMode(isBot);
        String startingFen = FenParser.resolveStartingFen(gameMode, fenOrNull);
        if (!startingFen.equals(FenParser.STARTING_FEN)) {
            FenParser.loadFen(gameManager, startingFen);
            refreshAfterPositionLoad();
        }
        drawBoard(null, null);
        removeStartMenuOverlay();
        gameTimer.start(timeInSeconds);
        gameReviewController.resetPositions();
        gameReviewController.recordPosition(FenParser.toFen(gameManager));
        fenLoadedGame = false;
        evaluationBarView.hide();
        showGameOverIfPositionEnded();
        if (isBot) {
            controller.triggerBotMoveIfNeeded();
        }
    }
    private void removeStartMenuOverlay() {
        appRoot.getChildren().remove(startMenuOverlay);
        startMenuOverlay = null;
    }

    private void endGameByTime(PieceColor winner) {
        controller.markGameTerminated();
        if (winner == PieceColor.WHITE) {
            gameManager.setStatus(GameManager.GameStatus.WHITE_WINS);
        } else {
            gameManager.setStatus(GameManager.GameStatus.BLACK_WINS);
        }

        Platform.runLater(() -> showGameOverDialog(GameOverDialog.TIME_UP_REASON));
    }

    public void showGameOverDialog() {
        showGameOverDialog("Game over");
    }

    private String lastGameOverReason = "Game over";

    private void showGameOverDialog(String reason) {
        gameTimer.stop();
        lastGameOverReason = reason;
        if (gameOverOverlay != null) {
            return;
        }

        gameOverOverlay = new GameOverDialog(
                gameManager,
                ui(),
                reason,
                this::restartGame,
                Platform::exit,
                this::startReview,
                !fenLoadedGame
        );
        appRoot.getChildren().add(gameOverOverlay);
    }

    private void startReview() {
        if (gameOverOverlay != null) {
            appRoot.getChildren().remove(gameOverOverlay);
            gameOverOverlay = null;
        }
        endGameButtonView.setVisible(false);
        endGameButtonView.setManaged(false);
        gameReviewController.start();
    }

    private void refreshAfterReviewStep() {
        drawBoard(null, null);
        updateGraveyards();
        showEvaluationBar();
    }

    private void onReviewClosed() {
        if (!fenLoadedGame) {
            evaluationBarView.hide();
        } else {
            showEvaluationBar();
        }
        endGameButtonView.setVisible(true);
        endGameButtonView.setManaged(true);
        showGameOverDialog(lastGameOverReason);
    }

    private void restartGame() {
        appRoot.getChildren().remove(gameOverOverlay);
        gameOverOverlay = null;

        createGameSession();
        openingPreviewController.bindSession(gameManager, controller);
        openingPreviewController.reset();
        gameReviewController.bindSession(gameManager, controller);
        gameReviewController.resetPositions();
        endGameButtonView.setLabel("End game");
        clearMoveHistory();
        drawBoard(null, null);
        updateGraveyards();
        fenLoadedGame = false;
        evaluationBarView.hide();
        showStartMenu();
    }

    private void showEvaluationBar() {
        int eval = BoardEvaluator.evaluate(gameManager);
        evaluationBarView.show(eval);
    }

    public void drawBoard(Square selectedSquare, List<Move> legalMoves) {
        boardView.setSelectedThemeId(selectedThemeId);
        boardView.draw(gameManager.getBoard(), selectedSquare, legalMoves);
    }

    public void showPromotionDialog(PieceColor color, Consumer<Piece> onPieceSelected) {
        if (promotionOverlay != null) {
            return;
        }

        promotionOverlay = new PromotionDialog(
                color,
                pieceImageFactory,
                ui(),
                chosenPiece -> handlePromotionSelection(chosenPiece, onPieceSelected)
        );
        appRoot.getChildren().add(promotionOverlay);
    }

    private void handlePromotionSelection(Piece chosenPiece, Consumer<Piece> onPieceSelected) {
        appRoot.getChildren().remove(promotionOverlay);
        promotionOverlay = null;
        onPieceSelected.accept(chosenPiece);
    }

    public void updateGraveyards() {
        blackGraveyard.update(
                gameManager.getCapturedWhitePieces(),
                gameManager.getMaterialAdvantage(PieceColor.BLACK)
        );

        whiteGraveyard.update(
                gameManager.getCapturedBlackPieces(),
                gameManager.getMaterialAdvantage(PieceColor.WHITE)
        );
    }
    private void loadFenPosition(String fen) {
        openingPreviewController.reset();
        endGameButtonView.setLabel("End game");
        controller.setBotMode(false);
        FenParser.loadFen(gameManager, fen);
        refreshAfterPositionLoad();
        removeStartMenuOverlay();
        gameTimer.start(0);
        gameReviewController.resetPositions();
        gameReviewController.recordPosition(FenParser.toFen(gameManager));
        fenLoadedGame = true;
        showEvaluationBar();
        showGameOverIfPositionEnded();
    }

    private void refreshAfterPositionLoad() {
        clearMoveHistory();
        syncUiAfterPositionLoad();
        drawBoard(null, null);
        updateGraveyards();
    }

    private void syncUiAfterPositionLoad() {
        moveHistoryView.setStartingPosition(gameManager.getFullMoveNumber(), gameManager.getCurrentTurn());
        moveHistoryView.setHalfMoveClock(gameManager.getHalfMoveClock());
    }

    private void showGameOverIfPositionEnded() {
        if (gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            Platform.runLater(this::showGameOverDialog);
        }
    }

    private void endGameManually() {
        if (startMenuOverlay != null) {
            return;
        }
        if (gameReviewController.isActive()) {
            return;
        }
        if (openingPreviewController.isPreviewActive()) {
            endOpeningStudy();
            return;
        }
        if (gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            return;
        }
        controller.markGameTerminated();
        gameManager.setStatus(GameManager.GameStatus.ENDED);
        showGameOverDialog(GameOverDialog.MANUAL_REASON);
    }

    private void endOpeningStudy() {
        openingPreviewController.reset();
        createGameSession();
        openingPreviewController.bindSession(gameManager, controller);
        gameReviewController.bindSession(gameManager, controller);
        gameReviewController.resetPositions();
        clearMoveHistory();
        drawBoard(null, null);
        updateGraveyards();
        gameTimer.stop();
        endGameButtonView.setLabel("End game");
        fenLoadedGame = false;
        evaluationBarView.hide();
        showStartMenu();
    }

    public void recordMove(String san, PieceColor mover) {
        moveHistoryView.addMove(san, mover);
        moveHistoryView.setHalfMoveClock(gameManager.getHalfMoveClock());
        gameReviewController.recordPosition(FenParser.toFen(gameManager), gameManager.getBoard().getLastMove());
        if (fenLoadedGame) {
            showEvaluationBar();
        }
    }
    public void clearMoveHistory() {
        moveHistoryView.clear();
    }
    private UiConfig ui() {
        return appConfig.getUi();
    }
}
