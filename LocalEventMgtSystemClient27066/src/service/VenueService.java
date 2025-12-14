package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Venue;

/**
 * Remote interface for Venue Service (Client side)
 * 
 * @author 27066
 */
public interface VenueService extends Remote {
    
    // CRUD operations
    Venue createVenue(Venue venue) throws RemoteException;
    Venue updateVenue(Venue venue) throws RemoteException;
    Venue deleteVenue(Venue venue) throws RemoteException;
    Venue findVenueById(Venue venue) throws RemoteException;
    List<Venue> findAllVenues() throws RemoteException;
    
    // Query operations
    List<Venue> findAvailableVenues() throws RemoteException;
}

