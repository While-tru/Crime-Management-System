/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import javax.swing.*;
import java.awt.*;

public class ReadOnlyView extends JPanel {
    public ReadOnlyView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Read-Only Access");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Info message
        JLabel infoLabel = new JLabel("<html>You have limited access. You can only view available records.<br>No editing or data entry is permitted.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Placeholder list/table for data
        JTextArea viewArea = new JTextArea("List of FIRs / Complaints / Cases...");
        viewArea.setEditable(false);
        viewArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        viewArea.setLineWrap(true);
        viewArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(viewArea);

        // Add components to panel
        add(titleLabel, BorderLayout.NORTH);
        add(infoLabel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
}
