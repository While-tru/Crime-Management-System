/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.crimemanagement.dao.EvidenceDAO;
import com.crimemanagement.dao.ForensicReportDAO;
import com.crimemanagement.model.Evidence;
import com.crimemanagement.model.ForensicReport;
import com.crimemanagement.util.DBConnection;

public class ViewForensicDetailsFrame extends JFrame {

    private JTable reportTable;
    private JTable evidenceTable;
    private DefaultTableModel reportTableModel;
    private DefaultTableModel evidenceTableModel;
    private JTabbedPane tabbedPane;

    public ViewForensicDetailsFrame() {
        setTitle("Forensic Details");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create Forensic Reports panel
        JPanel reportsPanel = createReportsPanel();
        tabbedPane.addTab("Forensic Reports", reportsPanel);
        
        // Create Evidence panel
        JPanel evidencePanel = createEvidencePanel();
        tabbedPane.addTab("Evidence", evidencePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Close");
        JButton refreshBtn = new JButton("Refresh");
        
        backBtn.addActionListener(e -> {
            dispose(); // Close this frame
        });
        
        refreshBtn.addActionListener(e -> {
            loadReports();
            loadEvidence();
        });
        
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);
        
        // Load data
        loadReports();
        loadEvidence();
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table columns
        String[] columns = {"Report ID", "Evidence ID", "Findings", "Prepared By", "Date"};
        reportTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col != 0; // Prevent editing of Report ID
            }
        };

        reportTable = new JTable(reportTableModel);
        
        // Set up cell editors for editable columns
        reportTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField())); // Evidence ID
        reportTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField())); // Findings
        reportTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField())); // Prepared By
        reportTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField())); // Date
        
        // Set left alignment for ID columns
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        reportTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer); // Report ID
        reportTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer); // Evidence ID
        
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        
        // Add save button for reports
        JButton saveReportsBtn = new JButton("Save Report Changes");
        saveReportsBtn.addActionListener(e -> updateReports());
        
        JPanel reportBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        reportBtnPanel.add(saveReportsBtn);
        panel.add(reportBtnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEvidencePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table columns
        String[] columns = {"Evidence ID", "Type", "Description", "Data Collected", "Crime ID"};
        evidenceTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col != 0; // Prevent editing of Evidence ID
            }
        };

        evidenceTable = new JTable(evidenceTableModel);
        
        // Set up cell editors for editable columns
        evidenceTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField())); // Type
        evidenceTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField())); // Description
        evidenceTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField())); // Data Collected
        evidenceTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField())); // Crime ID
        
        // Set left alignment for ID columns
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        evidenceTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer); // Evidence ID
        evidenceTable.getColumnModel().getColumn(4).setCellRenderer(leftRenderer); // Crime ID
        
        panel.add(new JScrollPane(evidenceTable), BorderLayout.CENTER);
        
        // Add save button for evidence
        JButton saveEvidenceBtn = new JButton("Save Evidence Changes");
        saveEvidenceBtn.addActionListener(e -> updateEvidence());
        
        JPanel evidenceBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        evidenceBtnPanel.add(saveEvidenceBtn);
        panel.add(evidenceBtnPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadReports() {
        reportTableModel.setRowCount(0); // Clear existing data
        try (Connection conn = DBConnection.getConnection()) {
            List<ForensicReport> reports = ForensicReportDAO.getAllReports();

            for (ForensicReport report : reports) {
                reportTableModel.addRow(new Object[]{
                        report.getReportId(),
                        report.getEvidenceId(),
                        report.getFindings(),
                        report.getPreparedBy(),
                        report.getDate()
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading forensic reports.");
        }
    }
    
    private void loadEvidence() {
        evidenceTableModel.setRowCount(0); // Clear existing data
        try {
            List<Evidence> evidenceList = EvidenceDAO.getAllEvidence();

            for (Evidence evidence : evidenceList) {
                evidenceTableModel.addRow(new Object[]{
                        evidence.getEvidenceId(),
                        evidence.getType(),
                        evidence.getDescription(),
                        evidence.getDataCollected(),
                        evidence.getCrimeId()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading evidence data.");
        }
    }

    private void updateReports() {
        try {
            // Stop cell editing if any cell is being edited
            if (reportTable.isEditing()) {
                reportTable.getCellEditor().stopCellEditing();
            }
            
            boolean allSuccess = true;
            
            for (int i = 0; i < reportTableModel.getRowCount(); i++) {
                try {
                    int reportId = (int) reportTableModel.getValueAt(i, 0);
                    
                    // Handle potential ClassCastException for evidenceId
                    Object evidenceIdObj = reportTableModel.getValueAt(i, 1);
                    int evidenceId;
                    if (evidenceIdObj instanceof Integer) {
                        evidenceId = (Integer) evidenceIdObj;
                    } else {
                        evidenceId = Integer.parseInt(evidenceIdObj.toString());
                    }
                    
                    String findings = String.valueOf(reportTableModel.getValueAt(i, 2));
                    String preparedBy = String.valueOf(reportTableModel.getValueAt(i, 3));
                    
                    // Handle date conversion
                    Object dateObj = reportTableModel.getValueAt(i, 4);
                    LocalDate date;
                    if (dateObj instanceof LocalDate) {
                        date = (LocalDate) dateObj;
                    } else {
                        try {
                            date = LocalDate.parse(dateObj.toString());
                        } catch (Exception e) {
                            System.err.println("Error parsing date: " + dateObj + ". Using current date.");
                            date = LocalDate.now();
                        }
                    }

                    ForensicReport report = new ForensicReport(reportId, evidenceId, findings, preparedBy, date);
                    boolean success = ForensicReportDAO.updateReport(report);
                    if (!success) {
                        allSuccess = false;
                        System.err.println("Failed to update report ID: " + reportId);
                    } else {
                        System.out.println("Successfully updated report ID: " + reportId);
                    }
                } catch (Exception e) {
                    allSuccess = false;
                    System.err.println("Error updating report at row " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (allSuccess) {
                JOptionPane.showMessageDialog(this, "Reports updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Some reports could not be updated.", "Update Warning", JOptionPane.WARNING_MESSAGE);
            }
            
            // Refresh the table to show updated data
            loadReports();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating reports: " + e.getMessage(), 
                "Update Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateEvidence() {
        try {
            // Stop cell editing if any cell is being edited
            if (evidenceTable.isEditing()) {
                evidenceTable.getCellEditor().stopCellEditing();
            }
            
            for (int i = 0; i < evidenceTableModel.getRowCount(); i++) {
                try {
                    int evidenceId = (int) evidenceTableModel.getValueAt(i, 0);
                    String type = String.valueOf(evidenceTableModel.getValueAt(i, 1));
                    String description = String.valueOf(evidenceTableModel.getValueAt(i, 2));
                    String dataCollected = String.valueOf(evidenceTableModel.getValueAt(i, 3));
                    
                    // Handle potential ClassCastException for crimeId
                    Object crimeIdObj = evidenceTableModel.getValueAt(i, 4);
                    int crimeId;
                    if (crimeIdObj instanceof Integer) {
                        crimeId = (Integer) crimeIdObj;
                    } else {
                        crimeId = Integer.parseInt(crimeIdObj.toString());
                    }

                    Evidence evidence = new Evidence(evidenceId, type, description, dataCollected, crimeId);
                    EvidenceDAO.updateEvidence(evidence);
                    
                } catch (Exception e) {
                    System.err.println("Error updating evidence at row " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            JOptionPane.showMessageDialog(this, "Evidence data updated successfully.");
            
            // Refresh the table to show updated data
            loadEvidence();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating evidence data: " + e.getMessage(), 
                "Update Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}