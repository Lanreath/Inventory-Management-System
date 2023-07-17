package com.ils.controllers.panels;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import com.ils.controllers.Component;
import com.ils.logic.Logic;
import com.ils.logic.management.CustomerManagement;
import com.ils.logic.management.PartManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.logic.management.TransferManagement;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

public class InputBar extends Component<ToolBar> {
    private static final String FXML = "InputBar.fxml";
    
    private CustomerManagement customerManagement;
    private ProductManagement productManagement;
    private PartManagement partManagement;
    private TransferManagement transferManagement;
    private SelectionModel<Customer> cust;
    private TreeTableViewSelectionModel<Object> prpt;
    private SelectionModel<Transfer> xact;

    private TextField nameInput;

    private TextField qtyInput;

    private ComboBox<Transfer.Action> actionInput;

    private Button saveBtn;

    private Button confirmBtn;

    private Button cancelBtn;

    public InputBar(CustomerManagement customerManagement, ProductManagement productManagement, PartManagement partManagement, TransferManagement transferManagement, SelectionModel<Customer> cust, TreeTableViewSelectionModel<Object> prpt, SelectionModel<Transfer> xact) {
        super(FXML);
        this.customerManagement = customerManagement;
        this.productManagement = productManagement;
        this.partManagement = partManagement;
        this.transferManagement = transferManagement;
        this.cust = cust;
        this.prpt = prpt;
        this.xact = xact;
        nameInput = new TextField();
        qtyInput = new TextField();
        actionInput = new ComboBox<Transfer.Action>();
        saveBtn = new Button();
        confirmBtn = new Button();
        cancelBtn = new Button();
        qtyInput.setPromptText("Quantity");
        qtyInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                qtyInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        actionInput.getItems().addAll(Transfer.Action.values());
        ImageView save = new ImageView("/images/save.png");
        ImageView confirm = new ImageView("/images/confirm-icon.png");
        ImageView cancel = new ImageView("/images/cancel-icon.png");
        save.setPreserveRatio(true);
        save.setFitHeight(24);
        saveBtn.setGraphic(save);
        saveBtn.setTooltip(new Tooltip("Save"));
        confirm.setPreserveRatio(true);
        confirm.setFitHeight(24);
        confirmBtn.setGraphic(confirm);
        confirmBtn.setTooltip(new Tooltip("Confirm"));
        cancel.setPreserveRatio(true);
        cancel.setFitHeight(24);
        cancelBtn.setGraphic(cancel);
        cancelBtn.setTooltip(new Tooltip("Cancel"));
        cancelBtn.setOnAction(e -> {
            getRoot().getItems().clear();
        });
    }

    private EventHandler<ActionEvent> addCustomerHandler = (event) -> {
        if (nameInput.getText().isEmpty()) {
            displayMsg("Please enter a name.");
            return;
        }
        customerManagement.addCustomer(nameInput.getText());
        nameInput.setText("");
        getRoot().getItems().clear();
    };

    private EventHandler<ActionEvent> addProductHandler = (event) -> {
        if (nameInput.getText().isEmpty()) {
            displayMsg("Please enter a name.");
            return;
        }
        productManagement.addProduct(nameInput.getText(), cust.getSelectedItem());
        nameInput.setText("");
        getRoot().getItems().clear();
    };

    private EventHandler<ActionEvent> addPartHandler = (event) -> {
        if (nameInput.getText().isEmpty()) {
            displayMsg("Please enter a name.");
            return;
        }
        if (qtyInput.getText().isEmpty()) {
            displayMsg("Please enter a quantity.");
            return;
        }
        partManagement.addPart(nameInput.getText(), Integer.parseInt(qtyInput.getText()), (Product) prpt.getSelectedItem().getValue());
        nameInput.setText("");
        qtyInput.clear();
        getRoot().getItems().clear();
    };

    private EventHandler<ActionEvent> addTransferHandler = (event) -> {
        if (qtyInput.getText().isEmpty()) {
            displayMsg("Please enter a quantity.");
            return;
        }
        if (actionInput.getValue() == null) {
            displayMsg("Please select an action.");
            return;
        }
        transferManagement.addTransfer((Part) prpt.getSelectedItem().getValue(), Integer.parseInt(qtyInput.getText()), actionInput.getValue());
        qtyInput.clear();
        getRoot().getItems().clear();
    };

