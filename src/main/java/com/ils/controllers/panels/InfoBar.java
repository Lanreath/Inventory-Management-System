package com.ils.controllers.panels;

import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.layout.Region;

import com.ils.controllers.Component;
import com.ils.logic.Logic;
import com.ils.models.Customer;
import com.ils.models.Transfer;

public class InfoBar extends Component<Region> {
    private static final String FXML = "InfoBar.fxml";
    
    private SelectionModel<Customer> cust;
    private TreeTableViewSelectionModel<Object> prpt;
    private SelectionModel<Transfer> xact;

    public InfoBar(Logic logic, SelectionModel<Customer> cust, TreeTableViewSelectionModel<Object> prpt, SelectionModel<Transfer> xact) {
        super(FXML, logic);
        this.cust = cust;
        this.prpt = prpt;
        this.xact = xact;
    }
}