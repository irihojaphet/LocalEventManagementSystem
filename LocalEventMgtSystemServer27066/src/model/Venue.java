package model;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;

/**
 * Venue entity with One-to-Many relationship with Events
 * 
 * @author 27066
 */
@Entity
@Table(name = "venues")
public class Venue implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private int venueId;
    
    @Column(name = "venue_name", nullable = false, length = 100)
    private String venueName;
    
    @Column(name = "location", nullable = false, length = 200)
    private String location;
    
    @Column(name = "capacity", nullable = false)
    private int capacity;
    
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    @Column(name = "rental_cost", nullable = false)
    private double rentalCost;
    
    @Column(name = "facilities", columnDefinition = "TEXT")
    private String facilities;
    
    @Column(name = "availability_status", length = 20)
    private String availabilityStatus;
    
    // One-to-Many relationship with Events
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    // Constructors
    public Venue() {}

    public Venue(String venueName, String location, int capacity, String contactPhone, 
                double rentalCost, String facilities, String availabilityStatus) {
        this.venueName = venueName;
        this.location = location;
        this.capacity = capacity;
        this.contactPhone = contactPhone;
        this.rentalCost = rentalCost;
        this.facilities = facilities;
        this.availabilityStatus = availabilityStatus;
    }

    // Getters and setters
    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }
    
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public double getRentalCost() { return rentalCost; }
    public void setRentalCost(double rentalCost) { this.rentalCost = rentalCost; }
    
    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
    
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    
    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }

    @Override
    public String toString() {
        return venueName;
    }
}

