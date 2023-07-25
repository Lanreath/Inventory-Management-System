package com.ils.controllers.panels;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ils.MainApp;
import com.ils.controllers.Component;
import com.ils.logic.DataSync;
import com.ils.logic.ExportUtil;
import com.ils.logic.Quantities;

public class ActionBar extends Component<Region> {
    private static final String FXML = "ActionBar.fxml";
    private DataSync dataSync;
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
    private DatePicker syncDate;

    @FXML
    private Button deleteBtn;

    @FXML
    private Label status;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    Button exportBtn;

    public ActionBar(DataSync dataSync, InputBar inputBar) {
        super(FXML);
        this.dataSync = dataSync;
        this.inputBar = inputBar;
        initCustBtn();
        initProdBtn();
        initPartBtn();
        initXactBtn();
        initDeleteBtn();
        initSyncBtn();
        initExportBtn();
        initDates();
    }

    private void initCustBtn() {
        ImageView cust = new ImageView("/images/customer-v2.png");
        cust.setPreserveRatio(true);
        cust.setFitHeight(24);
        addCustomerBtn.setGraphic(cust);
        addCustomerBtn.setTooltip(new Tooltip("Add customer"));
        addCustomerBtn.setOnAction(addCustomerHandler);
    }

    private void initProdBtn() {
        ImageView prod = new ImageView("/images/product-v2.png");
        prod.setPreserveRatio(true);
        prod.setFitHeight(24);
        addProductBtn.setGraphic(prod);
        addProductBtn.setTooltip(new Tooltip("Add product"));
        addProductBtn.setOnAction(addProductHandler);
    }

    private void initPartBtn() {
        ImageView part = new ImageView("/images/part-v2.png");
        part.setPreserveRatio(true);
        part.setFitHeight(24);
        addPartBtn.setGraphic(part);
        addPartBtn.setTooltip(new Tooltip("Add part"));
        addPartBtn.setOnAction(addPartHandler);
    }

    private void initXactBtn() {
        ImageView xact = new ImageView("/images/transfer-v2.png");
        xact.setPreserveRatio(true);
        xact.setFitHeight(24);
        addTransferBtn.setGraphic(xact);
        addTransferBtn.setTooltip(new Tooltip("Make transfer"));
        addTransferBtn.setOnAction(addTransferHandler);
    }

    private void initDeleteBtn() {
        ImageView dlte = new ImageView("/images/delete.png");
        dlte.setPreserveRatio(true);
        dlte.setFitHeight(24);
        deleteBtn.setGraphic(dlte);
        deleteBtn.setTooltip(new Tooltip("Delete entry"));
        deleteBtn.setOnAction(deleteEntryHandler);
    }

    private void initSyncBtn() {
        ImageView sync = new ImageView("/images/sync-icon.png");
        sync.setPreserveRatio(true);
        sync.setFitHeight(24);
        syncBtn.setGraphic(sync);
        syncBtn.setTooltip(new Tooltip("Sync data"));
        syncBtn.setOnAction(syncEventHandler);
    }

    private void initExportBtn() {
        // ImageView export = new ImageView("/images/export.png");
        // export.setPreserveRatio(true);
        // export.setFitHeight(24);
        // exportBtn.setGraphic(export);
        exportBtn.setTooltip(new Tooltip("Export monthly report"));
        exportBtn.setOnAction(exportEventHandler);
    }

    private void initDates() {
        syncDate.setValue(LocalDate.now());
        startDatePicker.setValue(Quantities.getFrom());
        endDatePicker.setValue(Quantities.getTo());
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isAfter(endDatePicker.getValue())) {
                startDatePicker.setValue(endDatePicker.getValue());
            }
            Quantities.setFrom(newValue);
        });
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(startDatePicker.getValue())) {
                endDatePicker.setValue(startDatePicker.getValue());
            }
            Quantities.setTo(newValue);
        });
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
            status.setText("Syncing...");
            status.setStyle("-fx-text-fill: #000000;");
            try {
                dataSync.syncData(syncDate.getValue());
                status.setText("Synced!");
                status.setStyle("-fx-text-fill: #00ff00;");
            } catch (IllegalStateException e) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, LocalDateTime.now() + ": IllegalStateException from database " + e.getMessage());
                status.setText("Sync failed!");
                status.setStyle("-fx-text-fill: #ff0000;");
            } catch (IllegalArgumentException e) {
                status.setText("Invalid username or password!");
                status.setStyle("-fx-text-fill: #ff0000;");
            }
    };

    private EventHandler<ActionEvent> exportEventHandler = (event) -> {
        ExportUtil.exportMonthlyReport();
    };
}
