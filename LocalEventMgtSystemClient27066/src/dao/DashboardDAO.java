package dao;

import model.Booking;
import model.Event;
import service.BookingService;
import service.EventService;
import util.RMIClientUtil;
import java.util.List;

/**
 * Dashboard DAO - Client-side wrapper using RMI services
 * Provides aggregated data for dashboard views
 * 
 * @author 27066
 */
public class DashboardDAO {
    private EventService eventService;
    private BookingService bookingService;
    
    public DashboardDAO() {
        eventService = RMIClientUtil.getEventService();
        bookingService = RMIClientUtil.getBookingService();
    }
    
    public int getTotalEvents() {
        try {
            List<Event> events = eventService != null ? eventService.findAllEvents() : null;
            return events != null ? events.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int getTotalBookings() {
        try {
            List<Booking> bookings = bookingService != null ? bookingService.findAllBookings() : null;
            return bookings != null ? bookings.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int getScheduledEventsCount() {
        try {
            List<Event> events = eventService != null ? eventService.findScheduledEvents() : null;
            return events != null ? events.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int getUpcomingEventsCount() {
        return getScheduledEventsCount(); // Same as scheduled events
    }
    
    public int getTotalUsers() {
        try {
            // This would require UserService - for now return 0 or implement if needed
            // You can add UserService to DashboardDAO if needed
            return 0; // Placeholder
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public double getTotalRevenue() {
        try {
            List<Booking> bookings = bookingService != null ? bookingService.findAllBookings() : null;
            if (bookings == null) return 0.0;
            
            double total = 0.0;
            for (Booking booking : bookings) {
                if ("paid".equals(booking.getPaymentStatus())) {
                    total += booking.getTotalAmount();
                }
            }
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}

