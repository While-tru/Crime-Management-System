/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.crimemanagement.model.FIR;
import com.crimemanagement.util.DBConnection;

public class FIRDAO {
    // Get all FIRs
    public static List<FIR> getAllFIRs() {
        List<FIR> firs = new ArrayList<>();
        String query = "SELECT * FROM FIR";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = DBConnection.getConnection();
            
            // We don't need to start a transaction for a read-only operation,
            // but we'll ensure auto-commit is enabled for consistency
            if (!con.getAutoCommit()) {
                con.setAutoCommit(true);
            }
            
            stmt = con.prepareStatement(query);
            rs = stmt.executeQuery();
            
            System.out.println("Executing query: " + query);
            
            while (rs.next()) {
                int firId = rs.getInt("fir_id");
                int caseId = rs.getInt("case_id");
                String description = rs.getString("description");
                LocalDate date = rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : LocalDate.now();
                LocalTime time = rs.getTime("time") != null ? rs.getTime("time").toLocalTime() : LocalTime.now();
                
                FIR fir = new FIR(firId, caseId, description, date, time);
                firs.add(fir);
                
                System.out.println("Loaded FIR: ID=" + fir.getId() + 
                                  ", Case ID=" + fir.getCaseId() + 
                                  ", Details=" + fir.getDetails() + 
                                  ", Date=" + fir.getDate() + 
                                  ", Time=" + fir.getTime());
            }
            
            System.out.println("Total FIRs loaded: " + firs.size());
        } catch (SQLException e) {
            System.err.println("Error loading FIRs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return firs;
    }

    // Update FIR
    public static boolean updateFIR(FIR fir) {
        // Debug output to verify the FIR object
        System.out.println("Inserting FIR with:");
        System.out.println("ID: " + fir.getId());
        System.out.println("Description: " + fir.getDescription());
        System.out.println("Details: " + fir.getDetails());
        System.out.println("Date Filed: " + fir.getDateFiled());
        System.out.println("Date: " + fir.getDate());
        System.out.println("Time Filed: " + fir.getTimeFiled());
        System.out.println("Time: " + fir.getTime());
        System.out.println("Case ID: " + fir.getCaseId());
        
        // Use a simpler query without specifying fir_id to let MySQL auto-increment handle it
        String query = "UPDATE FIR SET description = ?, date = ?, time = ?, case_id = ? WHERE fir_id = ?";
        Connection con = null;
        PreparedStatement stmt = null;
        
        try {
            con = DBConnection.getConnection();
            
            // Disable auto-commit to start a transaction - prefer dateFiled over date
            con.setAutoCommit(false);
            
            stmt = con.prepareStatement(query);
            
            // Use the correct getter method based on what's available in the FIR object
            String details = fir.getDetails() != null ? fir.getDetails() : 
                           (fir.getDescription() != null ? fir.getDescription() : "");
            stmt.setString(1, details);
            
            // Use the correct date getter method
            LocalDate date = fir.getDate() != null ? fir.getDate() : 
                           (fir.getDateFiled() != null ? fir.getDateFiled() : LocalDate.now());
            stmt.setDate(2, Date.valueOf(date));
            
            // Use the correct time getter method
            LocalTime time = fir.getTime() != null ? fir.getTime() : 
                           (fir.getTimeFiled() != null ? fir.getTimeFiled() : LocalTime.now());
            stmt.setTime(3, java.sql.Time.valueOf(time));
            
            // Handle case ID - if it's 0 or negative, get the original case ID from the database
            int caseId = fir.getCaseId();
            if (caseId <= 0) {
                FIR existingFir = getFIRById(fir.getId());
                if (existingFir != null) {
                    caseId = existingFir.getCaseId();
                    System.out.println("Using original case ID: " + caseId + " for FIR ID: " + fir.getId());
                } else {
                    // If we couldn't find the FIR in the database, use a default value
                    caseId = 1;
                    System.out.println("Could not find original case ID for FIR ID " + fir.getId() + ". Using default value: " + caseId);
                }
            }
            stmt.setInt(4, caseId);
            stmt.setInt(5, fir.getId());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Commit the transaction if update was successful
                con.commit();
                System.out.println("FIR updated successfully and committed: ID=" + fir.getId() + 
                                  ", Details=" + details + 
                                  ", Date=" + date + 
                                  ", Time=" + time + 
                                  ", Case ID=" + fir.getCaseId());
                return true;
            } else {
                // Rollback if no rows were updated
                con.rollback();
                System.out.println("No FIR found with ID=" + fir.getId() + 
                                  ". This may indicate the FIR doesn't exist in the database. Transaction rolled back.");
                return false;
            }
        } catch (SQLException e) {
            // Rollback on error
            try {
                if (con != null) {
                    con.rollback();
                    System.err.println("Transaction rolled back due to error.");
                }
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            System.err.println("Error updating FIR: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Reset auto-commit and close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Insert new FIR
    public static boolean insertFIR(FIR fir) {
        // Check the database schema
        checkFIRTableSchema();
        
        // Modified query to let the database handle the auto-increment for fir_id
        String query = "INSERT INTO FIR (description, date, time, case_id) VALUES (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            // Disable auto-commit to start a transaction
            con.setAutoCommit(false);
            
            stmt = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            
            // Use the details field from the FIR object
            String description = fir.getDetails();
            if (description == null || description.trim().isEmpty()) {
                description = "No description provided";
            }
            stmt.setString(1, description);
            System.out.println("Setting description: " + description);
            
            // Use the date field from the FIR object
            LocalDate date = fir.getDate();
            if (date == null) {
                date = LocalDate.now();
            }
            stmt.setDate(2, Date.valueOf(date));
            System.out.println("Setting date: " + date);
            
            // Use the time field from the FIR object
            LocalTime time = fir.getTime();
            if (time == null) {
                time = LocalTime.now();
            }
            stmt.setTime(3, java.sql.Time.valueOf(time));
            System.out.println("Setting time: " + time);
            
            // Use the case ID from the FIR object
            int caseId = fir.getCaseId();
            System.out.println("Case ID from FIR object: " + caseId);
            
            // Verify that the case exists in the database
            if (caseId > 0) {
                boolean caseExists = verifyCaseExists(caseId);
                if (!caseExists) {
                    System.out.println("Warning: Case ID " + caseId + " does not exist in the database. Using default case ID 1.");
                    caseId = 1;
                }
            } else {
                caseId = 1; // Default to case ID 1 if not provided or invalid
                System.out.println("Using default case ID: " + caseId);
            }
            
            stmt.setInt(4, caseId);
            System.out.println("Setting case ID: " + caseId);
            
            System.out.println("Executing SQL: " + query + " with params: [" + description + ", " + date + ", " + time + ", " + caseId + "]");
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Get the auto-generated ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    
                    // Commit the transaction if insertion was successful
                    con.commit();
                    System.out.println("New FIR inserted successfully and committed with ID: " + generatedId + 
                                      ", Description: " + description + 
                                      ", Date: " + date + 
                                      ", Time: " + time + 
                                      ", Case ID: " + caseId);
                    success = true;
                } else {
                    // Rollback if we couldn't get the generated ID
                    con.rollback();
                    System.out.println("New FIR inserted but couldn't retrieve the ID. Transaction rolled back.");
                }
                generatedKeys.close();
            } else {
                // Rollback if no rows were inserted
                con.rollback();
                System.out.println("Failed to insert FIR. Transaction rolled back.");
            }
        } catch (SQLException e) {
            // Rollback on error
            try {
                if (con != null) {
                    con.rollback();
                    System.err.println("Transaction rolled back due to error.");
                }
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            System.err.println("Error inserting FIR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Reset auto-commit and close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return success;
    }
    
    // Check if the FIR table has the expected schema
    private static void checkFIRTableSchema() {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection for schema check");
                return;
            }
            
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "FIR", null);
            
            System.out.println("FIR table columns:");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                System.out.println("Column: " + columnName + ", Type: " + dataType);
            }
            
            columns.close();
        } catch (SQLException e) {
            System.err.println("Error checking FIR table schema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    // Verify that a case exists in the database
    private static boolean verifyCaseExists(int caseId) {
        String query = "SELECT case_id FROM casetable WHERE case_id = ?";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection for case verification");
                return false;
            }
            
            stmt = con.prepareStatement(query);
            stmt.setInt(1, caseId);
            rs = stmt.executeQuery();
            
            return rs.next(); // Returns true if the case exists
        } catch (SQLException e) {
            System.err.println("Error verifying case existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    // Note: We no longer need the getNextFirId() method as we're using auto-increment
    
    // Get a specific FIR by ID
    public static FIR getFIRById(int firId) {
        String query = "SELECT * FROM FIR WHERE fir_id = ?";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        FIR fir = null;
        
        try {
            con = DBConnection.getConnection();
            
            // We don't need to start a transaction for a read-only operation
            if (!con.getAutoCommit()) {
                con.setAutoCommit(true);
            }
            
            stmt = con.prepareStatement(query);
            stmt.setInt(1, firId);
            rs = stmt.executeQuery();
            
            System.out.println("Executing query: " + query + " with ID: " + firId);
            
            if (rs.next()) {
                int caseId = rs.getInt("case_id");
                String description = rs.getString("description");
                LocalDate date = rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : LocalDate.now();
                LocalTime time = rs.getTime("time") != null ? rs.getTime("time").toLocalTime() : LocalTime.now();
                
                fir = new FIR(firId, caseId, description, date, time);
                
                System.out.println("Found FIR: ID=" + fir.getId() + 
                                  ", Case ID=" + fir.getCaseId() + 
                                  ", Details=" + fir.getDetails() + 
                                  ", Date=" + fir.getDate() + 
                                  ", Time=" + fir.getTime());
            } else {
                System.out.println("No FIR found with ID: " + firId);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting FIR by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return fir;
    }
}
