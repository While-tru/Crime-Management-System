/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/CrimeManagementNew?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "vinod123#"; // Replace with actual password if needed
    private static Connection connection;

    public static Connection getConnection() {
        try {
            // Always create a new connection for better isolation between operations
            // This helps prevent issues with transaction management across different operations
            if (connection != null && !connection.isClosed()) {
                try {
                    connection.close();
                    System.out.println("Closed existing database connection");
                } catch (SQLException e) {
                    System.err.println("Error closing existing connection: " + e.getMessage());
                }
                connection = null;
            }
            
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                connection.setAutoCommit(true); // Ensure auto-commit is enabled by default
                System.out.println("Database connection established successfully!");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                    "MySQL JDBC Driver not found. Please add the MySQL Connector JAR to your project.\n" +
                    "Download from: https://dev.mysql.com/downloads/connector/j/",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return null;
            } catch (SQLException e) {
                String errorMessage = "Failed to connect to database.\n\n" +
                    "Please check:\n" +
                    "1. MySQL server is running\n" +
                    "2. Database 'CrimeManagementNew' exists\n" +
                    "3. Username and password are correct\n\n" +
                    "Error details: " + e.getMessage();
                JOptionPane.showMessageDialog(null,
                    errorMessage,
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return null;
            }
            
            return connection;
        } catch (SQLException e) {
            System.err.println("Error in getConnection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnectionValid() {
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("Connection is null - cannot validate");
                return false;
            }
            
            boolean isValid = !conn.isClosed() && conn.isValid(5);
            System.out.println("Database connection is " + (isValid ? "valid" : "invalid"));
            
            // Test a simple query to ensure the connection works
            try (PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Database connection test query successful");
                }
            }
            
            return isValid;
        } catch (SQLException e) {
            System.err.println("Error validating connection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static void verifyDatabaseSchema() {
        try {
            Connection con = getConnection();
            if (con == null) {
                System.err.println("Cannot verify database schema: Connection is null");
                return;
            }
            
            // Check if PoliceStation table exists
            try (PreparedStatement stmt = con.prepareStatement("SHOW TABLES LIKE 'PoliceStation'");
                 ResultSet rs = stmt.executeQuery()) {
                
            // Check if FIR table exists and has auto_increment
            try (PreparedStatement firStmt = con.prepareStatement("SHOW TABLES LIKE 'FIR'");
                 ResultSet firRs = firStmt.executeQuery()) {
                
                if (firRs.next()) {
                    System.out.println("FIR table exists");
                    
                    // Check if fir_id is auto_increment
                    try (PreparedStatement descStmt = con.prepareStatement("DESCRIBE FIR");
                         ResultSet descRs = descStmt.executeQuery()) {
                        
                        boolean hasAutoIncrement = false;
                        System.out.println("FIR table structure:");
                        while (descRs.next()) {
                            String field = descRs.getString("Field");
                            String type = descRs.getString("Type");
                            String extra = descRs.getString("Extra");
                            System.out.println("  " + field + " (" + type + ") " + extra);
                            
                            if (field.equals("fir_id") && extra.contains("auto_increment")) {
                                hasAutoIncrement = true;
                            }
                        }
                        
                        if (!hasAutoIncrement) {
                            System.out.println("FIR table does not have auto_increment for fir_id. Attempting to modify...");
                            try (PreparedStatement alterStmt = con.prepareStatement(
                                    "ALTER TABLE FIR MODIFY fir_id INT AUTO_INCREMENT")) {
                                alterStmt.executeUpdate();
                                System.out.println("Successfully added auto_increment to fir_id in FIR table");
                            } catch (SQLException e) {
                                System.err.println("Error modifying FIR table: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    System.err.println("FIR table does NOT exist!");
                }
            }
            
                if (rs.next()) {
                    System.out.println("PoliceStation table exists");
                    
                    // Check if table has data
                    try (PreparedStatement countStmt = con.prepareStatement("SELECT COUNT(*) FROM PoliceStation");
                         ResultSet countRs = countStmt.executeQuery()) {
                        
                        if (countRs.next()) {
                            int count = countRs.getInt(1);
                            System.out.println("PoliceStation table has " + count + " records");
                        }
                    }
                    
                    // Check table structure
                    try (PreparedStatement descStmt = con.prepareStatement("DESCRIBE PoliceStation");
                         ResultSet descRs = descStmt.executeQuery()) {
                        
                        System.out.println("PoliceStation table structure:");
                        while (descRs.next()) {
                            System.out.println("  " + descRs.getString("Field") + 
                                              " (" + descRs.getString("Type") + ")");
                        }
                    }
                } else {
                    System.err.println("PoliceStation table does NOT exist!");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error verifying database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
