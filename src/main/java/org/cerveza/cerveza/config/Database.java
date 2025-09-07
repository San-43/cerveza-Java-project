package org.cerveza.cerveza.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    private static final Properties PROPS = new Properties();


    static {
        try (InputStream is = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) throw new IllegalStateException("No se encontró application.properties");
            PROPS.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando configuración DB", e);
        }
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("jdbc.url"),
                PROPS.getProperty("jdbc.user"),
                PROPS.getProperty("jdbc.password")
        );
    }
}
