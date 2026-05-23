package jchess.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jchess.controller.ChessController;
import jchess.model.*;
import java.net.URL;
import java.util.Objects;

public class ChessApp extends Application{

    private GameManager gameManager;
    private ChessController controller;
    private GridPane boardGrid;
    private static final int TILE_SIZE = 80;

    @Override
    public void start(Stage primaryStage){
        gameManager = new GameManager();
        controller = new ChessController(gameManager, this);
        boardGrid=new GridPane();
        drawBoard(null);        //basic inizalization //
        Scene scene = new Scene(boardGrid, TILE_SIZE * 8, TILE_SIZE * 8);
        primaryStage.setTitle("JChess");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
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
                if (piece != null){
                    ImageView pieceImage = getPieceImageView(piece);        //putting a piece as a top layer
                    if (pieceImage != null)
                        tile.getChildren().add(pieceImage);

                    final int clickedRow = row;
                    final int clickedCol = col; // Adds a click listener using lambda, passing the captured coordinates to the controller
                    tile.setOnMouseClicked(event -> controller.handleSquareClick(clickedRow, clickedCol));
                    boardGrid.add(tile, col, row);
                }
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
        URL imageUrl = getClass().getResource("/assets/" + fileName);

        if (imageUrl == null)
            return null;


        Image image = new Image(imageUrl.toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE - 10);
        imageView.setFitHeight(TILE_SIZE - 10);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}