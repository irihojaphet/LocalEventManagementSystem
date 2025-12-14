package util;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import model.Booking;
import model.User;
import model.Event;

/**
 * Notification Service using ActiveMQ Message Broker
 * Sends notifications when payment is confirmed
 * 
 * @author 27066
 */
public class NotificationService {
    
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "payment.notifications";
    
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    
    private static NotificationService instance;
    
    private NotificationService() {
        try {
            connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(QUEUE_NAME);
            producer = session.createProducer(queue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        } catch (JMSException e) {
            System.err.println("Failed to initialize NotificationService: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    /**
     * Send payment confirmation notification
     */
    public void sendPaymentConfirmationNotification(Booking booking, User user, Event event) {
        try {
            if (producer == null) {
                System.err.println("Notification producer not initialized. ActiveMQ may not be running.");
                return;
            }
            
            // Create notification message
            String message = String.format(
                "PAYMENT_CONFIRMED|%d|%s|%s|%s|%s|%s|%.2f|%s",
                booking.getBookingId(),
                booking.getTicketNumber(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFullName(),
                event.getEventName(),
                booking.getTotalAmount(),
                booking.getPaymentStatus()
            );
            
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setStringProperty("notificationType", "PAYMENT_CONFIRMED");
            textMessage.setStringProperty("userId", String.valueOf(user.getUserId()));
            textMessage.setStringProperty("bookingId", String.valueOf(booking.getBookingId()));
            textMessage.setStringProperty("targetRole", "customer"); // For customers
            
            producer.send(textMessage);
            System.out.println("Payment confirmation notification sent for booking: " + booking.getTicketNumber());
            
        } catch (JMSException e) {
            System.err.println("Failed to send payment confirmation notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send ticket ready notification
     */
    public void sendTicketReadyNotification(Booking booking, User user, Event event) {
        try {
            if (producer == null) {
                System.err.println("Notification producer not initialized. ActiveMQ may not be running.");
                return;
            }
            
            String message = String.format(
                "TICKET_READY|%d|%s|%s|%s|%s|%s",
                booking.getBookingId(),
                booking.getTicketNumber(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFullName(),
                event.getEventName()
            );
            
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setStringProperty("notificationType", "TICKET_READY");
            textMessage.setStringProperty("userId", String.valueOf(user.getUserId()));
            textMessage.setStringProperty("bookingId", String.valueOf(booking.getBookingId()));
            textMessage.setStringProperty("targetRole", "customer"); // For customers
            
            producer.send(textMessage);
            System.out.println("Ticket ready notification sent for booking: " + booking.getTicketNumber());
            
        } catch (JMSException e) {
            System.err.println("Failed to send ticket ready notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send new event notification to all users
     */
    public void sendNewEventNotification(Event event) {
        try {
            if (producer == null) {
                System.err.println("Notification producer not initialized. ActiveMQ may not be running.");
                return;
            }
            
            String message = String.format(
                "NEW_EVENT|%d|%s|%s|%s|%s",
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription() != null ? event.getEventDescription() : "",
                event.getEventDate() != null ? event.getEventDate().toString() : "",
                event.getEventTime() != null ? event.getEventTime().toString() : ""
            );
            
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setStringProperty("notificationType", "NEW_EVENT");
            textMessage.setStringProperty("eventId", String.valueOf(event.getEventId()));
            textMessage.setStringProperty("targetRole", "customer"); // For all customers
            
            producer.send(textMessage);
            System.out.println("New event notification sent: " + event.getEventName());
            
        } catch (JMSException e) {
            System.err.println("Failed to send new event notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send new booking notification to admin
     */
    public void sendNewBookingNotification(Booking booking, User user, Event event) {
        try {
            if (producer == null) {
                System.err.println("Notification producer not initialized. ActiveMQ may not be running.");
                return;
            }
            
            String message = String.format(
                "NEW_BOOKING|%d|%d|%s|%s|%s|%s|%.2f|%s",
                booking.getBookingId(),
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                event.getEventName(),
                booking.getTicketNumber(),
                booking.getTotalAmount(),
                booking.getPaymentStatus()
            );
            
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setStringProperty("notificationType", "NEW_BOOKING");
            textMessage.setStringProperty("bookingId", String.valueOf(booking.getBookingId()));
            textMessage.setStringProperty("targetRole", "admin"); // For admin
            
            producer.send(textMessage);
            System.out.println("New booking notification sent to admin for booking: " + booking.getTicketNumber());
            
        } catch (JMSException e) {
            System.err.println("Failed to send new booking notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send booking approved notification to user
     */
    public void sendBookingApprovedNotification(Booking booking, User user, Event event) {
        try {
            if (producer == null) {
                System.err.println("Notification producer not initialized. ActiveMQ may not be running.");
                return;
            }
            
            String message = String.format(
                "BOOKING_APPROVED|%d|%s|%s|%s|%.2f",
                booking.getBookingId(),
                booking.getTicketNumber(),
                user.getFullName(),
                event.getEventName(),
                booking.getTotalAmount()
            );
            
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setStringProperty("notificationType", "BOOKING_APPROVED");
            textMessage.setStringProperty("userId", String.valueOf(user.getUserId()));
            textMessage.setStringProperty("bookingId", String.valueOf(booking.getBookingId()));
            textMessage.setStringProperty("targetRole", "customer"); // For customers
            
            producer.send(textMessage);
            System.out.println("Booking approved notification sent for booking: " + booking.getTicketNumber());
            
        } catch (JMSException e) {
            System.err.println("Failed to send booking approved notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send event expired notification to admin
     */
    public void sendEventExpiredNotification(Event event) {
        try {
            if (producer == null) {
                System.err.println("Notification producer not initialized. ActiveMQ may not be running.");
                return;
            }
            
            String message = String.format(
                "EVENT_EXPIRED|%d|%s|%s",
                event.getEventId(),
                event.getEventName(),
                event.getEventDate() != null ? event.getEventDate().toString() : ""
            );
            
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setStringProperty("notificationType", "EVENT_EXPIRED");
            textMessage.setStringProperty("eventId", String.valueOf(event.getEventId()));
            textMessage.setStringProperty("targetRole", "admin"); // For admin
            
            producer.send(textMessage);
            System.out.println("Event expired notification sent for event: " + event.getEventName());
            
        } catch (JMSException e) {
            System.err.println("Failed to send event expired notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

