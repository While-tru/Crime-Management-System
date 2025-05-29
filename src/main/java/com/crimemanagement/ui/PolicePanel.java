/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PolicePanel extends JPanel {
    public PolicePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("Police Operations Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 15));

        JButton fileFIRButton = new JButton("File New FIR");
        fileFIRButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton viewFIRsButton = new JButton("View All FIRs");
        viewFIRsButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton manageCasesButton = new JButton("Manage Cases");
        manageCasesButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton viewComplaintsButton = new JButton("View Complaints");
        viewComplaintsButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JButton viewForensicButton = new JButton("View Forensic Reports");
        viewForensicButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Add buttons to panel
        buttonPanel.add(fileFIRButton);
        buttonPanel.add(viewFIRsButton);
        buttonPanel.add(manageCasesButton);
        buttonPanel.add(viewComplaintsButton);
        buttonPanel.add(viewForensicButton);
        
        fileFIRButton.addActionListener(e -> {
        JFrame firFrame = new FileFIRFrame();
        firFrame.setVisible(true);
        });

        viewFIRsButton.addActionListener(e -> {
        JFrame viewFIRFrame = new ViewFIRsFrame();
        viewFIRFrame.setVisible(true);
        });

        viewComplaintsButton.addActionListener(e -> {
        JFrame complaintsFrame = new ViewComplaintsFrame();
        complaintsFrame.setVisible(true);
        });
        
        viewForensicButton.addActionListener(e -> {
        JFrame forensicFrame = new ViewForensicReportsFrame();
        forensicFrame.setVisible(true);
        });
        
        manageCasesButton.addActionListener(e -> {
        JFrame casesFrame = new ManageCasesFrame();
        casesFrame.setVisible(true);
        });

        // Add new button for resources
        JButton policeResourcesButton = new JButton("Police Resources");
        policeResourcesButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        policeResourcesButton.addActionListener(e -> {
        JFrame resourcesFrame = new PoliceSourcesFrame();
        resourcesFrame.setVisible(true);
        });
        buttonPanel.add(policeResourcesButton);


        // Add to layout
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }
}

