package jchess.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import jchess.controller.ChessController;
import jchess.model.*;
import javafx.scene.Cursor;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
public class ChessApp extends Application{

    private GameManager gameManager;
    private ChessController controller;
    private StackPane appRoot;
    private GridPane boardGrid;
    private StackPane gameOverOverlay;
    private StackPane promotionOverlay;
    private StackPane startMenuOverlay;
    private static final int TILE_SIZE = 60;
    private static final int OFFSET_SIZE = 25;
    private int whiteTimeLeft = 0;
    private int blackTimeLeft = 0;    //time variables
    private Label whiteTimerLabel;
    private Label blackTimerLabel;
    private Timeline timeline;
    private GraveyardView blackGraveyard;
    private GraveyardView whiteGraveyard;
    @Override
    public void start(Stage primaryStage){
        gameManager = new GameManager();
        controller = new ChessController(gameManager, this);
        boardGrid = new GridPane();
        boardGrid.setStyle("-fx-background-color: #312e2b;");
        boardGrid.setAlignment(Pos.CENTER);
        BorderPane root = new BorderPane();
        appRoot = new StackPane(root);
        root.setCenter(boardGrid);
        blackTimerLabel = createTimerLabel("Black: --:--");
        whiteTimerLabel = createTimerLabel("White: --:--");
        blackGraveyard = new GraveyardView(this::getPieceImageView);
        whiteGraveyard = new GraveyardView(this::getPieceImageView);

        VBox topBar = new VBox(5, blackGraveyard, blackTimerLabel);
        topBar.setStyle("-fx-background-color: #312e2b; -fx-padding: 10; -fx-alignment: center;");

        VBox bottomBar = new VBox(5, whiteTimerLabel, whiteGraveyard);
        bottomBar.setStyle("-fx-background-color: #312e2b; -fx-padding: 10; -fx-alignment: center;");

        root.setTop(topBar);
        root.setBottom(bottomBar);
        showStartMenu();
        drawBoard(null, null);        //basic inizalization //
        Scene scene = new Scene(appRoot, (TILE_SIZE * 8) + OFFSET_SIZE, (TILE_SIZE * 8) + OFFSET_SIZE + 140);
        primaryStage.setTitle("JChess");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void showStartMenu() {
        if (startMenuOverlay != null) return;

        Label title = new Label("JChess");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        Label subtitle = new Label("Wybierz czas gry");
        subtitle.setTextFill(Color.web("#baca44"));
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button btn1Min = createTimeButton("1 min", 60);
        Button btn3Min = createTimeButton("3 min", 180);
        Button btn5Min = createTimeButton("5 min", 300);
        Button btn10Min = createTimeButton("10 min", 600);

        HBox topButtons = new HBox(15, btn1Min, btn3Min);
        topButtons.setAlignment(Pos.CENTER);

        HBox bottomButtons = new HBox(15, btn5Min, btn10Min);
        bottomButtons.setAlignment(Pos.CENTER);

        VBox content = new VBox(20, title, subtitle, topButtons, bottomButtons);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setMaxSize(350, 250);
        content.setStyle(
                "-fx-background-color: #312e2b;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #baca44;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;"
        );

        startMenuOverlay = new StackPane(content);
        startMenuOverlay.setAlignment(Pos.CENTER);
        startMenuOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        startMenuOverlay.setPickOnBounds(true);
        appRoot.getChildren().add(startMenuOverlay);
    }
    private Button createTimeButton(String text, int timeInSeconds) {
        Button btn = new Button(text);
        btn.setPrefSize(100, 40);
        btn.setTextFill(Color.web("#312e2b"));
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setCursor(Cursor.HAND);
        btn.setStyle(
                "-fx-background-color: #f0d9b5;" +
                        "-fx-background-radius: 8;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #f0d9b5; -fx-background-radius: 8;"));

        btn.setOnAction(event -> startGameWithTime(timeInSeconds));
        return btn;
    }
    private void startGameWithTime(int timeInSeconds) {

        appRoot.getChildren().remove(startMenuOverlay);
        startMenuOverlay = null;

        whiteTimeLeft = timeInSeconds;
        blackTimeLeft = timeInSeconds;

        whiteTimerLabel.setText("White: " + formatTime(whiteTimeLeft));
        blackTimerLabel.setText("Black: " + formatTime(blackTimeLeft));

        startTimer();
    }
    private Label createTimerLabel(String initialText) {
        Label label = new Label(initialText);
        label.setTextFill(Color.WHITE);
        label.setFont(new Font("Arial", 20));
        return label;
    }
    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (gameManager.getCurrentTurn() == Piece.Color.WHITE) {
                whiteTimeLeft--;
                whiteTimerLabel.setText("White: " + formatTime(whiteTimeLeft));
                if (whiteTimeLeft <= 0){
                    endGameByTime(Piece.Color.BLACK);
                }
            }else{
                blackTimeLeft--;
                blackTimerLabel.setText("Black: " + formatTime(blackTimeLeft));
                if (blackTimeLeft <= 0){
                    endGameByTime(Piece.Color.WHITE);
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void endGameByTime(Piece.Color winner) {
        timeline.stop();

      //ending game by changing game status
        if (winner == Piece.Color.WHITE) {
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
        if (timeline != null) {
            timeline.stop();
        }
        if (gameOverOverlay != null) {
            return;
        }

        Label badge = new Label(getGameOverBadge(reason));
        badge.setTextFill(Color.web("#312e2b"));
        badge.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        badge.setStyle("-fx-background-color: " + getGameOverAccent() + "; -fx-padding: 5 12; -fx-background-radius: 14;");

        Label title = new Label(getGameOverTitle());
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Label message = new Label(getGameOverMessage());
        message.setTextFill(Color.web("#d9d9d9"));
        message.setFont(Font.font("Arial", 15));
        message.setWrapText(true);
        message.setMaxWidth(280);
        message.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Zagraj ponownie");
        restartButton.setTextFill(Color.web("#312e2b"));
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        restartButton.setCursor(Cursor.HAND);
        restartButton.setStyle(
                "-fx-background-color: " + getGameOverAccent() + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;"
        );
        restartButton.setOnAction(event -> restartGame());

        Button exitButton = new Button("Wyjdź");
        exitButton.setTextFill(Color.WHITE);
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        exitButton.setCursor(Cursor.HAND);
        exitButton.setStyle(
                "-fx-background-color: #4a4744;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;"
        );
        exitButton.setOnAction(event -> Platform.exit());

        HBox buttonsBox = new HBox(15, exitButton, restartButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox content = new VBox(12, badge, title, message, buttonsBox);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24, 32, 20, 32));
        content.setMaxWidth(340);
        content.setStyle(
                "-fx-background-color: #312e2b;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + getGameOverAccent() + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;"
        );

        gameOverOverlay = new StackPane(content);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        gameOverOverlay.setPickOnBounds(true);
        appRoot.getChildren().add(gameOverOverlay);
    }
    private void restartGame() {

        appRoot.getChildren().remove(gameOverOverlay);
        gameOverOverlay = null;

        gameManager = new GameManager();
        controller = new ChessController(gameManager, this);


        drawBoard(null, null);

        showStartMenu();
    }

    private String getGameOverBadge(String reason) {
        if ("Time's up".equals(reason)) {
            return "TIME";
        } else if (gameManager.getStatus() == GameManager.GameStatus.STALEMATE) {
            return "STALEMATE";
        }
        return "CHECKMATE";
    }

    private String getGameOverTitle() {
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "White wins";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "Black wins";
        }
        return "Draw";
    }

    private String getGameOverMessage() {
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "White has won the game.";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "Black has won the game.";
        }
        return "No legal moves are available and the king is not in check.";
    }

