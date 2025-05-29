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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ForensicPanel extends JPanel {

    public ForensicPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Forensic Analyst Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Center content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(3, 1, 10, 15));

        // Upload Forensic Report
        JButton uploadButton = new JButton("Upload Forensic Report");
        uploadButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        uploadButton.addActionListener(e -> {
            UploadReportFrame uploadFrame = new UploadReportFrame();
            uploadFrame.setVisible(true);
        });

        // View Submitted Reports
        JButton viewReportsButton = new JButton("View Submitted Reports");
        viewReportsButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewReportsButton.addActionListener(e -> {
            // Open the new frame with tabbed interface for both evidence and forensic reports
            ViewForensicDetailsFrame viewFrame = new ViewForensicDetailsFrame();
            viewFrame.setVisible(true);
        });

        contentPanel.add(uploadButton);
        contentPanel.add(viewReportsButton);

        add(titleLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
}
