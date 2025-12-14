package view;

import util.SessionManager;
import util.Theme;
import util.NotificationManager;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BasicStroke;

/**
 * Navigation Panel for Single Page Application
 * Provides persistent sidebar navigation with session management
 * 
 * @author 27066
 */
public class NavigationPanel extends JPanel {
    
    private JPanel menuPanel;
    private JPanel userPanel;
    private JLabel userNameLabel;
    private JLabel userRoleLabel;
    private JButton logoutButton;
    private JButton notificationButton;
    private JLabel notificationBadge;
    
    public NavigationPanel() {
        initializeUI();
        updateUserInfo();
        setupNotificationListener();
    }
    
    private void setupNotificationListener() {
        NotificationManager.getInstance().addListener(count -> {
            SwingUtilities.invokeLater(() -> {
                updateNotificationBadge(count);
            });
        });
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.SIDEBAR_BG);
        setPreferredSize(new Dimension(280, 0));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Top section - Logo/Title
        JPanel topPanel = createTopPanel();
        
        // Middle section - Navigation Menu
        menuPanel = createMenuPanel();
        
        // Bottom section - User Info & Logout
        userPanel = createUserPanel();
        
        add(topPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
        add(userPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.SIDEBAR_BG);
        panel.setBorder(new EmptyBorder(25, 20, 25, 20));
        
        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Event Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Theme.TEXT_WHITE);
        
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(Theme.getBodyFont());
        subtitleLabel.setForeground(Theme.TEXT_ON_DARK);
        
        titleContainer.add(titleLabel, BorderLayout.NORTH);
        titleContainer.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Notification bell button
        notificationButton = createNotificationButton();
        
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(titleContainer, BorderLayout.CENTER);
        topRow.add(notificationButton, BorderLayout.EAST);
        
