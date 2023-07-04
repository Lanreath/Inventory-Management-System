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
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Optional;
import java.util.logging.Logger;

public class ProductPartTable extends Component<Region> {
    @FXML
    TreeTableView<Object> treeTable;

    @FXML
    TextField dbNameSearchField;

    @FXML
    Button clearBtn;

    @FXML
    private TreeTableColumn<Object, String> dbNameColumn;

    @FXML
    private TreeTableColumn<Object, String> defaultPartColumn;

    @FXML
    private TreeTableColumn<Object, Integer> quantityColumn;

    public ProductPartTable(Logic logic) {
        super("ProductPartTable.fxml", logic);

        initTable();
        initListeners();
        initProductColumn();
        initPartColumn();
        initQuantityColumn();
        rebuild();
        initFilters();
    }
    
    private void initTable() {
        treeTable.setMinWidth(400);
        treeTable.setPrefWidth(600);
        treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTable.setShowRoot(false);
        treeTable.setEditable(true);
        TreeItem<Object> root = new TreeItem<>();
        treeTable.setRoot(root);
    }

    private void initListeners() {
        treeTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        this.logic.getProducts().addListener(new ListChangeListener<Product>() {
            @Override
            public void onChanged(Change<? extends Product> c) {
                rebuild();
            }
        });
        this.logic.getParts().addListener(new ListChangeListener<Part>() {
            @Override
            public void onChanged(Change<? extends Part> c) {
                rebuild();
            }
        });
        this.logic.getProductCustomerFilter().addListener((observable, oldValue, newValue) -> {
            rebuild();
        });
        this.logic.getDBNameFilter().addListener((observable, oldValue, newValue) -> {
            rebuild();
        });
        this.logic.getSelectedProduct().addListener(this::handleForcedSelection);
    }

    private void initProductColumn() {
         dbNameColumn.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && rowItem.getValue() instanceof Product) {
                Product product = (Product) rowItem.getValue();
                return new SimpleStringProperty(product.getDBName());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    private void initPartColumn() {
        defaultPartColumn.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && rowItem.getValue() instanceof Product) {
                Product prod = (Product) rowItem.getValue();
                String name = prod.getProductName();
                if (name == null) {
                    return new SimpleStringProperty("-");
                }
                return new SimpleStringProperty(prod.getProductName());
            } else if (rowItem != null && rowItem.getValue() instanceof Part) {
                Part part = (Part) rowItem.getValue();
                return new SimpleStringProperty(part.getPartName());
            } else {
                throw new RuntimeException("Unknown row item type");
            }
        });
        defaultPartColumn.setCellFactory(c -> {
            return new TextFieldTreeTableCell<Object, String>() {
                @Override
                public void startEdit() {
                    TreeItem<Object> rowItem = getTreeTableRow().getTreeItem();
                    if (rowItem != null && rowItem.getValue() instanceof Product) {
                        return;
                    }
                    super.startEdit();
                }
            };
        });
        defaultPartColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<Object, String> event) -> {
            Object prpt = event.getTreeTableView().getTreeItem(event.getTreeTablePosition().getRow()).getValue();
            if (prpt instanceof Part) {
                Part part = (Part) prpt;
                this.logic.updatePartName(part, event.getNewValue());
                rebuild();
            }
            throw new RuntimeException("Unknown row item type");
        });
   }

    private void initQuantityColumn() {
        quantityColumn.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && rowItem.getValue() instanceof Product) {
                Product product = (Product) rowItem.getValue();
                return new SimpleIntegerProperty(this.logic.getProductQuantity(product)).asObject();
            } else if (rowItem != null && rowItem.getValue() instanceof Part) {
                Part part = (Part) rowItem.getValue();
                return new SimpleIntegerProperty(part.getPartQuantity()).asObject();
            } else {
                throw new RuntimeException("Unknown row item type");
            }
        });
        quantityColumn.setCellFactory(c -> {
            return new TextFieldTreeTableCell<Object, Integer>() {
                @Override
                public void startEdit() {
                    TreeItem<Object> rowItem = getTreeTableRow().getTreeItem();
                    if (rowItem != null && rowItem.getValue() instanceof Product) {
                        return;
                    }
                    super.startEdit();
                }
            };
        });
        quantityColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<Object, Integer> event) -> {
            Object prpt = event.getTreeTableView().getTreeItem(event.getTreeTablePosition().getRow()).getValue();
            if (prpt instanceof Part) {
                Part part = (Part) prpt;
                this.logic.updatePartQuantity(part, event.getNewValue());
                rebuild();
            }
            throw new RuntimeException("Unknown row item type");
        });
   }

   private void initFilters() {
        dbNameSearchField.setPromptText("Filter by product name");
        dbNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(dbNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);       
   }

    public TreeTableViewSelectionModel<Object> getSelectionModel() {
        return treeTable.getSelectionModel();
    }

    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        treeTable.getSelectionModel().clearSelection();
        dbNameSearchField.clear();
    };

    private void handleSelection(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldSelection,
            TreeItem<Object> newSelection) {
        if (newSelection != null && newSelection.getValue() instanceof Product) {
            Product product = (Product) newSelection.getValue();
            // Hack to prevent double selection
            if (product.equals(this.logic.getSelectedProduct().get())) {
                // Removed null selection
                return;
            }
            this.logic.selectProduct(product);
            this.logic.selectPart(null);
        } else if (newSelection != null && newSelection.getValue() instanceof Part) {
            Part part = (Part) newSelection.getValue();
            this.logic.selectPart(part);
            this.logic.selectProduct(null);
        } else {
            this.logic.selectProduct(null);
            this.logic.selectPart(null);
        }
    }

    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.logic.setProductNameFilter(newValue);
        rebuild();
    }

    private void handleForcedSelection(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
        if (newValue == null) {
            treeTable.getSelectionModel().clearSelection();
            return;
        }
        // Check for product in treetable
        Optional<TreeItem<Object>> prod = treeTable.getRoot().getChildren().stream().filter(item -> {
            if (item.getValue() instanceof Product) {
                Product product = (Product) item.getValue();
                return product.equals(newValue);
            } else {
                return false;
            }
        }).findFirst();
        if (!prod.isPresent()) {
            // Product not found
            throw new RuntimeException("Product not found in treetable");
        }
        // Select product
        treeTable.getSelectionModel().select(prod.get());
    }

    private void rebuild() {
        TreeItem<Object> root = treeTable.getRoot();
        root.getChildren().clear();
        for (Product product : this.logic.getProducts()) {
            TreeItem<Object> productItem = new TreeItem<>(product);
            root.getChildren().add(productItem);
            this.logic.getProductParts(product).forEach(part -> {
                TreeItem<Object> partItem = new TreeItem<>(part);
                productItem.getChildren().add(partItem);
            });
        }
        // Hack to ensure products cells are updated
        treeTable.refresh();
    }
}
