/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.cmsystem;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.crimemanagement.ui.LoginForm;
import com.crimemanagement.util.DBConnection;

public class CMSystem {

    private static JFrame parentFrame;
    public static void main(String[] args) {
        try {
            // Apply system's look and feel (Windows, macOS, etc.)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        
        // Verify and update database schema if needed
        System.out.println("Verifying database schema...");
        DBConnection.verifyDatabaseSchema();
        
        // Test database connection
        if (DBConnection.isConnectionValid()) {
            System.out.println("Database connection is valid.");
        } else {
            System.err.println("Database connection is not valid. Please check your database settings.");
        }

        // Start the login form on the EDT
        SwingUtilities.invokeLater(() -> {
            new LoginForm(parentFrame).setVisible(true);
        });
    }
}