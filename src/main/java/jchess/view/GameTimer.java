package jchess.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import jchess.config.UiConfig;
import jchess.model.PieceColor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GameTimer {
    private final Supplier<PieceColor> currentTurnSupplier;
    private final Consumer<PieceColor> timeoutHandler;
    private final Label whiteTimerLabel;
    private final Label blackTimerLabel;

    private Timeline timeline;
    private int whiteTimeLeft;
    private int blackTimeLeft;

    public GameTimer(UiConfig ui, Supplier<PieceColor> currentTurnSupplier, Consumer<PieceColor> timeoutHandler) {
        this.currentTurnSupplier = currentTurnSupplier;
        this.timeoutHandler = timeoutHandler;
        this.blackTimerLabel = createTimerLabel(ui, "Black: --:--");
        this.whiteTimerLabel = createTimerLabel(ui, "White: --:--");
    }

    public Label getWhiteTimerLabel() {
        return whiteTimerLabel;
    }

    public Label getBlackTimerLabel() {
        return blackTimerLabel;
    }

    public void start(int timeInSeconds) {
        stop();
        if (timeInSeconds <= 0) {
            whiteTimerLabel.setText("White: \u221E");
            blackTimerLabel.setText("Black: \u221E");
            return;
        }
        whiteTimeLeft = timeInSeconds;
        blackTimeLeft = timeInSeconds;
        updateLabels();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private Label createTimerLabel(UiConfig ui, String initialText) {
        Label label = new Label(initialText);
        label.setTextFill(Color.web(ui.getTextPrimary()));
        label.setFont(new Font("Arial", 20));
        return label;
    }

    private void tick() {
        if (currentTurnSupplier.get() == PieceColor.WHITE) {
            whiteTimeLeft--;
            whiteTimerLabel.setText("White: " + formatTime(whiteTimeLeft));
            if (whiteTimeLeft <= 0) {
                handleTimeout(PieceColor.BLACK);
            }
        } else {
            blackTimeLeft--;
            blackTimerLabel.setText("Black: " + formatTime(blackTimeLeft));
            if (blackTimeLeft <= 0) {
                handleTimeout(PieceColor.WHITE);
            }
        }
    }

    private void handleTimeout(PieceColor winner) {
        stop();
        timeoutHandler.accept(winner);
    }

    private void updateLabels() {
        whiteTimerLabel.setText("White: " + formatTime(whiteTimeLeft));
        blackTimerLabel.setText("Black: " + formatTime(blackTimeLeft));
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
