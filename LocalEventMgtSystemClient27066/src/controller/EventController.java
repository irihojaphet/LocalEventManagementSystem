package controller;

import model.Event;
import service.EventService;
import util.RMIClientUtil;
import util.SessionManager;
import javax.swing.JOptionPane;

/**
 * Event Controller using RMI services
 * 
 * @author 27066
 */
public class EventController {
    private EventService eventService;
    
    public EventController() {
        eventService = RMIClientUtil.getEventService();
    }
    
    public boolean createEvent(Event event) {
        try {
            if (eventService == null) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to server.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Check permissions
            if (!SessionManager.isAdmin() && !SessionManager.isOrganizer()) {
                JOptionPane.showMessageDialog(null, 
                    "You don't have permission to create events.", 
                    "Permission Denied", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate business rules
            if (event.getEventDate().before(new java.sql.Date(System.currentTimeMillis()))) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot create events in the past.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            Event result = eventService.createEvent(event);
            if (result != null) {
                JOptionPane.showMessageDialog(null, 
                    "Event created successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Failed to create event.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error creating event: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
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
        try {
            if (eventService == null) return false;
            
            Event event = new Event();
            event.setEventId(eventId);
            Event result = eventService.deleteEvent(event);
            
            if (result != null) {
                JOptionPane.showMessageDialog(null, 
                    "Event deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Cannot delete event with paid bookings.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error deleting event: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
}

