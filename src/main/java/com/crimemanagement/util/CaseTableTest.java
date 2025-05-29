package com.crimemanagement.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.crimemanagement.dao.CaseDAO;
import com.crimemanagement.model.Case;

public class CaseTableTest {
    public static void main(String[] args) {
        System.out.println("Testing Case table schema and operations...");
        
        Connection con = null;
        try {
            // Test connection
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to connect to database");
                return;
            }
            System.out.println("Database connection successful");
            
            // Check CaseTable schema
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "casetable", null);
            
            if (tables.next()) {
                System.out.println("CaseTable exists");
                
                // Check table structure
                ResultSet columns = metaData.getColumns(null, null, "casetable", null);
                System.out.println("Columns in CaseTable:");
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
                     ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM casetable")) {
                    if (countRs.next()) {
                        int count = countRs.getInt(1);
                        System.out.println("  Table has " + count + " rows");
                    }
                }
                
                // Test case creation
                System.out.println("\nTesting case creation...");
                Case newCase = new Case();
                newCase.setStatus("Open");
                newCase.setDateStarted(LocalDate.now());
                newCase.setCrimeType("Theft");
                newCase.setCrimeLocation("Downtown");
                
                boolean created = CaseDAO.createCase(newCase);
                if (created) {
                    System.out.println("Case created successfully with ID: " + newCase.getId());
                } else {
                    System.err.println("Failed to create case");
                }
                
                // Show sample data
                try (Statement stmt = con.createStatement();
                     ResultSet dataRs = stmt.executeQuery("SELECT * FROM casetable LIMIT 5")) {
                    System.out.println("\nSample data from CaseTable:");
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
                System.err.println("CaseTable does NOT exist - this is a critical error!");
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