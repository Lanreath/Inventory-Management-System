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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

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

    /**
     * Constructor.
     * @param customerManagement
     */
    public CustomerTable(CustomerManagement customerManagement) {
        super("CustomerTable.fxml");
        this.customerManagement = customerManagement;
        initTable();
        initCol();
        initFilter();
    }

    /**
     * Initialize the table and set the items.
     */
    private void initTable() {
        customerTable.setPrefWidth(100);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customerTable.setItems(this.customerManagement.getCustomers());
        customerTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        this.customerManagement.getCustomers().comparatorProperty().bind(customerTable.comparatorProperty());
    }

    /**
     * Initialize the columns.
     */
    private void initCol() {
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    }

    /**
     * Initialize the name filter and clear button.
     */
    private void initFilter() {
        customerNameSearchField.setPromptText("Filter by customer name");
        customerNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(customerNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    /**
     * Get the selection model
     * @return SelectionModel<Customer> for getting user input
     */
    public SelectionModel<Customer> getSelectionModel() {
        return customerTable.getSelectionModel();
    }

    /**
     * Clear the filter and selection.
     */
    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        customerTable.getSelectionModel().clearSelection();
        customerNameSearchField.clear();
    };

    /**
     * Handle the selection of a customer.
     * @param observable
     * @param oldSelection
     * @param newSelection
     */
    private void handleSelection(ObservableValue<? extends Customer> observable, Customer oldSelection,
            Customer newSelection) {
        this.customerManagement.selectCustomer(newSelection);
    }

    /**
     * Handle the name filter.
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.customerManagement.setCustomerNameFilter(newValue);
    }
}