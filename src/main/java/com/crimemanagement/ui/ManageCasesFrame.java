package com.crimemanagement.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

import com.crimemanagement.dao.CaseDAO;
import com.crimemanagement.model.Case;

public class ManageCasesFrame extends JFrame {
    private JTable casesTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JTextField searchField;
    private JButton searchButton;

    public ManageCasesFrame() {
        setTitle("Manage Cases");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model with column names
        String[] columnNames = {"ID", "Title", "Description", "Status", "Date Started", "Crime Type", "Location"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        casesTable = new JTable(tableModel);
        casesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(casesTable);
        
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add New Case");
        editButton = new JButton("Edit Case");
        deleteButton = new JButton("Delete Case");
        refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        // Add action listener for back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close this frame
            }
        });
        
        // Add components to the frame
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCaseDialog(null); // null indicates a new case
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = casesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int caseId = (int) tableModel.getValueAt(selectedRow, 0);
                    Case selectedCase = getCaseById(caseId);
                    if (selectedCase != null) {
                        openCaseDialog(selectedCase);
                    }
                } else {
                    JOptionPane.showMessageDialog(ManageCasesFrame.this, 
                        "Please select a case to edit.", 
                        "No Selection", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = casesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int caseId = (int) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(
                        ManageCasesFrame.this,
                        "Are you sure you want to delete this case? This action cannot be undone.",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = CaseDAO.deleteCase(caseId);
                        if (deleted) {
                            JOptionPane.showMessageDialog(ManageCasesFrame.this,
                                "Case deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(ManageCasesFrame.this,
                                "Failed to delete case. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                        loadCases();
                    }
                } else {
                    JOptionPane.showMessageDialog(ManageCasesFrame.this, 
                        "Please select a case to delete.", 
                        "No Selection", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCases();
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim().toLowerCase();
                if (searchTerm.isEmpty()) {
                    loadCases(); // If search field is empty, load all cases
                } else {
                    filterCases(searchTerm);
                }
            }
        });
        
        // Load cases when the frame is created
        loadCases();
    }
    
    private void loadCases() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all cases from the database
        List<Case> cases = CaseDAO.getCasesWithCrime();
        
        // Add cases to the table
        for (Case c : cases) {
            Object[] row = {
                c.getId(),
                c.getTitle() != null ? c.getTitle() : "",
                c.getDescription() != null ? c.getDescription() : "",
                c.getStatus() != null ? c.getStatus() : "",
                c.getDateStarted() != null ? c.getDateStarted().toString() : "",
                c.getCrimeType() != null ? c.getCrimeType() : "",
                c.getCrimeLocation() != null ? c.getCrimeLocation() : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterCases(String searchTerm) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all cases from the database
        List<Case> cases = CaseDAO.getCasesWithCrime();
        
        // Add filtered cases to the table
        for (Case c : cases) {
            // Check if any field contains the search term
            boolean matches = false;
            
            if (c.getTitle() != null && c.getTitle().toLowerCase().contains(searchTerm)) {
                matches = true;
            } else if (c.getDescription() != null && c.getDescription().toLowerCase().contains(searchTerm)) {
                matches = true;
            } else if (c.getStatus() != null && c.getStatus().toLowerCase().contains(searchTerm)) {
                matches = true;
            } else if (c.getCrimeType() != null && c.getCrimeType().toLowerCase().contains(searchTerm)) {
                matches = true;
            } else if (c.getCrimeLocation() != null && c.getCrimeLocation().toLowerCase().contains(searchTerm)) {
                matches = true;
            }
            
            if (matches) {
                Object[] row = {
                    c.getId(),
                    c.getTitle() != null ? c.getTitle() : "",
                    c.getDescription() != null ? c.getDescription() : "",
                    c.getStatus() != null ? c.getStatus() : "",
                    c.getDateStarted() != null ? c.getDateStarted().toString() : "",
                    c.getCrimeType() != null ? c.getCrimeType() : "",
                    c.getCrimeLocation() != null ? c.getCrimeLocation() : ""
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private Case getCaseById(int caseId) {
        List<Case> cases = CaseDAO.getCasesWithCrime();
        for (Case c : cases) {
            if (c.getId() == caseId) {
                return c;
            }
        }
        return null;
    }
    
    private void openCaseDialog(Case caseToEdit) {
        JDialog dialog = new JDialog(this, caseToEdit == null ? "Add New Case" : "Edit Case", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JTextField titleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        String[] statusOptions = {"Open", "In Progress", "Closed", "Suspended"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField crimeTypeField = new JTextField();
        JTextField locationField = new JTextField();
        
        // If editing an existing case, populate the fields
        if (caseToEdit != null) {
            titleField.setText(caseToEdit.getTitle());
            descriptionArea.setText(caseToEdit.getDescription());
            statusCombo.setSelectedItem(caseToEdit.getStatus());
            if (caseToEdit.getDateStarted() != null) {
                dateField.setText(caseToEdit.getDateStarted().toString());
            }
            crimeTypeField.setText(caseToEdit.getCrimeType());
            locationField.setText(caseToEdit.getCrimeLocation());
        }
        
        // Add components to panel
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        
        panel.add(new JLabel("Description:"));
        panel.add(descScrollPane);
        
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        
        panel.add(new JLabel("Date Started (YYYY-MM-DD):"));
        panel.add(dateField);
        
        panel.add(new JLabel("Crime Type:"));
        panel.add(crimeTypeField);
        
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        
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
                    String title = titleField.getText().trim();
                    String description = descriptionArea.getText().trim();
                    String status = (String) statusCombo.getSelectedItem();
                    String dateStr = dateField.getText().trim();
                    String crimeType = crimeTypeField.getText().trim();
                    String location = locationField.getText().trim();
                    
                    if (title.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Please enter a title for the case.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateStr);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Invalid date format. Please use YYYY-MM-DD format.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Create or update the case
                    Case caseObj;
                    if (caseToEdit == null) {
                        // Create new case
                        caseObj = new Case();
                        System.out.println("Creating new case");
                    } else {
                        // Update existing case
                        caseObj = caseToEdit;
                        System.out.println("Updating existing case with ID: " + caseObj.getId());
                    }
                    
                    caseObj.setTitle(title);
                    caseObj.setDescription(description);
                    caseObj.setStatus(status);
                    caseObj.setDateStarted(date);
                    caseObj.setCrimeType(crimeType);
                    caseObj.setCrimeLocation(location);
                    
                    // Debug output
                    System.out.println("Case object prepared for saving:");
                    System.out.println("  ID: " + caseObj.getId());
                    System.out.println("  Title: " + caseObj.getTitle());
                    System.out.println("  Description: " + caseObj.getDescription());
                    System.out.println("  Status: " + caseObj.getStatus());
                    System.out.println("  Date Started: " + caseObj.getDateStarted());
                    System.out.println("  Crime Type: " + caseObj.getCrimeType());
                    System.out.println("  Location: " + caseObj.getCrimeLocation());
                    
                    // Save to database
                    if (caseToEdit == null) {
                        // Create new case
                        boolean created = CaseDAO.createCase(caseObj);
                        if (created) {
                            JOptionPane.showMessageDialog(dialog,
                                "Case created successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "Failed to create case. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // Update existing case
                        boolean updated = CaseDAO.updateCase(caseObj);
                        if (updated) {
                            JOptionPane.showMessageDialog(dialog,
                                "Case updated successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "Failed to update case. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    
                    // Refresh the table
                    loadCases();
                    
                    // Close the dialog
                    dialog.dispose();
                    
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
            new ManageCasesFrame().setVisible(true);
        });
    }
}