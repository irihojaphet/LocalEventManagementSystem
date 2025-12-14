package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Event;

/**
 * Remote interface for Event Service (Client side)
 * 
 * @author 27066
 */
public interface EventService extends Remote {
    
    // CRUD operations
    Event createEvent(Event event) throws RemoteException;
    Event updateEvent(Event event) throws RemoteException;
    Event deleteEvent(Event event) throws RemoteException;
    Event findEventById(Event event) throws RemoteException;
    List<Event> findAllEvents() throws RemoteException;
    
    // Query operations
    List<Event> findEventsByOrganizer(int organizerId) throws RemoteException;
    List<Event> findScheduledEvents() throws RemoteException;
    int getAvailableCapacity(int eventId) throws RemoteException;
}

