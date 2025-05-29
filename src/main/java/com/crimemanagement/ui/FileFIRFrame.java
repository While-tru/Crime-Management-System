package com.crimemanagement.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import com.crimemanagement.dao.CaseDAO;
import com.crimemanagement.dao.FIRDAO;
import com.crimemanagement.model.Case;
import com.crimemanagement.model.FIR;
import java.time.LocalTime;

public class FileFIRFrame extends JFrame {
    private JTextField descriptionField;
    private JComboBox<String> caseComboBox;
    private JTextField dateField;
    private JTextField timeField;

    public FileFIRFrame() {
        setTitle("File New FIR");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);

        panel.add(new JLabel("Select Case:"));
        
        // Get all cases from the database
        List<Case> cases = CaseDAO.getCasesWithCrime();
        String[] caseOptions = new String[cases.size() + 1];
        caseOptions[0] = "Default Case (ID: 1)";
        
        for (int i = 0; i < cases.size(); i++) {
            Case c = cases.get(i);
            caseOptions[i + 1] = "ID: " + c.getId() + " - " + 
                                (c.getTitle() != null ? c.getTitle() : "Untitled") + 
                                " (" + (c.getStatus() != null ? c.getStatus() : "Unknown") + ")";
        }
        
        caseComboBox = new JComboBox<>(caseOptions);
        caseComboBox.setToolTipText("Select a case to associate this FIR with. The default case will be used if none is selected.");
        panel.add(caseComboBox);

        panel.add(new JLabel("Date Filed (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString()); // Default to today
        panel.add(dateField);
        
        panel.add(new JLabel("Time Filed (HH:MM:SS):"));
        timeField = new JTextField(LocalTime.now().toString()); // Default to current time
        panel.add(timeField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton submitButton = new JButton("Submit FIR");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Parse the data from fields
                    String description = descriptionField.getText();
                    if (description.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(FileFIRFrame.this, 
                            "Please enter a description for the FIR.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    // Get the selected case ID
                    int caseId = 1; // Default case ID
                    String selectedCase = (String) caseComboBox.getSelectedItem();
                    
                    if (selectedCase != null && !selectedCase.equals("Default Case (ID: 1)")) {
                        // Extract the case ID from the selected item
                        // Format is "ID: X - Title (Status)"
                        try {
                            String idPart = selectedCase.substring(4, selectedCase.indexOf(" -"));
                            caseId = Integer.parseInt(idPart.trim());
                            System.out.println("Selected case ID: " + caseId);
                        } catch (Exception ex) {
                            System.out.println("Warning: Could not parse case ID from '" + selectedCase + "'. Using default case ID: " + caseId);
                            ex.printStackTrace();
                        }
                    }
                    
                    System.out.println("Final selected case ID: " + caseId);
                    
                    // Parse the date
                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateField.getText().trim());
                    } catch (Exception dateEx) {
                        JOptionPane.showMessageDialog(FileFIRFrame.this, 
                            "Invalid date format. Please use YYYY-MM-DD format.", 
                            "Date Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Create FIR with the constructor that matches the database fields
                    FIR fir = new FIR();
                    fir.setId(0); // New FIR, ID will be auto-generated
                    fir.setDescription(description);
                    fir.setDateFiled(date);
                    fir.setCaseId(caseId);
                    
                    System.out.println("Created FIR object with case ID: " + fir.getCaseId());
                    
                    // Debug output
                    System.out.println("Attempting to insert FIR with:");
                    System.out.println("Description: " + description);
                    System.out.println("Case ID: " + caseId);
                    System.out.println("Date: " + date);
                    
                    FIRDAO.insertFIR(fir);
                    JOptionPane.showMessageDialog(FileFIRFrame.this, "FIR filed successfully!");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FileFIRFrame.this, 
                        "Error: " + ex.getMessage(), 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close this frame
            }
        });
        
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        
        panel.add(new JLabel("")); // Empty label for spacing
        panel.add(buttonPanel);
        add(panel);
    }
}
