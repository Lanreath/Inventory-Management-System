package com.ils.controllers.panels;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.logging.Logger;

import com.ils.controllers.Component;
import com.ils.logic.Logic;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

public class InfoBar extends Component<HBox> {
    private static final String FXML = "InfoBar.fxml";

    @FXML
    private TextFlow customerInfo;

    @FXML
    private TextFlow productInfo;

    @FXML
    private TextFlow productTransfers;

    @FXML
    private TextField productNotes;

    @FXML
    private Button setDefaultPartBtn;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;
    
    private SelectionModel<Customer> cust;
    private TreeTableViewSelectionModel<Object> prpt;

    private Text customerOpeningDesc;
    private Text customerClosingDesc;
    private Text customerChangeDesc;

    private Text customerOpeningBal;
    private Text customerClosingBal;
    private Text customerChangeBal;

    private Text productOpeningDesc;
    private Text productClosingDesc;
    private Text productChangeDesc;
    private Text productDailyDesc;
    private Text productRenewalDesc;
    private Text productRejectDesc;
    private Text productReceiveDesc;

    private Text productOpeningBal;
    private Text productClosingBal;
    private Text productChangeBal;
    private Text productDailyBal;
    private Text productRenewalBal;
    private Text productRejectBal;
    private Text productReceiveBal;

    public InfoBar(Logic logic, SelectionModel<Customer> cust, TreeTableViewSelectionModel<Object> prpt, SelectionModel<Transfer> xact) {
        super(FXML, logic);
        this.cust = cust;
        this.prpt = prpt;
        initLayout();
        initTexts();
        initListeners();
        initDates();
    }

    private void initLayout() {
        getRoot().setAlignment(Pos.CENTER_LEFT);
        customerInfo.setPrefWidth(191);
        startDatePicker.setPrefWidth(162);
        endDatePicker.setPrefWidth(161);
    }

    private void initTexts() {
        customerInfo.setLineSpacing(1);
        productInfo.setLineSpacing(1);
        productTransfers.setLineSpacing(1);

        Font descFont = new Font("System Bold", 14);

        customerOpeningDesc = new Text("Opening Balance: ");
        customerClosingDesc = new Text("Closing Balance: ");
        customerChangeDesc = new Text("Stock Change: ");
        customerOpeningDesc.setFont(descFont);
        customerClosingDesc.setFont(descFont);
        customerChangeDesc.setFont(descFont);

        customerOpeningBal = new Text();
        customerClosingBal = new Text();
        customerChangeBal = new Text();

        productOpeningDesc = new Text("Opening Balance: ");
        productClosingDesc = new Text("Closing Balance: ");
        productChangeDesc = new Text("Change: ");
        productDailyDesc = new Text("Daily: ");
        productRenewalDesc = new Text("Renewal: ");
        productRejectDesc = new Text("Reject: ");
        productOpeningDesc.setFont(descFont);
        productClosingDesc.setFont(descFont);
        productChangeDesc.setFont(descFont);
        productDailyDesc.setFont(descFont);
        productRenewalDesc.setFont(descFont);
        productRejectDesc.setFont(descFont);

        productOpeningBal = new Text();
        productClosingBal = new Text();
        productChangeBal = new Text();
        productDailyBal = new Text();
        productRenewalBal = new Text();
        productRejectBal = new Text();
    }

