package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.config.UiConfig;
import jchess.model.GameManager;

public class GameOverDialog extends StackPane {
    public static final String TIME_UP_REASON = "Time's up";
    public static final String MANUAL_REASON = "Manual end";

    private final GameManager gameManager;
    private final UiConfig ui;
    private final String reason;
    private final Runnable reviewHandler;
    private final boolean reviewAvailable;

    public GameOverDialog(GameManager gameManager, UiConfig ui, String reason, Runnable restartHandler, Runnable exitHandler, Runnable reviewHandler, boolean reviewAvailable) {
        this.gameManager = gameManager;
        this.ui = ui;
        this.reason = reason;
        this.reviewHandler = reviewHandler;
        this.reviewAvailable = reviewAvailable;

        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: transparent;");
        setPickOnBounds(false);
        getChildren().add(createContent(restartHandler, exitHandler));
    }

    private VBox createContent(Runnable restartHandler, Runnable exitHandler) {
        Label badge = new Label(getGameOverBadge());
        badge.setTextFill(Color.web(ui.getBackground()));
        badge.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        badge.setStyle("-fx-background-color: " + getGameOverAccent() + "; -fx-padding: 4 11; -fx-background-radius: 12;");

        Label title = new Label(getGameOverTitle());
        title.setTextFill(Color.web(ui.getTextPrimary()));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label message = new Label(getGameOverMessage());
        message.setTextFill(Color.web("#d9d9d9"));
        message.setFont(Font.font("Arial", 14));
        message.setWrapText(true);
        message.setMaxWidth(240);
        message.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Back to menu");
        restartButton.setTextFill(Color.web(ui.getBackground()));
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        restartButton.setCursor(Cursor.HAND);
        restartButton.setStyle(
                "-fx-background-color: " + getGameOverAccent() + ";" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 7 16;"
        );
        restartButton.setOnAction(event -> restartHandler.run());

        Button exitButton = new Button("Exit");
        exitButton.setTextFill(Color.WHITE);
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        exitButton.setCursor(Cursor.HAND);
        exitButton.setStyle(
                "-fx-background-color: #4a4744;" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 7 16;"
        );
        exitButton.setOnAction(event -> exitHandler.run());

        Button reviewButton = new Button("Review game");
        reviewButton.setTextFill(Color.WHITE);
        reviewButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        reviewButton.setCursor(Cursor.HAND);
        reviewButton.setStyle(
                "-fx-background-color: #4a4744;" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 7 16;"
        );
        reviewButton.setOnAction(event -> reviewHandler.run());

        HBox buttonsBox = new HBox(10, exitButton, restartButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox content = new VBox(11, badge, title, message, buttonsBox);
        if (reviewAvailable) {
            HBox reviewBox = new HBox(reviewButton);
            reviewBox.setAlignment(Pos.CENTER);
            content.getChildren().add(reviewBox);
        }
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20, 26, 18, 26));
        content.setMaxWidth(280);
        content.setMaxHeight(VBox.USE_PREF_SIZE);
        content.setOpacity(0.92);
        content.setStyle(
                "-fx-background-color: " + ui.getBackground() + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + getGameOverAccent() + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;"
        );
        return content;
    }

    private String getGameOverBadge() {
        if (MANUAL_REASON.equals(reason) || gameManager.getStatus() == GameManager.GameStatus.ENDED) {
            return "END";
        }
        if (TIME_UP_REASON.equals(reason)) {
            return "TIME";
        } else if (gameManager.getStatus() == GameManager.GameStatus.STALEMATE) {
            return "STALEMATE";
        } else if (gameManager.getStatus() == GameManager.GameStatus.DRAW) {
            return "DRAW";
        }
        return "CHECKMATE";
    }

    private String getGameOverTitle() {
        if (MANUAL_REASON.equals(reason) || gameManager.getStatus() == GameManager.GameStatus.ENDED) {
            return "End of game";
        }
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "White wins";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "Black wins";
        }
        if (gameManager.getStatus() == GameManager.GameStatus.DRAW) {
            return "Draw";
        }
        return "Draw";
    }

    private String getGameOverMessage() {
        if (MANUAL_REASON.equals(reason) || gameManager.getStatus() == GameManager.GameStatus.ENDED) {
            return "Game was ended.";
        }
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "White has won the game.";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "Black has won the game.";
        }
        if (gameManager.getStatus() == GameManager.GameStatus.DRAW) {
            return "Insufficient material to checkmate.";
        }
        return "No legal moves are available and the king is not in check.";
    }

    private String getGameOverAccent() {
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "#f0d9b5";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "#b58863";
        }
        return ui.getAccent();
    }
}
