package com.ils.controllers;

import com.ils.controllers.panels.ActionBar;
import com.ils.controllers.panels.InfoBar;
import com.ils.controllers.panels.InputBar;
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
    private InfoBar infoBar;

    @FXML
    private StackPane actionBarPlaceholder;

    @FXML
    private StackPane inputBarPlaceholder;

    @FXML
    private StackPane infoBarPlaceholder;

    @FXML
    private StackPane customerTablePlaceholder;

    @FXML
    private StackPane productPartTablePlaceholder;

    @FXML
    private StackPane transferTablePlaceholder;

    public MainWindow(Stage stage){
        super(FXML, stage);
        this.stage = stage;
        stage.setMinHeight(600);
        stage.setMinWidth(1000);
    };

    public void show() {
        this.stage.show();
    }

    public void fillInnerComponents() {
        fillCustomerTable();
        fillProductPartTable();
        fillTransferTable();
        fillInputBar();
        fillInfoBar();
        fillActionBar();
    }

    private void fillActionBar() {
        actionBar = new ActionBar(Logic.getDataSync(), Logic.getFilters(), inputBar);
        actionBarPlaceholder.getChildren().add(actionBar.getRoot());
    }

    private void fillCustomerTable() {
        customerTable = new CustomerTable(Logic.getCustomerManagement());
        customerTablePlaceholder.getChildren().add(customerTable.getRoot());
    }

    private void fillProductPartTable() {
        productPartTable = new ProductPartTable(Logic.getCustomerManagement(), Logic.getProductManagement(), Logic.getPartManagement(), Logic.getTransferManagement());
        productPartTablePlaceholder.getChildren().add(productPartTable.getRoot());
    }

    private void fillTransferTable() {
        transferTable = new TransferTable(Logic.getCustomerManagement(), Logic.getProductManagement(), Logic.getTransferManagement());
        transferTablePlaceholder.getChildren().add(transferTable.getRoot());
    }

    private void fillInputBar() {
        inputBar = new InputBar(Logic.getCustomerManagement(), Logic.getProductManagement(), Logic.getPartManagement(), Logic.getTransferManagement(), customerTable.getSelectionModel(), productPartTable.getSelectionModel(), transferTable.getSelectionModel());
        inputBarPlaceholder.getChildren().add(inputBar.getRoot());
    }

    private void fillInfoBar() {
        infoBar = new InfoBar(Logic.getProductManagement(), Logic.getPartManagement(), customerTable, productPartTable, transferTable);
        infoBarPlaceholder.getChildren().add(infoBar.getRoot());
    }
}