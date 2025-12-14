package service.implementation;

import dao.EventDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Event;
import service.EventService;

/**
 * Event Service Implementation
 * 
 * @author 27066
 */
public class EventServiceImpl extends UnicastRemoteObject implements EventService {

    private EventDao dao = new EventDao();
    
    public EventServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Event createEvent(Event event) throws RemoteException {
        Event result = dao.createEvent(event);
        if (result != null) {
            // Clear Hibernate collections to avoid serialization issues
            result.setBookings(null);
            result.setTags(null);
        }
        return result;
    }

    @Override
    public Event updateEvent(Event event) throws RemoteException {
        Event result = dao.updateEvent(event);
        if (result != null) {
            result.setBookings(null);
            result.setTags(null);
        }
        return result;
    }

    @Override
    public Event deleteEvent(Event event) throws RemoteException {
        return dao.deleteEvent(event);
    }

    @Override
    public Event findEventById(Event event) throws RemoteException {
        Event result = dao.findEventById(event);
        if (result != null) {
            result.setBookings(null);
            result.setTags(null);
        }
        return result;
    }

    @Override
    public List<Event> findAllEvents() throws RemoteException {
        List<Event> events = dao.findAllEvents();
        if (events != null) {
            // Clear Hibernate collections to avoid RMI serialization issues
            for (Event event : events) {
                event.setBookings(null);
                event.setTags(null);
            }
        }
        return events;
    }

    @Override
    public List<Event> findEventsByOrganizer(int organizerId) throws RemoteException {
        List<Event> events = dao.findEventsByOrganizer(organizerId);
        if (events != null) {
            for (Event event : events) {
                event.setBookings(null);
                event.setTags(null);
            }
        }
        return events;
    }

    @Override
    public List<Event> findScheduledEvents() throws RemoteException {
        List<Event> events = dao.findScheduledEvents();
        if (events != null) {
            for (Event event : events) {
                event.setBookings(null);
                event.setTags(null);
            }
        }
        return events;
    }

    @Override
    public int getAvailableCapacity(int eventId) throws RemoteException {
        return dao.getAvailableCapacity(eventId);
    }
}

