package com.ils.controllers.tables;

import com.ils.controllers.Component;
import com.ils.logic.Logic;

import javafx.scene.layout.Region;

abstract class Table<T> extends Component<Region> {
    //TODO: Add TableRowExpanderColumn for showing and editing parts

    public Table(String FXML, Logic logic) {
        super(FXML);
    }
}