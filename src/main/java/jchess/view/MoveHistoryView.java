package jchess.view;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.config.UiConfig;
import jchess.model.PieceColor;
public class MoveHistoryView extends VBox {
    private static final int FIFTY_MOVE_LIMIT = 50;

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ListView<String> listView;
    private final Label halfMoveLabel;
    private int moveNumber = 1;
    private boolean awaitingWhite = true;

    public MoveHistoryView(UiConfig ui) {
        setSpacing(10);
        setPadding(new Insets(12));
        setMinWidth(220);
        setPrefWidth(240);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setStyle("-fx-background-color: " + ui.getBackground() + ";");

        halfMoveLabel = new Label();
        halfMoveLabel.setTextFill(Color.web(ui.getTextPrimary()));
        halfMoveLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        halfMoveLabel.setWrapText(true);
        halfMoveLabel.setMaxWidth(Double.MAX_VALUE);
        setHalfMoveClock(0);

        Label title = new Label("Game history");
        title.setTextFill(Color.web(ui.getAccent()));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        listView = new ListView<>(items);
        listView.setFocusTraversable(false);
        listView.setStyle(
                "-fx-background-color: " + ui.getBackground() + ";" +
                        "-fx-control-inner-background: " + ui.getBackground() + ";" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;" +
                        "-fx-font-size: 14px;"
        );
        listView.setFixedCellSize(26);
        getChildren().addAll(halfMoveLabel, title, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
    }

    public void setHalfMoveClock(int halfMoves) {
        halfMoveLabel.setText("Half-moves: " + halfMoves + " / " + FIFTY_MOVE_LIMIT);
    }
    public void addMove(String san, PieceColor mover) {
        if (mover == PieceColor.WHITE) {
            items.add(moveNumber + ". " + san);
            awaitingWhite = false;
        } else {
            if (awaitingWhite || items.isEmpty()) {
                items.add(moveNumber + ". ... " + san);
            } else {
                int last = items.size() - 1;
                items.set(last, items.get(last) + "  " + san);
            }
            moveNumber++;
            awaitingWhite = true;
        }
        listView.scrollTo(items.size() - 1);
        listView.getSelectionModel().clearSelection();
    }
    public void clear() {
        items.clear();
        moveNumber = 1;
        awaitingWhite = true;
        setHalfMoveClock(0);
    }
    public void setStartingPosition(int fullMoveNumber, PieceColor turnToMove) {
        items.clear();
        moveNumber = Math.max(1, fullMoveNumber);
        awaitingWhite = (turnToMove == PieceColor.WHITE);
    }
}