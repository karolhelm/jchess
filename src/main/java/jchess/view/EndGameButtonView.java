package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jchess.config.UiConfig;

public class EndGameButtonView extends VBox {

    public EndGameButtonView(UiConfig ui, Runnable onEndGame) {
        setPadding(new Insets(0, 10, 6, 10));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + ui.getBackground() + ";");

        Button endBtn = new Button("Zakończ grę");
        endBtn.setMaxWidth(Double.MAX_VALUE);
        endBtn.setStyle(
                "-fx-background-color: #4a4744;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-background-radius: 6;"
        );
        endBtn.setOnAction(e -> onEndGame.run());

        getChildren().add(endBtn);
    }
}
