package com.ils.controllers.panels;

import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import com.ils.controllers.Component;
import com.ils.controllers.tables.CustomerTable;
import com.ils.controllers.tables.ProductPartTable;
import com.ils.controllers.tables.TransferTable;
import com.ils.logic.Quantities;
import com.ils.logic.management.PartManagement;
import com.ils.logic.management.ProductManagement;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;

public class InfoBar extends Component<HBox> {
    private static final String FXML = "InfoBar.fxml";

    @FXML
    private TextFlow customerQuantities;

    @FXML
    private HBox productInfo;

    @FXML
    private HBox transferInfo;

    @FXML
    private TextFlow productQuantities;

    @FXML
    private TextFlow transferQuantities1;

    @FXML
    private TextFlow transferQuantities2;

    @FXML
    private TextField productNotes;

    @FXML
    private Button setDefaultPartBtn;

    private ProductManagement productManagement;
    private PartManagement partManagement;
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
    private Text productReceiveDesc;
    private Text productDailyDesc;
    private Text productRenewalDesc;
    private Text productRejectDailyDesc;
    private Text productRejectRenewalDesc;
    private Text productRejectProjectDesc;
    private Text productSampleDesc;

    private Text productOpeningBal;
    private Text productClosingBal;
    private Text productChangeBal;
    private Text productReceiveBal;
    private Text productDailyBal;
    private Text productRenewalBal;
    private Text productRejectDailyBal;
    private Text productRejectRenewalBal;
    private Text productRejectProjectBal;
    private Text productSampleBal;

    public InfoBar(ProductManagement productManagement, PartManagement partManagement, CustomerTable cust,
            ProductPartTable prpt, TransferTable trnf) {
        super(FXML);
        this.productManagement = productManagement;
        this.partManagement = partManagement;
        this.cust = cust.getSelectionModel();
        this.prpt = prpt.getSelectionModel();
        initListeners();
        initLayout(cust, prpt, trnf);
        initTexts();
    }

    private void initLayout(CustomerTable cust, ProductPartTable prpt, TransferTable trnf) {
        productInfo.setAlignment(Pos.CENTER_LEFT);
        productInfo.setSpacing(10);
        transferInfo.setSpacing(10);
        transferInfo.prefWidthProperty().bind(trnf.getRoot().widthProperty());
        customerQuantities.prefWidthProperty().bind(cust.getRoot().widthProperty());
        customerQuantities.setLineSpacing(1);
        productQuantities.setLineSpacing(1);
        transferQuantities1.setLineSpacing(1);
        transferQuantities2.setLineSpacing(1);
    }

