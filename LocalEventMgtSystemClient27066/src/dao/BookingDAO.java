package dao;

import controller.BookingController;
import model.Booking;
import service.BookingService;
import util.RMIClientUtil;
import java.util.List;

/**
 * Booking DAO - Client-side wrapper using RMI services
 * This maintains compatibility with existing views
 * 
 * @author 27066
 */
public class BookingDAO {
    private BookingService bookingService;
    private BookingController bookingController;
    
    public BookingDAO() {
        bookingService = RMIClientUtil.getBookingService();
        bookingController = new BookingController();
    }
    
    public List<Booking> getAllBookings() {
        try {
            return bookingService != null ? bookingService.findAllBookings() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Booking> getBookingsByUser(int userId) {
        try {
            return bookingService != null ? bookingService.findBookingsByUser(userId) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean createBooking(Booking booking) {
        try {
            Booking result = bookingService != null ? bookingService.createBooking(booking) : null;
            return result != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cancelBooking(int bookingId) {
        try {
            return bookingService != null ? bookingService.cancelBooking(bookingId) : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String generateTicketNumber() {
        try {
            return bookingService != null ? bookingService.generateTicketNumber() : "TKT-" + System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return "TKT-" + System.currentTimeMillis();
        }
    }
    
    public boolean updatePaymentStatus(int bookingId, String status) {
        try {
            return bookingService != null ? bookingService.updatePaymentStatus(bookingId, status) : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean reactivateBooking(int bookingId) {
        try {
            // Reactivate by setting status back to pending
            return bookingService != null ? bookingService.updatePaymentStatus(bookingId, "pending") : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Booking getBookingById(int bookingId) {
        try {
            Booking booking = new Booking();
            booking.setBookingId(bookingId);
            return bookingService != null ? bookingService.findBookingById(booking) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

