package com.ils.controllers.tables;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Optional;

import com.ils.controllers.Component;
import com.ils.logic.management.CustomerManagement;
import com.ils.models.Customer;

public class CustomerTable extends Component<Region> {
    private CustomerManagement customerManagement;

    @FXML
    TextField customerNameSearchField;

    @FXML
    Button clearBtn;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    public CustomerTable(CustomerManagement customerManagement) {
        super("CustomerTable.fxml");
        this.customerManagement = customerManagement;
        initTable();
        initCol();
        initFilter();
    }

    private void initTable() {
        customerTable.setPrefWidth(100);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customerTable.setItems(this.customerManagement.getCustomers());
        customerTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        customerTable.setEditable(true);
        this.customerManagement.getCustomers().comparatorProperty().bind(customerTable.comparatorProperty());
        this.customerManagement.getSelectedCustomer().addListener(this::handleForcedSelection);
    }

    private void initCol() {
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        customerNameColumn.setOnEditCommit((TableColumn.CellEditEvent<Customer, String> event) -> {
            Customer customer = event.getTableView().getItems().get(event.getTablePosition().getRow());
            this.customerManagement.updateCustomer(customer, event.getNewValue());
        });
    }

    private void initFilter() {
        customerNameSearchField.setPromptText("Filter by customer name");
        customerNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(customerNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    public SelectionModel<Customer> getSelectionModel() {
        return customerTable.getSelectionModel();
    }

    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        customerTable.getSelectionModel().clearSelection();
        customerNameSearchField.clear();
    };

    private void handleSelection(ObservableValue<? extends Customer> observable, Customer oldSelection,
            Customer newSelection) {
        // Hack to prevent double selection
        if (newSelection != null && newSelection.equals(this.customerManagement.getSelectedCustomer().get())) {
            // Removed null selection
            return;
        }
        this.customerManagement.selectCustomer(newSelection);
    }

    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.customerManagement.setCustomerNameFilter(newValue);
    }

    private void handleForcedSelection(ObservableValue<? extends Customer> observable, Customer oldSelection,
            Customer newSelection) {
        if (newSelection == null) {
            customerTable.getSelectionModel().clearSelection();
            return;
        }
        // Check for customer tableitem
        Optional<Customer> customer = customerTable.getItems().stream().filter(c -> c.equals(newSelection)).findFirst();
        if (!customer.isPresent()) {
            // Customer not found in table
            throw new RuntimeException("Customer not found in table");
        }
        // Select customer
        customerTable.getSelectionModel().select(customer.get());
    }
}