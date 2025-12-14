package dao;

import java.util.List;
import model.Booking;
import model.Event;
import model.User;
import model.Venue;
import org.hibernate.*;
import org.hibernate.query.Query;

/**
 * Booking DAO with Hibernate implementation
 * 
 * @author 27066
 */
public class BookingDao {
    
    // CREATE
    public Booking createBooking(Booking bookingObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // If event/user are not set but IDs are, load them
            if (bookingObj.getEvent() == null && bookingObj.getEventId() > 0) {
                Event event = ss.get(Event.class, bookingObj.getEventId());
                bookingObj.setEvent(event);
            }
            if (bookingObj.getUser() == null && bookingObj.getUserId() > 0) {
                User user = ss.get(User.class, bookingObj.getUserId());
                bookingObj.setUser(user);
            }
            
            ss.save(bookingObj);
            tr.commit();
            ss.close();
            return bookingObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // UPDATE
    public Booking updateBooking(Booking bookingObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(bookingObj);
            tr.commit();
            ss.close();
            return bookingObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // DELETE
    public Booking deleteBooking(Booking bookingObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(bookingObj);
            tr.commit();
            ss.close();
            return bookingObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by ID
    public Booking findBookingById(Booking bookingObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Booking booking = ss.get(Booking.class, bookingObj.getBookingId());
            ss.close();
            return booking;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by ID (int)
    public Booking findBookingById(int bookingId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Booking booking = ss.get(Booking.class, bookingId);
            ss.close();
            return booking;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find all bookings with display data populated
    public List<Booking> findAllBookings() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            // Use JOIN FETCH to eagerly load relationships
            Query<Booking> query = ss.createQuery(
                "SELECT DISTINCT b FROM Booking b " +
                "LEFT JOIN FETCH b.event e " +
                "LEFT JOIN FETCH b.user u " +
                "LEFT JOIN FETCH e.venue v " +
                "ORDER BY b.bookingDate DESC", 
                Booking.class
            );
            List<Booking> bookings = query.list();
            
            // Populate display fields while session is open
            for (Booking booking : bookings) {
                populateDisplayFields(booking, ss);
            }
            
            ss.close();
            return bookings;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find bookings by user with display data populated
    public List<Booking> findBookingsByUser(int userId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            // Use JOIN FETCH to eagerly load relationships
            Query<Booking> query = ss.createQuery(
                "SELECT DISTINCT b FROM Booking b " +
                "LEFT JOIN FETCH b.event e " +
                "LEFT JOIN FETCH b.user u " +
                "LEFT JOIN FETCH e.venue v " +
                "WHERE b.user.userId = :userId " +
                "ORDER BY b.bookingDate DESC", 
                Booking.class
            );
            query.setParameter("userId", userId);
            List<Booking> bookings = query.list();
            
            // Populate display fields while session is open
            for (Booking booking : bookings) {
                populateDisplayFields(booking, ss);
            }
            
            ss.close();
            return bookings;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // Helper method to populate display fields while session is open
    private void populateDisplayFields(Booking booking, Session session) {
        try {
            // Force initialization of lazy-loaded relationships
            if (booking.getEvent() != null) {
                Event event = booking.getEvent();
                // Access fields to trigger lazy loading
                String eventName = event.getEventName();
                String eventDesc = event.getEventDescription();
                
                // Only set if not null/empty - let null pass through for client-side handling
                if (eventName != null && !eventName.trim().isEmpty()) {
                    booking.setEventName(eventName);
                }
                if (eventDesc != null && !eventDesc.trim().isEmpty()) {
                    booking.setEventDescription(eventDesc);
                }
                
                // Set event date - format it properly
                if (event.getEventDate() != null) {
                    booking.setEventDate(event.getEventDate().toString());
                } else {
                    // If event date is null, try to use booking date as fallback
                    if (booking.getBookingDate() != null) {
                        booking.setEventDate(booking.getBookingDate().toString());
                    }
                }
                if (event.getEventTime() != null) {
                    booking.setEventTime(event.getEventTime().toString());
                }
                
                // Load venue if available
                Venue venue = event.getVenue();
                if (venue != null) {
                    String venueName = venue.getVenueName();
                    if (venueName != null && !venueName.trim().isEmpty()) {
                        booking.setVenueName(venueName);
                    }
                    String venueLoc = venue.getLocation();
                    if (venueLoc != null && !venueLoc.trim().isEmpty()) {
                        booking.setVenueLocation(venueLoc);
                    }
                }
            }
            
            if (booking.getUser() != null) {
                User user = booking.getUser();
                // Access fields to trigger lazy loading
                String fullName = user.getFullName();
                String email = user.getEmail();
                String phone = user.getPhoneNumber();
                
                // Only set if not null/empty - let null pass through for client-side handling
                if (fullName != null && !fullName.trim().isEmpty()) {
                    booking.setUserName(fullName);
                }
                if (email != null && !email.trim().isEmpty()) {
                    booking.setUserEmail(email);
                }
                if (phone != null && !phone.trim().isEmpty()) {
                    booking.setUserPhone(phone);
                }
            }
        } catch (Exception e) {
            // If any field access fails, log but continue
            System.err.println("Error populating display fields for booking " + booking.getBookingId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // UPDATE - Cancel booking
    public boolean cancelBooking(int bookingId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            Booking booking = ss.get(Booking.class, bookingId);
            if (booking != null) {
                booking.setPaymentStatus("cancelled");
                ss.update(booking);
                tr.commit();
                ss.close();
                return true;
            }
            tr.rollback();
            ss.close();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    // UPDATE - Update payment status
    public boolean updatePaymentStatus(int bookingId, String status) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            Booking booking = ss.get(Booking.class, bookingId);
            if (booking != null) {
                String oldStatus = booking.getPaymentStatus();
                booking.setPaymentStatus(status);
                ss.update(booking);
                tr.commit();
                
                // If payment was confirmed (changed to 'paid'), trigger notification
                if ("paid".equalsIgnoreCase(status) && !"paid".equalsIgnoreCase(oldStatus)) {
                    // Load related entities for notification
                    booking = ss.get(Booking.class, bookingId);
                    if (booking.getEvent() != null && booking.getUser() != null) {
                        // Notification will be sent by service layer
                        // We return the booking with loaded relationships
                    }
                }
                
                ss.close();
                return true;
            }
            tr.rollback();
            ss.close();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    // Get booking with all relationships loaded for notifications
    public Booking getBookingWithRelations(int bookingId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Booking booking = ss.get(Booking.class, bookingId);
            if (booking != null) {
                // Force load relationships while session is open
                if (booking.getEvent() != null) {
                    booking.getEvent().getEventName(); // Trigger lazy load
                    if (booking.getEvent().getVenue() != null) {
                        booking.getEvent().getVenue().getVenueName(); // Trigger lazy load
                    }
                    if (booking.getEvent().getOrganizer() != null) {
                        booking.getEvent().getOrganizer().getFullName(); // Trigger lazy load
                    }
                }
                if (booking.getUser() != null) {
                    booking.getUser().getFullName(); // Trigger lazy load
                    booking.getUser().getEmail(); // Trigger lazy load
                    booking.getUser().getPhoneNumber(); // Trigger lazy load
                }
            }
            // Keep session open - will be closed by caller or use different approach
            // For now, we'll return and let service handle it
            Booking result = booking;
            ss.close();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // Generate ticket number
    public String generateTicketNumber() {
        return "TKT-" + System.currentTimeMillis();
    }
}

