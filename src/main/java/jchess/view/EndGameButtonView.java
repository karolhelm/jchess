package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jchess.config.UiConfig;

public class EndGameButtonView extends VBox {

    private final Button endBtn;

    public EndGameButtonView(UiConfig ui, Runnable onEndGame) {
        setPadding(new Insets(0, 12, 8, 12));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + ui.getBackground() + ";");

        endBtn = new Button("End game");
        endBtn.setMaxWidth(Double.MAX_VALUE);
        endBtn.setStyle(
                "-fx-background-color: #4a4744;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 12;" +
                        "-fx-font-size: 14px;"
        );
        endBtn.setOnAction(e -> onEndGame.run());

        getChildren().add(endBtn);
    }

    public void setLabel(String text) {
        endBtn.setText(text);
    }
}
