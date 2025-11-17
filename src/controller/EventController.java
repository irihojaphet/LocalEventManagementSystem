package controller;

import dao.EventDAO;
import model.Event;
import util.SessionManager;

public class EventController {
    private EventDAO eventDAO;
    
    public EventController() {
        eventDAO = new EventDAO();
    }
    
    public boolean createEvent(Event event) {
        // Check permissions
        if (!SessionManager.isAdmin() && !SessionManager.isOrganizer()) {
            return false;
        }
        
        // Validate business rules
        if (event.getEventDate().before(new java.sql.Date(System.currentTimeMillis()))) {
            return false; // Cannot create past events
        }
        
        return eventDAO.createEvent(event);
    }
    
    public boolean canEditEvent(int eventId, int organizerId) {
        if (SessionManager.isAdmin()) {
            return true; // Admins can edit any event
        }
        
        if (SessionManager.isOrganizer() && 
            SessionManager.getCurrentUser().getUserId() == organizerId) {
            return true; // Organizers can edit their own events
        }
        
        return false;
    }
    
    public boolean canDeleteEvent(int eventId) {
        // Check for existing paid bookings
        // This is handled in the DAO
        return eventDAO.deleteEvent(eventId);
    }
}