package jchess.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import javafx.scene.control.Dialog;
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
    private static final int TILE_SIZE = 60;
    private static final int OFFSET_SIZE = 25;
    private int whiteTimeLeft = 300;
    private int blackTimeLeft = 300;    //time variables
    private Label whiteTimerLabel;
    private Label blackTimerLabel;
    private Timeline timeline;
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
        blackTimerLabel = createTimerLabel("Black: 05:00");
        whiteTimerLabel = createTimerLabel("White: 05:00");
        HBox topBar = new HBox(blackTimerLabel);
        topBar.setStyle("-fx-background-color: #312e2b; -fx-padding: 10; -fx-alignment: center;");
        HBox bottomBar = new HBox(whiteTimerLabel);
        bottomBar.setStyle("-fx-background-color: #312e2b; -fx-padding: 10; -fx-alignment: center;");
        root.setTop(topBar);
        root.setBottom(bottomBar);
        startTimer();
        drawBoard(null, null);        //basic inizalization //
        Scene scene = new Scene(appRoot, (TILE_SIZE * 8) + OFFSET_SIZE, (TILE_SIZE * 8) + OFFSET_SIZE + 100);
        primaryStage.setTitle("JChess");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
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

        Button okButton = new Button("OK");
        okButton.setTextFill(Color.web("#312e2b"));
        okButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        okButton.setStyle(
                "-fx-background-color: " + getGameOverAccent() + ";" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 26;"
        );

        VBox content = new VBox(12, badge, title, message, okButton);
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
        okButton.setOnAction(event -> {
            appRoot.getChildren().remove(gameOverOverlay);
            gameOverOverlay = null;
        });

        appRoot.getChildren().add(gameOverOverlay);
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
        Dialog<Piece> dialog = new Dialog<>();
        dialog.setTitle("");
        dialog.setHeaderText("");

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


        qImg.setOnMouseClicked(e -> { dialog.setResult(queen); dialog.close(); });
        rImg.setOnMouseClicked(e -> { dialog.setResult(rook); dialog.close(); });
        bImg.setOnMouseClicked(e -> { dialog.setResult(bishop); dialog.close(); });
        nImg.setOnMouseClicked(e -> { dialog.setResult(knight); dialog.close(); });


        HBox box = new HBox(15, qImg, rImg, bImg, nImg);
        box.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(box);


        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);

        dialog.showAndWait().ifPresent(chosenPiece -> {
            onPieceSelected.accept(chosenPiece);
        });
    }
}
