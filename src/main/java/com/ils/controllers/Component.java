package com.ils.controllers;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URL;

import com.ils.MainApp;
import com.ils.logic.Logic;

import javafx.fxml.FXMLLoader;


public abstract class Component<T> {

    public static final String FXML_FILE_FOLDER = "/view/";

    private final FXMLLoader fxmlLoader = new FXMLLoader();

    protected Logic logic;


    public Component(URL fxmlFileUrl) {
        loadFxmlFile(fxmlFileUrl, null);
    }

    public Component(String fxmlFileName, Logic logic) {
        this(getFxmlFileUrl(fxmlFileName));
        this.logic = logic;
    }

    public Component(URL fxmlFileUrl, T root) {
        loadFxmlFile(fxmlFileUrl, root);
    }

    public Component(String fxmlFileName, T root, Logic logic) {
        this(getFxmlFileUrl(fxmlFileName), root);
        this.logic = logic;
    }

    public T getRoot() {
        return fxmlLoader.getRoot();
    }

    private void loadFxmlFile(URL location, T root) {
        requireNonNull(location);
        fxmlLoader.setLocation(location);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(root);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static URL getFxmlFileUrl(String fxmlFileName) {
        requireNonNull(fxmlFileName);
        String fxmlFileNameWithFolder = FXML_FILE_FOLDER + fxmlFileName;
        URL fxmlFileUrl = MainApp.class.getResource(fxmlFileNameWithFolder);
        return requireNonNull(fxmlFileUrl);
    }

}