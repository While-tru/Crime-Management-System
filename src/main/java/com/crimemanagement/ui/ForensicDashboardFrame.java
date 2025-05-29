/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import com.crimemanagement.dao.EvidenceDAO;
import com.crimemanagement.dao.ForensicReportDAO;
import com.crimemanagement.model.Evidence;
import com.crimemanagement.model.ForensicReport;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ForensicDashboardFrame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/crimemanagement";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public ForensicDashboardFrame() {
        setTitle("Forensic Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton uploadReportBtn = new JButton("Upload Forensic Report");
        JButton viewReportsBtn = new JButton("View Forensic Reports");
        JButton runAnalysisBtn = new JButton("Run Forensic Analysis");
        JButton viewEvidenceBtn = new JButton("View All Evidence");
        JButton backBtn = new JButton("Back");

        uploadReportBtn.addActionListener(e -> openUploadReportDialog());
        viewReportsBtn.addActionListener(e -> openReportViewer());
        runAnalysisBtn.addActionListener(e -> runAnalysis());
        viewEvidenceBtn.addActionListener(e -> openEvidenceViewer());
        backBtn.addActionListener(e -> dispose());

        add(uploadReportBtn);
        add(viewReportsBtn);
        add(runAnalysisBtn);
        add(viewEvidenceBtn);
        add(backBtn);
    }

    private void openUploadReportDialog() {
        JTextField evidenceIdField = new JTextField();
        JTextField findingsField = new JTextField();
        JTextField preparedByField = new JTextField();

        Object[] fields = {
                "Evidence ID:", evidenceIdField,
                "Findings:", findingsField,
                "Prepared By:", preparedByField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Upload Report", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                conn.setAutoCommit(false);
                ForensicReportDAO dao = new ForensicReportDAO(conn);
                ForensicReport report = new ForensicReport(0,
                        Integer.parseInt(evidenceIdField.getText()),
                        findingsField.getText(),
                        preparedByField.getText(),
                        LocalDate.now()
                );
                dao.insertReport(report);
                conn.commit();
                JOptionPane.showMessageDialog(this, "Report uploaded successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to upload report.");
            }
        }
    }

    private void openReportViewer() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            ForensicReportDAO dao = new ForensicReportDAO(conn);
            List<ForensicReport> reports = dao.getAllReports();

            StringBuilder sb = new StringBuilder("Forensic Reports:\n\n");
            for (ForensicReport r : reports) {
                sb.append("ID: ").append(r.getReportId())
                        .append(", Evidence ID: ").append(r.getEvidenceId())
                        .append(", By: ").append(r.getPreparedBy())
                        .append(", Findings: ").append(r.getFindings())
                        .append(", Date: ").append(r.getDate())
                        .append("\n\n");
            }

            JTextArea textArea = new JTextArea(sb.toString(), 20, 40);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scrollPane, "Forensic Reports", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading reports.");
        }
    }

    private void openEvidenceViewer() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            EvidenceDAO dao = new EvidenceDAO(conn);
            List<Evidence> evidenceList = dao.getAllEvidence();

            StringBuilder sb = new StringBuilder("Evidence List:\n\n");
            for (Evidence e : evidenceList) {
                sb.append("ID: ").append(e.getEvidenceId())
                        .append(", Type: ").append(e.getType())
                        .append(", Description: ").append(e.getDescription())
                        .append(", Crime ID: ").append(e.getCrimeId())
                        .append("\n\n");
            }

            JTextArea textArea = new JTextArea(sb.toString(), 20, 40);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scrollPane, "Evidence", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading evidence.");
        }
    }

    private void runAnalysis() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            ForensicReportDAO dao = new ForensicReportDAO(conn);
            List<ForensicReport> reports = dao.getAllReports();

            long matchCount = reports.stream()
                    .filter(r -> r.getFindings().toLowerCase().contains("match"))
                    .count();

            JOptionPane.showMessageDialog(this,
                    "Total Reports: " + reports.size() +
                            "\nMatches Found: " + matchCount,
                    "Forensic Analysis Summary",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Analysis failed.");
        }
    }
}

