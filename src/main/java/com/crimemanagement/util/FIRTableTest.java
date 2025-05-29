package com.crimemanagement.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FIRTableTest {
    public static void main(String[] args) {
        System.out.println("Testing FIR table schema...");
        
        Connection con = null;
        try {
            // Test connection
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to connect to database");
                return;
            }
            System.out.println("Database connection successful");
            
            // Check FIR table schema
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "FIR", null);
            
            if (tables.next()) {
                System.out.println("FIR table exists");
                
                // Check table structure
                ResultSet columns = metaData.getColumns(null, null, "FIR", null);
                System.out.println("Columns in FIR table:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    String isNullable = columns.getString("IS_NULLABLE");
                    String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
                    
                    System.out.println("  " + columnName + " (" + dataType + ")" +
                                      " Nullable: " + isNullable +
                                      " AutoIncrement: " + isAutoIncrement);
                }
                
                // Count rows
                try (Statement stmt = con.createStatement();
                     ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM FIR")) {
                    if (countRs.next()) {
                        int count = countRs.getInt(1);
                        System.out.println("  Table has " + count + " rows");
                    }
                }
                
                // Check if case_id column exists
                boolean caseIdExists = false;
                columns = metaData.getColumns(null, null, "FIR", "case_id");
                if (columns.next()) {
                    caseIdExists = true;
                    System.out.println("case_id column exists in FIR table");
                } else {
                    System.out.println("case_id column does NOT exist in FIR table");
                    
                    // Add case_id column if it doesn't exist
                    try (Statement stmt = con.createStatement()) {
                        String alterTableSQL = "ALTER TABLE FIR ADD COLUMN case_id INT DEFAULT 1";
                        stmt.executeUpdate(alterTableSQL);
                        System.out.println("Added case_id column to FIR table");
                    }
                }
                
                // Show sample data
                try (Statement stmt = con.createStatement();
                     ResultSet dataRs = stmt.executeQuery("SELECT * FROM FIR LIMIT 5")) {
                    System.out.println("\nSample data from FIR table:");
                    while (dataRs.next()) {
                        StringBuilder row = new StringBuilder();
                        for (int i = 1; i <= dataRs.getMetaData().getColumnCount(); i++) {
                            String columnName = dataRs.getMetaData().getColumnName(i);
                            String value = dataRs.getString(i);
                            row.append(columnName).append(": ").append(value).append(", ");
                        }
                        System.out.println(row.toString());
                    }
                }
                
            } else {
                System.out.println("FIR table does NOT exist");
                
                // Create FIR table if it doesn't exist
                try (Statement stmt = con.createStatement()) {
                    String createTableSQL = "CREATE TABLE FIR (" +
                                          "fir_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                          "description TEXT, " +
                                          "date DATE, " +
                                          "case_id INT DEFAULT 1)";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("Created FIR table");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}