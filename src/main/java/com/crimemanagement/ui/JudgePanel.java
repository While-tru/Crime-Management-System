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

public class JudgePanel extends JPanel {
    public JudgePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Title
        JLabel titleLabel = new JLabel("Judge Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 15));

        JButton viewCasesButton = new JButton("View and Update Cases");
        viewCasesButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewCasesButton.addActionListener(e -> new JudgeCaseFrame());

        JButton viewCriminalsButton = new JButton("View Criminals");
        viewCriminalsButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewCriminalsButton.addActionListener(e -> {
            ViewCriminalsFrame criminalsFrame = new ViewCriminalsFrame();
            criminalsFrame.setVisible(true);
        
    
        });

        buttonPanel.add(viewCasesButton);
        buttonPanel.add(viewCriminalsButton);

        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }
}