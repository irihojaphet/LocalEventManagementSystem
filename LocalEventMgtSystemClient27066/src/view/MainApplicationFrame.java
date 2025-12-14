package view;

import util.Theme;
import javax.swing.*;
import java.awt.*;

public class MainApplicationFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Card names
    public static final String LOGIN_CARD = "LOGIN";
    public static final String REGISTRATION_CARD = "REGISTRATION";
    public static final String ADMIN_DASHBOARD_CARD = "ADMIN_DASHBOARD";
    public static final String CUSTOMER_DASHBOARD_CARD = "CUSTOMER_DASHBOARD";
    public static final String EVENT_MANAGEMENT_CARD = "EVENT_MANAGEMENT";
    public static final String BOOKING_MANAGEMENT_CARD = "BOOKING_MANAGEMENT";
    
    private static MainApplicationFrame instance;
    
    public static MainApplicationFrame getInstance() {
        if (instance == null) {
            instance = new MainApplicationFrame();
        }
        return instance;
    }
    
    private MainApplicationFrame() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Theme.BACKGROUND_LIGHT);
        
        setTitle("Event Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Set application icon (if available)
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        // Initialize all views as panels
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.setName(LOGIN_CARD);
        RegistrationPanel registrationPanel = new RegistrationPanel();
        registrationPanel.setName(REGISTRATION_CARD);
        
        cardPanel.add(loginPanel, LOGIN_CARD);
        cardPanel.add(registrationPanel, REGISTRATION_CARD);
        
        add(cardPanel);
        showCard(LOGIN_CARD);
        setVisible(true);
    }
    
    public void showCard(String cardName) {
        // For dashboard and booking management cards, always recreate to refresh data and menu bars
        // EventManagement should not be recreated to avoid table refresh issues
        boolean shouldRecreate = cardName.equals(ADMIN_DASHBOARD_CARD) || 
                                 cardName.equals(CUSTOMER_DASHBOARD_CARD) ||
                                 cardName.equals(BOOKING_MANAGEMENT_CARD);
        
        // Remove existing card if it should be recreated (to force refresh)
        if (shouldRecreate) {
            Component existing = null;
            for (Component comp : cardPanel.getComponents()) {
                if (comp.getName() != null && comp.getName().equals(cardName)) {
                    existing = comp;
                    break;
                }
            }
            if (existing != null) {
                cardPanel.remove(existing);
            }
        }
        
        // Check if card exists (for cards that don't need recreation)
        boolean cardExists = false;
        if (!shouldRecreate) {
            for (Component comp : cardPanel.getComponents()) {
                if (comp.getName() != null && comp.getName().equals(cardName)) {
                    cardExists = true;
                    break;
                }
            }
        }
        
        // If card doesn't exist, create it
        if (!cardExists) {
            if (cardName.equals(ADMIN_DASHBOARD_CARD)) {
                AdminDashboardPanel panel = new AdminDashboardPanel();
                panel.setName(ADMIN_DASHBOARD_CARD);
                cardPanel.add(panel, ADMIN_DASHBOARD_CARD);
            } else if (cardName.equals(CUSTOMER_DASHBOARD_CARD)) {
                CustomerDashboardPanel panel = new CustomerDashboardPanel();
                panel.setName(CUSTOMER_DASHBOARD_CARD);
                cardPanel.add(panel, CUSTOMER_DASHBOARD_CARD);
            } else if (cardName.equals(EVENT_MANAGEMENT_CARD)) {
                EventManagementPanel panel = new EventManagementPanel();
                panel.setName(EVENT_MANAGEMENT_CARD);
                cardPanel.add(panel, EVENT_MANAGEMENT_CARD);
            } else if (cardName.equals(BOOKING_MANAGEMENT_CARD)) {
                BookingManagementPanel panel = new BookingManagementPanel();
                panel.setName(BOOKING_MANAGEMENT_CARD);
                cardPanel.add(panel, BOOKING_MANAGEMENT_CARD);
            }
        }
        
        cardLayout.show(cardPanel, cardName);
        revalidate();
        repaint();
    }
}

