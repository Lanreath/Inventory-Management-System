package com.ils.oracle;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ils.MainApp;

public abstract class Oracle {
    private static final Properties prop = new Properties();

    private static final String location = "@10.151.40.55:1521:PCMS";

    private static boolean checkDrivers() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            return true;
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not start Oracle drivers " + e.getMessage());
            return false;
        }
    }

    protected static Connection connect() {
        try {
            FileInputStream ip = new FileInputStream("database/oracle.properties");
            prop.load(ip);
        } catch (IOException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not load Oracle properties file " + e.getMessage());
            return null;
        }
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        String dbURL = prop.getProperty("db.url");
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:" + dbURL, username, password);
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not connect to Oracle database at " + location);
            return null;
        }
        return connection;
    }

    private static boolean checkConnection() {
        try (Connection connection = connect()) {
            return connection != null;
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not connect to Oracle database.");
            return false;
        }
    }

    public static boolean isOK() {
        try {
            FileInputStream ip = new FileInputStream("database/oracle.properties");
            prop.load(ip);
        } catch (IOException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not load Oracle properties file " + e.getMessage());
        }
        String offline = prop.getProperty("enable_offline");
        if (offline.equals("true")) {
            Logger.getLogger(MainApp.class.getName()).log(Level.INFO,
                    LocalDateTime.now() + ": Oracle is set to offline.");
            return true;
        }
        return checkDrivers() && checkConnection();
    }

}