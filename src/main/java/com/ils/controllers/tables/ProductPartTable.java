package com.ils.controllers.tables;

import com.ils.controllers.Component;
import com.ils.logic.management.PartManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.models.Part;
import com.ils.models.Product;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
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

public class ProductPartTable extends Component<Region> {
    private ProductManagement productManagement;
    private PartManagement partManagement;

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

    /**
     * Constructor.
     * @param productManagement
     * @param partManagement
     */
    public ProductPartTable(ProductManagement productManagement,
            PartManagement partManagement) {
        super("ProductPartTable.fxml");
        this.productManagement = productManagement;
        this.partManagement = partManagement;
        initTable();
        initListeners();
        initProductColumn();
        initPartColumn();
        initQuantityColumn();
        rebuild();
        initFilters();
    }

    /**
     * Initialize the table and set the items.
     */
    private void initTable() {
        treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTable.setShowRoot(false);
        treeTable.setEditable(true);
        TreeItem<Object> root = new TreeItem<>();
        treeTable.setRoot(root);
    }

    /**
     * Initialize the listeners for selection, filter, product and part changes.
     */
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
        this.productManagement.getProductCustomerFilter().addListener((observable, oldValue, newValue) -> {
            rebuild();
        });
        this.productManagement.getDBNameFilter().addListener((observable, oldValue, newValue) -> {
            rebuild();
        });
    }

    /**
     * Initialise product column
     */
    private void initProductColumn() {
        // Show product database names and not parts
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

    /**
     * Initialise part column
     */
    private void initPartColumn() {
        final PseudoClass defaultPart = PseudoClass.getPseudoClass("default-part");
        defaultPartColumn.setSortable(false);
        // Show product names and part names
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
        // Highlight default parts
        defaultPartColumn.setCellFactory(c -> {
            return new TextFieldTreeTableCell<Object, String>(new DefaultStringConverter()) {
                @Override
                public void updateItem(String item, boolean empty) {
                    boolean def = false;
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.toString());
                        TreeItem<Object> curr = getTreeTableView().getTreeItem(getIndex());
                        if (curr != null && curr.getValue() instanceof Part) {
                            Part pt = (Part) curr.getValue();
                            def = pt.getProduct().getDefaultPart().equals(pt);
                        }
                    }
                    pseudoClassStateChanged(defaultPart, def);
                }
            };
        });
        // Enable part name editing
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

    /**
     * Intialise quantity column
     */
    private void initQuantityColumn() {
        quantityColumn.setSortable(false);
        // Show product quantities and part quantities
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

    /**
     * Initialise the name filter and clear button.
     */
    private void initFilters() {
        dbNameSearchField.setPromptText("Filter by product name");
        dbNameSearchField.textProperty().addListener(this::handleNameFilter);
        HBox.setHgrow(dbNameSearchField, Priority.ALWAYS);
        clearBtn.setText("Clear");
        clearBtn.setOnAction(clearFilterHandler);
    }

    /**
     * Get the selection model
     * @return TreeTableViewSelectionModel<Object> for getting user input
     */
    public TreeTableViewSelectionModel<Object> getSelectionModel() {
        return treeTable.getSelectionModel();
    }

    /**
     * Clear the filter and selection
     */
    private EventHandler<ActionEvent> clearFilterHandler = (event) -> {
        treeTable.getSelectionModel().clearSelection();
        dbNameSearchField.clear();
    };

    /**
     * Handle selection of product/part
     * @param observable
     * @param oldSelection
     * @param newSelection
     */
    private void handleSelection(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldSelection,
            TreeItem<Object> newSelection) {
        if (newSelection != null && newSelection.getValue() instanceof Product) {
            Product product = (Product) newSelection.getValue();
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

    /**
     * Handle the name filter
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void handleNameFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.productManagement.setProductNameFilter(newValue);
        rebuild();
    }
    
    /**
     * Rebuild the tree view
     */
    private void rebuild() {
        TreeItem<Object> root = treeTable.getRoot();
        root.getChildren().clear();
        // Iterate through all products
        for (Product product : this.productManagement.getProducts()) {
            // Product
            TreeItem<Object> productItem = new TreeItem<>(product);
            root.getChildren().add(productItem);
            // List of linked parts
            List<Integer> linkedParts = new ArrayList<>();
            Part start = product.getDefaultPart();
            while (start != null) {
                // Add parts as product children
                linkedParts.add(start.getId());
                TreeItem<Object> partItem = new TreeItem<>(start);
                productItem.getChildren().add(partItem);
                start = start.getNextPart();
            }
        }
        // Hack to ensure products cells are updated
        treeTable.refresh();
    }
}
