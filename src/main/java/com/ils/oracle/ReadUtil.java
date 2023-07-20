package com.ils.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ils.MainApp;

public class ReadUtil {
    private static final String cte = "WITH summary AS (\r\n";
    private static final String customerName = "case\r\n" + //
        "when co.customername = 'CCL_APAC'\r\n" + //
        "then 'CCLSG'\r\n" + //
        "when co.customername = 'CVN'\r\n" + //
        "then 'UOB VN'\r\n" + //
        "when co.customername in ('TFWSG', 'TFW_APAC')\r\n" + //
        "then 'TFW_APAC'\r\n" + //
        "else co.customername\r\n" + //
        "END as customer, \r\n";
    private static final String dbNameMap = "case\r\n" + //
        "when co.customername = 'AMEX_BAHRAIN' then\r\n" + //
        "    case\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF01' then '01_CLASSIC'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF02' then '02_PAINTER'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF03' then '03_ARCH'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC1' then '01_CLASSIC_UAE'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC2' then '02_PAINTER_UAE'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC3' then '03_ARCH_UAE'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,5) = 'VBW01' then 'PRADA_WEAR'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBM01' then 'PLAT'\r\n" + //
        "    when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUP1' then 'PLAT_UAE'\r\n" + //
        "    else SUBSTR(get_token(pr.productname,1,'_'),9,8)\r\n" + //
        "    end\r\n" + //
        "when co.customername IN ('AMXSG', 'BOASG') then pr.productkey1\r\n" + //
        "when co.customername IN ('SCBSG','SCBBR') then\r\n" + //
        "  case\r\n" + //
        "  when INSTR(get_token(co.externalcustomerorderid,1,'.'), 'SCB') > 0\r\n" + //
        "  then get_token(pr.productalias,'1','_')\r\n" + //
        "  when INSTR(get_token(co.externalcustomerorderid,1,'.'), 'PN') > 0\r\n" + //
        "  then \r\n" + //
        "    case \r\n" + //
        "    when SUBSTR(co.externalcustomerorderid, 1, 3) IN ('MDX', 'PDX', 'EDX')\r\n" + //
        "    then 'MDX/PDX/EDX'\r\n" + //
        "    when SUBSTR(co.externalcustomerorderid, 1, 3) IN ('MDI', 'EDI')\r\n" + //
        "    then 'MDI/EDI'\r\n" + //
        "    else SUBSTR(co.externalcustomerorderid, 1, 3)\r\n" + //
        "    end\r\n" + //
        "  else get_token(co.externalcustomerorderid,1,'.')\r\n" + //
        "  end\r\n" + //
        "when co.customername = 'CCLSG' then\r\n" + //
        "get_token(pr.productalias,'1','_') || '-' || \r\n" + //
        "    case\r\n" + //
        "    when ca.custom ='00010' then 'Net App'\r\n" + //
        "    when ca.custom ='00016' then 'Net App'\r\n" + //
        "    when ca.custom ='00022' then 'Navitas'\r\n" + //
        "    when ca.custom ='00048' then 'Mc Premium'\r\n" + //
        "    when ca.custom ='00049' then 'CNPC Silver'\r\n" + //
        "    when ca.custom ='00050' then 'CNPC Gold'\r\n" + //
        "    when ca.custom ='00052' then 'Honeywell'\r\n" + //
        "    when ca.custom ='00067' then 'Bayer'\r\n" + //
        "    when ca.custom ='00068' then 'AIA'\r\n" + //
        "    when ca.custom ='00069' then 'AIA'\r\n" + //
        "    when ca.custom ='00071' then 'APMM TERMINAL'\r\n" + //
        "    when ca.custom ='00072' then 'APM DAMCO'\r\n" + //
        "    when ca.custom ='00073' then 'APM MAERSK LINE'\r\n" + //
        "    when ca.custom ='00076' then 'APMM TERMINAL'\r\n" + //
        "    when ca.custom ='00077' then 'APM DAMCO'\r\n" + //
        "    when ca.custom ='00078' then 'APM MAERSK LINE'\r\n" + //
        "    when ca.custom ='00079' then 'APM MAERSK TANKER'\r\n" + //
        "    when ca.custom ='00080' then 'APM SVITZER'\r\n" + //
        "    when ca.custom ='00081' then 'APM MAERSK DRILLING'\r\n" + //
        "    when ca.custom ='00082' then 'APM DAMCO'\r\n" + //
        "    when ca.custom ='00083' then 'APM MAERSK LINE'\r\n" + //
        "    when ca.custom ='00084' then 'APM SVITZER'\r\n" + //
        "    when ca.custom ='00085' then 'APM MAERSK DRILLING'\r\n" + //
        "    when ca.custom ='00089' then 'NBC UNIVERSAL'\r\n" + //
        "    when ca.custom ='00104' then 'GENERAL MOTORS'\r\n" + //
        "    when ca.custom ='00106' then 'SAMSUNG TAIWAN'\r\n" + //
        "    when ca.custom ='00107' then 'FONTERRA'\r\n" + //
        "    when ca.custom ='00120' then 'SCHRODERS'\r\n" + //
        "    when ca.custom ='00123' then 'JTI'\r\n" + //
        "    when ca.custom ='00149' then 'WHITE CARD PLASTIC'\r\n" + //
        "    when ca.custom ='00157' then 'ICARE Pcard'\r\n" + //
        "    when ca.custom ='00164' then 'NETFLIX'\r\n" + //
        "    when ca.custom ='00167' then 'PALANTIR'\r\n" + //
        "    else SUBSTR(pr.productkey1,1,4)\r\n" + //
        "    end\r\n" + //
        "else get_token(pr.productalias,'1','_')\r\n" + //
        "end as vaultname, \r\n";
    private static final String woId = "wo.workorderid as workorderid,\r\n";
    private static final String qty = "wo.quantity as qty\r\n";
    private static final String tableNames = "from customerorder co, workorder wo, product pr, part pt,\r\n";
    private static final String cards1 = "(SELECT workorderid, get_token(exportedkeyvalue4,'9',';') as custom FROM card\r\n" + //
            ") ca\r\n";
    private static final String cards2 = "(SELECT workorderid, get_token(exportedkeyvalue4,'9',';') as custom FROM card\r\n" + //
            "UNION\r\n" + //
            "SELECT workorderid, get_token(exportedkeyvalue4, '9', ';') as custom FROM card_arc" + //
            ") ca\r\n";
    private static final String join1 = "where wo.productid = pr.productid\r\n" + //
            "and pr.productkey1 = pt.productkey1\r\n" + //
            "and pr.configurationid = pt.configurationid\r\n" + //
            "and wo.workorderiddisplay = wo.workorderid\r\n" + //
            "and wo.status <> 700\r\n" + //
            "and wo.splitflag <> 1\r\n" + //
            "and wo.testflag <> 1\r\n" + //
            "and wo.customerorderid = co.customerorderid " + //
            "and ca.workorderid = wo.workorderid\r\n" +
            "and to_date(wo.creationdate,'DD/MM/YY') = to_date(";
    private static final String join2 = ", 'DD/MM/YY')\r\n";
    private static final String daily = "and get_token(pr.productname, 5,'_') != 'RNW'\r\n";
    private static final String renewal = "and get_token(pr.productname, 5,'_') = 'RNW'\r\n";

