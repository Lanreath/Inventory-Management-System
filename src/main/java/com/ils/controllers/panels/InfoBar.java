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
        getRoot().setAlignment(Pos.CENTER_LEFT);
        customerInfo.setPrefWidth(191);
        HBox.setHgrow(productInfo, Priority.ALWAYS);
        startDatePicker.setPrefWidth(162);
        endDatePicker.setPrefWidth(161);
    }

    private void initTexts() {
        customerInfo.setLineSpacing(1);
        productInfo.setLineSpacing(1);

        Font descFont = new Font("System Bold", 14);

        customerOpeningDesc = new Text("Opening Balance: ");
        customerClosingDesc = new Text("Closing Balance: ");
        customerChangeDesc = new Text("Stock Change: ");
        customerOpeningDesc.setFont(descFont);
        customerClosingDesc.setFont(descFont);
        customerChangeDesc.setFont(descFont);

        customerMonthlyOpeningBal = new Text();
        customerMonthlyClosingBal = new Text();
        customerMonthlyChange = new Text();

        productOpeningDesc = new Text("Opening Balance: ");
        productClosingDesc = new Text("Closing Balance: ");
        productChangeDesc = new Text("Stock Change: ");
        productOpeningDesc.setFont(descFont);
        productClosingDesc.setFont(descFont);
        productChangeDesc.setFont(descFont);

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
    }
}