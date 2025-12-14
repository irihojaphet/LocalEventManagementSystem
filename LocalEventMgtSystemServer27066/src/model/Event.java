package model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.persistence.Transient;

/**
 * Event entity with relationships:
 * - Many-to-One with Venue
 * - Many-to-One with User (organizer)
 * - One-to-Many with Bookings
 * - Many-to-Many with EventTag
 * 
 * @author 27066
 */
@Entity
@Table(name = "events")
public class Event implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;
    
    @Column(name = "event_date", nullable = false)
    private Date eventDate;
    
    @Column(name = "event_time", nullable = false)
    private Time eventTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @Column(name = "capacity", nullable = false)
    private int capacity;
    
    @Column(name = "ticket_price", nullable = false)
    private double ticketPrice;
    
    @Column(name = "vvip_price")
    private Double vvipPrice;
    
    @Column(name = "vip_price")
    private Double vipPrice;
    
    @Column(name = "casual_price")
    private Double casualPrice;
    
    @Column(name = "pricing_type", length = 20)
    private String pricingType;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    // One-to-Many relationship with Bookings
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
    
    // Many-to-Many relationship with EventTag
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "event_tag_assignments",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<EventTag> tags;

    // Constructors
    public Event() {}

    public Event(String eventName, String eventDescription, Date eventDate, 
                Time eventTime, Venue venue, User organizer, int capacity, 
                double ticketPrice, String status) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        this.organizer = organizer;
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
    
    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }
    
    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }
    
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
    
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    
    public Set<EventTag> getTags() { return tags; }
    public void setTags(Set<EventTag> tags) { this.tags = tags; }
    
    // Transient fields for RMI compatibility (received from client)
    @Transient
    private int venueId;
    
    @Transient
    private int organizerId;
    
    // Helper methods for backward compatibility
    public int getVenueId() {
        if (venue != null) {
            return venue.getVenueId();
        }
        return venueId; // Return transient field if venue not loaded
    }
    
    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }
    
    public int getOrganizerId() {
        if (organizer != null) {
            return organizer.getUserId();
        }
        return organizerId; // Return transient field if organizer not loaded
    }
    
    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }
    
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

