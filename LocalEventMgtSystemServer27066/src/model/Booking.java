package model;

import java.io.Serializable;
import java.sql.Timestamp;
import jakarta.persistence.*;
import jakarta.persistence.Transient;

/**
 * Booking entity with Many-to-One relationships:
 * - Many-to-One with Event
 * - Many-to-One with User
 * 
 * @author 27066
 */
@Entity
@Table(name = "bookings")
public class Booking implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private int bookingId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "booking_date")
    private Timestamp bookingDate;
    
    @Column(name = "payment_status", length = 20)
    private String paymentStatus;
    
    @Column(name = "ticket_number", unique = true, length = 50)
    private String ticketNumber;
    
    @Column(name = "number_of_tickets")
    private int numberOfTickets;
    
    @Column(name = "total_amount")
    private double totalAmount;
    
    @Column(name = "check_in_status")
    private boolean checkInStatus;
    
    @Column(name = "ticket_category", length = 20)
    private String ticketCategory;

    // Constructors
    public Booking() {}

    public Booking(Event event, User user, String paymentStatus, String ticketNumber,
                  int numberOfTickets, double totalAmount) {
        this.event = event;
        this.user = user;
        this.paymentStatus = paymentStatus;
        this.ticketNumber = ticketNumber;
        this.numberOfTickets = numberOfTickets;
        this.totalAmount = totalAmount;
        this.checkInStatus = false;
    }

    // Getters and setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    
    public int getNumberOfTickets() { return numberOfTickets; }
    public void setNumberOfTickets(int numberOfTickets) { this.numberOfTickets = numberOfTickets; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public boolean isCheckInStatus() { return checkInStatus; }
    public void setCheckInStatus(boolean checkInStatus) { this.checkInStatus = checkInStatus; }
    
    public String getTicketCategory() { return ticketCategory; }
    public void setTicketCategory(String ticketCategory) { this.ticketCategory = ticketCategory; }
    
    // Fields for RMI compatibility (received from client)
    // Note: NOT marked as transient so they can be serialized over RMI
    @Transient
    private int eventId;
    
    @Transient
    private int userId;
    
    // Helper methods for backward compatibility
    public int getEventId() {
        if (event != null) {
            return event.getEventId();
        }
        return eventId; // Return transient field if event not loaded
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public int getUserId() {
        if (user != null) {
            return user.getUserId();
        }
        return userId; // Return transient field if user not loaded
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    // Additional helper methods for display (populated by queries)
    // Note: NOT marked as transient so they can be serialized over RMI
    @Transient
    private String eventName;
    @Transient
    private String userName;
    @Transient
    private String eventDate;
    @Transient
    private String eventTime;
    @Transient
    private String eventDescription;
    @Transient
    private String venueName;
    @Transient
    private String venueLocation;
    @Transient
    private String userEmail;
    @Transient
    private String userPhone;
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    
    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    
    public String getVenueLocation() { return venueLocation; }
    public void setVenueLocation(String venueLocation) { this.venueLocation = venueLocation; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
}

