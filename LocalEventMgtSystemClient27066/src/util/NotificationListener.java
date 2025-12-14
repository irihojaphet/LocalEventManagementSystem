package util;

import javax.jms.*;
import javax.swing.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                        
                        handleNotification(notificationType, notificationText);
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
     * Handle incoming notification
     */
    private void handleNotification(String notificationType, String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                String[] parts = message.split("\\|");
                
                if ("PAYMENT_CONFIRMED".equals(notificationType)) {
                    if (parts.length >= 8) {
                        String ticketNumber = parts[1];
                        String eventName = parts[5];
                        String amount = parts[6];
                        
                        String notificationMessage = String.format(
                            "<html><body style='width: 300px;'>" +
                            "<h3>Payment Confirmed! 🎉</h3>" +
                            "<p><b>Ticket Number:</b> %s</p>" +
                            "<p><b>Event:</b> %s</p>" +
                            "<p><b>Amount:</b> RWF %s</p>" +
                            "<p>Your payment has been confirmed. You can now print your ticket!</p>" +
                            "</body></html>",
                            ticketNumber, eventName, amount
                        );
                        
                        JOptionPane.showMessageDialog(
                            null,
                            notificationMessage,
                            "Payment Confirmed",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } else if ("TICKET_READY".equals(notificationType)) {
                    if (parts.length >= 6) {
                        String ticketNumber = parts[1];
                        String eventName = parts[5];
                        
                        String notificationMessage = String.format(
                            "<html><body style='width: 300px;'>" +
                            "<h3>Ticket Ready! 🎫</h3>" +
                            "<p><b>Ticket Number:</b> %s</p>" +
                            "<p><b>Event:</b> %s</p>" +
                            "<p>Your ticket is ready to print!</p>" +
                            "</body></html>",
                            ticketNumber, eventName
                        );
                        
                        JOptionPane.showMessageDialog(
                            null,
                            notificationMessage,
                            "Ticket Ready",
                            JOptionPane.INFORMATION_MESSAGE
                        );
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

