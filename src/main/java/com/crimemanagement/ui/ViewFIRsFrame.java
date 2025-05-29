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

import com.crimemanagement.dao.FIRDAO;
import com.crimemanagement.model.FIR;

public class ViewFIRsFrame extends JFrame {
    private JTable firTable;
    private DefaultTableModel tableModel;

    public ViewFIRsFrame() {
        setTitle("View and Update FIRs");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        setLocationRelativeTo(null);

        // Validate database connection
        if (!com.crimemanagement.util.DBConnection.isConnectionValid()) {
            JOptionPane.showMessageDialog(this, 
                "Database connection is not valid. Please check your database settings.",
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        // Table model and JTable
        String[] columns = {"ID", "Case ID", "Details", "Date Filed"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID not editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) {
                    return Integer.class; // ID and Case ID are integers
                } else if (columnIndex == 3) {
                    return LocalDate.class; // Date is LocalDate
                }
                return String.class; // Details is String
            }
        };
        firTable = new JTable(tableModel);
        
        // Set up cell editors for editable columns
        // Custom editor for Case ID that only accepts numbers
        JTextField caseIdField = new JTextField();
        caseIdField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!(Character.isDigit(c) || c == java.awt.event.KeyEvent.VK_BACK_SPACE || c == java.awt.event.KeyEvent.VK_DELETE)) {
                    evt.consume(); // Ignore non-digit characters
                }
            }
        });
        firTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(caseIdField)); // Case ID
        
        firTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField())); // Details
        firTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField())); // Date Filed
        
        // Set left alignment for ID columns
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        firTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer); // FIR ID
        firTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer); // Case ID
        
        add(new JScrollPane(firTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back");
        JButton refreshBtn = new JButton("Refresh");
        JButton saveBtn = new JButton("Save Changes");

        backBtn.addActionListener(e -> {
            dispose(); // Close this frame
        });
        refreshBtn.addActionListener(e -> {
            System.out.println("Refreshing FIR data...");
            loadFIRs();
        });
        saveBtn.addActionListener(e -> {
            System.out.println("Saving FIR changes...");
            updateFIRs();
        });

        buttonPanel.add(backBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        System.out.println("Initial loading of FIR data...");
        loadFIRs();
    }

    private void loadFIRs() {
        tableModel.setRowCount(0);
        List<FIR> firs = FIRDAO.getAllFIRs();
        System.out.println("Loaded " + firs.size() + " FIRs from database");
        
        for (FIR fir : firs) {
            // Get the appropriate values, handling potential nulls
            int id = fir.getId();
            int caseId = fir.getCaseId();
            
            // Use details if available, otherwise use description
            String details = fir.getDetails() != null ? fir.getDetails() : 
                           (fir.getDescription() != null ? fir.getDescription() : "");
            
            // Use date if available, otherwise use dateFiled
            LocalDate date = fir.getDate() != null ? fir.getDate() : 
                           (fir.getDateFiled() != null ? fir.getDateFiled() : LocalDate.now());
            
            tableModel.addRow(new Object[]{
                    id,
                    caseId,
                    details,
                    date
            });
            
            System.out.println("Added row to table: ID=" + id + 
                              ", Case ID=" + caseId + 
                              ", Details=" + details + 
                              ", Date=" + date);
        }
    }

    private void updateFIRs() {
        try {
            // Stop cell editing if any cell is being edited
            if (firTable.isEditing()) {
                firTable.getCellEditor().stopCellEditing();
            }
            
            System.out.println("Starting to update " + tableModel.getRowCount() + " FIRs...");
            
            boolean anyUpdates = false;
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int id = (int) tableModel.getValueAt(i, 0);
                
                // Handle potential ClassCastException for caseId
                Object caseIdObj = tableModel.getValueAt(i, 1);
                int caseId;
                if (caseIdObj instanceof Integer) {
                    caseId = (Integer) caseIdObj;
                } else {
                    try {
                        String caseIdStr = caseIdObj.toString().trim();
                        if (caseIdStr.isEmpty()) {
                            // If the case ID is empty, get the original case ID from the database
                            System.out.println("Case ID is empty for FIR ID " + id + ". Retrieving original case ID from database.");
                            FIR existingFir = FIRDAO.getFIRById(id);
                            if (existingFir != null) {
                                caseId = existingFir.getCaseId();
                                System.out.println("Retrieved original case ID: " + caseId + " for FIR ID: " + id);
                            } else {
                                // If we couldn't find the FIR in the database, use the original value
                                System.err.println("Could not find original case ID for FIR ID " + id + ". Using default value.");
                                caseId = 1;
                            }
                        } else {
                            caseId = Integer.parseInt(caseIdStr);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid case ID format: " + caseIdObj + " for FIR ID " + id + ". Retrieving original case ID.");
                        // Try to get the original case ID from the database
                        FIR existingFir = FIRDAO.getFIRById(id);
                        if (existingFir != null) {
                            caseId = existingFir.getCaseId();
                            System.out.println("Retrieved original case ID: " + caseId + " for FIR ID: " + id);
                        } else {
                            System.err.println("Could not find original case ID for FIR ID " + id + ". Using default value.");
                            caseId = 1;
                        }
                    }
                }
                
                // Get details, ensuring it's not null
                Object detailsObj = tableModel.getValueAt(i, 2);
                String details = detailsObj != null ? detailsObj.toString() : "";
                
                // Handle date conversion
                Object dateObj = tableModel.getValueAt(i, 3);
                LocalDate date;

                if (dateObj instanceof LocalDate) {
                    date = (LocalDate) dateObj;
                } else {
                    try {
                        // Try to parse as LocalDate directly
                        date = LocalDate.parse(dateObj.toString().trim());
                    } catch (Exception e) {
                        // If that fails, use current date and log the error
                        System.err.println("Error parsing date: " + dateObj + ". Using current date instead.");
                        date = LocalDate.now();
                    }
                }

                System.out.println("Updating FIR: ID=" + id + 
                                  ", Case ID=" + caseId + 
                                  ", Details=" + details + 
                                  ", Date=" + date);
                
                FIR fir = new FIR(id, caseId, details, date); 
                
                try {
                    boolean success = FIRDAO.updateFIR(fir);
                    if (success) {
                        anyUpdates = true;
                        System.out.println("Successfully updated FIR ID: " + id);
                    } else {
                        System.err.println("Failed to update FIR ID: " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error updating FIR with ID " + id + ": " + e.getMessage());
                    e.printStackTrace();
                    
                    // Show error but continue with other updates
                    JOptionPane.showMessageDialog(this, 
                        "Error updating FIR with ID " + id + ": " + e.getMessage() + 
                        "\nContinuing with other updates.", 
                        "Update Warning", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }

            if (anyUpdates) {
                JOptionPane.showMessageDialog(this, "FIR records updated successfully.");
                
                // Refresh the table to show updated data
                System.out.println("Refreshing FIR data after updates...");
                loadFIRs();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No FIR records were updated. Please check the console for error messages.", 
                    "Update Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating FIRs: " + e.getMessage(), 
                                         "Update Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error in updateFIRs method: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
