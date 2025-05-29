package com.crimemanagement.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("Failed to connect to database");
            return;
        }
        
        try {
            // List all tables in the database
            System.out.println("Listing all tables in the database:");
            try (PreparedStatement stmt = con.prepareStatement("SHOW TABLES");
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    System.out.println("Table: " + tableName);
                    
                    // Show table structure
                    try (PreparedStatement descStmt = con.prepareStatement("DESCRIBE " + tableName);
                         ResultSet descRs = descStmt.executeQuery()) {
                        
                        System.out.println("  Structure of " + tableName + ":");
                        while (descRs.next()) {
                            String field = descRs.getString("Field");
                            String type = descRs.getString("Type");
                            String key = descRs.getString("Key");
                            String extra = descRs.getString("Extra");
                            
                            System.out.println("    " + field + " (" + type + ") " + 
                                              (key != null && !key.isEmpty() ? "Key: " + key : "") + " " + 
                                              (extra != null && !extra.isEmpty() ? extra : ""));
                        }
                    }
                    
                    System.out.println();
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection();
        }
    }
}