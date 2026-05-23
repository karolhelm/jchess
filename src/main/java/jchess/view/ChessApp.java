package jchess.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import jchess.controller.ChessController;
import jchess.model.*;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.Cursor;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
public class ChessApp extends Application{

    private GameManager gameManager;
    private ChessController controller;
    private GridPane boardGrid;
    private static final int TILE_SIZE = 80;
    private int whiteTimeLeft = 300;
    private int blackTimeLeft = 300;    //time variables
    private Label whiteTimerLabel;
    private Label blackTimerLabel;
    private Timeline timeline;
    @Override
    public void start(Stage primaryStage){
        gameManager = new GameManager();
        controller = new ChessController(gameManager, this);
        boardGrid=new GridPane();
        BorderPane root = new BorderPane();
        root.setCenter(boardGrid);
        blackTimerLabel = createTimerLabel("Black: 05:00");
        whiteTimerLabel = createTimerLabel("White: 05:00");
        HBox topBar = new HBox(blackTimerLabel);
        topBar.setStyle("-fx-background-color: #312e2b; -fx-padding: 10; -fx-alignment: center;");
        HBox bottomBar = new HBox(whiteTimerLabel);
        bottomBar.setStyle("-fx-background-color: #312e2b; -fx-padding: 10; -fx-alignment: center;");
        root.setTop(topBar);
        root.setBottom(bottomBar);
        root.setTop(topBar);
        root.setBottom(bottomBar);
        startTimer();
        drawBoard(null);        //basic inizalization //
        Scene scene = new Scene(root, TILE_SIZE * 8, (TILE_SIZE * 8) + 80);

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


        Platform.runLater(() -> {                            //feel free to change that
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");        //window informing about game's end
            alert.setHeaderText("Time's up");
            alert.setContentText((winner == Piece.Color.WHITE ? "Whites" : "Black") + " won!");
            alert.showAndWait();
        });
    }
    public void drawBoard(Square selectedSquare){
        boardGrid.getChildren().clear();
        Board board = gameManager.getBoard();

        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                StackPane tile = new StackPane();
                //coloring board
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


                Piece piece = board.getPiece(new Square(row, col));
                if (piece != null) {
                    ImageView pieceImage = getPieceImageView(piece);        //putting a piece as a top layer
                    if (pieceImage != null)
                        tile.getChildren().add(pieceImage);
                }
                    final int clickedRow = row;
                    final int clickedCol = col; // Adds a click listener using lambda, passing the captured coordinates to the controller
                    tile.setOnMouseClicked(event -> controller.handleSquareClick(clickedRow, clickedCol));
                    boardGrid.add(tile, col, row);

            }
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