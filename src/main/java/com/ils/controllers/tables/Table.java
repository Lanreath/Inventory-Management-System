package com.ils.controllers.tables;

import com.ils.controllers.Component;
import com.ils.logic.Logic;

import javafx.scene.layout.Region;

abstract class Table extends Component<Region> {
    protected Logic logic;

    public Table(String FXML, Logic logic) {
        super(FXML);
        this.logic = logic;
    }
}