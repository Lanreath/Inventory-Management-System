package com.ils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class Config {
    private static Properties prop;
    private static FileInputStream ip;
    private static Logger logger;
    private static FileHandler fh;

    public static void init() {
        try {
            prop = new Properties();
            ip = new FileInputStream("database.properties");
            prop.load(ip);

            logger = Logger.getLogger(MainApp.class.getName());
            fh = new FileHandler("database/ILSLogs.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.addHandler(fh);
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    LocalDateTime.now() + ": Could not load database properties file " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Starting ILS...");
    }

    public static String getValue(String property) {
        return prop.getProperty(property);
    }
}
