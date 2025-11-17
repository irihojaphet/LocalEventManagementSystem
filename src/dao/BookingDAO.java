package dao;

import model.Booking;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private Connection connection;
    
    public BookingDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public boolean createBooking(Booking booking) {
        String query = "INSERT INTO bookings (event_id, user_id, payment_status, ticket_number, " +
                      "number_of_tickets, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, booking.getEventId());
            stmt.setInt(2, booking.getUserId());
            stmt.setString(3, booking.getPaymentStatus());
            stmt.setString(4, booking.getTicketNumber());
            stmt.setInt(5, booking.getNumberOfTickets());
            stmt.setDouble(6, booking.getTotalAmount());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, e.event_name, e.event_date::text, u.full_name " +
                      "FROM bookings b " +
                      "JOIN events e ON b.event_id = e.event_id " +
                      "JOIN users u ON b.user_id = u.user_id " +
                      "WHERE b.user_id = ? " +
                      "ORDER BY b.booking_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, e.event_name, e.event_date::text, u.full_name " +
                      "FROM bookings b " +
                      "JOIN events e ON b.event_id = e.event_id " +
                      "JOIN users u ON b.user_id = u.user_id " +
                      "ORDER BY b.booking_date DESC";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public boolean cancelBooking(int bookingId) {
        String query = "UPDATE bookings SET payment_status = 'cancelled' WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePaymentStatus(int bookingId, String status) {
        String query = "UPDATE bookings SET payment_status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String generateTicketNumber() {
        return "TKT-" + System.currentTimeMillis();
    }
    
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setEventId(rs.getInt("event_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setTicketNumber(rs.getString("ticket_number"));
        booking.setNumberOfTickets(rs.getInt("number_of_tickets"));
        booking.setTotalAmount(rs.getDouble("total_amount"));
        booking.setCheckInStatus(rs.getBoolean("check_in_status"));
        
        // Additional fields
        try {
            booking.setEventName(rs.getString("event_name"));
            booking.setEventDate(rs.getString("event_date"));
            booking.setUserName(rs.getString("full_name"));
        } catch (SQLException e) {
            // These fields may not exist in all queries
        }
        
        return booking;
    }
}