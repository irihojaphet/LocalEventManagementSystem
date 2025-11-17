package dao;

import model.Event;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private Connection connection;
    
    public EventDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public boolean createEvent(Event event) {
        String query = "INSERT INTO events (event_name, event_description, event_date, event_time, " +
                      "venue_id, organizer_id, capacity, ticket_price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getEventDescription());
            stmt.setDate(3, event.getEventDate());
            stmt.setTime(4, event.getEventTime());
            stmt.setInt(5, event.getVenueId());
            stmt.setInt(6, event.getOrganizerId());
            stmt.setInt(7, event.getCapacity());
            stmt.setDouble(8, event.getTicketPrice());
            stmt.setString(9, event.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.*, v.venue_name, u.full_name as organizer_name " +
                      "FROM events e " +
                      "JOIN venues v ON e.venue_id = v.venue_id " +
                      "JOIN users u ON e.organizer_id = u.user_id " +
                      "ORDER BY e.event_date, e.event_time";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public List<Event> getEventsByOrganizer(int organizerId) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.*, v.venue_name, u.full_name as organizer_name " +
                      "FROM events e " +
                      "JOIN venues v ON e.venue_id = v.venue_id " +
                      "JOIN users u ON e.organizer_id = u.user_id " +
                      "WHERE e.organizer_id = ? " +
                      "ORDER BY e.event_date, e.event_time";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, organizerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public List<Event> getScheduledEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.*, v.venue_name, u.full_name as organizer_name " +
                      "FROM events e " +
                      "JOIN venues v ON e.venue_id = v.venue_id " +
                      "JOIN users u ON e.organizer_id = u.user_id " +
                      "WHERE e.status = 'scheduled' AND e.event_date >= CURRENT_DATE " +
                      "ORDER BY e.event_date, e.event_time";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public boolean updateEvent(Event event) {
        String query = "UPDATE events SET event_name = ?, event_description = ?, event_date = ?, " +
                      "event_time = ?, venue_id = ?, capacity = ?, ticket_price = ?, status = ? " +
                      "WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getEventDescription());
            stmt.setDate(3, event.getEventDate());
            stmt.setTime(4, event.getEventTime());
            stmt.setInt(5, event.getVenueId());
            stmt.setInt(6, event.getCapacity());
            stmt.setDouble(7, event.getTicketPrice());
            stmt.setString(8, event.getStatus());
            stmt.setInt(9, event.getEventId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteEvent(int eventId) {
        // Check for existing bookings first
        String checkQuery = "SELECT COUNT(*) FROM bookings WHERE event_id = ? AND payment_status = 'paid'";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, eventId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Cannot delete event with paid bookings
            }
            
            String deleteQuery = "DELETE FROM events WHERE event_id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, eventId);
                return deleteStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getAvailableCapacity(int eventId) {
        String query = "SELECT e.capacity - COALESCE(SUM(b.number_of_tickets), 0) as available " +
                      "FROM events e " +
                      "LEFT JOIN bookings b ON e.event_id = b.event_id AND b.payment_status != 'cancelled' " +
                      "WHERE e.event_id = ? " +
                      "GROUP BY e.capacity";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setEventName(rs.getString("event_name"));
        event.setEventDescription(rs.getString("event_description"));
        event.setEventDate(rs.getDate("event_date"));
        event.setEventTime(rs.getTime("event_time"));
        event.setVenueId(rs.getInt("venue_id"));
        event.setOrganizerId(rs.getInt("organizer_id"));
        event.setCapacity(rs.getInt("capacity"));
        event.setTicketPrice(rs.getDouble("ticket_price"));
        event.setStatus(rs.getString("status"));
        event.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Additional fields
        try {
            event.setVenueName(rs.getString("venue_name"));
            event.setOrganizerName(rs.getString("organizer_name"));
        } catch (SQLException e) {
            // These fields may not exist in all queries
        }
        
        return event;
    }
}