package dao;

import model.Venue;
import service.VenueService;
import util.RMIClientUtil;
import java.util.List;

/**
 * Venue DAO - Client-side wrapper using RMI services
 * This maintains compatibility with existing views
 * 
 * @author 27066
 */
public class VenueDAO {
    private VenueService venueService;
    
    public VenueDAO() {
        venueService = RMIClientUtil.getVenueService();
    }
    
    public List<Venue> getAllVenues() {
        try {
            return venueService != null ? venueService.findAllVenues() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Venue> getAvailableVenues() {
        try {
            return venueService != null ? venueService.findAvailableVenues() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Venue getVenueById(int venueId) {
        try {
            Venue venue = new Venue();
            venue.setVenueId(venueId);
            return venueService != null ? venueService.findVenueById(venue) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean createVenue(Venue venue) {
        try {
            Venue result = venueService != null ? venueService.createVenue(venue) : null;
            return result != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

