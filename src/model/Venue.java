package model;

public class Venue {
    private int venueId;
    private String venueName;
    private String location;
    private int capacity;
    private String contactPhone;
    private double rentalCost;
    private String facilities;
    private String availabilityStatus;

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

    @Override
    public String toString() {
        return venueName;
    }
}