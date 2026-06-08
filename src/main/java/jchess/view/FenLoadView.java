package jchess.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.config.UiConfig;

import java.util.function.Consumer;

public class FenLoadView extends VBox {

    private final Consumer<String> fenLoadHandler;
    private final Label errorLabel;

    public FenLoadView(UiConfig ui, Consumer<String> fenLoadHandler) {
        this.fenLoadHandler = fenLoadHandler;

        setSpacing(6);
        setAlignment(Pos.CENTER);

        Label subtitle = new Label("Load FEN position");
        subtitle.setTextFill(Color.web(ui.getAccent()));
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField input = new TextField();
        input.setPromptText("Paste FEN...");
        input.setPrefWidth(300);
        input.setStyle(
                "-fx-background-color: " + ui.getBackground() + ";" +
                        "-fx-text-fill: " + ui.getTextPrimary() + ";" +
                        "-fx-border-color: " + ui.getAccent() + ";" +
                        "-fx-border-radius: 4;"
        );

        Button loadBtn = new Button("Load FEN");
        loadBtn.setTextFill(Color.web(ui.getBackground()));
        loadBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        loadBtn.setStyle(
                "-fx-background-color: " + ui.getButtonBackground() + ";" +
                        "-fx-background-radius: 8;"
        );
        loadBtn.setOnAction(e -> tryLoad(input.getText()));

        errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#ff6b6b"));
        errorLabel.setFont(Font.font("Arial", 11));
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(300);
        errorLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(subtitle, input, loadBtn, errorLabel);
    }

    private void tryLoad(String fen) {
        String trimmed = fen.trim();
        if (trimmed.isEmpty()) {
            errorLabel.setText("Empty FEN");
            return;
        }
        try {
            fenLoadHandler.accept(trimmed);
            errorLabel.setText("");
        } catch (Exception ex) {
            errorLabel.setText("Invalid FEN");
        }
    }
}
