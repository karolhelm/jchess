package jchess.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jchess.config.AppConfig;
import jchess.config.BoardTheme;
import jchess.config.UiConfig;
import jchess.model.Opening;
import jchess.model.OpeningLibrary;

import java.util.function.Consumer;

public class StartMenuView extends StackPane {

    @FunctionalInterface
    public interface GameStartHandler {
        void onGameStart(int timeInSeconds, boolean isBot, String fenOrNull);
    }

    private final AppConfig appConfig;
    private final UiConfig ui;
    private final Consumer<String> themeSelectedHandler;
    private final GameStartHandler gameStartHandler;
    private final Consumer<String> fenLoadHandler;
    private final Consumer<Opening> openingSelectedHandler;

    private boolean isBotMode = false;
    private TextField fenInputOnTimeScreen;

    public StartMenuView(
            AppConfig appConfig,
            Consumer<String> themeSelectedHandler,
            GameStartHandler gameStartHandler,
            Consumer<String> fenLoadHandler,
            Consumer<Opening> openingSelectedHandler
    ) {
        this.appConfig = appConfig;
        this.ui = appConfig.getUi();
        this.themeSelectedHandler = themeSelectedHandler;
        this.gameStartHandler = gameStartHandler;
        this.fenLoadHandler = fenLoadHandler;
        this.openingSelectedHandler = openingSelectedHandler;

        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        setPickOnBounds(true);

        showMainMenu();
    }

    private void showMainMenu() {
        getChildren().clear();

        Label title = new Label("JChess");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        VBox.setMargin(title, new Insets(0, 0, 20, 0));

        Button btnFriend = createMenuButton("Play with a friend");
        btnFriend.setOnAction(e -> {
            isBotMode = false;
            showTimeSelection();
        });

        Button btnBot = createMenuButton("Play vs bot");
        btnBot.setOnAction(e -> {
            isBotMode = true;
            showTimeSelection();
        });

        Button btnFen = createMenuButton("Load FEN position");
        btnFen.setOnAction(e -> showFenLoad());

        Button btnOpenings = createMenuButton("Openings");
        btnOpenings.setOnAction(e -> showOpeningsList());

        Button btnSettings = createMenuButton("Settings");
        btnSettings.setOnAction(e -> showSettings());

        VBox content = createContainer();
        content.getChildren().addAll(title, btnFriend, btnBot, btnFen, btnOpenings, btnSettings);
        getChildren().add(content);
    }

    private void showTimeSelection() {
        getChildren().clear();

        Label title = new Label(isBotMode ? "Play vs bot" : "Play with a friend");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label subtitle = new Label("Choose time control");
        subtitle.setTextFill(Color.web(ui.getAccent()));
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button btn1Min = createTimeButton("1 min", 60);
        Button btn3Min = createTimeButton("3 min", 180);
        Button btn5Min = createTimeButton("5 min", 300);
        Button btn10Min = createTimeButton("10 min", 600);
        Button btnUnlimited = createTimeButton("Unlimited", 0);


        HBox topButtons = new HBox(15, btn1Min, btn3Min);
        topButtons.setAlignment(Pos.CENTER);

        HBox bottomButtons = new HBox(15, btn5Min, btn10Min);
        bottomButtons.setAlignment(Pos.CENTER);

        HBox unlimitedButtonBox = new HBox(15, btnUnlimited);
        unlimitedButtonBox.setAlignment(Pos.CENTER);

        Label fenSubtitle = new Label("Optional: FEN position");
        fenSubtitle.setTextFill(Color.web(ui.getAccent()));
        fenSubtitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        VBox.setMargin(fenSubtitle, new Insets(10, 0, 0, 0));

        fenInputOnTimeScreen = new TextField();
        fenInputOnTimeScreen.setPromptText("Paste FEN (empty = starting position)");
        fenInputOnTimeScreen.setPrefWidth(300);
        fenInputOnTimeScreen.setStyle(
                "-fx-background-color: " + ui.getBackground() + ";" +
                        "-fx-text-fill: " + ui.getTextPrimary() + ";" +
                        "-fx-border-color: " + ui.getAccent() + ";" +
                        "-fx-border-radius: 4;"
        );

        Button btnBack = createMenuButton("Back");
        btnBack.setOnAction(e -> showMainMenu());
        VBox.setMargin(btnBack, new Insets(20, 0, 0, 0));

        VBox content = createContainer();
        content.getChildren().addAll(title, subtitle, topButtons, bottomButtons, unlimitedButtonBox,
                fenSubtitle, fenInputOnTimeScreen, btnBack);
        getChildren().add(content);
    }

