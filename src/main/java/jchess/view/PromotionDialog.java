package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.config.UiConfig;
import jchess.model.Piece;
import jchess.model.PieceColor;

import java.util.function.Consumer;

public class PromotionDialog extends StackPane {
    private final PieceImageFactory pieceImageFactory;
    private final UiConfig ui;
    private final Consumer<Piece> pieceSelectedHandler;

    public PromotionDialog(
            PieceColor color,
            PieceImageFactory pieceImageFactory,
            UiConfig ui,
            Consumer<Piece> pieceSelectedHandler
    ) {
        this.pieceImageFactory = pieceImageFactory;
        this.ui = ui;
        this.pieceSelectedHandler = pieceSelectedHandler;

        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        getChildren().add(createContent(color));
    }

    private VBox createContent(PieceColor color) {
        HBox piecesBox = new HBox(15);
        piecesBox.setAlignment(Pos.CENTER);
        piecesBox.getChildren().addAll(
                createPieceChoice(Piece.fromFenSymbol(color == PieceColor.WHITE ? 'Q' : 'q')),
                createPieceChoice(Piece.fromFenSymbol(color == PieceColor.WHITE ? 'R' : 'r')),
                createPieceChoice(Piece.fromFenSymbol(color == PieceColor.WHITE ? 'B' : 'b')),
                createPieceChoice(Piece.fromFenSymbol(color == PieceColor.WHITE ? 'N' : 'n'))
        );

        Label titleLabel = new Label("Choose promotion piece");
        titleLabel.setTextFill(Color.web(ui.getTextPrimary()));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox dialogContent = new VBox(20, titleLabel, piecesBox);
        dialogContent.setAlignment(Pos.CENTER);
        dialogContent.setPadding(new Insets(20, 30, 20, 30));
        dialogContent.setMaxSize(400, 150);
        dialogContent.setStyle(
                "-fx-background-color: " + ui.getBackground() + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + ui.getAccent() + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;"
        );
        return dialogContent;
    }

    private ImageView createPieceChoice(Piece piece) {
        ImageView image = pieceImageFactory.create(piece);
        if (image == null) {
            return new ImageView();
        }

        image.setCursor(Cursor.HAND);
        image.setOnMouseEntered(e -> {
            image.setScaleX(1.1);
            image.setScaleY(1.1);
        });
        image.setOnMouseExited(e -> {
            image.setScaleX(1.0);
            image.setScaleY(1.0);
        });
        image.setOnMouseClicked(e -> pieceSelectedHandler.accept(piece));
        return image;
    }
}
