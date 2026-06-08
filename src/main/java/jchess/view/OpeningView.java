package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jchess.config.UiConfig;

public class OpeningView extends VBox {

    private final Label infoLabel;
    private final Button prevBtn;
    private final Button nextBtn;

    public OpeningView(UiConfig ui, Runnable onPrev, Runnable onNext) {
        setSpacing(6);
        setPadding(new Insets(6, 10, 6, 10));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + ui.getBackground() + ";");
        setVisible(false);
        setManaged(false);

        infoLabel = new Label();
        infoLabel.setStyle("-fx-text-fill: " + ui.getTextPrimary() + ";");

        prevBtn = createBtn(ui, "‹");
        nextBtn = createBtn(ui, "›");
        prevBtn.setOnAction(e -> onPrev.run());
        nextBtn.setOnAction(e -> onNext.run());

        HBox nav = new HBox(10, prevBtn, infoLabel, nextBtn);
        nav.setAlignment(Pos.CENTER);
        getChildren().add(nav);
    }

    private Button createBtn(UiConfig ui, String text) {
        Button btn = new Button(text);
        btn.setPrefSize(36, 30);
        btn.setStyle(
                "-fx-background-color: " + ui.getButtonBackground() + ";" +
                        "-fx-text-fill: " + ui.getBackground() + ";" +
                        "-fx-background-radius: 6;"
        );
        return btn;
    }

    public void show(String openingName, int stepIndex, int stepCount) {
        setVisible(true);
        setManaged(true);
        update(openingName, stepIndex, stepCount);
    }

    public void hide() {
        setVisible(false);
        setManaged(false);
    }

    public void update(String openingName, int stepIndex, int stepCount) {
        infoLabel.setText(openingName + "  " + (stepIndex + 1) + " / " + stepCount);
        prevBtn.setDisable(stepIndex <= 0);
        nextBtn.setDisable(stepIndex >= stepCount - 1);
    }
}
