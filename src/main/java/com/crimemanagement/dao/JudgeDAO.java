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

import com.crimemanagement.model.Judge;
import com.crimemanagement.util.DBConnection;

public class JudgeDAO {

    public static List<Judge> getAllJudges() {
        List<Judge> judges = new ArrayList<>();
        String query = "SELECT j.judge_id, j.f_name, j.l_name, j.experience, c.name as court_name " +
                      "FROM Judge j LEFT JOIN Court c ON j.court_id = c.court_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Judge judge = new Judge();
                judge.setId(rs.getInt("judge_id"));
                judge.setFirstName(rs.getString("f_name"));
                judge.setLastName(rs.getString("l_name"));
                judge.setexperience(rs.getInt("experience"));
                judge.setCourt(rs.getString("court_name"));
                judges.add(judge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return judges;
    }

    public static void updateJudge(Judge judge) {
        // First, find the court_id based on the court name
        String findCourtIdQuery = "SELECT court_id FROM Court WHERE name = ?";
        String updateQuery = "UPDATE Judge SET f_name = ?, l_name = ?, experience = ?, court_id = ? WHERE judge_id = ?";
        Connection con = null;
        PreparedStatement findStmt = null;
        PreparedStatement updateStmt = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Begin transaction

            // Find court_id
            int courtId = 0;
            findStmt = con.prepareStatement(findCourtIdQuery);
            findStmt.setString(1, judge.getCourt());
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                courtId = rs.getInt("court_id");
            }
            rs.close();
            findStmt.close();

            // Update judge
            updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setString(1, judge.getFirstName());
            updateStmt.setString(2, judge.getLastName());
            updateStmt.setInt(3, judge.getexperience());
            updateStmt.setInt(4, courtId);
            updateStmt.setInt(5, judge.getId());

            int rows = updateStmt.executeUpdate();

            if (rows == 1) {
                con.commit(); // Commit if update was successful
                System.out.println("Judge updated successfully and committed.");
            } else {
                con.rollback(); // Rollback if update fails
                System.out.println("Update failed. Transaction rolled back.");
            }

        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback(); // Rollback on exception
                    System.out.println("Transaction rolled back due to exception.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (findStmt != null) findStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (con != null) {
                    con.setAutoCommit(true); // Reset autocommit
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

