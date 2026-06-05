package jchess.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.config.AppConfig;
import jchess.config.BoardTheme;
import jchess.model.Board;
import jchess.model.Move;
import jchess.model.Piece;
import jchess.model.Square;

import java.util.List;
import java.util.function.BiConsumer;

public class BoardView extends GridPane {
    private static final String[] FILES = {"a", "b", "c", "d", "e", "f", "g", "h"};

    private final AppConfig appConfig;
    private final PieceImageFactory pieceImageFactory;
    private final BiConsumer<Integer, Integer> squareClickHandler;
    private final int tileSize;
    private final int offsetSize;
    private String selectedThemeId;

    public BoardView(
            AppConfig appConfig,
            PieceImageFactory pieceImageFactory,
            BiConsumer<Integer, Integer> squareClickHandler,
            int tileSize,
            int offsetSize
    ) {
        this.appConfig = appConfig;
        this.pieceImageFactory = pieceImageFactory;
        this.squareClickHandler = squareClickHandler;
        this.tileSize = tileSize;
        this.offsetSize = offsetSize;
        this.selectedThemeId = appConfig.getDefaultBoardThemeId();

        setStyle("-fx-background-color: " + appConfig.getUi().getBackground() + ";");
        setAlignment(Pos.CENTER);
    }

    public void setSelectedThemeId(String selectedThemeId) {
        this.selectedThemeId = selectedThemeId;
    }

    public void draw(Board board, Square selectedSquare, List<Move> legalMoves) {
        getChildren().clear();
        BoardTheme theme = appConfig.getThemeById(selectedThemeId);

        addRankLabels();
        addBoardTiles(board, selectedSquare, legalMoves, theme);
        addFileLabels();
    }

    private void addRankLabels() {
        for (int i = 0; i < 8; i++) {
            Label rankLabel = new Label(String.valueOf(8 - i));
            rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            rankLabel.setTextFill(Color.web(appConfig.getUi().getCoordinate()));
            rankLabel.setPrefSize(offsetSize, tileSize);
            rankLabel.setAlignment(Pos.CENTER);
            add(rankLabel, 0, i);
        }
    }

    private void addBoardTiles(Board board, Square selectedSquare, List<Move> legalMoves, BoardTheme theme) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane tile = createTile(board, selectedSquare, legalMoves, theme, row, col);
                add(tile, col + 1, row);
            }
        }
    }

    private StackPane createTile(Board board, Square selectedSquare, List<Move> legalMoves, BoardTheme theme, int row, int col) {
        StackPane tile = new StackPane();
        Piece piece = board.getPiece(new Square(row, col));

        tile.getChildren().add(createSquareBackground(theme, row, col));
        addSelectedHighlight(tile, selectedSquare, row, col);
        addPieceImage(tile, piece);
        addLegalMoveIndicator(tile, piece, legalMoves, row, col);

        final int clickedRow = row;
        final int clickedCol = col;
        tile.setOnMouseClicked(event -> squareClickHandler.accept(clickedRow, clickedCol));
        return tile;
    }

    private Rectangle createSquareBackground(BoardTheme theme, int row, int col) {
        Rectangle background = new Rectangle(tileSize, tileSize);
        boolean isLightSquare = (row + col) % 2 == 0;
        background.setFill(isLightSquare ? Color.web(theme.getLightSquare()) : Color.web(theme.getDarkSquare()));
        return background;
    }

    private void addSelectedHighlight(StackPane tile, Square selectedSquare, int row, int col) {
        if (selectedSquare != null && selectedSquare.getRow() == row && selectedSquare.getCol() == col) {
            Rectangle highlight = new Rectangle(tileSize, tileSize);
            highlight.setFill(Color.web(appConfig.getUi().getHighlight(), 0.5));
            tile.getChildren().add(highlight);
        }
    }

    private void addPieceImage(StackPane tile, Piece piece) {
        if (piece == null) {
            return;
        }

        ImageView pieceImage = pieceImageFactory.create(piece);
        if (pieceImage != null) {
            tile.getChildren().add(pieceImage);
        }
    }

    private void addLegalMoveIndicator(StackPane tile, Piece piece, List<Move> legalMoves, int row, int col) {
        if (!isLegalMove(legalMoves, row, col)) {
            return;
        }

        if (piece != null) {
            Circle captureIndicator = new Circle(tileSize / 2.5);
            captureIndicator.setFill(Color.TRANSPARENT);
            captureIndicator.setStroke(Color.web("#000000", 0.25));
            captureIndicator.setStrokeWidth(4);
            tile.getChildren().add(captureIndicator);
        } else {
            Circle moveIndicator = new Circle(tileSize / 6.0);
            moveIndicator.setFill(Color.web("#000000", 0.25));
            tile.getChildren().add(moveIndicator);
        }
    }

    private boolean isLegalMove(List<Move> legalMoves, int row, int col) {
        if (legalMoves == null) {
            return false;
        }

        for (Move move : legalMoves) {
            if (move.getEnd().getRow() == row && move.getEnd().getCol() == col) {
                return true;
            }
        }
        return false;
    }

    private void addFileLabels() {
        for (int i = 0; i < 8; i++) {
            Label fileLabel = new Label(FILES[i]);
            fileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            fileLabel.setTextFill(Color.web(appConfig.getUi().getCoordinate()));
            fileLabel.setPrefSize(tileSize, offsetSize);
            fileLabel.setAlignment(Pos.CENTER);
            add(fileLabel, i + 1, 8);
        }
    }
}
