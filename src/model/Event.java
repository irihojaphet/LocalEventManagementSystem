package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Event {
    private int eventId;
    private String eventName;
    private String eventDescription;
    private Date eventDate;
    private Time eventTime;
    private int venueId;
    private int organizerId;
    private int capacity;
    private double ticketPrice; // Default/standard price
    private double vvipPrice;
    private double vipPrice;
    private double casualPrice;
    private String pricingType; // "single" or "category"
    private String status;
    private Timestamp createdAt;
    
    // Additional fields for joins
    private String venueName;
    private String organizerName;

    // Constructors
    public Event() {}

    public Event(String eventName, String eventDescription, Date eventDate, 
                Time eventTime, int venueId, int organizerId, int capacity, 
                double ticketPrice, String status) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venueId = venueId;
        this.organizerId = organizerId;
        this.capacity = capacity;
        this.ticketPrice = ticketPrice;
        this.status = status;
    }

    // Getters and setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    
    public Time getEventTime() { return eventTime; }
    public void setEventTime(Time eventTime) { this.eventTime = eventTime; }
    
    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }
    
    public int getOrganizerId() { return organizerId; }
    public void setOrganizerId(int organizerId) { this.organizerId = organizerId; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    
    public double getVvipPrice() { return vvipPrice; }
    public void setVvipPrice(double vvipPrice) { this.vvipPrice = vvipPrice; }
    
    public double getVipPrice() { return vipPrice; }
    public void setVipPrice(double vipPrice) { this.vipPrice = vipPrice; }
    
    public double getCasualPrice() { return casualPrice; }
    public void setCasualPrice(double casualPrice) { this.casualPrice = casualPrice; }
    
    public String getPricingType() { return pricingType; }
    public void setPricingType(String pricingType) { this.pricingType = pricingType; }
    
    public double getPriceForCategory(String category) {
        if ("category".equals(pricingType)) {
            switch (category.toLowerCase()) {
                case "vvip": return vvipPrice;
                case "vip": return vipPrice;
                case "casual": return casualPrice;
                default: return ticketPrice;
            }
        }
        return ticketPrice;
    }
}