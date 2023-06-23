package com.ils.controllers.tables;

import com.ils.controllers.Component;
import com.ils.logic.Logic;
import com.ils.models.Part;
import com.ils.models.Product;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class ProductPartTable extends Component<Region> {
    @FXML
    TreeTableView<Object> treeTable;

    @FXML
    TextField productNameSearchField;

    @FXML
    Button clearBtn;

    @FXML
    private TreeTableColumn<Object, String> productNameColumn;

    @FXML
    private TreeTableColumn<Object, String> defaultPartColumn;

    @FXML
    private TreeTableColumn<Object, Integer> quantityColumn;

    public ProductPartTable(Logic logic) {
        super("ProductPartTable.fxml", logic);
        treeTable.setMinWidth(400);
        treeTable.setPrefWidth(700);
        treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTable.setShowRoot(false);

        initProductColumn();
        initPartColumn();
        initQuantityColumn();

        TreeItem<Object> root = new TreeItem<>();
        treeTable.setRoot(root);
        rebuild();
        this.logic.getProducts().addListener((ListChangeListener.Change<? extends Product> change) -> {
            rebuild();
        });

        treeTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        productNameSearchField.setPromptText("Filter by product name");
        productNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(productNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    private void initProductColumn() {
         productNameColumn.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && rowItem.getValue() instanceof Product) {
                Product product = (Product) rowItem.getValue();
                return new SimpleStringProperty(product.getProductName());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    private void initPartColumn() {
        defaultPartColumn.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && rowItem.getValue() instanceof Product) {
                Product product = (Product) rowItem.getValue();
                return new SimpleStringProperty(product.getDefaultPart().getPartName());
            } else if (rowItem != null && rowItem.getValue() instanceof Part) {
                Part part = (Part) rowItem.getValue();
                return new SimpleStringProperty(part.getPartName());
            } else {
                throw new RuntimeException("Unknown row item type");
            }
        });
   }

    private void initQuantityColumn() {
        quantityColumn.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && rowItem.getValue() instanceof Product) {
                Product product = (Product) rowItem.getValue();
                Integer quantity = this.logic.getProductQuantity(product);
                return new SimpleIntegerProperty(quantity).asObject();
            } else if (rowItem != null && rowItem.getValue() instanceof Part) {
                Part part = (Part) rowItem.getValue();
                return new SimpleIntegerProperty(part.getPartQuantity()).asObject();
            } else {
                throw new RuntimeException("Unknown row item type");
            }
        });

   }

    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        treeTable.getSelectionModel().clearSelection();
        productNameSearchField.clear();
    };

    private void handleSelection(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldSelection,
            TreeItem<Object> newSelection) {
        if (newSelection != null && newSelection.getValue() instanceof Product) {
            Product product = (Product) newSelection.getValue();
            this.logic.setSelectedProduct(product);
            this.logic.setSelectedPart(null);
        } else if (newSelection != null && newSelection.getValue() instanceof Part) {
            Part part = (Part) newSelection.getValue();
            this.logic.setSelectedPart(part);
            this.logic.setSelectedProduct(null);
        } else {
            this.logic.setSelectedProduct(null);
            this.logic.setSelectedPart(null);
        }
    }

    private void rebuild() {
        TreeItem<Object> root = treeTable.getRoot();
        for (Product product : this.logic.getProducts()) {
            TreeItem<Object> productItem = new TreeItem<>(product);
            root.getChildren().add(productItem);
            this.logic.getProductParts(product).forEach(part -> {
                TreeItem<Object> partItem = new TreeItem<>(part);
                productItem.getChildren().add(partItem);
            });
        }
    }

    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.logic.setProductNameFilter(newValue);
    }
}
