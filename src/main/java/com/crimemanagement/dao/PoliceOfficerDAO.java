package com.crimemanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crimemanagement.model.PoliceOfficer;
import com.crimemanagement.util.DBConnection;

public class PoliceOfficerDAO {

    /**
     * Fetches all officers along with their station names.
     */
    public static List<PoliceOfficer> getAllPoliceOfficers() {
        List<PoliceOfficer> officers = new ArrayList<>();
        String sql = 
            "SELECT o.`officer_id` AS id, o.`name` AS name, o.`post` AS officer_rank, s.`name` AS station " +
            "FROM `PoliceOfficer` o " +
            "JOIN `PoliceStation` s ON o.`station_id` = s.`station_id`";

        System.out.println("Executing SQL: " + sql);
        
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.err.println("Failed to get database connection");
                return officers;
            }
            
            System.out.println("Database connection established");
            
            try (PreparedStatement stmt = con.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                System.out.println("Query executed successfully");
                int count = 0;
                
                while (rs.next()) {
                    count++;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String rank = rs.getString("officer_rank");
                    String station = rs.getString("station");
                    
                    System.out.println("Retrieved officer: ID=" + id + ", Name=" + name + 
                                      ", Rank=" + rank + ", Station=" + station);
                    
                    PoliceOfficer officer = new PoliceOfficer();
                    officer.setId(id);
                    officer.setName(name);
                    officer.setRank(rank);
                    officer.setStation(station);
                    officers.add(officer);
                }
                
                System.out.println("Total officers retrieved: " + count);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        return officers;
    }
    
    /**
     * Updates a police officer's information.
     * Note: This method updates the officer's name and rank directly.
     * For station updates, it finds the station_id based on the station name.
     */
    public static boolean updatePoliceOfficer(PoliceOfficer officer) {
        // First, get the station_id for the given station name
        String findStationIdSql = "SELECT `station_id` FROM `PoliceStation` WHERE `name` = ?";
        String updateOfficerSql = "UPDATE `PoliceOfficer` SET `name` = ?, `post` = ?, `station_id` = ? WHERE `officer_id` = ?";
        
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            // Find station_id
            int stationId = -1;
            try (PreparedStatement stmt = con.prepareStatement(findStationIdSql)) {
                stmt.setString(1, officer.getStation());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stationId = rs.getInt("station_id");
                    } else {
                        System.err.println("Station not found: " + officer.getStation());
                        return false;
                    }
                }
            }
            
            // Update officer
            try (PreparedStatement stmt = con.prepareStatement(updateOfficerSql)) {
                stmt.setString(1, officer.getName());
                stmt.setString(2, officer.getRank());
                stmt.setInt(3, stationId);
                stmt.setInt(4, officer.getId());
                
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Updated officer ID=" + officer.getId() + ", Rows affected: " + rowsAffected);
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating police officer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
