package view;

import util.NotificationManager;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Notification Panel to display all notifications
 * 
 * @author 27066
 */
public class NotificationPanel extends JDialog {
    
    private JPanel notificationsListPanel;
    private JLabel unreadLabel;
    private NotificationManager notificationManager;
    private SimpleDateFormat dateFormat;
    
    public NotificationPanel(JFrame parent) {
        super(parent, "Notifications", false);
        notificationManager = NotificationManager.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        
        initializeUI();
        loadNotifications();
        
        // Update when notifications change
        notificationManager.addListener(count -> {
            SwingUtilities.invokeLater(() -> {
                updateUnreadLabel();
                loadNotifications();
            });
        });
    }
    
    private void initializeUI() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(Theme.getSubheadingFont());
        titleLabel.setForeground(Theme.TEXT_WHITE);
        
        unreadLabel = new JLabel();
        unreadLabel.setFont(Theme.getBodyFont());
        unreadLabel.setForeground(Theme.TEXT_ON_DARK);
        updateUnreadLabel();
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(unreadLabel, BorderLayout.SOUTH);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        
        JButton markAllReadButton = new JButton("Mark All Read");
        markAllReadButton.setFont(Theme.getBodyFont());
        markAllReadButton.setBackground(Theme.SUCCESS);
        markAllReadButton.setForeground(Theme.TEXT_WHITE);
        markAllReadButton.setFocusPainted(false);
        markAllReadButton.setBorderPainted(false);
        markAllReadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        markAllReadButton.addActionListener(e -> {
            notificationManager.markAllAsRead();
            loadNotifications();
        });
        
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.setFont(Theme.getBodyFont());
        clearAllButton.setBackground(Theme.ERROR);
        clearAllButton.setForeground(Theme.TEXT_WHITE);
        clearAllButton.setFocusPainted(false);
        clearAllButton.setBorderPainted(false);
        clearAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearAllButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all notifications?",
                "Clear All Notifications",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                notificationManager.clearAll();
                loadNotifications();
            }
        });
        
        buttonPanel.add(markAllReadButton);
        buttonPanel.add(clearAllButton);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Notifications list panel
        notificationsListPanel = new JPanel();
        notificationsListPanel.setLayout(new BoxLayout(notificationsListPanel, BoxLayout.Y_AXIS));
        notificationsListPanel.setBackground(Theme.BACKGROUND_LIGHT);
        notificationsListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(notificationsListPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void updateUnreadLabel() {
        int unreadCount = notificationManager.getUnreadCount();
        if (unreadCount > 0) {
            unreadLabel.setText(unreadCount + " unread notification" + (unreadCount > 1 ? "s" : ""));
        } else {
            unreadLabel.setText("All caught up!");
        }
    }
    
    private void loadNotifications() {
        notificationsListPanel.removeAll();
        
        List<NotificationManager.Notification> notifications = notificationManager.getNotifications();
        
        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("<html><center>No notifications yet.<br>You're all caught up!</center></html>");
            emptyLabel.setFont(Theme.getBodyFont());
            emptyLabel.setForeground(Theme.TEXT_SECONDARY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(new EmptyBorder(50, 20, 50, 20));
            notificationsListPanel.add(emptyLabel);
        } else {
            for (NotificationManager.Notification notification : notifications) {
                notificationsListPanel.add(createNotificationItem(notification));
                notificationsListPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        notificationsListPanel.add(Box.createVerticalGlue());
        notificationsListPanel.revalidate();
        notificationsListPanel.repaint();
    }
    
    private JPanel createNotificationItem(NotificationManager.Notification notification) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(notification.isRead() ? Theme.BACKGROUND_LIGHT : new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_LIGHT, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        // Title
        JLabel titleLabel = new JLabel(notification.getTitle());
        titleLabel.setFont(Theme.getSubheadingFont());
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        // Message
        JTextArea messageArea = new JTextArea(notification.getMessage());
        messageArea.setFont(Theme.getBodyFont());
        messageArea.setForeground(Theme.TEXT_SECONDARY);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Timestamp
        String timeStr = dateFormat.format(new java.util.Date(notification.getTimestamp()));
        JLabel timeLabel = new JLabel(timeStr);
        timeLabel.setFont(new Font(Theme.getBodyFont().getName(), Font.ITALIC, Theme.getBodyFont().getSize() - 2));
        timeLabel.setForeground(Theme.TEXT_SECONDARY);
        
        // Unread indicator
        if (!notification.isRead()) {
            JLabel unreadDot = new JLabel("●");
            unreadDot.setForeground(Theme.PRIMARY);
            unreadDot.setFont(new Font(unreadDot.getFont().getName(), Font.BOLD, 12));
            panel.add(unreadDot, BorderLayout.EAST);
        }
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(messageArea, BorderLayout.CENTER);
        contentPanel.add(timeLabel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Click to mark as read
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = notificationManager.getNotifications().indexOf(notification);
                notificationManager.markAsRead(index);
                loadNotifications();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(245, 250, 255));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(notification.isRead() ? Theme.BACKGROUND_LIGHT : new Color(240, 248, 255));
            }
        });
        
        return panel;
    }
}

