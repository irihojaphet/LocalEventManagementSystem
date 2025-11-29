package model;

import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int eventId;
    private int userId;
    private Timestamp bookingDate;
    private String paymentStatus;
    private String ticketNumber;
    private int numberOfTickets;
    private double totalAmount;
    private boolean checkInStatus;
    private String ticketCategory; // VVIP, VIP, Casual, or null for single price
    
    // Additional fields for joins
    private String eventName;
    private String userName;
    private String eventDate;
    private String eventTime;
    private String eventDescription;
    private String venueName;
    private String venueLocation;
    private String userEmail;
    private String userPhone;

    // Constructors
    public Booking() {}

    public Booking(int eventId, int userId, String paymentStatus, String ticketNumber,
                  int numberOfTickets, double totalAmount) {
        this.eventId = eventId;
        this.userId = userId;
        this.paymentStatus = paymentStatus;
        this.ticketNumber = ticketNumber;
        this.numberOfTickets = numberOfTickets;
        this.totalAmount = totalAmount;
        this.checkInStatus = false;
    }

    // Getters and setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
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
    
    public String getTicketCategory() { return ticketCategory; }
    public void setTicketCategory(String ticketCategory) { this.ticketCategory = ticketCategory; }
}