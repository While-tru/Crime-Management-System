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

import com.crimemanagement.model.Evidence;
import com.crimemanagement.util.DBConnection;

public class EvidenceDAO {

    // Empty constructor
    public EvidenceDAO() {
    }
    
    // Constructor with connection parameter (for backward compatibility)
    public EvidenceDAO(Connection conn) {
        // This constructor is kept for backward compatibility
    }

    // Insert Evidence
    public static void insertEvidence(Evidence evidence) {
        String query = "INSERT INTO Evidence (evidence_id, type, description, data_collected, crime_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            stmt.setInt(1, evidence.getEvidenceId());
            stmt.setString(2, evidence.getType());
            stmt.setString(3, evidence.getDescription());
            stmt.setString(4, evidence.getDataCollected());
            stmt.setInt(5, evidence.getCrimeId());
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection con = DBConnection.getConnection()) {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Update Evidence
    public static void updateEvidence(Evidence evidence) {
        String query = "UPDATE Evidence SET type = ?, description = ?, data_collected = ?, crime_id = ? WHERE evidence_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            stmt.setString(1, evidence.getType());
            stmt.setString(2, evidence.getDescription());
            stmt.setString(3, evidence.getDataCollected());
            stmt.setInt(4, evidence.getCrimeId());
            stmt.setInt(5, evidence.getEvidenceId());
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection con = DBConnection.getConnection()) {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Delete Evidence
    public static void deleteEvidence(int evidenceId) {
        String query = "DELETE FROM Evidence WHERE evidence_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            stmt.setInt(1, evidenceId);
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection con = DBConnection.getConnection()) {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Get All Evidence
    public static List<Evidence> getAllEvidence() {
        List<Evidence> evidenceList = new ArrayList<>();
        String query = "SELECT * FROM Evidence";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Evidence evidence = new Evidence(
                    rs.getInt("evidence_id"),
                    rs.getString("type"),
                    rs.getString("description"),
                    rs.getString("data_collected"),
                    rs.getInt("crime_id")
                );
                evidenceList.add(evidence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evidenceList;
    }
}

