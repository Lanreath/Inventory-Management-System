package com.ils.controllers.tables;

import com.ils.controllers.Component;
import com.ils.logic.management.CustomerManagement;
import com.ils.logic.management.PartManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.logic.management.TransferManagement;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

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
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProductPartTable extends Component<Region> {
    private CustomerManagement customerManagement;
    private ProductManagement productManagement;
    private PartManagement partManagement;
    private TransferManagement transferManagement;

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

    public ProductPartTable(CustomerManagement customerManagement, ProductManagement productManagement,
            PartManagement partManagement, TransferManagement transferManagement) {
        super("ProductPartTable.fxml");
        this.customerManagement = customerManagement;
        this.productManagement = productManagement;
        this.partManagement = partManagement;
        this.transferManagement = transferManagement;
        initTable();
        initListeners();
        initProductColumn();
        initPartColumn();
        initQuantityColumn();
        rebuild();
        initFilters();
    }

    private void initTable() {
        treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTable.setShowRoot(false);
        treeTable.setEditable(true);
        TreeItem<Object> root = new TreeItem<>();
        treeTable.setRoot(root);
        // Row factory to highlight the default part corresponding to the first child of every product
        // treeTable.setRowFactory(tv -> {
        //     TreeTableRow<Object> row = new TreeTableRow<>();
        //     row.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> {
        //         if (newTreeItem != null && newTreeItem.getValue() instanceof Product) {
        //             row.getStyleClass().add("product");
        //         } else if (newTreeItem != null && newTreeItem.getValue() instanceof Part) {
        //             Part part = (Part) newTreeItem.getValue();
        //             if (part.equals(part.getProduct().getDefaultPart())) {
        //                 row.getStyleClass().add("default-part");
        //             } else {
        //                 row.getStyleClass().add("part");
        //             }
        //         } else if (newTreeItem != null) {
        //             throw new RuntimeException("Unknown row item type");
        //         }
        //     });
        //     return row;
        // });
    }

    private void initListeners() {
        treeTable.getSelectionModel().selectedItemProperty().addListener(this::handleSelection);
        this.productManagement.getProducts().addListener(new ListChangeListener<Product>() {
            @Override
            public void onChanged(Change<? extends Product> c) {
                rebuild();
            }
        });
        this.partManagement.getParts().addListener(new ListChangeListener<Part>() {
            @Override
            public void onChanged(Change<? extends Part> c) {
                rebuild();
            }
        });
        // this.transferManagement.getTransfers().addListener(new ListChangeListener<Transfer>() {
        //     @Override
        //     public void onChanged(Change<? extends Transfer> c) {
        //         rebuild();
        //     }
        // });
        this.productManagement.getProductCustomerFilter().addListener((observable, oldValue, newValue) -> {
            rebuild();
        });
        this.productManagement.getDBNameFilter().addListener((observable, oldValue, newValue) -> {
            rebuild();
        });
        // this.productManagement.getSelectedProduct().addListener(this::handleForcedSelection);
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
                return new SimpleStringProperty(name);
            } else if (rowItem != null && rowItem.getValue() instanceof Part) {
                Part part = (Part) rowItem.getValue();
                return new SimpleStringProperty(part.getPartName());
            } else {
                throw new RuntimeException("Unknown row item type");
            }
        });
        defaultPartColumn.setCellFactory(c -> {
            return new TextFieldTreeTableCell<Object, String>(new DefaultStringConverter());
        });
        defaultPartColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<Object, String> event) -> {
            Object prpt = event.getTreeTableView().getTreeItem(event.getTreeTablePosition().getRow()).getValue();
            if (prpt instanceof Part) {
                Part part = (Part) prpt;
                this.partManagement.updatePartName(part, event.getNewValue());
            } else if (prpt instanceof Product) {
                Product product = (Product) prpt;
                this.productManagement.updateProductName(product, event.getNewValue());
            } else {
                throw new RuntimeException("Unknown row item type");
            }
            rebuild();
        });
    }

    private void initQuantityColumn() {
        quantityColumn.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && rowItem.getValue() instanceof Product) {
                Product product = (Product) rowItem.getValue();
                return new SimpleIntegerProperty(this.partManagement.getProductQuantity(product)).asObject();
            } else if (rowItem != null && rowItem.getValue() instanceof Part) {
                Part part = (Part) rowItem.getValue();
                return new SimpleIntegerProperty(part.getPartQuantity()).asObject();
            } else {
                throw new RuntimeException("Unknown row item type");
            }
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
        this.customerManagement.setSelectedCustomer(null);
        dbNameSearchField.clear();
    };

    private void handleSelection(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldSelection,
            TreeItem<Object> newSelection) {
        if (newSelection != null && newSelection.getValue() instanceof Product) {
            Product product = (Product) newSelection.getValue();
            // Hack to prevent double selection
            if (product.equals(this.productManagement.getSelectedProduct().get())) {
                // Removed null selection
                return;
            }
            this.productManagement.selectProduct(product);
            this.partManagement.selectPart(null);
        } else if (newSelection != null && newSelection.getValue() instanceof Part) {
            Part part = (Part) newSelection.getValue();
            this.partManagement.selectPart(part);
            this.productManagement.selectProduct(null);
        } else {
            this.productManagement.selectProduct(null);
            this.partManagement.selectPart(null);
        }
    }

    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.productManagement.setProductNameFilter(newValue);
        rebuild();
    }

    // private void handleForcedSelection(ObservableValue<? extends Product> observable, Product oldValue,
    //         Product newValue) {
    //     if (newValue == null) {
    //         treeTable.getSelectionModel().clearSelection();
    //         return;
    //     }
    //     // Check for product in treetable
    //     Optional<TreeItem<Object>> prod = treeTable.getRoot().getChildren().stream().filter(item -> {
    //         if (item.getValue() instanceof Product) {
    //             Product product = (Product) item.getValue();
    //             return product.equals(newValue);
    //         } else {
    //             return false;
    //         }
    //     }).findFirst();
    //     if (!prod.isPresent()) {
    //         // Product not found
    //         throw new RuntimeException("Product not found in treetable");
    //     }
    //     // Select product
    //     treeTable.getSelectionModel().select(prod.get());
    // }

    private void rebuild() {
        TreeItem<Object> root = treeTable.getRoot();
        root.getChildren().clear();
        for (Product product : this.productManagement.getProducts()) {
            TreeItem<Object> productItem = new TreeItem<>(product);
            root.getChildren().add(productItem);
            // List of linked parts
            List<Integer> linkedParts = new ArrayList<>();
            Part start = product.getDefaultPart();
            while (start != null) {
                linkedParts.add(start.getId());
                TreeItem<Object> partItem = new TreeItem<>(start);
                productItem.getChildren().add(partItem);
                start = start.getNextPart();
            }
            // Add remaining parts
            Stream<Part> parts = this.partManagement.getProductParts(product);
            parts.filter(part -> !linkedParts.contains(part.getId())).forEach(part -> {
                TreeItem<Object> partItem = new TreeItem<>(part);
                productItem.getChildren().add(partItem);
            });
        }
        // Hack to ensure products cells are updated
        treeTable.refresh();
    }
}
