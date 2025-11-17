package dao;

import model.Venue;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {
    private Connection connection;
    
    public VenueDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public boolean createVenue(Venue venue) {
        String query = "INSERT INTO venues (venue_name, location, capacity, contact_phone, " +
                      "rental_cost, facilities, availability_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, venue.getVenueName());
            stmt.setString(2, venue.getLocation());
            stmt.setInt(3, venue.getCapacity());
            stmt.setString(4, venue.getContactPhone());
            stmt.setDouble(5, venue.getRentalCost());
            stmt.setString(6, venue.getFacilities());
            stmt.setString(7, venue.getAvailabilityStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Venue> getAllVenues() {
        List<Venue> venues = new ArrayList<>();
        String query = "SELECT * FROM venues ORDER BY venue_name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                venues.add(extractVenueFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    public List<Venue> getAvailableVenues() {
        List<Venue> venues = new ArrayList<>();
        String query = "SELECT * FROM venues WHERE availability_status = 'available' ORDER BY venue_name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                venues.add(extractVenueFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    public boolean updateVenue(Venue venue) {
        String query = "UPDATE venues SET venue_name = ?, location = ?, capacity = ?, " +
                      "contact_phone = ?, rental_cost = ?, facilities = ?, availability_status = ? " +
                      "WHERE venue_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, venue.getVenueName());
            stmt.setString(2, venue.getLocation());
            stmt.setInt(3, venue.getCapacity());
            stmt.setString(4, venue.getContactPhone());
            stmt.setDouble(5, venue.getRentalCost());
            stmt.setString(6, venue.getFacilities());
            stmt.setString(7, venue.getAvailabilityStatus());
            stmt.setInt(8, venue.getVenueId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteVenue(int venueId) {
        String query = "DELETE FROM venues WHERE venue_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, venueId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Venue extractVenueFromResultSet(ResultSet rs) throws SQLException {
        Venue venue = new Venue();
        venue.setVenueId(rs.getInt("venue_id"));
        venue.setVenueName(rs.getString("venue_name"));
        venue.setLocation(rs.getString("location"));
        venue.setCapacity(rs.getInt("capacity"));
        venue.setContactPhone(rs.getString("contact_phone"));
        venue.setRentalCost(rs.getDouble("rental_cost"));
        venue.setFacilities(rs.getString("facilities"));
        venue.setAvailabilityStatus(rs.getString("availability_status"));
        return venue;
    }
}