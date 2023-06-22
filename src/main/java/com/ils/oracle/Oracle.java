package com.ils.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Oracle {
    
    private static final String location = "@10.151.40.55:1521:PCMS";

    private static boolean checkDrivers() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            return true;
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not start Oracle drivers " + e.getMessage());
            return false;
        }
    }

    protected static Connection connect(String username, String password) {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:" + location, username, password);
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not connect to Oracle database at " + location);
            return null;
        }
        return connection;
    }

    private static boolean checkConnection(String username, String password) {
        try (Connection connection = connect(username, password)) {
            return connection != null;
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not connect to Oracle database.");
            return false;
        }
    }
    public static boolean isOK(String username, String password){
        return checkDrivers() && checkConnection(username, password);
    }

}