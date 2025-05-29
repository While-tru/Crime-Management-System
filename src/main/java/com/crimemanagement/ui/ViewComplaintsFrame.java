/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.crimemanagement.dao.ComplaintDAO;
import com.crimemanagement.model.Complaint;

public class ViewComplaintsFrame extends JFrame {
    private JTable complaintsTable;
    private DefaultTableModel tableModel;

    public ViewComplaintsFrame() {
        setTitle("View Complaints");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize components
        JPanel panel = new JPanel(new BorderLayout());

        // Table setup with custom model that controls editability
        tableModel = new DefaultTableModel(new Object[]{"ID", "Complainant", "Description", "Status", "Case ID", "Witness"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make all columns except ID editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class; // ID is integer
                }
                return String.class; // Other columns are strings
            }
            
            @Override
            public void setValueAt(Object value, int row, int column) {
                // Debug output
                System.out.println("Setting value at row " + row + ", column " + column + ": " + value);
                super.setValueAt(value, row, column);
            }
        };
        complaintsTable = new JTable(tableModel);
        
        // Set up cell editors for editable columns
        complaintsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField())); // Complainant
        complaintsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField())); // Description
        complaintsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField())); // Status
        complaintsTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField())); // Case ID
        complaintsTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JTextField())); // Witness
        
        // Set left alignment for ID column
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        complaintsTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer); // ID
        
        // Add a listener to detect when editing stops
        complaintsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                System.out.println("Selection changed to row: " + complaintsTable.getSelectedRow());
            }
        });
        
        // Add a listener to detect when a cell is edited
        complaintsTable.addPropertyChangeListener("tableCellEditor", evt -> {
            if (complaintsTable.isEditing()) {
                System.out.println("Started editing at row: " + complaintsTable.getEditingRow() + 
                                  ", column: " + complaintsTable.getEditingColumn());
            } else {
                System.out.println("Stopped editing");
            }
        });

        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load complaints from the database
        loadComplaints();

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        JButton updateSelectedButton = new JButton("Update Selected");
        JButton refreshButton = new JButton("Refresh");

        // Add listeners to buttons
        backButton.addActionListener(e -> {
            dispose(); // Close this frame
        });
        
        updateSelectedButton.addActionListener(e -> {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow != -1) {
                // Make sure to stop any cell editing in progress
                if (complaintsTable.isEditing()) {
                    complaintsTable.getCellEditor().stopCellEditing();
                }
                updateSingleComplaint(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a complaint to update.");
            }
        });

        refreshButton.addActionListener(e -> {
            loadComplaints();
        });

        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateSelectedButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        setVisible(true);
    }

    // Method to load complaints into the table
    private void loadComplaints() {
        List<Complaint> complaints = ComplaintDAO.getAllComplaints();
        tableModel.setRowCount(0); // Clear existing rows
        for (Complaint complaint : complaints) {
            tableModel.addRow(new Object[]{
                    complaint.getId(),
                    complaint.getComplainantName(),
                    complaint.getDescription(),
                    complaint.getStatus(),
                    complaint.getCaseId(),
                    complaint.getWitness()
            });
        }
    }

    // This method is no longer needed as we have separate buttons for updating selected and all complaints
    
    // Update a single complaint
    private void updateSingleComplaint(int rowIndex) {
        try {
            // Convert to model row index in case the table is sorted
            int modelRowIndex = complaintsTable.convertRowIndexToModel(rowIndex);
            
            // Get values from the table
            int id = (int) tableModel.getValueAt(modelRowIndex, 0);
            String complainant = String.valueOf(tableModel.getValueAt(modelRowIndex, 1));
            String description = String.valueOf(tableModel.getValueAt(modelRowIndex, 2));
            String status = String.valueOf(tableModel.getValueAt(modelRowIndex, 3));
            String caseId = String.valueOf(tableModel.getValueAt(modelRowIndex, 4));
            String witness = String.valueOf(tableModel.getValueAt(modelRowIndex, 5));

            // Debug output
            System.out.println("Updating complaint with values:");
            System.out.println("ID: " + id);
            System.out.println("Complainant: " + complainant);
            System.out.println("Description: " + description);
            System.out.println("Status: " + status);
            System.out.println("Case ID: " + caseId);
            System.out.println("Witness: " + witness);

            // Create complaint object with selected data
            Complaint complaint = new Complaint(id, complainant, description, status, caseId, witness);

            // Update complaint in the database
            boolean success = ComplaintDAO.updateComplaint(complaint);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Complaint updated successfully.");
                // Refresh the table to show updated data
                loadComplaints();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update complaint. Please check the console for details.", 
                                             "Update Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating complaint: " + e.getMessage(), 
                                         "Update Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // This method has been removed as we no longer have the "Update All" button

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewComplaintsFrame());
    }
}

