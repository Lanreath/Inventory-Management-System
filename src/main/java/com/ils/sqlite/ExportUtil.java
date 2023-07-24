package com.ils.sqlite;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.ils.Config;
import com.ils.MainApp;

public class ExportUtil {
    private static final CSVFormat format = CSVFormat.EXCEL;
    private CSVPrinter printer;

    public ExportUtil() {
        try {
            printer = new CSVPrinter(new FileWriter(Config.getValue("export.location")), format);
        } catch (Exception e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not create CSVPrinter: " + e.getMessage());
        }
    }

    public void exportQuery(ResultSet rs) {
        try {
            printer.printRecords(rs);
        } catch (IOException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not export query result: " + e.getMessage());
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": SQLite invalid query result: " + e.getMessage());
        }
    }

}
 