    private void initTexts() {
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
        productReceiveDesc = new Text("Received: ");
        productDailyDesc = new Text("Daily: ");
        productRenewalDesc = new Text("Renewal: ");
        productRejectDailyDesc = new Text("Reject (Daily): ");
        productRejectRenewalDesc = new Text("Reject (Renewal): ");
        productRejectProjectDesc = new Text("Reject (Project): ");
        productSampleDesc = new Text("Sample: ");

        productOpeningDesc.setFont(descFont);
        productClosingDesc.setFont(descFont);
        productChangeDesc.setFont(descFont);
        productReceiveDesc.setFont(descFont);
        productDailyDesc.setFont(descFont);
        productRenewalDesc.setFont(descFont);
        productSampleDesc.setFont(descFont);
        productRejectDailyDesc.setFont(descFont);
        productRejectRenewalDesc.setFont(descFont);
        productRejectProjectDesc.setFont(descFont);

        productOpeningBal = new Text();
        productClosingBal = new Text();
        productChangeBal = new Text();
        productReceiveBal = new Text();
        productDailyBal = new Text();
        productRenewalBal = new Text();
        productSampleBal = new Text();
        productRejectDailyBal = new Text();
        productRejectRenewalBal = new Text();
        productRejectProjectBal = new Text();
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
                        partManagement.updatePartNotes(part, productNotes.getText());
                    } else if (prpt.getSelectedItem().getValue() instanceof Product) {
                        Product product = (Product) prpt.getSelectedItem().getValue();
                        productManagement.updateProductNotes(product, productNotes.getText());
                    } else {
                        return;
                    }
                }
            }
        });
        setDefaultPartBtn.visibleProperty().bind(new BooleanBinding() {
            {
                super.bind(prpt.selectedItemProperty());
            }

            @Override
            protected boolean computeValue() {
                if (prpt.getSelectedItem() == null) {
                    return false;
                }
                if (prpt.getSelectedItem().getValue() instanceof Part) {
                    return true;
                } else if (prpt.getSelectedItem().getValue() instanceof Product) {
                    return false;
                } else {
                    throw new AssertionError("Unexpected value: " + prpt.getSelectedItem().getValue());
                }
            }
        });
        setDefaultPartBtn.setOnAction(event -> {
            if (prpt.getSelectedItem() == null) {
                return;
            }
            if (prpt.getSelectedItem().getValue() instanceof Part) {
                Part part = (Part) prpt.getSelectedItem().getValue();
                this.productManagement.updateDefaultPart(part);
                return;
            }
            throw new AssertionError("Unexpected value: " + prpt.getSelectedItem().getValue());
        });
    }

    private void updateCustomerInfo() {
        if (cust.getSelectedItem() == null) {
            return;
        }
        Integer opening = Quantities.getOpeningBalByCustomer(cust.getSelectedItem());
        Integer closing = Quantities.getClosingBalByCustomer(cust.getSelectedItem());
        customerOpeningBal.setText(Integer.toString(opening) + "\n");
        customerClosingBal.setText(Integer.toString(closing) + "\n");
        customerChangeBal.setText(Integer.toString(closing - opening));
        this.customerQuantities.getChildren().addAll(customerOpeningDesc, customerOpeningBal, customerClosingDesc,
                customerClosingBal, customerChangeDesc, customerChangeBal);
    }

    private void updateProductInfo() {
        Product product;
        Part part;
        Integer opening;
        Integer closing;
        Integer received;
        Integer daily;
        Integer renewal;
        Integer sample;
        Integer rejectDaily;
        Integer rejectRenewal;
        Integer rejectProject;
        if (prpt.getSelectedItem() == null) {
            return;
        }
        if (prpt.getSelectedItem().getValue() instanceof Product) {
            product = (Product) prpt.getSelectedItem().getValue();
            opening = Quantities.getOpeningBalByProduct(product);
            closing = Quantities.getClosingBalByProduct(product);
            received = Quantities.getReceivedTransferSumByProduct(product);
            daily = Quantities.getDailyTransferSumByProduct(product);
            renewal = Quantities.getRenewalTransferSumByProduct(product);
            sample = Quantities.getSampleTransferSumByProduct(product);
            rejectDaily = Quantities.getRejectDailyTransferSumByProduct(product);
            rejectRenewal = Quantities.getRejectRenewalTransferSumByProduct(product);
            rejectProject = Quantities.getRejectProjectTransferSumByProduct(product);
        } else if (prpt.getSelectedItem().getValue() instanceof Part) {
            part = (Part) prpt.getSelectedItem().getValue();
            opening = Quantities.getOpeningBalByPart(part);
            closing = Quantities.getClosingBalByPart(part);
            received = Quantities.getReceivedTransferSumByPart(part);
            daily = Quantities.getDailyTransferSumByPart(part);
            renewal = Quantities.getRenewalTransferSumByPart(part);
            sample = Quantities.getSampleTransferSumByPart(part);
            rejectDaily = Quantities.getRejectDailyTransferSumByPart(part);
            rejectRenewal = Quantities.getRejectRenewalTransferSumByPart(part);
            rejectProject = Quantities.getRejectProjectTransferSumByPart(part);
        } else {
            throw new RuntimeException("Invalid selection type for Product/Part table");
        }

        productOpeningBal.setText(Integer.toString(opening) + "\n");
        productClosingBal.setText(Integer.toString(closing) + "\n");
        productChangeBal.setText(Integer.toString(opening - closing));
        this.productQuantities.getChildren().addAll(productOpeningDesc, productOpeningBal, productClosingDesc,
                productClosingBal, productChangeDesc, productChangeBal);

        productReceiveBal.setText(Integer.toString(received) + "\n");
        productDailyBal.setText(Integer.toString(daily) + "\n");
        productRenewalBal.setText(Integer.toString(renewal));
        productSampleBal.setText(Integer.toString(sample) + "\n");
        productRejectDailyBal.setText(Integer.toString(rejectDaily) + "\n");
        productRejectRenewalBal.setText(Integer.toString(rejectRenewal));
        productRejectProjectBal.setText(Integer.toString(rejectProject));
        this.transferQuantities1.getChildren().addAll(productReceiveDesc, productReceiveBal, productDailyDesc,
                productDailyBal, productRenewalDesc, productRenewalBal);
        this.transferQuantities2.getChildren().addAll(productSampleDesc, productSampleBal, productRejectDailyDesc,
                productRejectDailyBal, productRejectRenewalDesc, productRejectRenewalBal);
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
        this.customerQuantities.getChildren().clear();
    }

    private void removeProductInfo() {
        this.productQuantities.getChildren().clear();
        this.transferQuantities1.getChildren().clear();
        this.transferQuantities2.getChildren().clear();
    }
}