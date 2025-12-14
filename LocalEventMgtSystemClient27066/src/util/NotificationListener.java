package util;

import javax.jms.*;
import javax.swing.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import util.SessionManager;
import util.NotificationManager;

/**
 * Notification Listener for receiving real-time notifications via ActiveMQ
 * 
 * @author 27066
 */
public class NotificationListener {
    
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "payment.notifications";
    
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private ExecutorService executor;
    
    private static NotificationListener instance;
    
    private NotificationListener() {
        executor = Executors.newSingleThreadExecutor();
    }
    
    public static NotificationListener getInstance() {
        if (instance == null) {
            instance = new NotificationListener();
        }
        return instance;
    }
    
    /**
     * Start listening for notifications
     */
    public void startListening() {
        executor.submit(() -> {
            try {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
                connection = connectionFactory.createConnection();
                connection.start();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue queue = session.createQueue(QUEUE_NAME);
                consumer = session.createConsumer(queue);
                
                System.out.println("Notification listener started. Waiting for notifications...");
                
                while (true) {
                    Message message = consumer.receive();
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String notificationText = textMessage.getText();
                        String notificationType = textMessage.getStringProperty("notificationType");
                        String targetRole = textMessage.getStringProperty("targetRole");
                        String userId = textMessage.getStringProperty("userId");
                        
                        // Check if notification should be shown to current user
                        if (shouldShowNotification(targetRole, userId)) {
                            handleNotification(notificationType, notificationText);
                        }
                    }
                }
            } catch (JMSException e) {
                System.err.println("Error in notification listener: " + e.getMessage());
                // Don't print stack trace for connection errors (ActiveMQ may not be running)
                if (!e.getMessage().contains("Connection refused")) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Check if notification should be shown to current user based on role and userId
     */
    private boolean shouldShowNotification(String targetRole, String userId) {
        if (!SessionManager.isLoggedIn()) {
            return false; // Don't show notifications if not logged in
        }
        
        // If targetRole is "admin", only show to admins
        if ("admin".equals(targetRole)) {
            return SessionManager.isAdmin();
        }
        
        // If targetRole is "customer", show to customers
        if ("customer".equals(targetRole)) {
            // If userId is specified, only show to that specific user
            if (userId != null && !userId.isEmpty()) {
                try {
                    int targetUserId = Integer.parseInt(userId);
                    return SessionManager.getCurrentUser() != null && 
                           SessionManager.getCurrentUser().getUserId() == targetUserId;
                } catch (NumberFormatException e) {
                    // If userId parsing fails, show to all customers
                    return SessionManager.isCustomer();
                }
            }
            // If no userId specified, show to all customers
            return SessionManager.isCustomer();
        }
        
        // Default: show to all logged-in users
        return true;
    }
    
    /**
     * Handle incoming notification
     */
    private void handleNotification(String notificationType, String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                String[] parts = message.split("\\|");
                NotificationManager notificationManager = NotificationManager.getInstance();
                
                if ("PAYMENT_CONFIRMED".equals(notificationType)) {
                    if (parts.length >= 8) {
                        String ticketNumber = parts[1];
                        String eventName = parts[5];
                        String amount = parts[6];
                        
                        String title = "Payment Confirmed! 🎉";
                        String notificationMessage = String.format(
                            "Ticket Number: %s\nEvent: %s\nAmount: RWF %s\n\nYour payment has been confirmed. You can now print your ticket!",
                            ticketNumber, eventName, amount
                        );
                        
                        notificationManager.addNotification(notificationType, title, notificationMessage);
                    }
                } else if ("TICKET_READY".equals(notificationType)) {
                    if (parts.length >= 6) {
                        String ticketNumber = parts[1];
                        String eventName = parts[5];
                        
                        String title = "Ticket Ready! 🎫";
                        String notificationMessage = String.format(
                            "Ticket Number: %s\nEvent: %s\n\nYour ticket is ready to print!",
                            ticketNumber, eventName
                        );
                        
                        notificationManager.addNotification(notificationType, title, notificationMessage);
                    }
                } else if ("NEW_EVENT".equals(notificationType)) {
                    if (parts.length >= 5) {
                        String eventName = parts[1];
                        String eventDate = parts.length > 3 ? parts[3] : "";
                        String eventTime = parts.length > 4 ? parts[4] : "";
                        
                        String title = "New Event Available! 🎊";
                        String notificationMessage = String.format(
                            "Event: %s\n%s%s\n\nCheck out this new event and book your tickets now!",
                            eventName,
                            eventDate.isEmpty() ? "" : "Date: " + eventDate + "\n",
                            eventTime.isEmpty() ? "" : "Time: " + eventTime + "\n"
                        );
                        
                        notificationManager.addNotification(notificationType, title, notificationMessage);
                    }
                } else if ("NEW_BOOKING".equals(notificationType)) {
                    if (parts.length >= 8) {
                        String customerName = parts[2];
                        String eventName = parts[4];
                        String ticketNumber = parts[5];
                        String amount = parts[6];
                        String status = parts[7];
                        
                        String title = "New Booking Received! 📋";
                        String notificationMessage = String.format(
                            "Customer: %s\nEvent: %s\nTicket Number: %s\nAmount: RWF %s\nStatus: %s\n\nPlease review and confirm payment.",
                            customerName, eventName, ticketNumber, amount, status
                        );
                        
                        notificationManager.addNotification(notificationType, title, notificationMessage);
                    }
                } else if ("BOOKING_APPROVED".equals(notificationType)) {
                    if (parts.length >= 5) {
                        String ticketNumber = parts[1];
                        String eventName = parts[3];
                        String amount = parts[4];
                        
                        String title = "Booking Approved! ✅";
                        String notificationMessage = String.format(
                            "Ticket Number: %s\nEvent: %s\nAmount: RWF %s\n\nYour booking has been approved. You can now print your ticket!",
                            ticketNumber, eventName, amount
                        );
                        
                        notificationManager.addNotification(notificationType, title, notificationMessage);
                    }
                } else if ("EVENT_EXPIRED".equals(notificationType)) {
                    if (parts.length >= 3) {
                        String eventName = parts[1];
                        String eventDate = parts[2];
                        
                        String title = "Event Expired! ⚠️";
                        String notificationMessage = String.format(
                            "Event: %s\nDate: %s\n\nThis event has expired. You can now delete it even if payments were made.",
                            eventName, eventDate
                        );
                        
                        notificationManager.addNotification(notificationType, title, notificationMessage);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error handling notification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Stop listening for notifications
     */
    public void stopListening() {
        try {
            if (consumer != null) consumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            if (executor != null) executor.shutdown();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

