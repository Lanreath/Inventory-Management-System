package com.ils;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ils.controllers.MainWindow;
import com.ils.db.Database;
import com.ils.logic.Logic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApp extends Application {
    private MainWindow window;
    private Logic logic;
    private Logger logger = Logger.getLogger(MainApp.class.getName());
    private FileHandler fh;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            fh = new FileHandler("database/ILSLogs.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Starting ILS...");
        if (!Database.isOK()) {
            Platform.exit();
        }
        logic = new Logic();
        window = new MainWindow(stage);
        window.fillInnerComponents();
        window.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}