    private String getGameOverAccent() {
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "#f0d9b5";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "#b58863";
        }
        return "#baca44";
    }
    public void drawBoard(Square selectedSquare, List<Move> legalMoves){
        boardGrid.getChildren().clear();
        Board board = gameManager.getBoard();
        for (int i = 0; i < 8; i++) {
            Label rankLabel = new Label(String.valueOf(8 - i));
            rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            rankLabel.setTextFill(Color.web("#BABABA"));
            rankLabel.setPrefSize(OFFSET_SIZE, TILE_SIZE);
            rankLabel.setAlignment(Pos.CENTER);
            boardGrid.add(rankLabel, 0, i);
        }
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                StackPane tile = new StackPane();
                //coloring board
                int visualRow = row;
                int visualCol = col + 1;
                Rectangle background = new Rectangle(TILE_SIZE, TILE_SIZE);
                boolean isLightSquare = (row + col) % 2 == 0;
                background.setFill(isLightSquare ? Color.web("#F0D9B5") : Color.web("#B58863"));
                tile.getChildren().add(background);

                //mixing color with green to highlight a square

                if (selectedSquare != null && selectedSquare.getRow() == row && selectedSquare.getCol() == col){
                    Rectangle highlight = new Rectangle(TILE_SIZE, TILE_SIZE);
                    highlight.setFill(Color.web("#BACA44", 0.5));  //0.5 is alpha,parameter of transparency
                    tile.getChildren().add(highlight);
                }

                boolean isLegalMove = false;
                if (legalMoves != null) {
                    for (Move move : legalMoves) {
                        if (move.getEnd().getRow() == row && move.getEnd().getCol() == col) {
                            isLegalMove = true;
                            break;
                        }
                    }
                }
                Piece piece = board.getPiece(new Square(row, col));
                if (piece != null) {
                    ImageView pieceImage = getPieceImageView(piece);        //putting a piece as a top layer
                    if (pieceImage != null)
                        tile.getChildren().add(pieceImage);
                }
                if (isLegalMove) { //highlighting moves
                    if (piece != null) {
                        Circle captureIndicator = new Circle(TILE_SIZE / 2.5);
                        captureIndicator.setFill(Color.TRANSPARENT);
                        captureIndicator.setStroke(Color.web("#000000", 0.25));
                        captureIndicator.setStrokeWidth(4);
                        tile.getChildren().add(captureIndicator);
                    } else {
                        Circle moveIndicator = new Circle(TILE_SIZE / 6.0);
                        moveIndicator.setFill(Color.web("#000000", 0.25));
                        tile.getChildren().add(moveIndicator);
                    }
                }
                final int clickedRow = row;
                final int clickedCol = col; // Adds a click listener using lambda, passing the captured coordinates to the controller
                tile.setOnMouseClicked(event -> controller.handleSquareClick(clickedRow, clickedCol));
                boardGrid.add(tile, visualCol, visualRow);

            }
        }
        String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (int i = 0; i < 8; i++) {
            Label fileLabel = new Label(files[i]);
            fileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            fileLabel.setTextFill(Color.web("#BABABA"));
            fileLabel.setPrefSize(TILE_SIZE, OFFSET_SIZE);
            fileLabel.setAlignment(Pos.CENTER);
            boardGrid.add(fileLabel, i + 1, 8);
        }
    }

    private ImageView getPieceImageView(Piece piece){
        String colorChar = piece.getColor() == Piece.Color.WHITE ? "w" : "b";
        String pieceChar = piece.getClass().getSimpleName();
        if(Objects.equals(pieceChar, "King"))
            pieceChar="k";
        else if(Objects.equals(pieceChar, "Knight"))
            pieceChar="n";
        else if(Objects.equals(pieceChar, "Rook"))
            pieceChar="r";
        else if(Objects.equals(pieceChar, "Queen"))
            pieceChar="q";
        else if(Objects.equals(pieceChar, "Bishop"))
            pieceChar="b";                  //finding the file with the graphic
        else if(Objects.equals(pieceChar, "Pawn"))
            pieceChar="p";

        String fileName = colorChar + pieceChar + ".png";
        URL imageUrl = getClass().getResource("/assets/Pieces_images1/" + fileName);

        if (imageUrl == null)
            return null;


        Image image = new Image(imageUrl.toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE - 10);
        imageView.setFitHeight(TILE_SIZE - 10);
        imageView.setPreserveRatio(true);
        return imageView;
    }
    public void showPromotionDialog(Piece.Color color, Consumer<Piece> onPieceSelected) {
        if (promotionOverlay != null) {
            return;
        }

        Piece queen = new Queen(color);
        Piece rook = new Rook(color);
        Piece bishop = new Bishop(color);
        Piece knight = new Knight(color); //setting images

        ImageView qImg = getPieceImageView(queen);
        ImageView rImg = getPieceImageView(rook);
        ImageView bImg = getPieceImageView(bishop);
        ImageView nImg = getPieceImageView(knight);

        ImageView[] images = {qImg, rImg, bImg, nImg};
        for (ImageView img : images) {
            if (img != null) {
                img.setCursor(Cursor.HAND);

                img.setOnMouseEntered(e -> {
                    img.setScaleX(1.1);
                    img.setScaleY(1.1);     //scaling actually clicked piece to distinguish
                });
                img.setOnMouseExited(e -> {
                    img.setScaleX(1.0);
                    img.setScaleY(1.0);
                });
            }
        }


        qImg.setOnMouseClicked(e -> handlePromotionSelection(queen, onPieceSelected));
        rImg.setOnMouseClicked(e -> handlePromotionSelection(rook, onPieceSelected));
        bImg.setOnMouseClicked(e -> handlePromotionSelection(bishop, onPieceSelected));
        nImg.setOnMouseClicked(e -> handlePromotionSelection(knight, onPieceSelected));


        HBox box = new HBox(15, qImg, rImg, bImg, nImg);
        box.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Wybierz figurę do promocji");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox dialogContent = new VBox(20, titleLabel, box);
        dialogContent.setAlignment(Pos.CENTER);
        dialogContent.setPadding(new Insets(20, 30, 20, 30));
        dialogContent.setMaxSize(400, 150);

        dialogContent.setStyle(
                "-fx-background-color: #312e2b;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #baca44;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;"
        );

        promotionOverlay = new StackPane(dialogContent);
        promotionOverlay.setAlignment(Pos.CENTER);
        promotionOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        appRoot.getChildren().add(promotionOverlay);
    }
    private void handlePromotionSelection(Piece chosenPiece, Consumer<Piece> onPieceSelected) {
        appRoot.getChildren().remove(promotionOverlay);
        promotionOverlay = null;
        onPieceSelected.accept(chosenPiece);
    }
    public void updateGraveyards(){
        blackGraveyard.update(
                gameManager.getCapturedWhitePieces(),
                gameManager.getMaterialAdvantage(Piece.Color.BLACK)
        );

        whiteGraveyard.update(
                gameManager.getCapturedBlackPieces(),
                gameManager.getMaterialAdvantage(Piece.Color.WHITE)
        );
    }
}