    private void initListeners() {
        cust.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            removeCustomerInfo();
            if (oldValue == null || !oldValue.equals(newValue)) {
                updateCustomerInfo();
            }
        });
        prpt.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            removeProductInfo();
            if (oldValue == null || !oldValue.equals(newValue)) {
                updateProductInfo();
                updateProductNotes();
            }
        });
        productNotes.visibleProperty().bind(prpt.selectedItemProperty().isNotNull());
        productNotes.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    if (prpt.getSelectedItem().getValue() instanceof Part) {
                        Part part = (Part) prpt.getSelectedItem().getValue();
                        logic.updatePartNotes(part, productNotes.getText());
                    } else if (prpt.getSelectedItem().getValue() instanceof Product) {
                        Product product = (Product) prpt.getSelectedItem().getValue();
                        logic.updateProductNotes(product, productNotes.getText());
                    } else {
                        return;
                    }
                }
            }
        });
        setDefaultPartBtn.visibleProperty().bind(prpt.selectedItemProperty().isNotNull());
        setDefaultPartBtn.setOnAction(event -> {
            if (prpt.getSelectedItem() == null) {
                return;
            }
            Part part = (Part) prpt.getSelectedItem().getValue();
            this.logic.updateDefaultPart(part);
        });
    }

    private void initDates() {
        startDatePicker.setValue(YearMonth.from(LocalDate.now()).atDay(1));
        endDatePicker.setValue(YearMonth.from(LocalDate.now()).atEndOfMonth());
    }

    private void updateCustomerInfo() {
        if (cust.getSelectedItem() == null) {
            return;
        }
        Integer opening = this.logic.getOpeningBalByCustomer(cust.getSelectedItem(), startDatePicker.getValue());
        Integer closing = this.logic.getClosingBalByCustomer(cust.getSelectedItem(), endDatePicker.getValue());
        customerOpeningBal.setText(Integer.toString(opening) + "\n");
        customerClosingBal.setText(Integer.toString(closing) + "\n");
        customerChangeBal.setText(Integer.toString(closing - opening));
        this.customerInfo.getChildren().addAll(customerOpeningDesc, customerOpeningBal, customerClosingDesc, customerClosingBal, customerChangeDesc, customerChangeBal);
    }

    private void updateProductInfo() {
        Product product;
        Part part;
        Integer opening;
        Integer closing;
        Integer daily;
        Integer renewal;
        Integer reject;
        if (prpt.getSelectedItem() == null) {
            return;
        }
        if (prpt.getSelectedItem().getValue() instanceof Product) {
            product = (Product) prpt.getSelectedItem().getValue();
            opening = this.logic.getOpeningBalByProduct(product, startDatePicker.getValue());
            closing = this.logic.getClosingBalByProduct(product, endDatePicker.getValue());
            daily = this.logic.getDailyTransferSumByProduct(product, startDatePicker.getValue(), endDatePicker.getValue());
            renewal = this.logic.getRenewalTransferSumByProduct(product, startDatePicker.getValue(), endDatePicker.getValue());
            reject = this.logic.getRejectTransferSumByProduct(product, startDatePicker.getValue(), endDatePicker.getValue());
        } else if (prpt.getSelectedItem().getValue() instanceof Part) {
            part = (Part) prpt.getSelectedItem().getValue();
            opening = this.logic.getOpeningBalByPart(part, startDatePicker.getValue());
            closing = this.logic.getClosingBalByPart(part, endDatePicker.getValue());
            daily = this.logic.getDailyTransferSumByPart(part, startDatePicker.getValue(), endDatePicker.getValue());
            renewal = this.logic.getRenewalTransferSumByPart(part, startDatePicker.getValue(), endDatePicker.getValue());
            reject = this.logic.getRejectTransferSumByPart(part, startDatePicker.getValue(), endDatePicker.getValue());
        } else {
            throw new RuntimeException("Invalid selection type for Product/Part table");
        }

        productOpeningBal.setText(Integer.toString(opening) + "\n");
        productClosingBal.setText(Integer.toString(closing) + "\n");
        productChangeBal.setText(Integer.toString(opening-closing));
        this.productInfo.getChildren().addAll(productOpeningDesc, productOpeningBal, productClosingDesc, productClosingBal, productChangeDesc, productChangeBal);

        productDailyBal.setText(Integer.toString(daily) + "\n");
        productRenewalBal.setText(Integer.toString(renewal) + "\n");
        productRejectBal.setText(Integer.toString(reject));
        this.productTransfers.getChildren().addAll(productDailyDesc, productDailyBal, productRenewalDesc, productRenewalBal, productRejectDesc, productRejectBal);
    }

    private void updateProductNotes() {
        if (prpt.getSelectedItem() == null) {
            return;
        }
        if (prpt.getSelectedItem().getValue() instanceof Product) {
            Product product = (Product) prpt.getSelectedItem().getValue();
            productNotes.setText(product.getProductNotes());
        } else {
            Part part = (Part) prpt.getSelectedItem().getValue();
            productNotes.setText(part.getPartNotes());
        }
    }

    private void removeCustomerInfo() {
        this.customerInfo.getChildren().clear();
    }

    private void removeProductInfo() {
        this.productInfo.getChildren().clear();
        this.productTransfers.getChildren().clear();
    }
}