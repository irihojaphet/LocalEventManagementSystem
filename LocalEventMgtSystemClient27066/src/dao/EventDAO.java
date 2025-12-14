package dao;

import model.Event;
import service.EventService;
import util.RMIClientUtil;
import java.util.List;

/**
 * Event DAO - Client-side wrapper using RMI services
 * This maintains compatibility with existing views
 * 
 * @author 27066
 */
public class EventDAO {
    private EventService eventService;
    
    public EventDAO() {
        eventService = RMIClientUtil.getEventService();
    }
    
    public List<Event> getAllEvents() {
        try {
            return eventService != null ? eventService.findAllEvents() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Event> getEventsByOrganizer(int organizerId) {
        try {
            return eventService != null ? eventService.findEventsByOrganizer(organizerId) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Event> getScheduledEvents() {
        try {
            return eventService != null ? eventService.findScheduledEvents() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Event getEventById(int eventId) {
        try {
            Event event = new Event();
            event.setEventId(eventId);
            return eventService != null ? eventService.findEventById(event) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean createEvent(Event event) {
        try {
            Event result = eventService != null ? eventService.createEvent(event) : null;
            return result != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateEvent(Event event) {
        try {
            Event result = eventService != null ? eventService.updateEvent(event) : null;
            return result != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteEvent(int eventId) {
        try {
            Event event = new Event();
            event.setEventId(eventId);
            Event result = eventService != null ? eventService.deleteEvent(event) : null;
            return result != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getAvailableCapacity(int eventId) {
        try {
            return eventService != null ? eventService.getAvailableCapacity(eventId) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

