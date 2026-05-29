package jchess.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.model.Piece;

import java.util.List;
import java.util.function.Function;

public class GraveyardView extends HBox{
    private HBox piecesBox;
    private Label scoreLabel;
    private Function<Piece, ImageView> imageProvider;

    public GraveyardView(Function<Piece, ImageView> imageProvider){
        this.imageProvider = imageProvider;
        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);

        piecesBox = new HBox(2);
        piecesBox.setAlignment(Pos.CENTER);
        piecesBox.setMinHeight(30);

        scoreLabel = new Label();
        scoreLabel.setTextFill(Color.web("#a3a3a3"));
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        this.getChildren().addAll(piecesBox, scoreLabel);
    }

    public void update(List<Piece> capturedPieces, int advantage){
        piecesBox.getChildren().clear();
        scoreLabel.setText("");


        capturedPieces.sort((p1, p2) -> Integer.compare(p2.getValue(), p1.getValue()));

        for(Piece p : capturedPieces){
            ImageView img = imageProvider.apply(p);
            if(img != null){
                img.setFitWidth(25);
                img.setFitHeight(25);
                piecesBox.getChildren().add(img);
            }
        }

        if(advantage > 0)    //displaying only for player with advantage
            scoreLabel.setText("+" + advantage);

    }
}