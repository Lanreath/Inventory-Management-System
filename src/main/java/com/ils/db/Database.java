package com.ils.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.ils.MainApp;
import com.ils.models.Model;

public abstract class Database {
    private static final Properties prop = new Properties();

    private static final Class<?>[] requiredTables = Model.getModels().toArray(Class[]::new);

    private static boolean checkDrivers() {
        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return true;
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, LocalDateTime.now() + ": Could not start SQLite drivers");
            return false;
        }
    }

    public static Connection connect() {
        try {
            FileInputStream ip = new FileInputStream("database/database.properties");
            prop.load(ip);
        } catch (IOException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not load database properties file " + e.getMessage());
            return null;
        }
        String location = prop.getProperty("sqlite.location");
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + location);
            if (conn != null) {
                Logger.getLogger(MainApp.class.getName()).log(Level.FINE,
                        LocalDateTime.now() + ": Connected to SQLite database at " + location);
            }
            return conn;
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not connect to SQLite database at " + location);
            return null;
        }
    }

    private static boolean checkConnection() {
        Connection connection = connect();
        return connection != null;
    }

    private static void checkTables() {
        String checkTables = "SELECT name FROM sqlite_schema WHERE type ='table' AND name NOT LIKE 'sqlite_%'";
        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(checkTables);
            ResultSet rs = statement.executeQuery();
            String[] tblnames = new String[requiredTables.length];
            // If no tables exist, create them
            if (!rs.isBeforeFirst()) {
                Logger.getLogger(MainApp.class.getName()).log(Level.INFO,
                        LocalDateTime.now() + ": Empty database, creating tables...");
                Model.getModels().forEach(CRUDUtil::createTable);
                return;
            }
            // Find all table names in database
            while (rs.next()) {
                tblnames[rs.getRow() - 1] = rs.getString(1);
            }
            if (tblnames[requiredTables.length - 1] == null) {
                Logger.getLogger(MainApp.class.getName()).log(Level.INFO, 
                        LocalDateTime.now() + ": Database is missing tables, creating them...");
            }
            // Find all tables that are missing
            Supplier<Stream<String>> missingTables = () -> Model.getModels().map(Class::getSimpleName).filter(n -> Stream.of(tblnames).noneMatch(n::equals));
            // Create missing tables
            Model.getModels().filter(
                    t -> {
                        String name = t.getSimpleName();
                        return missingTables.get().anyMatch(name::equals);
                    })
                    .forEach(CRUDUtil::createTable);
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not check tables in database." + e.getMessage());
        }
    }

    public static boolean isOK() {
        if (!checkDrivers()) {
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        checkTables();
        return true;
    }

}