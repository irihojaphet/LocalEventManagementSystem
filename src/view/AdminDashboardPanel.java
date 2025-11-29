package view;

import dao.DashboardDAO;
import util.SessionManager;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    private final DashboardDAO dashboardDAO;
    private JLabel totalEventsValueLabel;
    private JLabel totalUsersValueLabel;
    private JLabel totalRevenueValueLabel;
    private JLabel upcomingEventsValueLabel;
    
    public AdminDashboardPanel() {
        dashboardDAO = new DashboardDAO();
        setupMenuBar();
        initializeUI();
        loadStatistics();
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = Theme.createStyledMenuBar();
        
        JMenu eventsMenu = Theme.createStyledMenu("Events");
        JMenuItem manageEventsItem = Theme.createStyledMenuItem("Manage Events");
        manageEventsItem.addActionListener(e -> openEventManagement());
        eventsMenu.add(manageEventsItem);
        
        JMenu bookingsMenu = Theme.createStyledMenu("Bookings");
        JMenuItem viewBookingsItem = Theme.createStyledMenuItem("View All Bookings");
        viewBookingsItem.addActionListener(e -> openBookingManagement());
        bookingsMenu.add(viewBookingsItem);
        
        JMenu accountMenu = Theme.createStyledMenu("Account");
        JMenuItem logoutItem = Theme.createStyledMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        
        menuBar.add(eventsMenu);
        menuBar.add(bookingsMenu);
        menuBar.add(accountMenu);
        
        MainApplicationFrame.getInstance().setJMenuBar(menuBar);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Welcome panel with gradient
        JPanel welcomePanel = Theme.createCardPanel();
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        JLabel welcomeLabel = Theme.createStyledLabel(
            "Welcome, " + SessionManager.getCurrentUser().getFullName() + "!",
            Theme.getHeadingFont(), Theme.TEXT_PRIMARY);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Create stat cards
        totalEventsValueLabel = new JLabel();
        totalUsersValueLabel = new JLabel();
        totalRevenueValueLabel = new JLabel();
        upcomingEventsValueLabel = new JLabel();
        
        statsPanel.add(Theme.createStatCard("Total Events", totalEventsValueLabel.getText(), Theme.PRIMARY_COLOR));
        statsPanel.add(Theme.createStatCard("Total Users", totalUsersValueLabel.getText(), Theme.SUCCESS_COLOR));
        statsPanel.add(Theme.createStatCard("Total Revenue", totalRevenueValueLabel.getText(), Theme.WARNING_COLOR));
        statsPanel.add(Theme.createStatCard("Upcoming Events", upcomingEventsValueLabel.getText(), Theme.SECONDARY_COLOR));
        
        add(welcomePanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }
    
    private void loadStatistics() {
        totalEventsValueLabel.setText(String.valueOf(dashboardDAO.getTotalEvents()));
        totalUsersValueLabel.setText(String.valueOf(dashboardDAO.getTotalUsers()));
        
        double revenue = dashboardDAO.getTotalRevenue();
        totalRevenueValueLabel.setText(String.format("RWF %,.0f", revenue));
        
        upcomingEventsValueLabel.setText(String.valueOf(dashboardDAO.getUpcomingEventsCount()));
        
        // Update stat cards with actual values
        JPanel statsPanel = (JPanel) getComponent(1);
        statsPanel.removeAll();
        statsPanel.add(Theme.createStatCard("Total Events", totalEventsValueLabel.getText(), Theme.PRIMARY_COLOR));
        statsPanel.add(Theme.createStatCard("Total Users", totalUsersValueLabel.getText(), Theme.SUCCESS_COLOR));
        statsPanel.add(Theme.createStatCard("Total Revenue", totalRevenueValueLabel.getText(), Theme.WARNING_COLOR));
        statsPanel.add(Theme.createStatCard("Upcoming Events", upcomingEventsValueLabel.getText(), Theme.SECONDARY_COLOR));
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    private void openEventManagement() {
        MainApplicationFrame.getInstance().showCard(MainApplicationFrame.EVENT_MANAGEMENT_CARD);
    }
    
    private void openBookingManagement() {
        MainApplicationFrame.getInstance().showCard(MainApplicationFrame.BOOKING_MANAGEMENT_CARD);
    }
    
    private void logout() {
        SessionManager.logout();
        MainApplicationFrame.getInstance().setJMenuBar(null);
        MainApplicationFrame.getInstance().showCard(MainApplicationFrame.LOGIN_CARD);
    }
}

