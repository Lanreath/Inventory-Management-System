package com.ils.controllers.tables;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import com.ils.controllers.Component;
import com.ils.logic.Logic;
import com.ils.models.Customer;

public class CustomerTable extends Component<Region> {
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
        // customerNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        // customerNameColumn.setOnEditCommit((TableColumn.CellEditEvent<Customer, String> event) -> {
        //     Customer customer = event.getRowValue();
        //     this.logic.updateCustomer(customer, event.getNewValue());
        // });
        customerTable.setItems(this.logic.getCustomers());
        this.logic.getCustomers().comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        customerNameSearchField.setPromptText("Filter by customer name");
        customerNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(customerNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    public SelectionModel<Customer> getSelectionModel() {
        return customerTable.getSelectionModel();
    }

    private EventHandler<ActionEvent> clearFilterHandler = (event) ->{
        customerTable.getSelectionModel().clearSelection();
        customerNameSearchField.clear();
    };

    private void handleSelection(ObservableValue<? extends Customer> observable, Customer oldSelection, Customer newSelection) {
        // Hack to prevent double selection
        if (newSelection != null && newSelection.equals(this.logic.getSelectedCustomer().get())) {
            this.logic.setSelectedCustomer(null);
            return;
        }
        this.logic.selectCustomer(newSelection);
    }

    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.logic.setCustomerNameFilter(newValue);
    }
}