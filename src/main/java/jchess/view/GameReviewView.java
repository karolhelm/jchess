package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.config.UiConfig;

public class GameReviewView extends VBox {

    private final Label titleLabel;
    private final Label infoLabel;
    private final Button firstBtn;
    private final Button prevBtn;
    private final Button nextBtn;
    private final Button lastBtn;
    private final Button closeBtn;

    public GameReviewView(UiConfig ui, Runnable onFirst, Runnable onPrev, Runnable onNext, Runnable onLast, Runnable onClose) {
        setSpacing(6);
        setPadding(new Insets(8, 10, 8, 10));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + ui.getBackground() + ";");
        setVisible(false);
        setManaged(false);

        titleLabel = new Label("Game review");
        titleLabel.setTextFill(Color.web(ui.getAccent()));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        infoLabel = new Label();
        infoLabel.setTextFill(Color.web(ui.getTextPrimary()));
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        firstBtn = createNavBtn(ui, "«");
        prevBtn = createNavBtn(ui, "‹");
        nextBtn = createNavBtn(ui, "›");
        lastBtn = createNavBtn(ui, "»");

        firstBtn.setOnAction(e -> onFirst.run());
        prevBtn.setOnAction(e -> onPrev.run());
        nextBtn.setOnAction(e -> onNext.run());
        lastBtn.setOnAction(e -> onLast.run());

        HBox nav = new HBox(3, firstBtn, prevBtn, nextBtn, lastBtn);
        nav.setAlignment(Pos.CENTER);

        closeBtn = new Button("Close review");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setStyle(
                "-fx-background-color: #4a4744;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 6 12;" +
                        "-fx-font-size: 13px;"
        );
        closeBtn.setOnAction(e -> onClose.run());

        getChildren().addAll(titleLabel, infoLabel, nav, closeBtn);
    }

    private Button createNavBtn(UiConfig ui, String text) {
        Button btn = new Button(text);
        btn.setPrefSize(44, 32);
        btn.setMinSize(44, 32);
        btn.setStyle(
                "-fx-background-color: " + ui.getButtonBackground() + ";" +
                        "-fx-text-fill: " + ui.getBackground() + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;"
        );
        return btn;
    }

    public void show(int positionIndex, int positionCount) {
        setVisible(true);
        setManaged(true);
        update(positionIndex, positionCount);
    }

    public void hide() {
        setVisible(false);
        setManaged(false);
    }

    public void update(int positionIndex, int positionCount) {
        int totalMoves = Math.max(0, positionCount - 1);
        infoLabel.setText(positionIndex + " / " + totalMoves);
        firstBtn.setDisable(positionIndex <= 0);
        prevBtn.setDisable(positionIndex <= 0);
        nextBtn.setDisable(positionIndex >= positionCount - 1);
        lastBtn.setDisable(positionIndex >= positionCount - 1);
    }
}
