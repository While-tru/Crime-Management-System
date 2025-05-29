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

import com.crimemanagement.model.Jail;
import com.crimemanagement.util.DBConnection;

public class JailDAO {

    public static List<Jail> getAllJails() {
        List<Jail> jails = new ArrayList<>();
        String query = "SELECT * FROM `Jail`";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Jail jail = new Jail();
                jail.setJailId(rs.getInt("jail_id"));
                jail.setName(rs.getString("name"));
                jail.setLocation(rs.getString("location"));
                jail.setCapacity(rs.getInt("capacity"));
                jails.add(jail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jails;
    }
    
    public static boolean updateJail(Jail jail) {
        String query = "UPDATE `Jail` SET `name` = ?, `location` = ?, `capacity` = ? WHERE `jail_id` = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            
            stmt.setString(1, jail.getName());
            stmt.setString(2, jail.getLocation());
            stmt.setInt(3, jail.getCapacity());
            stmt.setInt(4, jail.getJailId());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Updated jail ID=" + jail.getJailId() + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating jail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

