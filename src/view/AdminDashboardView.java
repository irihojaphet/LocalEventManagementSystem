package view;

import util.SessionManager;
import javax.swing.*;
import java.awt.*;

public class AdminDashboardView extends JFrame {
    
    public AdminDashboardView() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Admin Dashboard - Event Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu eventsMenu = new JMenu("Events");
        JMenuItem manageEventsItem = new JMenuItem("Manage Events");
        manageEventsItem.addActionListener(e -> openEventManagement());
        eventsMenu.add(manageEventsItem);
        
        JMenu bookingsMenu = new JMenu("Bookings");
        JMenuItem viewBookingsItem = new JMenuItem("View All Bookings");
        viewBookingsItem.addActionListener(e -> openBookingManagement());
        bookingsMenu.add(viewBookingsItem);
        
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        
        menuBar.add(eventsMenu);
        menuBar.add(bookingsMenu);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome panel
        JPanel welcomePanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Welcome, " + SessionManager.getCurrentUser().getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createTitledBorder("System Statistics"));
        
        // Create stat cards
        statsPanel.add(createStatCard("Total Events", "15", new Color(70, 130, 180)));
        statsPanel.add(createStatCard("Total Users", "42", new Color(60, 179, 113)));
        statsPanel.add(createStatCard("Total Revenue", "RWF 250,000", new Color(255, 165, 0)));
        statsPanel.add(createStatCard("Upcoming Events", "8", new Color(220, 20, 60)));
        
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void openEventManagement() {
        new EventManagementView();
    }
    
    private void openBookingManagement() {
        new BookingManagementView();
    }
    
    private void logout() {
        SessionManager.logout();
        dispose();
        new LoginView();
    }
}