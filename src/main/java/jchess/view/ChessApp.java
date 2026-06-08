package jchess.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jchess.config.AppConfig;
import jchess.config.AppConfigLoader;
import jchess.config.UiConfig;
import jchess.controller.ChessController;
import jchess.model.GameManager;
import jchess.model.Move;
import jchess.model.Piece;
import jchess.model.PieceColor;
import jchess.model.Square;
import jchess.model.FenParser;
import java.util.List;
import java.util.function.Consumer;

public class ChessApp extends Application {
    private static final int TILE_SIZE = 60;
    private static final int OFFSET_SIZE = 25;

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
        gameTimer = new GameTimer(ui(), () -> gameManager.getCurrentTurn(), this::endGameByTime);

        BorderPane root = new BorderPane();
        appRoot = new StackPane(root);
        root.setCenter(boardView);
        moveHistoryView = new MoveHistoryView(ui());
        EndGameButtonView endGameButtonView = new EndGameButtonView(ui(), this::endGameManually);
        FenCopyButtonView fenCopyButtonView = new FenCopyButtonView(ui(), () -> FenParser.toFen(gameManager));
        VBox rightSide = new VBox(moveHistoryView, endGameButtonView, fenCopyButtonView);
        rightSide.setPrefWidth(200);
        rightSide.setStyle("-fx-background-color: " + ui().getBackground() + ";");
        VBox.setVgrow(moveHistoryView, Priority.ALWAYS);
        root.setRight(rightSide);
        blackGraveyard = new GraveyardView(pieceImageFactory::create);
        whiteGraveyard = new GraveyardView(pieceImageFactory::create);
        root.setTop(createTopBar());
        root.setBottom(createBottomBar());

        showStartMenu();
        drawBoard(null, null);

        Scene scene = new Scene(appRoot, (TILE_SIZE * 8) + OFFSET_SIZE + 200, (TILE_SIZE * 8) + OFFSET_SIZE + 140);
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

        startMenuOverlay = new StartMenuView(appConfig, this::selectTheme, this::startGameWithTime, this::loadFenPosition);
        appRoot.getChildren().add(startMenuOverlay);
    }

    private void selectTheme(String themeId) {
        selectedThemeId = themeId;
        drawBoard(null, null);
    }

    private void startGameWithTime(int timeInSeconds, boolean isBot) {
        controller.setBotMode(isBot);
        drawBoard(null, null);
        removeStartMenuOverlay();
        gameTimer.start(timeInSeconds);
    }
    private void removeStartMenuOverlay() {
        appRoot.getChildren().remove(startMenuOverlay);
        startMenuOverlay = null;
    }

    private void endGameByTime(PieceColor winner) {
        if (winner == PieceColor.WHITE) {
            gameManager.setStatus(GameManager.GameStatus.WHITE_WINS);
        } else {
            gameManager.setStatus(GameManager.GameStatus.BLACK_WINS);
        }

        Platform.runLater(() -> showGameOverDialog("Time's up"));
    }

    public void showGameOverDialog() {
        showGameOverDialog("Game over");
    }

    private void showGameOverDialog(String reason) {
        gameTimer.stop();
        if (gameOverOverlay != null) {
            return;
        }

        gameOverOverlay = new GameOverDialog(gameManager, ui(), reason, this::restartGame, Platform::exit);
        appRoot.getChildren().add(gameOverOverlay);
    }

    private void restartGame() {
        appRoot.getChildren().remove(gameOverOverlay);
        gameOverOverlay = null;

        selectedThemeId = appConfig.getDefaultBoardThemeId();
        createGameSession();
        clearMoveHistory();
        drawBoard(null, null);
        updateGraveyards();
        showStartMenu();
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
        FenParser.loadFen(gameManager, fen);
        clearMoveHistory();
        drawBoard(null, null);
        updateGraveyards();
        moveHistoryView.setHalfMoveClock(gameManager.getHalfMoveClock());
        removeStartMenuOverlay();
    }

    private void endGameManually() {
        if (startMenuOverlay != null || gameManager.getStatus() != GameManager.GameStatus.ACTIVE) {
            return;
        }
        gameManager.setStatus(GameManager.GameStatus.ENDED);
        showGameOverDialog();
    }

    public void recordMove(String san, PieceColor mover) {
        moveHistoryView.addMove(san, mover);
        moveHistoryView.setHalfMoveClock(gameManager.getHalfMoveClock());
    }
    public void clearMoveHistory() {
        moveHistoryView.clear();
    }
    private UiConfig ui() {
        return appConfig.getUi();
    }
}
