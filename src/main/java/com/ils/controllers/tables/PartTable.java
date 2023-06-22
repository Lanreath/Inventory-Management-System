package com.ils.controllers.tables;

import com.ils.logic.Logic;
import com.ils.models.Part;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PartTable extends Table {
    @FXML
    private TableView<Part> partTable;

    @FXML
    private TableColumn<Part, Integer> idColumn;

    @FXML
    private TableColumn<Part, String> partNameColumn;

    @FXML
    private TableColumn<Part, Integer> partQuantityColumn;

    public PartTable(Logic logic) {
        super("PartTable.fxml", logic);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("partId"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("partName"));
        partQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("partQuantity"));
        // partTable.setItems(logic.getParts());
    }
}
