package dao;

import java.sql.Date;
import java.util.List;
import model.Event;
import model.Venue;
import model.User;
import org.hibernate.*;
import org.hibernate.query.Query;

/**
 * Event DAO with Hibernate implementation
 * 
 * @author 27066
 */
public class EventDao {
    
    // CREATE
    public Event createEvent(Event eventObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // If venue/organizer are not set but IDs are, load them
            if (eventObj.getVenue() == null && eventObj.getVenueId() > 0) {
                Venue venue = ss.get(Venue.class, eventObj.getVenueId());
                eventObj.setVenue(venue);
            }
            if (eventObj.getOrganizer() == null && eventObj.getOrganizerId() > 0) {
                User organizer = ss.get(User.class, eventObj.getOrganizerId());
                eventObj.setOrganizer(organizer);
            }
            
            ss.save(eventObj);
            tr.commit();
            ss.close();
            return eventObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // UPDATE
    public Event updateEvent(Event eventObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // Load existing event to preserve relationships
            Event existing = ss.get(Event.class, eventObj.getEventId());
            if (existing == null) {
                tr.rollback();
                ss.close();
                return null;
            }
            
            // Update venue if changed
            if (eventObj.getVenueId() > 0 && 
                (existing.getVenue() == null || existing.getVenue().getVenueId() != eventObj.getVenueId())) {
                Venue venue = ss.get(Venue.class, eventObj.getVenueId());
                existing.setVenue(venue);
            }
            
            // Update other fields
            existing.setEventName(eventObj.getEventName());
            existing.setEventDescription(eventObj.getEventDescription());
            existing.setEventDate(eventObj.getEventDate());
            existing.setEventTime(eventObj.getEventTime());
            existing.setCapacity(eventObj.getCapacity());
            existing.setTicketPrice(eventObj.getTicketPrice());
            existing.setVvipPrice(eventObj.getVvipPrice());
            existing.setVipPrice(eventObj.getVipPrice());
            existing.setCasualPrice(eventObj.getCasualPrice());
            existing.setPricingType(eventObj.getPricingType());
            existing.setStatus(eventObj.getStatus());
            
            ss.update(existing);
            tr.commit();
            ss.close();
            return existing;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // DELETE
    public Event deleteEvent(Event eventObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // Load the event to check if it's expired
            Event existingEvent = ss.get(Event.class, eventObj.getEventId());
            if (existingEvent == null) {
                tr.rollback();
                ss.close();
                return null;
            }
            
            // Check if event is expired (event date has passed)
            boolean isExpired = false;
            if (existingEvent.getEventDate() != null) {
                java.util.Date today = new java.util.Date();
                java.util.Date eventDate = new java.util.Date(existingEvent.getEventDate().getTime());
                isExpired = eventDate.before(today);
            }
            
            // Only check for paid bookings if event is NOT expired
            // Expired events can be deleted even with paid bookings (event has passed, tickets used)
            if (!isExpired) {
                Query<Long> checkQuery = ss.createQuery(
                    "SELECT COUNT(*) FROM Booking WHERE event.eventId = :eventId AND paymentStatus = 'paid'", 
                    Long.class
                );
                checkQuery.setParameter("eventId", eventObj.getEventId());
                Long count = checkQuery.uniqueResult();
                
                if (count != null && count > 0) {
                    tr.rollback();
                    ss.close();
                    return null; // Cannot delete non-expired event with paid bookings
                }
            }
            
            ss.delete(existingEvent);
            tr.commit();
            ss.close();
            return eventObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by ID
    public Event findEventById(Event eventObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Event event = ss.get(Event.class, eventObj.getEventId());
            ss.close();
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by ID (int)
    public Event findEventById(int eventId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Event event = ss.get(Event.class, eventId);
            ss.close();
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find all events
    public List<Event> findAllEvents() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Event> query = ss.createQuery(
                "FROM Event ORDER BY eventDate, eventTime", 
                Event.class
            );
            List<Event> events = query.list();
            ss.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find events by organizer
    public List<Event> findEventsByOrganizer(int organizerId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Event> query = ss.createQuery(
                "FROM Event WHERE organizer.userId = :organizerId ORDER BY eventDate, eventTime", 
                Event.class
            );
            query.setParameter("organizerId", organizerId);
            List<Event> events = query.list();
            ss.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find scheduled events
    public List<Event> findScheduledEvents() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Event> query = ss.createQuery(
                "FROM Event WHERE status = 'scheduled' AND eventDate >= CURRENT_DATE ORDER BY eventDate, eventTime", 
                Event.class
            );
            List<Event> events = query.list();
            ss.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Get available capacity
    public int getAvailableCapacity(int eventId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            
            // Get event capacity
            Event event = ss.get(Event.class, eventId);
            if (event == null) {
                ss.close();
                return 0;
            }
            
            int totalCapacity = event.getCapacity();
            
            // Count booked tickets
            Query<Long> bookingQuery = ss.createQuery(
                "SELECT COALESCE(SUM(numberOfTickets), 0) FROM Booking WHERE event.eventId = :eventId AND paymentStatus != 'cancelled'", 
                Long.class
            );
            bookingQuery.setParameter("eventId", eventId);
            Long bookedTickets = bookingQuery.uniqueResult();
            
            ss.close();
            
            int booked = bookedTickets != null ? bookedTickets.intValue() : 0;
            return totalCapacity - booked;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}

