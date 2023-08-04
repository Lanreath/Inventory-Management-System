package com.ils.controllers.tables;

import java.time.LocalDateTime;

import com.ils.controllers.Component;
import com.ils.logic.management.TransferManagement;
import com.ils.models.Transfer;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

public class TransferTable extends Component<Region> {
    private TransferManagement transferManagement;

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

    /**
     * Constructor.
     * @param transferManagement
     */
    public TransferTable(TransferManagement transferManagement) {
        super("TransferTable.fxml");
        this.transferManagement = transferManagement;
        initTable();
        initCol();
        initFilters();
    }

    /**
     * Initialise the table and set the items.
     */
    private void initTable() {
        transferTable.setPrefWidth(300);
        transferTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transferTable.setItems(transferManagement.getTransfers());
        this.transferManagement.getTransfers().comparatorProperty().bind(transferTable.comparatorProperty());
    }

    /**
     * Initialise the columns.
     */
    private void initCol() {
        transferDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transferDateTime"));
        transferQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("transferQuantity"));
        transferTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transferType"));
        transferDateTimeColumn.setCellFactory(column -> new TableCell<Transfer, LocalDateTime>(){
            // DateTime formatting
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.toLocalDate().toString() + " " + item.toLocalTime().getHour() + ":" + String.format("%02d", item.toLocalTime().getMinute()));
                }
            }
        });
    }

    /**
     * Initialise the transfer type filter and clear button
     */
    private void initFilters() {
        transferTypeComboBox.getItems().addAll(Transfer.Action.values());
        transferTypeComboBox.getSelectionModel().selectedItemProperty().addListener(this::handleTypeFilter);
        transferTypeComboBox.setPromptText("Filter by type");
        transferTypeComboBox.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    /**
     * Get the selection model
     * @return SelectionModel<Transfer> for getting user input
     */
    public SelectionModel<Transfer> getSelectionModel() {
        return transferTable.getSelectionModel();
    }

    /**
     * Clear the filter and selection
     */
    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        transferTable.getSelectionModel().clearSelection();
        transferTypeComboBox.getSelectionModel().clearSelection();
    };

    /**
     * Handle the transfer type filter
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void handleTypeFilter(ObservableValue<? extends Transfer.Action> observable, Transfer.Action oldValue, Transfer.Action newValue) {
        transferManagement.setTransferActionFilter(newValue);
    }
}