    private static final String union1 = "SELECT\r\n" + //
        "customer, vaultname, SUM(qty) AS qty\r\n" + //
        "FROM (\r\n" + //
        "SELECT DISTINCT\r\n" + //
        "customer, vaultname, workorderid, qty\r\n" + //
        "FROM summary\r\n" + //
        "where customer <> 'CCLSG' or REGEXP_INSTR(vaultname, '[A-Z]+-[A-Z|a-z]+') <= 0\r\n" + //
        ") sub\r\n" + //
        "GROUP BY customer, vaultname\r\n";
    private static final String union2 = "SELECT\r\n" + //
        "customer, vaultname, COUNT(*) AS qty\r\n" + //
        "FROM summary\r\n" + //
        "where customer = 'CCLSG' and REGEXP_instr(vaultname, '[A-Z]+-[A-Z|a-z]+') > 0\r\n" + //
        "GROUP BY customer, vaultname\r\n";
    private static final String order = "ORDER BY customer, vaultname";

    public static ResultSet readCustomers() {
        Connection conn;
        String query = "SELECT * FROM CUSTOMER";
        try {
            conn = Oracle.connect();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not retrieve customers from Oracle database." + e.getMessage());
            return null;
        }
    }

    public static ResultSet readDailyTransfersByDate(LocalDate date) {
        Connection conn;
        String cards;
        if (date.equals(LocalDate.now())) {
            cards = cards1;
        } else {
            cards = cards2;
        }
        String subquery = cte + "SELECT\r\n" + customerName + dbNameMap + woId + qty + tableNames + cards + join1 + "'" + date.format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "'" + join2 + daily + ")\r\n";
        String query = subquery + union1 + "UNION\r\n" + union2 + order;
        try {
            conn = Oracle.connect();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not retrieve transfers from Oracle database." + e.getMessage());
            return null;
        }
    }
    public static ResultSet readRenewalTransfersByDate(LocalDate date) {
        Connection conn;
        String cards;
        if (date.equals(LocalDate.now())) {
            cards = cards1;
        } else {
            cards = cards2;
        }
        String subquery = cte + "SELECT\r\n" + customerName + dbNameMap + woId + qty + tableNames + cards +join1 + "'" + date.format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "'" + join2 + renewal + ")\r\n";
        String query = subquery + union1 + "UNION\r\n" + union2 + order;
        try {
            conn = Oracle.connect();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not retrieve transfers from Oracle database." + e.getMessage());
            return null;
        }
    }
}
