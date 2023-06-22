package com.ils.controllers.tables;

import java.util.Optional;

import com.ils.logic.Logic;
import com.ils.models.Part;
import com.ils.models.Product;

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


public class ProductTable extends Table<Product>{
    private Logic logic;

    @FXML
    TextField productNameSearchField;

    @FXML
    Button clearBtn;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Optional<Part>> defaultPartColumn;

    public ProductTable(Logic logic) {
        super("ProductTable.fxml", logic);
        this.logic = logic;
        productTable.setMinWidth(400);
        productTable.setPrefWidth(700);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        defaultPartColumn.setCellValueFactory(new PropertyValueFactory<>("defaultPart"));
        productTable.setItems(this.logic.getProducts());
        this.logic.getProducts().comparatorProperty().bind(productTable.comparatorProperty());
        productTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        productNameSearchField.setPromptText("Filter by product name");
        productNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(productNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        productTable.getSelectionModel().clearSelection();
        productNameSearchField.clear();
    };  

    private void handleSelection(ObservableValue<? extends Product> observable, Product oldSelection, Product newSelection) {
        this.logic.setSelectedProduct(newSelection);
    }

    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.logic.setProductNameFilter(newValue);
    }
}
