package model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Event model for Client (POJO without Hibernate annotations)
 * 
 * @author 27066
 */
public class Event implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int eventId;
    private String eventName;
    private String eventDescription;
    private Date eventDate;
    private Time eventTime;
    private int venueId;
    private int organizerId;
    private int capacity;
    private double ticketPrice;
    private Double vvipPrice;  // Must match server (Double wrapper for nullable fields)
    private Double vipPrice;   // Must match server (Double wrapper for nullable fields)
    private Double casualPrice; // Must match server (Double wrapper for nullable fields)
    private String pricingType;
    private String status;
    private Timestamp createdAt;
    
    // Additional fields for display
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
    
    public Double getVvipPrice() { return vvipPrice; }
    public void setVvipPrice(Double vvipPrice) { this.vvipPrice = vvipPrice; }
    
    public Double getVipPrice() { return vipPrice; }
    public void setVipPrice(Double vipPrice) { this.vipPrice = vipPrice; }
    
    public Double getCasualPrice() { return casualPrice; }
    public void setCasualPrice(Double casualPrice) { this.casualPrice = casualPrice; }
    
    public String getPricingType() { return pricingType; }
    public void setPricingType(String pricingType) { this.pricingType = pricingType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    
    public double getPriceForCategory(String category) {
        if ("category".equals(pricingType)) {
            switch (category.toLowerCase()) {
                case "vvip": return vvipPrice != null ? vvipPrice : ticketPrice;
                case "vip": return vipPrice != null ? vipPrice : ticketPrice;
                case "casual": return casualPrice != null ? casualPrice : ticketPrice;
                default: return ticketPrice;
            }
        }
        return ticketPrice;
    }
}

