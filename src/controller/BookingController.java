package controller;

import dao.BookingDAO;
import dao.EventDAO;
import model.Booking;
import model.Event;

public class BookingController {
    private BookingDAO bookingDAO;
    private EventDAO eventDAO;
    
    public BookingController() {
        bookingDAO = new BookingDAO();
        eventDAO = new EventDAO();
    }
    
    public boolean createBooking(int eventId, int userId, int numberOfTickets) {
        // Check available capacity
        int available = eventDAO.getAvailableCapacity(eventId);
        if (available < numberOfTickets) {
            return false; // Not enough tickets available
        }
        
        // Get event details for pricing
        Event event = null;
        for (Event e : eventDAO.getAllEvents()) {
            if (e.getEventId() == eventId) {
                event = e;
                break;
            }
        }
        
        if (event == null) return false;
        
        // Calculate total amount
        double totalAmount = event.getTicketPrice() * numberOfTickets;
        
        // Create booking
        Booking booking = new Booking();
        booking.setEventId(eventId);
        booking.setUserId(userId);
        booking.setPaymentStatus("pending");
        booking.setTicketNumber(bookingDAO.generateTicketNumber());
        booking.setNumberOfTickets(numberOfTickets);
        booking.setTotalAmount(totalAmount);
        
        return bookingDAO.createBooking(booking);
    }
    
    public boolean cancelBooking(int bookingId) {
        // Business rule: Can cancel up to 24 hours before event
        // For simplicity, we're allowing cancellation anytime
        return bookingDAO.cancelBooking(bookingId);
    }
}