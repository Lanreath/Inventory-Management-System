package com.ils.controllers.tables;

import java.time.LocalDateTime;

import com.ils.controllers.Component;
import com.ils.logic.management.CustomerManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.logic.management.TransferManagement;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

public class TransferTable extends Component<Region> {
    private CustomerManagement customerManagement;
    private ProductManagement productManagement;
    private TransferManagement transferManagement;

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

    public TransferTable(CustomerManagement customerManagement, ProductManagement productManagement, TransferManagement transferManagement) {
        super("TransferTable.fxml");
        this.customerManagement = customerManagement;
        this.productManagement = productManagement;
        this.transferManagement = transferManagement;
        initTable();
        initCol();
        initFilter();
    }

    private void initTable() {
        transferTable.setPrefWidth(300);
        transferTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transferTable.setItems(transferManagement.getTransfers());
        transferTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        this.transferManagement.getTransfers().comparatorProperty().bind(transferTable.comparatorProperty());
    }

    private void initCol() {
        transferDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transferDateTime"));
        transferQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("transferQuantity"));
        transferTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transferType"));
        transferDateTimeColumn.setCellFactory(column -> new TableCell<Transfer, LocalDateTime>(){
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

    private void initFilter() {
        transferDatePicker.setOnAction(dateFilterHandler);
        transferDatePicker.setPromptText("Filter by date");
        transferTypeComboBox.getItems().addAll(Transfer.Action.values());
        transferTypeComboBox.getSelectionModel().selectedItemProperty().addListener(this::handleTypeFilter);
        transferTypeComboBox.setPromptText("Filter by type");
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    public SelectionModel<Transfer> getSelectionModel() {
        return transferTable.getSelectionModel();
    }

    private EventHandler<ActionEvent> dateFilterHandler = (event) -> {
        transferManagement.setTransferDateFilter(transferDatePicker.getValue());
    };

    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        transferTable.getSelectionModel().clearSelection();
        transferDatePicker.setValue(null);
        transferTypeComboBox.getSelectionModel().clearSelection();
    };

    private void handleSelection(ObservableValue<? extends Transfer> observable, Transfer oldValue, Transfer newValue) {
        if (newValue == null) return;
        Part part = newValue.getPart();
        Product product = part.getProduct();
        Customer customer = product.getCustomer();
        productManagement.setSelectedProduct(product);
        customerManagement.setSelectedCustomer(customer);
    }

    private void handleTypeFilter(ObservableValue<? extends Transfer.Action> observable, Transfer.Action oldValue, Transfer.Action newValue) {
        transferManagement.setTransferActionFilter(newValue);
    }
}