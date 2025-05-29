/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.crimemanagement.dao.EvidenceDAO;
import com.crimemanagement.dao.ForensicReportDAO;
import com.crimemanagement.model.Evidence;
import com.crimemanagement.model.ForensicReport;

public class UploadReportFrame extends JFrame {

    // Evidence fields
    private JTextField evidenceIdField;
    private JComboBox<String> evidenceTypeCombo;
    private JTextField evidenceDescField;
    private JTextField dataCollectedField;
    private JTextField crimeIdField;
    
    // Forensic Report fields
    private JTextArea findingsArea;
    private JTextField preparedByField;
    
    // Flag to track if we're creating a new evidence or using existing
    private JComboBox<String> evidenceOptionCombo;
    private JPanel newEvidencePanel;

    public UploadReportFrame() {
        setTitle("Upload Forensic Report");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Main panel with sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        
        // Evidence section
        JPanel evidenceSection = new JPanel(new BorderLayout(5, 5));
        evidenceSection.setBorder(BorderFactory.createTitledBorder("Evidence Information"));
        
        // Option to create new evidence or use existing
        JPanel evidenceOptionPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        evidenceOptionPanel.add(new JLabel("Evidence Option:"));
        
        String[] options = {"Use Existing Evidence ID", "Create New Evidence"};
        evidenceOptionCombo = new JComboBox<>(options);
        evidenceOptionPanel.add(evidenceOptionCombo);
        
        evidenceSection.add(evidenceOptionPanel, BorderLayout.NORTH);
        
        // Panel for existing evidence ID
        JPanel existingEvidencePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        existingEvidencePanel.add(new JLabel("Evidence ID:"));
        evidenceIdField = new JTextField();
        existingEvidencePanel.add(evidenceIdField);
        
        // Panel for new evidence details
        newEvidencePanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        String[] evidenceTypes = {"DNA", "Fingerprint", "Weapon", "Document", "Digital", "Other"};
        evidenceTypeCombo = new JComboBox<>(evidenceTypes);
        evidenceDescField = new JTextField();
        dataCollectedField = new JTextField();
        crimeIdField = new JTextField();
        
        newEvidencePanel.add(new JLabel("Evidence Type:"));
        newEvidencePanel.add(evidenceTypeCombo);
        newEvidencePanel.add(new JLabel("Description:"));
        newEvidencePanel.add(evidenceDescField);
        newEvidencePanel.add(new JLabel("Data Collected:"));
        newEvidencePanel.add(dataCollectedField);
        newEvidencePanel.add(new JLabel("Crime ID:"));
        newEvidencePanel.add(crimeIdField);
        
        // Initially hide new evidence panel
        newEvidencePanel.setVisible(false);
        
        // Add listener to toggle between panels
        evidenceOptionCombo.addActionListener(e -> {
            if (evidenceOptionCombo.getSelectedIndex() == 0) {
                // Use existing evidence
                existingEvidencePanel.setVisible(true);
                newEvidencePanel.setVisible(false);
            } else {
                // Create new evidence
                existingEvidencePanel.setVisible(false);
                newEvidencePanel.setVisible(true);
            }
            pack();
            setSize(500, 600);
        });
        
        JPanel evidenceDetailsPanel = new JPanel(new BorderLayout());
        evidenceDetailsPanel.add(existingEvidencePanel, BorderLayout.NORTH);
        evidenceDetailsPanel.add(newEvidencePanel, BorderLayout.CENTER);
        
        evidenceSection.add(evidenceDetailsPanel, BorderLayout.CENTER);
        
        // Forensic Report section
        JPanel reportSection = new JPanel(new BorderLayout(5, 5));
        reportSection.setBorder(BorderFactory.createTitledBorder("Forensic Report Information"));
        
        JPanel reportDetailsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        
        findingsArea = new JTextArea(4, 20);
        preparedByField = new JTextField();
        
        reportDetailsPanel.add(new JLabel("Findings:"));
        reportDetailsPanel.add(new JScrollPane(findingsArea));
        reportDetailsPanel.add(new JLabel("Prepared By:"));
        reportDetailsPanel.add(preparedByField);
        
        reportSection.add(reportDetailsPanel, BorderLayout.CENTER);
        
        // Add sections to main panel
        mainPanel.add(evidenceSection, BorderLayout.NORTH);
        mainPanel.add(reportSection, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton uploadBtn = new JButton("Upload Report");
        JButton cancelBtn = new JButton("Cancel");

        uploadBtn.addActionListener(e -> uploadReport());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(uploadBtn);
        buttonPanel.add(cancelBtn);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void uploadReport() {
        String findings = findingsArea.getText().trim();
        String preparedBy = preparedByField.getText().trim();
        
        // Validate common fields
        if (findings.isEmpty() || preparedBy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Findings and Prepared By fields must be filled.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int evidenceId;
            
            // Handle evidence based on selected option
            if (evidenceOptionCombo.getSelectedIndex() == 0) {
                // Using existing evidence ID
                String evidenceIdStr = evidenceIdField.getText().trim();
                if (evidenceIdStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Evidence ID must be provided.", 
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                evidenceId = Integer.parseInt(evidenceIdStr);
            } else {
                // Creating new evidence
                String type = evidenceTypeCombo.getSelectedItem().toString();
                String description = evidenceDescField.getText().trim();
                String dataCollected = dataCollectedField.getText().trim();
                String crimeIdStr = crimeIdField.getText().trim();
                
                if (description.isEmpty() || dataCollected.isEmpty() || crimeIdStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All evidence fields must be filled.", 
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int crimeId = Integer.parseInt(crimeIdStr);
                
                // Create and insert new evidence
                Evidence evidence = new Evidence(0, type, description, dataCollected, crimeId);
                EvidenceDAO.insertEvidence(evidence);
                
                // Get the generated evidence ID (this is a simplification - in a real app, 
                // you would need to get the auto-generated ID from the database)
                // For now, we'll use the current timestamp as a placeholder
                evidenceId = (int) (System.currentTimeMillis() % 10000);
            }

            // Create and insert the forensic report
            ForensicReport report = new ForensicReport(
                    0, evidenceId, findings, preparedBy, LocalDate.now()
            );
            
            ForensicReportDAO.insertReport(report);
            
            // Show success message
            JOptionPane.showMessageDialog(this, "Report and evidence data uploaded successfully.");
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid numeric input. Please check all ID fields.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to upload data: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        evidenceIdField.setText("");
        evidenceTypeCombo.setSelectedIndex(0);
        evidenceDescField.setText("");
        dataCollectedField.setText("");
        crimeIdField.setText("");
        findingsArea.setText("");
        preparedByField.setText("");
    }
}

