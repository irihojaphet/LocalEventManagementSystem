package dao;

import java.util.List;
import model.Booking;
import model.Event;
import model.User;
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
    
    // READ - Find all bookings
    public List<Booking> findAllBookings() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Booking> query = ss.createQuery(
                "FROM Booking ORDER BY bookingDate DESC", 
                Booking.class
            );
            List<Booking> bookings = query.list();
            ss.close();
            return bookings;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find bookings by user
    public List<Booking> findBookingsByUser(int userId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Booking> query = ss.createQuery(
                "FROM Booking WHERE user.userId = :userId ORDER BY bookingDate DESC", 
                Booking.class
            );
            query.setParameter("userId", userId);
            List<Booking> bookings = query.list();
            ss.close();
            return bookings;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
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

