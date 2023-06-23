package com.ils.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import com.ils.logic.Logic;

public class InputBar extends Component<Region> {
    private static final String FXML = "InputBar.fxml";

    @FXML
    private TextField inputName;

    @FXML
    private Button editBtn;

    @FXML
    private Button saveBtn;

    public InputBar(Logic logic) {
        super(FXML, logic);
    }
}