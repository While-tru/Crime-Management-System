/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;
import com.crimemanagement.dao.UserDAO;
import com.crimemanagement.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {

    private static JFrame parentFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    public LoginForm(JFrame parentFrame) {
        setTitle("Login - Crime Management System");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Username
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        // Password
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // Role selection
        panel.add(new JLabel("Role:"));
        String[] roles = {"JUDGE", "POLICE", "FORENSIC"};
        roleComboBox = new JComboBox<>(roles);
        panel.add(roleComboBox);

        // Login button (spans two columns)
        loginButton = new JButton("Login");
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        User user = new UserDAO().login(username, password, role);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            new Dashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid login.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginForm(parentFrame);
    }
}