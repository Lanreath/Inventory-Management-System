package com.ils.controllers.panels;

import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.layout.HBox;

import com.ils.controllers.Component;
import com.ils.logic.Logic;
import com.ils.models.Customer;
import com.ils.models.Transfer;

public class InfoBar extends Component<HBox> {
    private static final String FXML = "InfoBar.fxml";
    
    private SelectionModel<Customer> cust;
    private TreeTableViewSelectionModel<Object> prpt;
    // private SelectionModel<Transfer> xact;

    private Label customerName;
    private Label customerMonthlyOpeningBal;
    private Label customerMonthlyClosingBal;

    private Label productName;
    private Label productMonthlyOpeningBal;
    private Label productMonthlyClosingBal;

    public InfoBar(Logic logic, SelectionModel<Customer> cust, TreeTableViewSelectionModel<Object> prpt, SelectionModel<Transfer> xact) {
        super(FXML, logic);
        this.cust = cust;
        this.prpt = prpt;
        // this.xact = xact;
        fillInnerComponents();
        cust.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                customerName.setText(newValue.getCustomerName());
                customerMonthlyOpeningBal.setText(Integer.toString(this.logic.getMonthlyOpeningBal()));
                customerMonthlyClosingBal.setText(Integer.toString(this.logic.getMonthlyClosingBal()));
            }
        });
    }


    public void fillInnerComponents() {
        customerName = new Label();
        customerMonthlyOpeningBal = new Label();
        customerMonthlyClosingBal = new Label();

        productName = new Label();
        productMonthlyOpeningBal = new Label();
        productMonthlyClosingBal = new Label();
    }
}