/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crimemanagement.model.ForensicReport;
import com.crimemanagement.util.DBConnection;

public class ForensicReportDAO {

    // Empty constructor
    public ForensicReportDAO() {
    }
    
    // Constructor with connection parameter (for backward compatibility)
    public ForensicReportDAO(Connection conn) {
        // This constructor is kept for backward compatibility
    }

    // Insert Forensic Report
    public static void insertReport(ForensicReport report) {
        String query = "INSERT INTO ForensicReport (report_id, evidence_id, findings, prepared_by, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            stmt.setInt(1, report.getReportId());
            stmt.setInt(2, report.getEvidenceId());
            stmt.setString(3, report.getFindings());
            stmt.setString(4, report.getPreparedBy());
            stmt.setDate(5, Date.valueOf(report.getDate()));
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

    // Update Forensic Report
    public static boolean updateReport(ForensicReport report) {
        String query = "UPDATE ForensicReport SET evidence_id = ?, findings = ?, prepared_by = ?, date = ? WHERE report_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            stmt.setInt(1, report.getEvidenceId());
            stmt.setString(2, report.getFindings());
            stmt.setString(3, report.getPreparedBy());
            stmt.setDate(4, Date.valueOf(report.getDate()));
            stmt.setInt(5, report.getReportId());
            
            int rowsAffected = stmt.executeUpdate();
            con.commit();
            
            System.out.println("Updated forensic report ID=" + report.getReportId() + 
                              ", Evidence ID=" + report.getEvidenceId() + 
                              ", Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection con = DBConnection.getConnection()) {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    // Delete Forensic Report
    public static void deleteReport(int reportId) {
        String query = "DELETE FROM ForensicReport WHERE report_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            stmt.setInt(1, reportId);
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

    // Get All Forensic Reports
    public static List<ForensicReport> getAllReports() {
        List<ForensicReport> reportList = new ArrayList<>();
        String query = "SELECT * FROM ForensicReport";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ForensicReport report = new ForensicReport(
                    rs.getInt("report_id"),
                    rs.getInt("evidence_id"),
                    rs.getString("findings"),
                    rs.getString("prepared_by"),
                    rs.getDate("date").toLocalDate()
                );
                reportList.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportList;
    }
}
