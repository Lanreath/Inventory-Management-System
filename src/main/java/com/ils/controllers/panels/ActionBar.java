package com.ils.controllers.panels;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ils.controllers.Component;
import com.ils.logic.Logic;

public class ActionBar extends Component<Region> {
    private static final String FXML = "ActionBar.fxml";

    private InputBar inputBar;

    @FXML
    private Button addCustomerBtn;

    @FXML
    private Button addProductBtn;

    @FXML
    private Button addPartBtn;

    @FXML
    private Button addTransferBtn;

    @FXML
    private Button syncBtn;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private DatePicker syncDate;

    @FXML
    private Button deleteBtn;

    @FXML
    private Label status;

    public ActionBar(Logic logic, InputBar inputBar) {
        super(FXML, logic);
        this.inputBar = inputBar;
        ImageView cust = new ImageView("/images/customer-v2.png");
        ImageView prod = new ImageView("/images/product-v2.png");
        ImageView part = new ImageView("/images/part-v2.png");
        ImageView xact = new ImageView("/images/transfer-v2.png");
        ImageView dlte = new ImageView("/images/delete.png");
        ImageView sync = new ImageView("/images/sync-icon.png");
        cust.setPreserveRatio(true);
        cust.setFitHeight(24);
        addCustomerBtn.setGraphic(cust);
        addCustomerBtn.setTooltip(new Tooltip("Add customer"));
        addCustomerBtn.setOnAction(addCustomerHandler);
        prod.setPreserveRatio(true);
        prod.setFitHeight(24);
        addProductBtn.setGraphic(prod);
        addProductBtn.setTooltip(new Tooltip("Add product"));
        addProductBtn.setOnAction(addProductHandler);
        part.setPreserveRatio(true);
        part.setFitHeight(24);
        addPartBtn.setGraphic(part);
        addPartBtn.setTooltip(new Tooltip("Add part"));
        addPartBtn.setOnAction(addPartHandler);
        xact.setPreserveRatio(true);
        xact.setFitHeight(24);
        addTransferBtn.setGraphic(xact);
        addTransferBtn.setTooltip(new Tooltip("Make transfer"));
        addTransferBtn.setOnAction(addTransferHandler);
        deleteBtn.setGraphic(dlte);
        deleteBtn.setTooltip(new Tooltip("Delete entry"));
        deleteBtn.setOnAction(deleteEntryHandler);
        sync.setPreserveRatio(true);
        sync.setFitHeight(24);
        syncBtn.setGraphic(sync);
        syncBtn.setTooltip(new Tooltip("Sync data"));
        syncBtn.setOnAction(syncEventHandler);
        username.setPromptText("Username");
        username.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                syncEventHandler.handle(new ActionEvent());
            }
        });
        password.setPromptText("Password");
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                syncEventHandler.handle(new ActionEvent());
            }
        });
        syncDate.setValue(LocalDate.now());
    }

    private EventHandler<ActionEvent> addCustomerHandler = (event) -> {
        inputBar.addCustomer();
    };

    private EventHandler<ActionEvent> addProductHandler = (event) -> {
        inputBar.addProduct();
    };

    private EventHandler<ActionEvent> addPartHandler = (event) -> {
        inputBar.addPart();
    };

    private EventHandler<ActionEvent> addTransferHandler = (event) -> {
        inputBar.addTransfer();
    };
 
    private EventHandler<ActionEvent> deleteEntryHandler = (event) -> {
        inputBar.deleteEntry();
    };


    private EventHandler<ActionEvent> syncEventHandler = (event) -> {
            if (username.getText().isEmpty() || password.getText().isEmpty()) {
                status.setText("Please enter username and password!");
                status.setStyle("-fx-text-fill: #ff0000;");
                return;
            }
            status.setText("Syncing...");
            status.setStyle("-fx-text-fill: #000000;");
            try {
                this.logic.syncData(username.getText(), password.getText(), syncDate.getValue());
                status.setText("Synced!");
                status.setStyle("-fx-text-fill: #00ff00;");
            } catch (IllegalStateException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": IllegalStateException from database " + e.getMessage());
                status.setText("Sync failed!");
                status.setStyle("-fx-text-fill: #ff0000;");
            } catch (IllegalArgumentException e) {
                status.setText("Invalid username or password!");
                status.setStyle("-fx-text-fill: #ff0000;");
            }
    };
}
