package com.crimemanagement.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Testing database connection and tables...");
        
        Connection con = null;
        try {
            // Test connection
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to connect to database");
                return;
            }
            System.out.println("Database connection successful");
            
            // Check if tables exist
            checkTable(con, "casetable");
            checkTable(con, "crime");
            checkTable(con, "criminal");
            
            // If tables don't exist, create them
            createTablesIfNeeded(con);
            
            // Check tables again
            checkTable(con, "casetable");
            checkTable(con, "crime");
            checkTable(con, "criminal");
            
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
    
    private static void checkTable(Connection con, String tableName) throws SQLException {
        String query = "SHOW TABLES LIKE ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Table '" + tableName + "' exists");
                    
                    // Check table structure
                    try (ResultSet columns = con.getMetaData().getColumns(null, null, tableName, null)) {
                        System.out.println("Columns in '" + tableName + "':");
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String dataType = columns.getString("TYPE_NAME");
                            String isNullable = columns.getString("IS_NULLABLE");
                            String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
                            
                            System.out.println("  " + columnName + " (" + dataType + ")" +
                                              " Nullable: " + isNullable +
                                              " AutoIncrement: " + isAutoIncrement);
                        }
                    }
                    
                    // Count rows
                    try (Statement countStmt = con.createStatement();
                         ResultSet countRs = countStmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
                        if (countRs.next()) {
                            int count = countRs.getInt(1);
                            System.out.println("  Table has " + count + " rows");
                        }
                    }
                } else {
                    System.out.println("Table '" + tableName + "' does NOT exist");
                }
            }
        }
    }
    
    private static void createTablesIfNeeded(Connection con) throws SQLException {
        // Create casetable if it doesn't exist
        String createCaseTable = "CREATE TABLE IF NOT EXISTS casetable (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "title VARCHAR(100) DEFAULT 'Untitled Case', " +
                                "description TEXT, " +
                                "status VARCHAR(50) DEFAULT 'Pending', " +
                                "date_started DATE)";
        
        // Create crime table if it doesn't exist
        String createCrimeTable = "CREATE TABLE IF NOT EXISTS crime (" +
                                 "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                 "type VARCHAR(100), " +
                                 "location VARCHAR(255), " +
                                 "case_id INT, " +
                                 "FOREIGN KEY (case_id) REFERENCES casetable(id) ON DELETE CASCADE)";
        
        // Create criminal table if it doesn't exist
        String createCriminalTable = "CREATE TABLE IF NOT EXISTS criminal (" +
                                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                    "name VARCHAR(100) NOT NULL, " +
                                    "crime_type VARCHAR(100), " +
                                    "status VARCHAR(50), " +
                                    "arrest_date DATE, " +
                                    "jail_name VARCHAR(100), " +
                                    "case_ids VARCHAR(255))";
        
        try (Statement stmt = con.createStatement()) {
            // Create tables
            stmt.executeUpdate(createCaseTable);
            System.out.println("Created casetable if it didn't exist");
            
            stmt.executeUpdate(createCrimeTable);
            System.out.println("Created crime table if it didn't exist");
            
            stmt.executeUpdate(createCriminalTable);
            System.out.println("Created criminal table if it didn't exist");
            
            // Insert sample data if tables are empty
            insertSampleDataIfEmpty(con);
        }
    }
    
    private static void insertSampleDataIfEmpty(Connection con) throws SQLException {
        // Check if casetable is empty
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM casetable")) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert sample data into casetable
                String insertCaseData = "INSERT INTO casetable (title, description, status, date_started) VALUES " +
                                       "('Theft at Main Street', 'Investigation of theft at 123 Main Street', 'Open', '2023-01-10'), " +
                                       "('Fraud Case #2023-F-001', 'Corporate fraud investigation', 'Under Investigation', '2023-02-15'), " +
                                       "('Assault at City Park', 'Assault reported at City Park on March 5', 'Closed', '2023-03-05'), " +
                                       "('Robbery at First National Bank', 'Armed robbery investigation', 'Open', '2023-04-20')";
                
                stmt.executeUpdate(insertCaseData);
                System.out.println("Inserted sample data into casetable");
            }
        }
        
        // Check if crime table is empty
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM crime")) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert sample data into crime table
                String insertCrimeData = "INSERT INTO crime (type, location, case_id) VALUES " +
                                        "('Theft', '123 Main Street', 1), " +
                                        "('Fraud', 'ABC Corporation Headquarters', 2), " +
                                        "('Assault', 'City Park, North Entrance', 3), " +
                                        "('Armed Robbery', 'First National Bank, Downtown Branch', 4)";
                
                stmt.executeUpdate(insertCrimeData);
                System.out.println("Inserted sample data into crime table");
            }
        }
        
        // Check if criminal table is empty
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM criminal")) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert sample data into criminal table
                String insertCriminalData = "INSERT INTO criminal (name, crime_type, status, arrest_date, jail_name, case_ids) VALUES " +
                                          "('John Doe', 'Theft', 'In Custody', '2023-01-15', 'Central Jail', '1,3'), " +
                                          "('Jane Smith', 'Fraud', 'Released', '2022-11-20', NULL, '2'), " +
                                          "('Robert Johnson', 'Assault', 'Wanted', NULL, NULL, '4'), " +
                                          "('Michael Brown', 'Robbery', 'Under Trial', '2023-03-10', 'State Prison', '5,6')";
                
                stmt.executeUpdate(insertCriminalData);
                System.out.println("Inserted sample data into criminal table");
            }
        }
    }
}