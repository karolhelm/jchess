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
import jchess.config.AppConfig;
import jchess.config.BoardTheme;
import jchess.config.UiConfig;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StartMenuView extends StackPane {
    private final AppConfig appConfig;
    private final UiConfig ui;
    private final Consumer<String> themeSelectedHandler;
    private final BiConsumer<Integer, Boolean> timeSelectedHandler; // Zmienione na BiConsumer (czas, czyBot)
    private final Consumer<String> fenLoadHandler;

    private boolean isBotMode = false;

    public StartMenuView(
            AppConfig appConfig,
            Consumer<String> themeSelectedHandler,
            BiConsumer<Integer, Boolean> timeSelectedHandler,
            Consumer<String> fenLoadHandler
    ) {
        this.appConfig = appConfig;
        this.ui = appConfig.getUi();
        this.themeSelectedHandler = themeSelectedHandler;
        this.timeSelectedHandler = timeSelectedHandler;
        this.fenLoadHandler = fenLoadHandler;

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

        Button btnFriend = createMenuButton("Gra ze znajomym");
        btnFriend.setOnAction(e -> {
            isBotMode = false;
            showTimeSelection();
        });

        Button btnBot = createMenuButton("Gra z botem");
        btnBot.setOnAction(e -> {
            isBotMode = true;
            showTimeSelection();
        });

        Button btnFen = createMenuButton("Wczytaj pozycję FEN");
        btnFen.setOnAction(e -> showFenLoad());

        Button btnSettings = createMenuButton("Ustawienia");
        btnSettings.setOnAction(e -> showSettings());

        VBox content = createContainer();
        content.getChildren().addAll(title, btnFriend, btnBot, btnFen, btnSettings);
        getChildren().add(content);
    }

    private void showTimeSelection() {
        getChildren().clear();

        Label title = new Label(isBotMode ? "Gra z botem" : "Gra ze znajomym");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label subtitle = new Label("Wybierz czas gry");
        subtitle.setTextFill(Color.web(ui.getAccent()));
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button btn1Min = createTimeButton("1 min", 60);
        Button btn3Min = createTimeButton("3 min", 180);
        Button btn5Min = createTimeButton("5 min", 300);
        Button btn10Min = createTimeButton("10 min", 600);
        Button btnUnlimited = createTimeButton("Bez limitu", 0);


        HBox topButtons = new HBox(15, btn1Min, btn3Min);
        topButtons.setAlignment(Pos.CENTER);

        HBox bottomButtons = new HBox(15, btn5Min, btn10Min);
        bottomButtons.setAlignment(Pos.CENTER);

        HBox unlimitedButtonBox = new HBox(15, btnUnlimited);
        unlimitedButtonBox.setAlignment(Pos.CENTER);

        Button btnBack = createMenuButton("Wróć");
        btnBack.setOnAction(e -> showMainMenu());
        VBox.setMargin(btnBack, new Insets(20, 0, 0, 0));

        VBox content = createContainer();
        content.getChildren().addAll(title, subtitle, topButtons, bottomButtons, unlimitedButtonBox, btnBack);
        getChildren().add(content);
    }

    private void showSettings() {
        getChildren().clear();

        Label title = new Label("Ustawienia");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Label themeSubtitle = new Label("Motyw planszy");
        themeSubtitle.setTextFill(Color.web(ui.getAccent()));
        themeSubtitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox themeButtons = new HBox(10);
        themeButtons.setAlignment(Pos.CENTER);
        for (BoardTheme theme : appConfig.getBoardThemes()) {
            themeButtons.getChildren().add(createThemeButton(theme));
        }

        Button btnBack = createMenuButton("Wróć");
        btnBack.setOnAction(e -> showMainMenu());
        VBox.setMargin(btnBack, new Insets(20, 0, 0, 0));

        VBox content = createContainer();
        content.getChildren().addAll(title, themeSubtitle, themeButtons, btnBack);
        getChildren().add(content);
    }

    private void showFenLoad() {
        getChildren().clear();

        Label title = new Label("Wczytaj FEN");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        FenLoadView fenLoadView = new FenLoadView(ui, fenLoadHandler);

        Button btnBack = createMenuButton("Wróć");
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
        content.setMaxSize(380, 460);
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

        // Przekazanie czasu oraz flagi trybu (bot / znajomy)
        btn.setOnAction(event -> timeSelectedHandler.accept(timeInSeconds, isBotMode));
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