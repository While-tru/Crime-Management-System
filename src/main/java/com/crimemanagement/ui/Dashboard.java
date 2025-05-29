/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import com.crimemanagement.model.User;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private User user;

    public Dashboard(User user) {
        this.user = user;

        setTitle("Dashboard - " + user.getRole());
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 144, 255)); // Dodger Blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Crime Management Dashboard - " + user.getRole().toUpperCase());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Role-specific content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel rolePanel;

        switch (user.getRole().toLowerCase()) {
            case "police":
                rolePanel = new PolicePanel();
                break;
            case "judge":
                rolePanel = new JudgePanel();
                break;
            case "forensic":
                rolePanel = new ForensicPanel();
                break;
            default:
                rolePanel = new ReadOnlyView();
                break;
        }

        contentPanel.add(rolePanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
}

