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
    
    public boolean createBooking(int eventId, int userId, int numberOfTickets, String ticketCategory) {
        // Check available capacity
        int available = eventDAO.getAvailableCapacity(eventId);
        if (available < numberOfTickets) {
            return false; // Not enough tickets available
        }
        
        // Get event details for pricing
        Event event = eventDAO.getEventById(eventId);
        if (event == null) return false;
        
        // Calculate total amount based on category
        double pricePerTicket;
        if ("category".equals(event.getPricingType()) && ticketCategory != null) {
            pricePerTicket = event.getPriceForCategory(ticketCategory);
        } else {
            pricePerTicket = event.getTicketPrice();
            ticketCategory = null; // No category for single pricing
        }
        
        double totalAmount = pricePerTicket * numberOfTickets;
        
        // Create booking
        Booking booking = new Booking();
        booking.setEventId(eventId);
        booking.setUserId(userId);
        booking.setPaymentStatus("pending");
        booking.setTicketNumber(bookingDAO.generateTicketNumber());
        booking.setNumberOfTickets(numberOfTickets);
        booking.setTotalAmount(totalAmount);
        booking.setTicketCategory(ticketCategory);
        
        return bookingDAO.createBooking(booking);
    }
    
    public boolean cancelBooking(int bookingId) {
        // Business rule: Can cancel up to 24 hours before event
        // For simplicity, we're allowing cancellation anytime
        return bookingDAO.cancelBooking(bookingId);
    }
}