        panel.add(topRow, BorderLayout.CENTER);
        return panel;
    }
    
    private JButton createNotificationButton() {
        // Create a custom button that draws a bell icon
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                int centerY = height / 2;
                
                // Draw bell icon using shapes
                g2.setColor(Theme.TEXT_WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Bell body (semicircle at top, wider at bottom)
                int bellWidth = 20;
                int bellHeight = 18;
                int bellX = centerX - bellWidth / 2;
                int bellY = centerY - bellHeight / 2;
                
                // Draw bell shape: top curve, sides, and bottom opening
                // Top curve (semicircle)
                g2.drawArc(bellX, bellY, bellWidth, 12, 0, 180);
                
                // Left side
                g2.drawLine(bellX, bellY + 6, bellX, bellY + bellHeight - 4);
                
                // Right side
                g2.drawLine(bellX + bellWidth, bellY + 6, bellX + bellWidth, bellY + bellHeight - 4);
                
                // Bottom opening (curved)
                g2.drawArc(bellX - 2, bellY + bellHeight - 8, bellWidth + 4, 8, 180, 180);
                
                // Bell clapper (small circle at bottom)
                g2.fillOval(centerX - 2, bellY + bellHeight - 2, 4, 4);
                
                // Bell handle (small rectangle at top)
                g2.fillRect(centerX - 2, bellY - 4, 4, 6);
                
                // Draw notification badge if needed
                if (notificationBadge != null && notificationBadge.isVisible()) {
                    int badgeX = width - 20;
                    int badgeY = 5;
                    int badgeSize = 18;
                    
                    g2.setColor(Theme.ERROR);
                    g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                    
                    // Draw badge text
                    g2.setColor(Theme.TEXT_WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    String text = notificationBadge.getText();
                    int textX = badgeX + (badgeSize - fm.stringWidth(text)) / 2;
                    int textY = badgeY + (badgeSize + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(text, textX, textY);
                }
                
                g2.dispose();
            }
        };
        
        button.setBackground(Theme.SIDEBAR_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(50, 50));
        
        // Create badge label (for text storage)
        notificationBadge = new JLabel("0");
        notificationBadge.setVisible(false);
        
        // Add click action
        button.addActionListener(e -> showNotificationPanel());
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(51, 65, 85));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.SIDEBAR_BG);
            }
        });
        
        // Initialize badge
        updateNotificationBadge(0);
        
        return button;
    }
    
    private void showNotificationPanel() {
        NotificationPanel notificationPanel = new NotificationPanel((JFrame) SwingUtilities.getWindowAncestor(this));
        notificationPanel.setVisible(true);
    }
    
    private void updateNotificationBadge(int count) {
        if (count > 0) {
            if (count > 99) {
                notificationBadge.setText("99+");
            } else {
                notificationBadge.setText(String.valueOf(count));
            }
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
        
        if (notificationButton != null) {
            notificationButton.repaint();
        }
    }
    
    public void refreshNotificationBadge() {
        if (notificationButton != null) {
            int count = NotificationManager.getInstance().getUnreadCount();
            updateNotificationBadge(count);
        }
    }
    
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.SIDEBAR_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return panel;
        }
        
        // Dashboard Menu Item
        addMenuItem(panel, "📊 Dashboard", () -> {
            if (SessionManager.isAdmin()) {
                MainApplicationFrame.getInstance().showCard(MainApplicationFrame.ADMIN_DASHBOARD_CARD);
            } else {
                MainApplicationFrame.getInstance().showCard(MainApplicationFrame.CUSTOMER_DASHBOARD_CARD);
            }
        });
        
        // Events Menu (Admin/Organizer only)
        if (SessionManager.isAdmin() || SessionManager.isOrganizer()) {
            addMenuItem(panel, "📅 Manage Events", () -> {
                MainApplicationFrame.getInstance().showCard(MainApplicationFrame.EVENT_MANAGEMENT_CARD);
            });
        }
        
        // Bookings Menu - Different for admin vs customer
        if (SessionManager.isAdmin()) {
            // Admin: Manage all bookings
            addMenuItem(panel, "👥 Manage Bookings", () -> {
                MainApplicationFrame.getInstance().showCard(MainApplicationFrame.BOOKING_MANAGEMENT_CARD);
            });
            
            addMenuItem(panel, "📊 Reports", () -> {
                // Show report options dialog
                showReportsMenu();
            });
        } else {
            // Customer: View own bookings
            addMenuItem(panel, "🎫 My Bookings", () -> {
                MainApplicationFrame.getInstance().showCard(MainApplicationFrame.BOOKING_MANAGEMENT_CARD);
            });
        }
        
        // Add spacing
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void addMenuItem(JPanel parent, String text, Runnable action) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setBackground(Theme.SIDEBAR_BG);
        menuItem.setBorder(new EmptyBorder(8, 15, 8, 15));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Check if text contains emoji and use appropriate font
        JLabel label = new JLabel(text);
        // Use icon font for emoji support, but keep subheading size
        Font baseFont = Theme.getSubheadingFont();
        Font iconFont = Theme.getIconFont(baseFont.getSize());
        label.setFont(iconFont);
        label.setForeground(Theme.TEXT_ON_DARK);
        
        menuItem.add(label, BorderLayout.WEST);
        
        // Hover effect
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(new Color(51, 65, 85));
                label.setForeground(Theme.TEXT_WHITE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Theme.SIDEBAR_BG);
                label.setForeground(Theme.TEXT_ON_DARK);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        
        parent.add(menuItem);
        parent.add(Box.createVerticalStrut(5));
    }
    
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 23, 42)); // Darker background
        panel.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        // User info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        
        userNameLabel = new JLabel();
        userNameLabel.setFont(Theme.getSubheadingFont());
        userNameLabel.setForeground(Theme.TEXT_WHITE);
        
        userRoleLabel = new JLabel();
        userRoleLabel.setFont(Theme.getBodyFont());
        userRoleLabel.setForeground(Theme.TEXT_ON_DARK);
        
        infoPanel.add(userNameLabel, BorderLayout.NORTH);
        infoPanel.add(userRoleLabel, BorderLayout.SOUTH);
        
        // Logout button
        logoutButton = new JButton("🚪 Logout");
        // Use icon font for emoji support
        Font logoutFont = Theme.getIconFont(Theme.getBodyFont().getSize());
        logoutButton.setFont(logoutFont);
        logoutButton.setBackground(Theme.ERROR);
        logoutButton.setForeground(Theme.TEXT_WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(0, 40));
        logoutButton.addActionListener(e -> handleLogout());
        
        // Hover effect for logout button
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(220, 38, 38));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(Theme.ERROR);
            }
        });
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(logoutButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void showReportsMenu() {
        String[] options = {"Export Events to Excel", "Export Bookings to Excel", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Select a report to export:",
            "Export Reports",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]
        );
        
        if (choice == 0) {
            // Export Events
            try {
                AdminDashboardPanel panel = new AdminDashboardPanel();
                panel.exportEventsReport();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to export events: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else if (choice == 1) {
            // Export Bookings
            try {
                AdminDashboardPanel panel = new AdminDashboardPanel();
                panel.exportBookingsReport();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to export bookings: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            MainApplicationFrame.getInstance().showCard(MainApplicationFrame.LOGIN_CARD);
            MainApplicationFrame.getInstance().setNavigationVisible(false);
        }
    }
    
    public void updateUserInfo() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getFullName());
            String role = user.getUserRole();
            String roleDisplay = role.substring(0, 1).toUpperCase() + role.substring(1);
            userRoleLabel.setText(roleDisplay);
        }
    }
    
    public void refreshMenu() {
        remove(menuPanel);
        menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.CENTER);
        refreshNotificationBadge();
        revalidate();
        repaint();
    }
}

