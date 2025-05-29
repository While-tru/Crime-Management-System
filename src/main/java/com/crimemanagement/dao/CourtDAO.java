/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.dao;

import com.crimemanagement.model.Court;
import com.crimemanagement.util.DBConnection;

import java.sql.*;
import java.util.*;

public class CourtDAO {

    public static List<Court> getAllCourts() {
        List<Court> courts = new ArrayList<>();
        String sql = "SELECT * FROM Court";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Court c = new Court();
                c.setId(rs.getInt("court_id"));
                c.setName(rs.getString("name"));
                c.setLocation(rs.getString("location"));
                c.setVerdict(rs.getString("verdict"));
                courts.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courts;
    }

    public static void updateCourt(Court court) {
        String sql = "UPDATE Court SET name=?, location=?, verdict=? WHERE court_id=?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            ps = conn.prepareStatement(sql);
            ps.setString(1, court.getName());
            ps.setString(2, court.getLocation());
            ps.setString(3, court.getVerdict());
            ps.setInt(4, court.getId());

            int rows = ps.executeUpdate();

            if (rows == 1) {
                conn.commit(); // Commit if successful
                System.out.println("Court updated successfully and committed.");
            } else {
                conn.rollback(); // Rollback if update fails
                System.out.println("Court update failed. Transaction rolled back.");
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on exception
                    System.out.println("Transaction rolled back due to exception.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore default state
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
