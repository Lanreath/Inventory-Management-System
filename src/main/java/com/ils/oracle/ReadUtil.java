package com.ils.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadUtil {
    // private static String inputDate =
    // LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));

    private static final String productNameMap = "case\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF01' then '01_CLASSIC'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF02' then '02_PAINTER'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF03' then '03_ARCH'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC1' then '01_CLASSIC_UAE'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC2' then '02_PAINTER_UAE'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC3' then '03_ARCH_UAE'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,5) = 'VBW01' then 'PRADA_WEAR'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBM01' then 'PLAT'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUP1' then 'PLAT_UAE'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC1' then 'VBWC1'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC5' then 'VBWC5'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBW01' then 'VBW01'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC3' then 'VBWC3'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWL1' then 'VBWL1'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG2' then 'VBWG2'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG3' then 'VBWG3'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG4' then 'VBWG4'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG5' then 'VBWG5'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG6' then 'VBWG6'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG7' then 'VBWG7'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF1' then 'VBWF1'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF4' then 'VBWF4'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF5' then 'VBWF5'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB1' then 'VBWB1'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB2' then 'VBWB2'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB3' then 'VBWB3'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB4' then 'VBWB4'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC4' then 'VBWC4'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC2' then 'VBWC2'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC5' then 'VBWC5'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC6' then 'VBWC6'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC7' then 'VBWC7'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF3' then 'VBWF3'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF6' then 'VBWF6'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF7' then 'VBWF7'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG1' then 'VBWG1'\r\n" + //
            "when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF2' then 'VBWF2'\r\n" + //
            "else get_token(pr.productalias,'1','_')\r\n" + //
            "END ";
    private static final String customerName = "co.customername\r\n";
    private static final String productName = "get_token(pr.productname,1,'_')";
    private static final String customerAlias = " as customer, ";
    private static final String productAlias = " as product, ";
    private static final String vaultAlias = "as vaultname, ";
    private static final String quantityAlias = "as quantity ";
    private static final String tableNames = "from customerorder co, workorder wo, product pr, part pt ";
    private static final String join1 = "where wo.productid = pr.productid\r\n" + //
            "and pr.productkey1 = pt.productkey1\r\n" + //
            "and pr.configurationid = pt.configurationid\r\n" + //
            "and wo.workorderiddisplay = wo.workorderid\r\n" + //
            "and wo.status <> 700\r\n" + //
            "and wo.splitflag <> 1\r\n" + //
            "and (get_token(pr.productname, 5,'_') != 'RNW')\r\n" + //
            "and co.customername <> 'CSG'\r\n" + //
            "and to_date(wo.creationdate,'DD/MM/YY') = to_date(";
    private static final String join2 = ", 'DD/MM/YY')\r\n" + "and wo.customerorderid = co.customerorderid ";

    public static ResultSet readCustomers(String username, String password) {
        Connection conn;
        String query = "SELECT * FROM CUSTOMER";
        try {
            conn = Oracle.connect(username, password);
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not retrieve customers from Oracle database." + e.getMessage());
            return null;
        }
    }

    public static ResultSet readTransfersByDate(String username, String password, LocalDate date) {
        Connection conn;
        String query = "select " + customerName + customerAlias + productName + productAlias + productNameMap
                + vaultAlias + "SUM(wo.quantity)\r\n" + quantityAlias + //
                tableNames + //
                join1 + "'" + date.format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "'" + //
                join2 + //
                "group by " + customerName + ", " + productName + ", " + productNameMap //
                + "order by customer, product, vaultname";
        try {
            conn = Oracle.connect(username, password);
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not retrieve transfers from Oracle database." + e.getMessage());
            return null;
        }
    }
}
