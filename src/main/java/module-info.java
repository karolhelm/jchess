module jchess {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    exports jchess;
    exports jchess.config;
    exports jchess.controller;
    exports jchess.model;
    exports jchess.view;

    opens jchess.config to com.fasterxml.jackson.databind;
}
