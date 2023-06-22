package com.ils.controllers.tables;

import java.time.LocalDateTime;


import com.ils.logic.Logic;
import com.ils.models.Transfer;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TransferTable extends Table<Transfer> {
    private Logic logic;

    @FXML
    DatePicker transferDatePicker;

    @FXML
    ComboBox<Transfer.Action> transferTypeComboBox;

    @FXML
    Button clearBtn;

    @FXML
    private TableView<Transfer> transferTable;

    @FXML
    private TableColumn<Transfer, LocalDateTime> transferDateTimeColumn;

    @FXML
    private TableColumn<Transfer, Integer> transferQuantityColumn;

    @FXML
    private TableColumn<Transfer, Transfer.Action> transferTypeColumn;

    public TransferTable(Logic logic) {
        super("TransferTable.fxml", logic);
        this.logic=logic;
        transferTable.setPrefWidth(400);
        transferTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transferDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transferDateTime"));
        transferQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("transferQuantity"));
        transferTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transferType"));
        transferTable.setItems(logic.getTransfers());
        this.logic.getTransfers().comparatorProperty().bind(transferTable.comparatorProperty());
        transferDatePicker.setOnAction(dateFilterHandler);
        transferDatePicker.setPromptText("Filter by date");
        transferTypeComboBox.getItems().addAll(Transfer.Action.values());
        transferTypeComboBox.getSelectionModel().selectedItemProperty().addListener(this::handleTypeFilter);
        transferTypeComboBox.setPromptText("Filter by type");
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    private EventHandler<ActionEvent> dateFilterHandler = (event) -> {
        logic.setTransferDateFilter(transferDatePicker.getValue());
    };

    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        transferTable.getSelectionModel().clearSelection();
        transferDatePicker.setValue(null);
        transferTypeComboBox.getSelectionModel().clearSelection();
    };

    private void handleTypeFilter(ObservableValue<? extends Transfer.Action> observable, Transfer.Action oldValue, Transfer.Action newValue) {
        logic.setTransferActionFilter(newValue);
    }
}