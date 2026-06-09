package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EvaluationBarView extends VBox {

    public static final double CONTAINER_WIDTH = 60;
    private static final double BAR_WIDTH = 28;
    private static final int CLAMP_CENTIPAWNS = 1000;
    private static final int MATE_THRESHOLD = 50_000;

    private final Region whitePart;
    private final Region blackPart;
    private final Label evalLabel;
    private final double barHeight;

    public EvaluationBarView(double barHeight) {
        this.barHeight = barHeight;
        setSpacing(4);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(4, 4, 4, 4));
        setPrefWidth(CONTAINER_WIDTH);
        setMinWidth(CONTAINER_WIDTH);
        setMaxWidth(CONTAINER_WIDTH);
        setVisible(false);
        setManaged(false);

        evalLabel = new Label("0.00");
        evalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        evalLabel.setTextFill(Color.web("#f0f0f0"));
        evalLabel.setMinWidth(Region.USE_PREF_SIZE);
        evalLabel.setEllipsisString("");
        evalLabel.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.55);" +
                        "-fx-padding: 2 5;" +
                        "-fx-background-radius: 4;"
        );

        blackPart = new Region();
        blackPart.setStyle("-fx-background-color: #2b2b2b;");

        whitePart = new Region();
        whitePart.setStyle("-fx-background-color: #f0f0f0;");

        VBox column = new VBox(blackPart, whitePart);
        column.setAlignment(Pos.CENTER);
        column.setPrefSize(BAR_WIDTH, barHeight);
        column.setMinSize(BAR_WIDTH, barHeight);
        column.setMaxSize(BAR_WIDTH, barHeight);
        column.setStyle("-fx-background-color: #1a1a1a;");

        StackPane barWrapper = new StackPane(column);
        barWrapper.setAlignment(Pos.CENTER);

        getChildren().addAll(evalLabel, barWrapper);
        applyEvaluation(0);
    }

    public void show(int centipawns) {
        setVisible(true);
        setManaged(true);
        applyEvaluation(centipawns);
    }

    public void update(int centipawns) {
        applyEvaluation(centipawns);
    }

    public void hide() {
        setVisible(false);
        setManaged(false);
    }

    private void applyEvaluation(int centipawns) {
        evalLabel.setText(formatEvaluation(centipawns));

        double whiteFraction;
        if (centipawns >= MATE_THRESHOLD) {
            whiteFraction = 1.0;
        } else if (centipawns <= -MATE_THRESHOLD) {
            whiteFraction = 0.0;
        } else {
            int clamped = Math.max(-CLAMP_CENTIPAWNS, Math.min(CLAMP_CENTIPAWNS, centipawns));
            whiteFraction = 0.5 + (clamped / (double) (2 * CLAMP_CENTIPAWNS));
        }

        double whiteHeight = barHeight * whiteFraction;
        double blackHeight = barHeight - whiteHeight;
        whitePart.setPrefHeight(whiteHeight);
        whitePart.setMinHeight(whiteHeight);
        whitePart.setMaxHeight(whiteHeight);
        blackPart.setPrefHeight(blackHeight);
        blackPart.setMinHeight(blackHeight);
        blackPart.setMaxHeight(blackHeight);
    }

    private String formatEvaluation(int centipawns) {
        if (centipawns >= MATE_THRESHOLD) {
            return "+M";
        }
        if (centipawns <= -MATE_THRESHOLD) {
            return "-M";
        }
        double pawns = centipawns / 100.0;
        if (Math.abs(pawns) >= 100) {
            return String.format("%+.0f", pawns);
        }
        return String.format("%+.2f", pawns);
    }
}
