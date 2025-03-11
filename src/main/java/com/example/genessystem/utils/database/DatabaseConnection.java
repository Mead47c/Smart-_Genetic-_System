package com.example.genessystem.utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE_PATH = "database/genesystem.db";
    private static Connection conn;

    public static Connection connect() {
        conn = null;
        try {
            String url = "jdbc:sqlite:" + DATABASE_PATH;
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
        }
        return conn;
    }

    public static void disconnect() throws SQLException {
        conn.close();
    }
}

