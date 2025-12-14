package service.implementation;

import dao.VenueDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Venue;
import service.VenueService;

/**
 * Venue Service Implementation
 * 
 * @author 27066
 */
public class VenueServiceImpl extends UnicastRemoteObject implements VenueService {

    private VenueDao dao = new VenueDao();
    
    public VenueServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Venue createVenue(Venue venue) throws RemoteException {
        Venue result = dao.createVenue(venue);
        if (result != null) {
            // Clear Hibernate collections to avoid serialization issues
            result.setEvents(null);
        }
        return result;
    }

    @Override
    public Venue updateVenue(Venue venue) throws RemoteException {
        Venue result = dao.updateVenue(venue);
        if (result != null) {
            result.setEvents(null);
        }
        return result;
    }

    @Override
    public Venue deleteVenue(Venue venue) throws RemoteException {
        return dao.deleteVenue(venue);
    }

    @Override
    public Venue findVenueById(Venue venue) throws RemoteException {
        Venue result = dao.findVenueById(venue);
        if (result != null) {
            result.setEvents(null);
        }
        return result;
    }

    @Override
    public List<Venue> findAllVenues() throws RemoteException {
        List<Venue> venues = dao.findAllVenues();
        if (venues != null) {
            // Clear Hibernate collections to avoid RMI serialization issues
            for (Venue venue : venues) {
                venue.setEvents(null);
            }
        }
        return venues;
    }

    @Override
    public List<Venue> findAvailableVenues() throws RemoteException {
        List<Venue> venues = dao.findAvailableVenues();
        if (venues != null) {
            for (Venue venue : venues) {
                venue.setEvents(null);
            }
        }
        return venues;
    }
}

