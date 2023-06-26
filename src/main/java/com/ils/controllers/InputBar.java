package com.ils.controllers;

import javafx.scene.control.Button;
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

    public InputBar(Logic logic, SelectionModel<Customer> cust, TreeTableViewSelectionModel<Object> prpt, SelectionModel<Transfer> xact) {
        super(FXML, logic);
        this.cust = cust;
        this.prpt = prpt;
        this.xact = xact;
        nameInput = new TextField();
        qtyInput = new TextField();
        saveBtn = new Button();
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
    }

    private void displayForm() {
        getRoot().getItems().add(nameInput);
        getRoot().getItems().add(qtyInput);
        getRoot().getItems().add(saveBtn);
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
        displayForm();
        nameInput.setPromptText("Transfer Name");
        nameInput.setText("");
        saveBtn.setOnAction(e -> {
            logic.addTransfer((Part) prpt.getSelectedItem().getValue(), Integer.parseInt(qtyInput.getText()), null);
            nameInput.setText("");
            getRoot().getItems().clear();
        });
    }
}