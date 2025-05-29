/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.crimemanagement.dao.ForensicReportDAO;
import com.crimemanagement.model.ForensicReport;

public class ViewForensicReportsFrame extends JFrame {

    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ViewForensicReportsFrame() {
        setTitle("View Forensic Reports");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Table columns
        String[] columns = {"Report ID", "Evidence ID", "Findings", "Prepared By", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col != 0; // Prevent editing of Report ID
            }
        };

        reportTable = new JTable(tableModel);
        
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
        
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back");
        JButton refreshBtn = new JButton("Refresh");
        JButton saveChangesBtn = new JButton("Save Changes");

        backBtn.addActionListener(e -> {
            dispose(); // Close this frame
        });
        refreshBtn.addActionListener(e -> loadReports());
        saveChangesBtn.addActionListener(e -> updateReports());

        btnPanel.add(backBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(saveChangesBtn);

        add(btnPanel, BorderLayout.SOUTH);

        loadReports();
    }

    private void loadReports() {
        tableModel.setRowCount(0); // Clear existing data
        
        try {
            List<ForensicReport> reports = ForensicReportDAO.getAllReports();
            System.out.println("Loaded " + reports.size() + " forensic reports");
            
            for (ForensicReport report : reports) {
                tableModel.addRow(new Object[]{
                        report.getReportId(),
                        report.getEvidenceId(),
                        report.getFindings(),
                        report.getPreparedBy(),
                        report.getDate()
                });
                System.out.println("Added report: ID=" + report.getReportId() + 
                                  ", Evidence ID=" + report.getEvidenceId() + 
                                  ", Findings=" + report.getFindings());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading forensic reports: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReports() {
        try {
            // Stop cell editing if any cell is being edited
            if (reportTable.isEditing()) {
                reportTable.getCellEditor().stopCellEditing();
            }
            
            boolean allSuccess = true;
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                try {
                    int reportId = (int) tableModel.getValueAt(i, 0);
                    
                    // Handle potential ClassCastException for evidenceId
                    Object evidenceIdObj = tableModel.getValueAt(i, 1);
                    int evidenceId;
                    if (evidenceIdObj instanceof Integer) {
                        evidenceId = (Integer) evidenceIdObj;
                    } else {
                        evidenceId = Integer.parseInt(evidenceIdObj.toString());
                    }
                    
                    String findings = String.valueOf(tableModel.getValueAt(i, 2));
                    String preparedBy = String.valueOf(tableModel.getValueAt(i, 3));
                    
                    // Handle date conversion
                    Object dateObj = tableModel.getValueAt(i, 4);
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
                JOptionPane.showMessageDialog(this, "All reports updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Some reports could not be updated. Check console for details.", 
                    "Update Warning", 
                    JOptionPane.WARNING_MESSAGE);
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
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ViewForensicReportsFrame().setVisible(true);
        });
    }
}