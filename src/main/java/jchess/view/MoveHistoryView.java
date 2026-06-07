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

    public MoveHistoryView(UiConfig ui) {
        setSpacing(8);
        setPadding(new Insets(10));
        setMinWidth(180);
        setPrefWidth(200);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setStyle("-fx-background-color: " + ui.getBackground() + ";");

        halfMoveLabel = new Label();
        halfMoveLabel.setTextFill(Color.web(ui.getTextPrimary()));
        halfMoveLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        setHalfMoveClock(0);

        Label title = new Label("Historia partii");
        title.setTextFill(Color.web(ui.getAccent()));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        listView = new ListView<>(items);
        listView.setFocusTraversable(false);
        listView.setStyle(
                "-fx-background-color: " + ui.getBackground() + ";" +
                        "-fx-control-inner-background: " + ui.getBackground() + ";" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        listView.setFixedCellSize(22);
        getChildren().addAll(halfMoveLabel, title, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
    }

    public void setHalfMoveClock(int halfMoves) {
        halfMoveLabel.setText("Półruchy bez postępu: " + halfMoves + " / " + FIFTY_MOVE_LIMIT);
    }
    public void addMove(String san, PieceColor mover) {
        if (mover == PieceColor.WHITE) {
            items.add(moveNumber + ". " + san);
        } else {
            int last = items.size() - 1;
            items.set(last, items.get(last) + "  " + san);
            moveNumber++;
        }
        listView.scrollTo(items.size() - 1);
        listView.getSelectionModel().clearSelection();
    }
    public void clear() {
        items.clear();
        moveNumber = 1;
        setHalfMoveClock(0);
    }
}