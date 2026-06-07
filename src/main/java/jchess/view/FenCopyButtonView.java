package jchess.view;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import jchess.config.UiConfig;

import java.util.function.Supplier;

public class FenCopyButtonView extends VBox {

    private final Supplier<String> fenSupplier;
    private final Button copyButton;

    public FenCopyButtonView(UiConfig ui, Supplier<String> fenSupplier) {
        this.fenSupplier = fenSupplier;

        setPadding(new Insets(0, 10, 10, 10));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + ui.getBackground() + ";");

        copyButton = new Button("Kopiuj FEN");
        copyButton.setMaxWidth(Double.MAX_VALUE);
        copyButton.setStyle(
                "-fx-background-color: " + ui.getButtonBackground() + ";" +
                        "-fx-text-fill: " + ui.getBackground() + ";" +
                        "-fx-background-radius: 6;"
        );
        copyButton.setOnAction(e -> copyToClipboard());

        getChildren().add(copyButton);
    }

    private void copyToClipboard() {
        ClipboardContent content = new ClipboardContent();
        content.putString(fenSupplier.get());
        Clipboard.getSystemClipboard().setContent(content);

        copyButton.setText("Skopiowano!");
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> copyButton.setText("Kopiuj FEN"));
        pause.play();
    }
}
