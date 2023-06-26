package com.ils.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import com.ils.logic.Logic;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

public class InputBar extends Component<ToolBar> {
    private static final String FXML = "InputBar.fxml";
    
    private SelectionModel<Customer> cust;
    private TreeTableViewSelectionModel<Object> prpt;
    private SelectionModel<Transfer> xact;

    private TextField nameInput;

    private TextField qtyInput;

    // @FXML
    // private Button editBtn;

    private Button saveBtn;

    private Button confirmBtn;

    private Button cancelBtn;

    public InputBar(Logic logic, SelectionModel<Customer> cust, TreeTableViewSelectionModel<Object> prpt, SelectionModel<Transfer> xact) {
        super(FXML, logic);
        this.cust = cust;
        this.prpt = prpt;
        this.xact = xact;
        nameInput = new TextField();
        qtyInput = new TextField();
        saveBtn = new Button();
        confirmBtn = new Button("Confirm");
        cancelBtn = new Button("Cancel");
        qtyInput.setPromptText("Quantity");
        qtyInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                qtyInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        ImageView save = new ImageView("/images/save.png");
        save.setPreserveRatio(true);
        save.setFitHeight(24);
        saveBtn.setGraphic(save);
        saveBtn.setTooltip(new Tooltip("Save"));
        cancelBtn.setOnAction(e -> {
            getRoot().getItems().clear();
        });
    }

    private void displayForm() {
        getRoot().getItems().clear();
        getRoot().getItems().add(nameInput);
        getRoot().getItems().add(qtyInput);
        getRoot().getItems().add(saveBtn);
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
        displayForm();
        nameInput.setPromptText("Customer Name");
        nameInput.setText("");
        saveBtn.setOnAction(e -> {
            logic.addCustomer(nameInput.getText());
            nameInput.setText("");
            getRoot().getItems().clear();
        });
    }

    protected void addProduct() {
        if (cust.getSelectedItem() == null) {
            displayMsg("Please select a customer first.");
            return;
        }
        displayForm();
        nameInput.setPromptText("Product Name");
        nameInput.setText("");
        saveBtn.setOnAction(e -> {
            logic.addProduct(nameInput.getText(), cust.getSelectedItem());
            nameInput.setText("");
            getRoot().getItems().clear();
        });
    }

    protected void addPart() {
        if (prpt.getSelectedItem() == null || !(prpt.getSelectedItem().getValue() instanceof Product)) {
            displayMsg("Please select a product first.");
            return;
        }
        displayForm();
        nameInput.setPromptText("Part Name");
        nameInput.setText("");
        saveBtn.setOnAction(e -> {
            logic.addPart(nameInput.getText(), Integer.parseInt(qtyInput.getText()), (Product) prpt.getSelectedItem().getValue());
            nameInput.setText("");
            getRoot().getItems().clear();
        });
    }

    protected void addTransfer() {
        if (prpt.getSelectedItem() == null || !(prpt.getSelectedItem().getValue() instanceof Product)) {
            displayMsg("Please select a part first.");
            return;
        }
        displayForm();
        nameInput.setPromptText("Transfer Name");
        nameInput.setText("");
        saveBtn.setOnAction(e -> {
            logic.addTransfer((Part) prpt.getSelectedItem().getValue(), Integer.parseInt(qtyInput.getText()), null);
            nameInput.setText("");
            getRoot().getItems().clear();
        });
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
        displayDelete("customer?: " + customer.getCustomerName());
        confirmBtn.setOnAction(e -> {
            logic.deleteCustomer(customer);
            getRoot().getItems().clear();
        });
    }

    private void deleteProductPart() {
        if (prpt.getSelectedItem().getValue() instanceof Product) {
            Product product = (Product) prpt.getSelectedItem().getValue();
            displayDelete("product and its parts/transfers?: " + product.getProductName() + " by " + product.getCustomer().getCustomerName());
            confirmBtn.setOnAction(e -> {
                logic.deleteProduct(product);
                getRoot().getItems().clear();
            });
        } else {
            Part part = (Part) prpt.getSelectedItem().getValue();
            displayDelete("part and its transfers?: " + part.getPartName() + " from " + part.getProduct().getProductName() + " by " + part.getProduct().getCustomer().getCustomerName());
            confirmBtn.setOnAction(e -> {
                logic.deletePart(part);
                getRoot().getItems().clear();
            });
        }
    }

    private void deleteTransfer() {
        Transfer transfer = xact.getSelectedItem();
        displayDelete("transfer?: " + transfer.getTransferDateTime() + " Qty: " + transfer.getTransferQuantity() + " from " + transfer.getPart().getPartName() + " from " + transfer.getPart().getProduct().getProductName() + " by " + transfer.getPart().getProduct().getCustomer().getCustomerName());
        confirmBtn.setOnAction(e -> {
            logic.deleteTransfer(transfer);
            getRoot().getItems().clear();
        });
    }
}