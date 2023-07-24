package com.ils;

import com.ils.controllers.MainWindow;
import com.ils.sqlite.Database;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApp extends Application {
    private MainWindow window;

    @Override
    public void start(Stage stage) throws Exception {
        Config.init();
        if (!Database.isOK()) {
            Platform.exit();
        }
        window = new MainWindow(stage);
        window.fillInnerComponents();
        window.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}