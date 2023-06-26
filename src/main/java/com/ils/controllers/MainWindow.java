package com.ils.controllers;

import com.ils.controllers.tables.CustomerTable;
import com.ils.controllers.tables.ProductPartTable;
import com.ils.controllers.tables.TransferTable;
import com.ils.logic.Logic;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainWindow extends Component<Stage> {
    private static final String FXML = "MainWindow.fxml";
    private Stage stage;

    private ActionBar actionBar;
    private CustomerTable customerTable;
    private ProductPartTable productPartTable;
    private TransferTable transferTable;
    private InputBar inputBar;

    @FXML
    private StackPane actionBarPlaceholder;

    @FXML
    private StackPane inputBarPlaceholder;

    @FXML
    private StackPane customerTablePlaceholder;

    @FXML
    private StackPane productPartTablePlaceholder;

    @FXML
    private StackPane transferTablePlaceholder;

    public MainWindow(Stage stage, Logic logic){
        super(FXML, stage, logic);
        this.stage = stage;
        stage.setMinHeight(600);
        stage.setMinWidth(1000);
    };

    public void show() {
        this.stage.show();
    }

    public void fillInnerComponents() {
        actionBar = new ActionBar(logic);
        actionBarPlaceholder.getChildren().add(actionBar.getRoot());
        customerTable = new CustomerTable(logic);
        customerTablePlaceholder.getChildren().add(customerTable.getRoot());
        fillProductPartTable();
        fillTransferTable();
        fillInputBar();
    }

    private void fillProductPartTable() {
        productPartTable = new ProductPartTable(logic);
        productPartTable.rebuild();
        productPartTablePlaceholder.getChildren().add(productPartTable.getRoot());
    }

    private void fillTransferTable() {
        transferTable = new TransferTable(logic);
        transferTablePlaceholder.getChildren().add(transferTable.getRoot());
    }

    private void fillInputBar() {
        inputBar = new InputBar(logic, customerTable.getSelectionModel(), productPartTable.getSelectionModel(), transferTable.getSelectionModel());
        inputBarPlaceholder.getChildren().add(inputBar.getRoot());
    }
}