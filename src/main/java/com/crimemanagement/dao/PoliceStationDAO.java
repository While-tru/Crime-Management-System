/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crimemanagement.model.PoliceStation;
import com.crimemanagement.util.DBConnection;


public class PoliceStationDAO {

    public static List<PoliceStation> getAllPoliceStation() {
        List<PoliceStation> stations = new ArrayList<>();
        String query = "SELECT * FROM `PoliceStation`";
        
        System.out.println("Attempting to get all police stations...");
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Get connection and print status
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("ERROR: Database connection is null");
                return stations;
            }
            System.out.println("Database connection established successfully");
            
            // Prepare statement
            stmt = con.prepareStatement(query);
            System.out.println("SQL Query: " + query);
            
            // Execute query
            rs = stmt.executeQuery();
            System.out.println("Query executed successfully");
            
            // Process results
            int count = 0;
            while (rs.next()) {
                PoliceStation station = new PoliceStation();
                station.setStationId(rs.getInt("station_id"));
                station.setName(rs.getString("name"));
                station.setLocation(rs.getString("location"));
                station.setContact(rs.getString("contact"));
                stations.add(station);
                count++;
            }
            System.out.println("Processed " + count + " police stations from database");
            
        } catch (SQLException e) {
            System.err.println("ERROR in getAllPoliceStation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources in reverse order
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Don't close connection here as it's managed by DBConnection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return stations;
    }
    
    public static boolean updatePoliceStation(PoliceStation station) {
        String query = "UPDATE `PoliceStation` SET `name` = ?, `location` = ?, `contact` = ? WHERE `station_id` = ?";
        
        System.out.println("Attempting to update police station with ID: " + station.getStationId());
        
        Connection con = null;
        PreparedStatement stmt = null;
        
        try {
            // Get connection
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("ERROR: Database connection is null");
                return false;
            }
            
            // Prepare statement
            stmt = con.prepareStatement(query);
            stmt.setString(1, station.getName());
            stmt.setString(2, station.getLocation());
            stmt.setString(3, station.getContact());
            stmt.setInt(4, station.getStationId());
            
            System.out.println("Executing update query: " + query);
            System.out.println("Parameters: name=" + station.getName() + 
                              ", location=" + station.getLocation() + 
                              ", contact=" + station.getContact() + 
                              ", id=" + station.getStationId());
            
            // Execute update
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Update completed. Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR updating police station: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                // Don't close connection here as it's managed by DBConnection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}

