package com.ils.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ils.Config;
import com.ils.MainApp;

public abstract class Oracle {
    /**
     *  Check if the Oracle drivers are available.
     * @return boolean
     */
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

    /**
     * Connect to the Oracle database.
     * @return Connection
     */
    protected static Connection connect() {
        String username = Config.getValue("oracle.username");
        String password = Config.getValue("oracle.password");
        String dbURL = Config.getValue("oracle.url");
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:" + dbURL, username, password);
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not connect to Oracle database at " + dbURL);
            return null;
        }
        return connection;
    }

    /**
     * Check if the Oracle connection is available.
     * @return boolean
     */
    private static boolean checkConnection() {
        try (Connection connection = connect()) {
            return connection != null;
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not connect to Oracle database.");
            return false;
        }
    }

    /**
     * Check if Oracle is available.
     * @return boolean
     */
    public static boolean isOK() {
        String offline = Config.getValue("enable_offline");
        if (offline.equals("true")) {
            Logger.getLogger(MainApp.class.getName()).log(Level.INFO,
                    LocalDateTime.now() + ": Oracle is set to offline.");
            return true;
        }
        return checkDrivers() && checkConnection();
    }
}