package controller;

import model.Booking;
import model.Event;
import service.BookingService;
import service.EventService;
import util.RMIClientUtil;
import javax.swing.JOptionPane;

/**
 * Booking Controller using RMI services
 * 
 * @author 27066
 */
public class BookingController {
    private BookingService bookingService;
    private EventService eventService;
    
    public BookingController() {
        bookingService = RMIClientUtil.getBookingService();
        eventService = RMIClientUtil.getEventService();
    }
    
    public boolean createBooking(int eventId, int userId, int numberOfTickets, String ticketCategory) {
        try {
            if (bookingService == null || eventService == null) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to server.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Check available capacity
            int available = eventService.getAvailableCapacity(eventId);
            if (available < numberOfTickets) {
                JOptionPane.showMessageDialog(null, 
                    "Not enough tickets available. Only " + available + " tickets left.", 
                    "Capacity Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Get event details for pricing
            Event event = new Event();
            event.setEventId(eventId);
            Event eventDetails = eventService.findEventById(event);
            if (eventDetails == null) {
                JOptionPane.showMessageDialog(null, 
                    "Event not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Calculate total amount based on category
            double pricePerTicket;
            if ("category".equals(eventDetails.getPricingType()) && ticketCategory != null) {
                pricePerTicket = eventDetails.getPriceForCategory(ticketCategory);
            } else {
                pricePerTicket = eventDetails.getTicketPrice();
                ticketCategory = null; // No category for single pricing
            }
            
            double totalAmount = pricePerTicket * numberOfTickets;
            
            // Create booking
            Booking booking = new Booking();
            booking.setEventId(eventId);
            booking.setUserId(userId);
            booking.setPaymentStatus("pending");
            booking.setTicketNumber(bookingService.generateTicketNumber());
            booking.setNumberOfTickets(numberOfTickets);
            booking.setTotalAmount(totalAmount);
            booking.setTicketCategory(ticketCategory);
            
            Booking result = bookingService.createBooking(booking);
            if (result != null) {
                JOptionPane.showMessageDialog(null, 
                    "Booking created successfully! Ticket Number: " + result.getTicketNumber(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Failed to create booking.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error creating booking: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean cancelBooking(int bookingId) {
        try {
            if (bookingService == null) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to server.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            boolean result = bookingService.cancelBooking(bookingId);
            if (result) {
                JOptionPane.showMessageDialog(null, 
                    "Booking cancelled successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Failed to cancel booking.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error cancelling booking: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
}

