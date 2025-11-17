package view;

import controller.AuthController;
import util.PasswordUtil;
import util.ValidationUtil;
import javax.swing.*;
import java.awt.*;

public class RegistrationView extends JFrame {
    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private AuthController authController;
    
    public RegistrationView() {
        authController = new AuthController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Event Management System - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Form fields
        gbc.gridwidth = 1;
        
        // Full Name
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        mainPanel.add(fullNameField, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, gbc);
        
        // Password hint
        JLabel hintLabel = new JLabel("<html><small>Password must be at least 8 characters with 1 uppercase and 1 number</small></html>");
        hintLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        mainPanel.add(hintLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.BLACK);
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridy = 8;
        mainPanel.add(buttonPanel, gbc);
        
        // Action listeners
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            dispose();
            new LoginView();
        });
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void handleRegistration() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "All fields are required!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!ValidationUtil.isValidUsername(username)) {
            JOptionPane.showMessageDialog(this, 
                "Username must be 4-20 alphanumeric characters only!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!ValidationUtil.isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this, 
                "Phone number must be in format: +250XXXXXXXXX or 07XXXXXXXX", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!PasswordUtil.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 8 characters with 1 uppercase and 1 number!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (authController.register(fullName, username, email, phone, password, "customer")) {
            JOptionPane.showMessageDialog(this, 
                "Account created successfully! You can now login.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginView();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed. Username or email may already exist.", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}