    private void displayForm(String type) {
        getRoot().getItems().clear();
        switch (type) {
            case "Customer":
            case "Product":
                getRoot().getItems().add(nameInput);
                break;
            case "Part":
                getRoot().getItems().add(nameInput);
                getRoot().getItems().add(qtyInput);
                break;
            case "Transfer":
                getRoot().getItems().add(qtyInput);
                getRoot().getItems().add(actionInput);
                break;
        }
        getRoot().getItems().add(saveBtn);
        getRoot().getItems().add(cancelBtn);
    }

    private void displayDelete(String msg) {
        getRoot().getItems().clear();
        getRoot().getItems().add(new Label("Are you sure you want to delete this " + msg));
        getRoot().getItems().add(confirmBtn);
        getRoot().getItems().add(cancelBtn);
    }

    private void displayMsg(String msg) {
        getRoot().getItems().clear();
        getRoot().getItems().add(new Label(msg));
    }

    protected void addCustomer() {
        displayForm("Customer");
        nameInput.setPromptText("Customer Name");
        nameInput.setText("");
        nameInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                addCustomerHandler.handle(new ActionEvent());
            }
        });
        saveBtn.setOnAction(addCustomerHandler);
    }

    protected void addProduct() {
        if (cust.getSelectedItem() == null) {
            displayMsg("Please select a customer first.");
            return;
        }
        displayForm("Product");
        nameInput.setPromptText("Product Name");
        nameInput.setText("");
        nameInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                addProductHandler.handle(new ActionEvent());
            }
        });
        saveBtn.setOnAction(addProductHandler);
    }

    protected void addPart() {
        if (prpt.getSelectedItem() == null || !(prpt.getSelectedItem().getValue() instanceof Product)) {
            displayMsg("Please select a product first.");
            return;
        }
        displayForm("Part");
        nameInput.setPromptText("Part Name");
        nameInput.setText("");
        nameInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                addPartHandler.handle(new ActionEvent());
            }
        });
        qtyInput.setPromptText("Quantity");
        qtyInput.setText("");
        qtyInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                addPartHandler.handle(new ActionEvent());
            }
        });
        saveBtn.setOnAction(addPartHandler);
    }

    protected void addTransfer() {
        if (prpt.getSelectedItem() == null || !(prpt.getSelectedItem().getValue() instanceof Part)) {
            displayMsg("Please select a part first.");
            return;
        }
        displayForm("Transfer");
        qtyInput.setPromptText("Quantity");
        qtyInput.setText("");
        saveBtn.setOnAction(addTransferHandler);
    }

    protected void deleteEntry() {
        if (xact.getSelectedItem() != null) {
            deleteTransfer();
        } else if (prpt.getSelectedItem() != null) {
            deleteProductPart();
        } else if (cust.getSelectedItem() != null) {
            deleteCustomer();
        } else {
            displayMsg("Please select an entry first.");
        }
    }

    private void deleteCustomer() {
        Customer customer = cust.getSelectedItem();
        displayDelete("customer? " + customer.getCustomerName());
        confirmBtn.setOnAction(e -> {
            customerManagement.deleteCustomer(customer);
            getRoot().getItems().clear();
        });
    }

    private void deleteProductPart() {
        if (prpt.getSelectedItem().getValue() instanceof Product) {
            Product product = (Product) prpt.getSelectedItem().getValue();
            displayDelete("product and its parts/transfers? " + product.getDBName() + " by " + product.getCustomer().getCustomerName());
            confirmBtn.setOnAction(e -> {
                productManagement.deleteProduct(product);
                getRoot().getItems().clear();
            });
        } else {
            Part part = (Part) prpt.getSelectedItem().getValue();
            displayDelete("part and its transfers? " + part.getPartName() + " from " + part.getProduct().getDBName() + " by " + part.getProduct().getCustomer().getCustomerName());
            confirmBtn.setOnAction(e -> {
                getRoot().getItems().clear();
                if (partManagement.getProductParts(part.getProduct()).count() == 1) {
                    displayMsg("Unable to delete part. Product must have at least one part.");     
                    return;
                }
                partManagement.deletePart(part);
            });
        }
    }

    private void deleteTransfer() {
        Transfer transfer = xact.getSelectedItem();
        displayDelete("transfer? " + transfer.getTransferDateTime() + " Qty: " + transfer.getTransferQuantity() + " from " + transfer.getPart().getPartName() + " from " + transfer.getPart().getProduct().getDBName() + " by " + transfer.getPart().getProduct().getCustomer().getCustomerName());
        confirmBtn.setOnAction(e -> {
            transferManagement.deleteTransfer(transfer);
            getRoot().getItems().clear();
        });
    }
}