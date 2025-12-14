package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

/**
 * Notification Manager to track unread notifications
 * 
 * @author 27066
 */
public class NotificationManager {
    
    private static NotificationManager instance;
    private List<Notification> notifications;
    private List<NotificationCountListener> listeners;
    private int unreadCount;
    
    public NotificationManager() {
        notifications = new CopyOnWriteArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
        unreadCount = 0;
    }
    
    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }
    
    /**
     * Add a new notification
     */
    public void addNotification(String type, String title, String message) {
        Notification notification = new Notification(type, title, message);
        notifications.add(0, notification); // Add to beginning (newest first)
        unreadCount++;
        notifyListeners();
    }
    
    /**
     * Mark all notifications as read
     */
    public void markAllAsRead() {
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        unreadCount = 0;
        notifyListeners();
    }
    
    /**
     * Mark a specific notification as read
     */
    public void markAsRead(int index) {
        if (index >= 0 && index < notifications.size()) {
            Notification notification = notifications.get(index);
            if (!notification.isRead()) {
                notification.setRead(true);
                unreadCount--;
                notifyListeners();
            }
        }
    }
    
    /**
     * Get all notifications
     */
    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }
    
    /**
     * Get unread count
     */
    public int getUnreadCount() {
        return unreadCount;
    }
    
    /**
     * Clear all notifications
     */
    public void clearAll() {
        notifications.clear();
        unreadCount = 0;
        notifyListeners();
    }
    
    /**
     * Add listener for notification count changes
     */
    public void addListener(NotificationCountListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove listener
     */
    public void removeListener(NotificationCountListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners of count change
     */
    private void notifyListeners() {
        SwingUtilities.invokeLater(() -> {
            for (NotificationCountListener listener : listeners) {
                listener.onNotificationCountChanged(unreadCount);
            }
        });
    }
    
    /**
     * Notification data class
     */
    public static class Notification {
        private String type;
        private String title;
        private String message;
        private long timestamp;
        private boolean read;
        
        public Notification(String type, String title, String message) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
            this.read = false;
        }
        
        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }
    
    /**
     * Listener interface for notification count changes
     */
    public interface NotificationCountListener {
        void onNotificationCountChanged(int count);
    }
}

