package com.ils;

import com.ils.controllers.MainWindow;
import com.ils.db.Database;
import com.ils.logic.Logic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApp extends Application {
    private MainWindow window;
    private Logic logic;

    @Override
    public void start(Stage stage) throws Exception {
        if (!Database.isOK()) {
            Platform.exit();
        }
        logic = new Logic();
        window = new MainWindow(stage, logic);
        window.fillInnerComponents();
        window.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}