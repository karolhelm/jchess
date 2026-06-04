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
    private static final String TIME_UP_REASON = "Time's up";

    private final GameManager gameManager;
    private final UiConfig ui;
    private final String reason;

    public GameOverDialog(GameManager gameManager, UiConfig ui, String reason, Runnable restartHandler, Runnable exitHandler) {
        this.gameManager = gameManager;
        this.ui = ui;
        this.reason = reason;

        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        setPickOnBounds(true);
        getChildren().add(createContent(restartHandler, exitHandler));
    }

    private VBox createContent(Runnable restartHandler, Runnable exitHandler) {
        Label badge = new Label(getGameOverBadge());
        badge.setTextFill(Color.web(ui.getBackground()));
        badge.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        badge.setStyle("-fx-background-color: " + getGameOverAccent() + "; -fx-padding: 5 12; -fx-background-radius: 14;");

        Label title = new Label(getGameOverTitle());
        title.setTextFill(Color.web(ui.getTextPrimary()));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Label message = new Label(getGameOverMessage());
        message.setTextFill(Color.web("#d9d9d9"));
        message.setFont(Font.font("Arial", 15));
        message.setWrapText(true);
        message.setMaxWidth(280);
        message.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Zagraj ponownie");
        restartButton.setTextFill(Color.web(ui.getBackground()));
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        restartButton.setCursor(Cursor.HAND);
        restartButton.setStyle(
                "-fx-background-color: " + getGameOverAccent() + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;"
        );
        restartButton.setOnAction(event -> restartHandler.run());

        Button exitButton = new Button("Wyjd\u017a");
        exitButton.setTextFill(Color.WHITE);
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        exitButton.setCursor(Cursor.HAND);
        exitButton.setStyle(
                "-fx-background-color: #4a4744;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;"
        );
        exitButton.setOnAction(event -> exitHandler.run());

        HBox buttonsBox = new HBox(15, exitButton, restartButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox content = new VBox(12, badge, title, message, buttonsBox);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24, 32, 20, 32));
        content.setMaxWidth(340);
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
        if (TIME_UP_REASON.equals(reason)) {
            return "TIME";
        } else if (gameManager.getStatus() == GameManager.GameStatus.STALEMATE) {
            return "STALEMATE";
        }
        return "CHECKMATE";
    }

    private String getGameOverTitle() {
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "White wins";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "Black wins";
        }
        return "Draw";
    }

    private String getGameOverMessage() {
        if (gameManager.getStatus() == GameManager.GameStatus.WHITE_WINS) {
            return "White has won the game.";
        } else if (gameManager.getStatus() == GameManager.GameStatus.BLACK_WINS) {
            return "Black has won the game.";
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
