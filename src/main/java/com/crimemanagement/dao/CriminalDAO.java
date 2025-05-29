package com.crimemanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.crimemanagement.model.Criminal;
import com.crimemanagement.util.DBConnection;

public class CriminalDAO {

    public static List<Criminal> getAllCriminals() {
        List<Criminal> criminals = new ArrayList<>();
        String query = "SELECT * FROM criminal";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con != null ? con.prepareStatement(query) : null;
             ResultSet rs = stmt != null ? stmt.executeQuery() : null) {

            if (con == null || stmt == null || rs == null) {
                System.err.println("Database connection or statement is null in getAllCriminals");
                return criminals;
            }

            while (rs.next()) {
                Criminal criminal = new Criminal();
                criminal.setId(rs.getInt("criminal_id"));
                criminal.setFName(rs.getString("f_name"));
                criminal.setLName(rs.getString("l_name"));
                criminal.setAddress(rs.getString("address"));
                criminals.add(criminal);
            }

        } catch (SQLException e) {
            System.err.println("Error in getAllCriminals: " + e.getMessage());
            e.printStackTrace();
        }

        return criminals;
    }

    public static boolean createCriminal(Criminal criminal) {
        String query = "INSERT INTO criminal (f_name, l_name, record_status) VALUES (?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con != null ? con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS) : null) {

            if (con == null || stmt == null) {
                System.err.println("Database connection or statement is null in createCriminal");
                return false;
            }

            // Split the name into first and last name
            String[] nameParts = criminal.getName().split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, criminal.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        criminal.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error in createCriminal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public static boolean updateCriminal(Criminal criminal) {
        String query = "UPDATE criminal SET f_name = ?, l_name = ?, record_status = ? WHERE criminal_id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con != null ? con.prepareStatement(query) : null) {

            if (con == null || stmt == null) {
                System.err.println("Database connection or statement is null in updateCriminal");
                return false;
            }

            // Split the name into first and last name
            String[] nameParts = criminal.getName().split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, criminal.getStatus());
            stmt.setInt(4, criminal.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in updateCriminal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public static boolean deleteCriminal(int id) {
        String query = "DELETE FROM criminal WHERE criminal_id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con != null ? con.prepareStatement(query) : null) {

            if (con == null || stmt == null) {
                System.err.println("Database connection or statement is null in deleteCriminal");
                return false;
            }

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in deleteCriminal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static Criminal getCriminalById(int id) {
        String query = "SELECT * FROM criminal WHERE criminal_id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con != null ? con.prepareStatement(query) : null) {

            if (con == null || stmt == null) {
                System.err.println("Database connection or statement is null in getCriminalById");
                return null;
            }

            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Criminal criminal = new Criminal();
                    criminal.setId(rs.getInt("criminal_id"));
                    
                    // Combine first and last name for the name field
                    String firstName = rs.getString("f_name");
                    String lastName = rs.getString("l_name");
                    String fullName = (firstName != null ? firstName : "") + 
                                     (lastName != null ? " " + lastName : "");
                    criminal.setName(fullName.trim());
                    
                    // Map record_status to status
                    criminal.setStatus(rs.getString("record_status"));
                    
                    // Set other fields with default values since they don't exist in the table
                    criminal.setAddress(""); // This would need to be linked from another table
                    
                 
                    return criminal;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getCriminalById: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}