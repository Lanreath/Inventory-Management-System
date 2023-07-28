package com.ils.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.ils.Config;
import com.ils.MainApp;
import com.ils.logic.DAO.PartDAO;
import com.ils.models.Part;

public class ExportUtil {
    private static final CSVFormat format = CSVFormat.EXCEL;
    private static CSVPrinter printer;

    static {
        try {
            printer = new CSVPrinter(new FileWriter(Config.getValue("export.location")), format);
        } catch (Exception e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not create CSVPrinter: " + e.getMessage());
        }
    }

    private static void exportArray(Object[] list) {
        try {
            printer.printRecord(list);
        } catch (IOException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not export query result: " + e.getMessage());
        }
    }

    public static void exportMonthlyReport() throws IOException {
        // Print headers
        exportArray(new String[] { "Customer", "Product", "Part", "Opening Balance", "Sample", "Received",
                "Daily (Output)", "Daily (Reject)", "Renewal (Output)", "Renewal (Reject)", "Project (Reject)",
                "Closing Balance" });
        // Get all parts and their quantities
        for (Part p : PartDAO.getAllParts()) {
            Object[] row = new String[12];
            row[0] = p.getProduct().getCustomer().getCustomerName();
            row[1] = p.getProduct().getProductName();
            row[2] = p.getPartName();
            row[3] = Integer.toString(Quantities.getOpeningBalByPart(p));
            row[4] = Integer.toString(Quantities.getSampleTransferSumByPart(p));
            row[5] = Integer.toString(Quantities.getReceivedTransferSumByPart(p));
            row[6] = Integer.toString(Quantities.getDailyTransferSumByPart(p));
            row[7] = Integer.toString(Quantities.getRejectDailyTransferSumByPart(p));
            row[8] = Integer.toString(Quantities.getRenewalTransferSumByPart(p));
            row[9] = Integer.toString(Quantities.getRejectRenewalTransferSumByPart(p));
            row[10] = Integer.toString(Quantities.getRejectProjectTransferSumByPart(p));
            row[11] = Integer.toString(Quantities.getClosingBalByPart(p));
            exportArray(row);
        }
        printer.close();
        Logger.getLogger(MainApp.class.getName()).log(Level.INFO,
                LocalDateTime.now() + ": Monthly report exported to " + Config.getValue("export.location"));
    }
}
