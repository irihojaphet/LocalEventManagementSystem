package view;

import controller.AuthController;
import util.PasswordUtil;
import util.ValidationUtil;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistrationPanel extends JPanel {
    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private AuthController authController;
    
    public RegistrationPanel() {
        authController = new AuthController();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_LIGHT);
        
        // Main container with gradient
        JPanel mainContainer = Theme.createGradientPanel();
        mainContainer.setLayout(new GridBagLayout());
        mainContainer.setOpaque(false);
        
        // Registration card with scroll
        JPanel regCard = Theme.createCardPanel();
        regCard.setLayout(new GridBagLayout());
        regCard.setPreferredSize(new Dimension(550, 650));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 25, 12, 25);
        gbc.weightx = 1.0;
        
        // Title
        JLabel titleLabel = Theme.createStyledLabel("Create New Account", Theme.getTitleFont(), Theme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 25, 20, 25);
        regCard.add(titleLabel, gbc);
        
        // Form fields in two columns
        gbc.gridwidth = 1;
        gbc.insets = new Insets(12, 25, 12, 25);
        
        // Full Name
        gbc.gridy = 1;
        gbc.gridx = 0;
        regCard.add(Theme.createStyledLabel("Full Name", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        fullNameField = Theme.createStyledTextField(20);
        regCard.add(fullNameField, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        regCard.add(Theme.createStyledLabel("Username", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        usernameField = Theme.createStyledTextField(20);
        regCard.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        regCard.add(Theme.createStyledLabel("Email", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        emailField = Theme.createStyledTextField(20);
        regCard.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        regCard.add(Theme.createStyledLabel("Phone Number", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        phoneField = Theme.createStyledTextField(20);
        regCard.add(phoneField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        regCard.add(Theme.createStyledLabel("Password", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        passwordField = Theme.createStyledPasswordField(20);
        regCard.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 6;
        regCard.add(Theme.createStyledLabel("Confirm Password", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        confirmPasswordField = Theme.createStyledPasswordField(20);
        regCard.add(confirmPasswordField, gbc);
        
        // Password hint
        JLabel hintLabel = Theme.createStyledLabel(
            "<html><small>Password must be at least 8 characters with 1 uppercase and 1 number</small></html>",
            Theme.getBodyFont(), Theme.TEXT_SECONDARY);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 25, 20, 25);
        regCard.add(hintLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 25, 30, 25));
        
        registerButton = Theme.createSuccessButton("Register");
        backButton = Theme.createSecondaryButton("Back to Login");
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridy = 8;
        regCard.add(buttonPanel, gbc);
        
        // Scroll pane for registration card
        JScrollPane scrollPane = new JScrollPane(regCard);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        mainContainer.add(scrollPane);
        add(mainContainer, BorderLayout.CENTER);
        
        // Action listeners
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            MainApplicationFrame.getInstance().showCard(MainApplicationFrame.LOGIN_CARD);
        });
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
            MainApplicationFrame.getInstance().showCard(MainApplicationFrame.LOGIN_CARD);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed. Username or email may already exist.", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
