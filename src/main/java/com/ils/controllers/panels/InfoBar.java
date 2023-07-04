package com.ils.controllers.panels;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.YearMonth;

import com.ils.controllers.Component;
import com.ils.logic.Logic;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

public class InfoBar extends Component<Region> {
    private static final String FXML = "InfoBar.fxml";

    @FXML
    private TextFlow customerInfo;

    @FXML
    private TextFlow productInfo;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;
    
    private SelectionModel<Customer> cust;
    private TreeTableViewSelectionModel<Object> prpt;

    private Text customerOpeningDesc;
    private Text customerClosingDesc;
    private Text customerChangeDesc;

    private Text customerMonthlyOpeningBal;
    private Text customerMonthlyClosingBal;
    private Text customerMonthlyChange;

    private Text productOpeningDesc;
    private Text productClosingDesc;
    private Text productChangeDesc;

    private Text productMonthlyOpeningBal;
    private Text productMonthlyClosingBal;
    private Text productMonthlyChange;

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
        customerInfo.setPrefWidth(191);
        HBox.setHgrow(productInfo, Priority.ALWAYS);
        startDatePicker.setPrefWidth(162);
        endDatePicker.setPrefWidth(161);
    }

    private void initTexts() {
        customerOpeningDesc = new Text("Opening Balance: ");
        customerClosingDesc = new Text("Closing Balance: ");
        customerChangeDesc = new Text("Stock Change: ");
        customerOpeningDesc.setStyle("-fx-font-weight: bold");
        customerClosingDesc.setStyle("-fx-font-weight: bold");
        customerChangeDesc.setStyle("-fx-font-weight: bold");

        customerMonthlyOpeningBal = new Text();
        customerMonthlyClosingBal = new Text();
        customerMonthlyChange = new Text();

        productOpeningDesc = new Text("Opening Balance: ");
        productClosingDesc = new Text("Closing Balance: ");
        productChangeDesc = new Text("Stock Change: ");
        productOpeningDesc.setStyle("-fx-font-weight: bold");
        productClosingDesc.setStyle("-fx-font-weight: bold");
        productChangeDesc.setStyle("-fx-font-weight: bold");

        productMonthlyOpeningBal = new Text();
        productMonthlyClosingBal = new Text();
        productMonthlyChange = new Text();
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
            }
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
        Integer opening = this.logic.getMonthlyOpeningBalByCustomer(cust.getSelectedItem(), startDatePicker.getValue());
        Integer closing = this.logic.getMonthlyClosingBalByCustomer(cust.getSelectedItem(), endDatePicker.getValue());
        customerMonthlyOpeningBal.setText(Integer.toString(opening) + "\n");
        customerMonthlyClosingBal.setText(Integer.toString(closing) + "\n");
        customerMonthlyChange.setText(Integer.toString(closing - opening));
        this.customerInfo.getChildren().addAll(customerOpeningDesc, customerMonthlyOpeningBal, customerClosingDesc, customerMonthlyClosingBal, customerChangeDesc, customerMonthlyChange);
    }

    private void updateProductInfo() {
        Product product;
        Part part;
        Integer opening;
        Integer closing;
        if (prpt.getSelectedItem() == null) {
            return;
        }
        if (prpt.getSelectedItem().getValue() instanceof Product) {
            product = (Product) prpt.getSelectedItem().getValue();
            opening = this.logic.getMonthlyOpeningBalByProduct(product, startDatePicker.getValue());
            closing = this.logic.getMonthlyClosingBalByProduct(product, endDatePicker.getValue());
        } else {
            part = (Part) prpt.getSelectedItem().getValue();
            opening = this.logic.getMonthlyOpeningBalByPart(part, startDatePicker.getValue());
            closing = this.logic.getMonthlyClosingBalByPart(part, endDatePicker.getValue());
        }
        productMonthlyOpeningBal.setText(Integer.toString(opening) + "\n");
        productMonthlyClosingBal.setText(Integer.toString(closing) + "\n");
        productMonthlyChange.setText(Integer.toString(closing - opening));
        this.productInfo.getChildren().addAll(productOpeningDesc, productMonthlyOpeningBal, productClosingDesc, productMonthlyClosingBal, productChangeDesc, productMonthlyChange);
    }

    private void removeCustomerInfo() {
        this.customerInfo.getChildren().clear();
    }

    private void removeProductInfo() {
        this.productInfo.getChildren().clear();
    }
}