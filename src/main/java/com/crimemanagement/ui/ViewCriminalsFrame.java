package com.crimemanagement.ui;

import com.crimemanagement.dao.CriminalDAO;
import com.crimemanagement.dao.JailDAO;
import com.crimemanagement.model.Criminal;
import com.crimemanagement.model.Jail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ViewCriminalsFrame extends JFrame {
    private JTable criminalsTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private JTextField searchField;
    private JButton searchButton;

    public ViewCriminalsFrame() {
        setTitle("Manage Criminals");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        // Create the table model with column names
        String[] columnNames = {"ID", "Name", "Crime Type", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        criminalsTable = new JTable(tableModel);
        criminalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(criminalsTable);
        
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add New Criminal");
        editButton = new JButton("Edit Criminal");
        deleteButton = new JButton("Delete Criminal");
        refreshButton = new JButton("Refresh");
        backButton = new JButton("Back");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        // Add components to the frame
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCriminalDialog(null); // null indicates a new criminal
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = criminalsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int criminalId = (int) tableModel.getValueAt(selectedRow, 0);
                    Criminal selectedCriminal = CriminalDAO.getCriminalById(criminalId);
                    if (selectedCriminal != null) {
                        openCriminalDialog(selectedCriminal);
                    }
                } else {
                    JOptionPane.showMessageDialog(ViewCriminalsFrame.this, 
                        "Please select a criminal to edit.", 
                        "No Selection", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = criminalsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int criminalId = (int) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(
                        ViewCriminalsFrame.this,
                        "Are you sure you want to delete this criminal record? This action cannot be undone.",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = CriminalDAO.deleteCriminal(criminalId);
                        if (deleted) {
                            JOptionPane.showMessageDialog(ViewCriminalsFrame.this,
                                "Criminal record deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(ViewCriminalsFrame.this,
                                "Failed to delete criminal record. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                        loadCriminals();
                    }
                } else {
                    JOptionPane.showMessageDialog(ViewCriminalsFrame.this, 
                        "Please select a criminal to delete.", 
                        "No Selection", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCriminals();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close this frame
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim().toLowerCase();
                if (searchTerm.isEmpty()) {
                    loadCriminals(); // If search field is empty, load all criminals
                } else {
                    filterCriminals(searchTerm);
                }
            }
        });
        
        // Load criminals when the frame is created
        loadCriminals();
    }
    
    private void loadCriminals() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all criminals from the database
        List<Criminal> criminals = CriminalDAO.getAllCriminals();
        
        // Add criminals to the table
        for (Criminal c : criminals) {
            Object[] row = {
                c.getId(),
                c.getName() != null ? c.getName() : "",
                c.getAddress() != null ? c.getAddress() : "",
                c.getStatus() != null ? c.getStatus() : "",
                
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterCriminals(String searchTerm) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all criminals from the database
        List<Criminal> criminals = CriminalDAO.getAllCriminals();
        
        // Add filtered criminals to the table
        for (Criminal c : criminals) {
            // Check if any field contains the search term
            boolean matches = false;
            
            if (c.getName() != null && c.getName().toLowerCase().contains(searchTerm)) {
                matches = true;
            } else if (c.getAddress() != null && c.getAddress().toLowerCase().contains(searchTerm)) {
                matches = true;
            } else if (c.getStatus() != null && c.getStatus().toLowerCase().contains(searchTerm)) {
                matches = true;
           
            }
            
            if (matches) {
                Object[] row = {
                    c.getId(),
                    c.getName() != null ? c.getName() : "",
                    c.getAddress() != null ? c.getAddress() : "",
                    c.getStatus() != null ? c.getStatus() : "",
                    
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void openCriminalDialog(Criminal criminalToEdit) {
        JDialog dialog = new JDialog(this, criminalToEdit == null ? "Add New Criminal" : "Edit Criminal", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Simplified panel with only the fields that match the database
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JTextField nameField = new JTextField();
        
        String[] statusOptions = {"In Custody", "Released", "Wanted", "Under Trial"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
       
        
        // If editing an existing criminal, populate the fields
        if (criminalToEdit != null) {
            nameField.setText(criminalToEdit.getName());
            statusCombo.setSelectedItem(criminalToEdit.getStatus());
        }
        
        // Add components to panel
        panel.add(new JLabel("Name (First Last):"));
        panel.add(nameField);
        
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        
        // Add note about database limitations
        JLabel noteLabel = new JLabel("<html><b>Note:</b> Only name and status are stored in the database.</html>");
        panel.add(noteLabel);
        
        // Add buttons
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate input
                    String name = nameField.getText().trim();
                    String status = (String) statusCombo.getSelectedItem();
                    
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Please enter a name for the criminal.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    
                    
                    // Create or update the criminal
                    Criminal criminalObj;
                    if (criminalToEdit == null) {
                        // Create new criminal
                        criminalObj = new Criminal();
                        System.out.println("Creating new criminal");
                    } else {
                        // Update existing criminal
                        criminalObj = criminalToEdit;
                        System.out.println("Updating existing criminal with ID: " + criminalObj.getId());
                    }
                    
                    criminalObj.setName(name);
                   
                   
                    
                    // Debug output
                    System.out.println("Criminal object prepared for saving:");
                    System.out.println("  ID: " + (criminalToEdit != null ? criminalObj.getId() : "New"));
                    System.out.println("  Name: " + criminalObj.getName());
                    System.out.println("  Status: " + criminalObj.getStatus());
                    
                    // Save to database
                    boolean success;
                    if (criminalToEdit == null) {
                        // Create new criminal
                        success = CriminalDAO.createCriminal(criminalObj);
                        if (success) {
                            JOptionPane.showMessageDialog(dialog,
                                "Criminal record created successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "Failed to create criminal record. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // Update existing criminal
                        success = CriminalDAO.updateCriminal(criminalObj);
                        if (success) {
                            JOptionPane.showMessageDialog(dialog,
                                "Criminal record updated successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "Failed to update criminal record. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    
                    if (success) {
                        // Refresh the table
                        loadCriminals();
                        
                        // Close the dialog
                        dialog.dispose();
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        // Add panels to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ViewCriminalsFrame().setVisible(true);
        });
    }
}