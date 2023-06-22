package com.ils.controllers.tables;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import com.ils.logic.Logic;
import com.ils.models.Customer;

public class CustomerTable extends Table {
    @FXML
    TextField customerNameSearchField;

    @FXML
    Button clearBtn;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    public CustomerTable(Logic logic) {
        super("CustomerTable.fxml", logic);
        customerTable.setPrefWidth(200);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerTable.setItems(this.logic.getCustomers());
        this.logic.getCustomers().comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        customerNameSearchField.setPromptText("Filter by customer name");
        customerNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(customerNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    private EventHandler<ActionEvent> clearFilterHandler = (event) ->{
        customerTable.getSelectionModel().clearSelection();
        customerNameSearchField.clear();
    };

    private void handleSelection(ObservableValue<? extends Customer> observable, Customer oldSelection, Customer newSelection) {
        this.logic.setSelectedCustomer(newSelection);
    }

    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.logic.setCustomerNameFilter(newValue);
    }
}