    private void showSettings() {
        getChildren().clear();

        Label title = new Label("Settings");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Label themeSubtitle = new Label("Board theme");
        themeSubtitle.setTextFill(Color.web(ui.getAccent()));
        themeSubtitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox themeButtons = new HBox(10);
        themeButtons.setAlignment(Pos.CENTER);
        for (BoardTheme theme : appConfig.getBoardThemes()) {
            themeButtons.getChildren().add(createThemeButton(theme));
        }

        Button btnBack = createMenuButton("Back");
        btnBack.setOnAction(e -> showMainMenu());
        VBox.setMargin(btnBack, new Insets(20, 0, 0, 0));

        VBox content = createContainer();
        content.getChildren().addAll(title, themeSubtitle, themeButtons, btnBack);
        getChildren().add(content);
    }

    private void showOpeningsList() {
        getChildren().clear();

        Label title = new Label("Openings");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        VBox content = createContainer();
        content.getChildren().add(title);

        for (Opening opening : OpeningLibrary.getAll()) {
            Button btn = createMenuButton(opening.getName());
            btn.setOnAction(e -> openingSelectedHandler.accept(opening));
            content.getChildren().add(btn);
        }

        Button btnBack = createMenuButton("Back");
        btnBack.setOnAction(e -> showMainMenu());
        VBox.setMargin(btnBack, new Insets(20, 0, 0, 0));
        content.getChildren().add(btnBack);

        getChildren().add(content);
    }

    private void showFenLoad() {
        getChildren().clear();

        Label title = new Label("Load FEN");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        FenLoadView fenLoadView = new FenLoadView(ui, fenLoadHandler);

        Button btnBack = createMenuButton("Back");
        btnBack.setOnAction(e -> showMainMenu());
        VBox.setMargin(btnBack, new Insets(20, 0, 0, 0));

        VBox content = createContainer();
        content.getChildren().addAll(title, fenLoadView, btnBack);
        getChildren().add(content);
    }

    private VBox createContainer() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setMaxSize(380, 540);
        content.setStyle(
                "-fx-background-color: " + ui.getBackground() + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + ui.getAccent() + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;"
        );
        return content;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(200, 40);
        btn.setTextFill(Color.web(ui.getBackground()));
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setCursor(Cursor.HAND);
        String btnBg = ui.getButtonBackground();
        btn.setStyle("-fx-background-color: " + btnBg + "; -fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + btnBg + "; -fx-background-radius: 8;"));
        return btn;
    }

    private Button createTimeButton(String text, int timeInSeconds) {
        Button btn = new Button(text);
        btn.setPrefSize(100, 40);
        btn.setTextFill(Color.web(ui.getBackground()));
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setCursor(Cursor.HAND);
        String btnBg = ui.getButtonBackground();
        btn.setStyle("-fx-background-color: " + btnBg + "; -fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + btnBg + "; -fx-background-radius: 8;"));

        btn.setOnAction(event -> {
            String fen = fenInputOnTimeScreen == null ? null : fenInputOnTimeScreen.getText();
            String trimmedFen = (fen == null || fen.trim().isEmpty()) ? null : fen.trim();
            gameStartHandler.onGameStart(timeInSeconds, isBotMode, trimmedFen);
        });
        return btn;
    }

    private Button createThemeButton(BoardTheme theme) {
        Button btn = new Button(theme.getDisplayName());
        btn.setPrefSize(90, 36);
        btn.setTextFill(Color.web(ui.getBackground()));
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btn.setCursor(Cursor.HAND);
        String btnBg = ui.getButtonBackground();
        btn.setStyle("-fx-background-color: " + btnBg + "; -fx-background-radius: 8;");
        btn.setOnAction(e -> themeSelectedHandler.accept(theme.getId()));
        return btn;
    }
}