package view;

import controller.AuthController;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private AuthController authController;
    
    public LoginPanel() {
        authController = new AuthController();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_LIGHT);
        
        // Main container with gradient background
        JPanel mainContainer = Theme.createGradientPanel();
        mainContainer.setLayout(new GridBagLayout());
        mainContainer.setOpaque(false);
        
        // Login card
        JPanel loginCard = Theme.createCardPanel();
        loginCard.setLayout(new GridBagLayout());
        loginCard.setPreferredSize(new Dimension(450, 500));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.weightx = 1.0;
        
        // Title with icon
        JLabel titleLabel = Theme.createStyledLabel("Event Management System", Theme.getTitleFont(), Theme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 20, 30, 20);
        loginCard.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = Theme.createStyledLabel("Sign in to your account", Theme.getBodyFont(), Theme.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 30, 20);
        loginCard.add(subtitleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        JLabel usernameLabel = Theme.createStyledLabel("Username", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY);
        loginCard.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = Theme.createStyledTextField(20);
        loginCard.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = Theme.createStyledLabel("Password", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY);
        loginCard.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = Theme.createStyledPasswordField(20);
        loginCard.add(passwordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));
        
        loginButton = Theme.createPrimaryButton("Login");
        registerButton = Theme.createSecondaryButton("Create Account");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginCard.add(buttonPanel, gbc);
        
        // Add card to main container
        mainContainer.add(loginCard);
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegistration());
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (authController.login(username, password)) {
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome back, " + username + "!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            authController.openDashboard();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password. Please try again.", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    private void openRegistration() {
        MainApplicationFrame.getInstance().showCard(MainApplicationFrame.REGISTRATION_CARD);
    }
}

