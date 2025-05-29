/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crimemanagement.model.Complaint;
import com.crimemanagement.util.DBConnection;

public class ComplaintDAO {
    
    // Check the complaints table schema
    public static void checkComplaintsTableSchema() {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection for schema check");
                return;
            }
            
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "complaints", null);
            
            System.out.println("complaints table columns:");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                System.out.println("Column: " + columnName + ", Type: " + dataType);
            }
            
            columns.close();
        } catch (SQLException e) {
            System.err.println("Error checking complaints table schema: " + e.getMessage());
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
    // Get all complaints
    public static List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT * FROM complaints";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Complaint complaint = new Complaint(
                        rs.getInt("id"),
                        rs.getString("complainant_name"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("case_id"),
                        rs.getString("witness")
                );
                complaints.add(complaint);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return complaints;
    }

    // Update complaint status (or any other fields if needed)
    public static boolean updateComplaint(Complaint complaint) {
        // Check the schema first
        checkComplaintsTableSchema();
        String sql = "UPDATE complaints SET complainant_name = ?, description = ?, status = ?, case_id = ?, witness = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            // Disable auto-commit to start a transaction
            con.setAutoCommit(false);
            
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                // Debug output
                System.out.println("Preparing to update complaint in database:");
                System.out.println("ID: " + complaint.getId());
                System.out.println("Complainant: " + complaint.getComplainantName());
                System.out.println("Description: " + complaint.getDescription());
                System.out.println("Status: " + complaint.getStatus());
                System.out.println("Case ID: " + complaint.getCaseId());
                System.out.println("Witness: " + complaint.getWitness());
                
                stmt.setString(1, complaint.getComplainantName());
                stmt.setString(2, complaint.getDescription());
                stmt.setString(3, complaint.getStatus());
                stmt.setString(4, complaint.getCaseId());
                stmt.setString(5, complaint.getWitness());
                stmt.setInt(6, complaint.getId());
                
                System.out.println("Executing SQL: " + sql);
                System.out.println("With parameters: [" + 
                                  complaint.getComplainantName() + ", " + 
                                  complaint.getDescription() + ", " + 
                                  complaint.getStatus() + ", " + 
                                  complaint.getCaseId() + ", " + 
                                  complaint.getWitness() + ", " + 
                                  complaint.getId() + "]");
    
                int rowsAffected = stmt.executeUpdate();
                
                // Commit the transaction
                con.commit();
                
                System.out.println("Updated complaint ID=" + complaint.getId() + 
                                  ", Rows affected: " + rowsAffected);
                
                return rowsAffected > 0;
            } catch (SQLException e) {
                // Rollback on error
                con.rollback();
                System.err.println("Error updating complaint, transaction rolled back: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                // Reset auto-commit
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        System.out.println("Testing ComplaintDAO...");
        checkComplaintsTableSchema();
        
        // Test getting all complaints
        List<Complaint> complaints = getAllComplaints();
        System.out.println("Found " + complaints.size() + " complaints");
        
        // Test updating a complaint if any exist
        if (!complaints.isEmpty()) {
            Complaint complaint = complaints.get(0);
            System.out.println("Testing update on complaint ID: " + complaint.getId());
            
            // Make a small change to the description
            String originalDesc = complaint.getDescription();
            complaint.setDescription(originalDesc + " (Updated)");
            
            boolean success = updateComplaint(complaint);
            System.out.println("Update " + (success ? "succeeded" : "failed"));
            
            // Restore original description
            complaint.setDescription(originalDesc);
            updateComplaint(complaint);
        }
    }
}
