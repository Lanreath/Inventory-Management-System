package com.ils.controllers;

import com.ils.controllers.panels.ActionBar;
import com.ils.controllers.panels.InfoBar;
import com.ils.controllers.panels.InputBar;
import com.ils.controllers.tables.CustomerTable;
import com.ils.controllers.tables.ProductPartTable;
import com.ils.controllers.tables.TransferTable;
import com.ils.logic.DataSync;
import com.ils.logic.Filters;
import com.ils.logic.Logic;
import com.ils.logic.management.CustomerManagement;
import com.ils.logic.management.PartManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.logic.management.TransferManagement;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainWindow extends Component<Stage> {
    private static final String FXML = "MainWindow.fxml";
    private Stage stage;
    private Logic logic;
    private Filters filters;
    private DataSync dataSync;
    private CustomerManagement customerManagement;
    private ProductManagement productManagement;
    private PartManagement partManagement;
    private TransferManagement transferManagement;

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
        // this.logic = logic;
        this.filters = new Filters();
        this.dataSync = new DataSync();
        this.customerManagement = new CustomerManagement(filters);
        this.productManagement = new ProductManagement(filters);
        this.partManagement = new PartManagement(filters);
        this.transferManagement = new TransferManagement(filters);
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
        actionBar = new ActionBar(dataSync, inputBar);
        actionBarPlaceholder.getChildren().add(actionBar.getRoot());
    }

    private void fillCustomerTable() {
        customerTable = new CustomerTable(customerManagement);
        customerTablePlaceholder.getChildren().add(customerTable.getRoot());
    }

    private void fillProductPartTable() {
        productPartTable = new ProductPartTable(customerManagement, productManagement, partManagement);
        productPartTablePlaceholder.getChildren().add(productPartTable.getRoot());
    }

    private void fillTransferTable() {
        transferTable = new TransferTable(customerManagement, productManagement, transferManagement);
        transferTablePlaceholder.getChildren().add(transferTable.getRoot());
    }

    private void fillInputBar() {
        inputBar = new InputBar(customerManagement, productManagement, partManagement, transferManagement, customerTable.getSelectionModel(), productPartTable.getSelectionModel(), transferTable.getSelectionModel());
        inputBarPlaceholder.getChildren().add(inputBar.getRoot());
    }

    private void fillInfoBar() {
        infoBar = new InfoBar(logic, customerTable, productPartTable);
        infoBarPlaceholder.getChildren().add(infoBar.getRoot());
    }
}