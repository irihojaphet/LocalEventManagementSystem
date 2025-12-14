package dao;

import java.util.List;
import model.Venue;
import org.hibernate.*;
import org.hibernate.query.Query;

/**
 * Venue DAO with Hibernate implementation
 * 
 * @author 27066
 */
public class VenueDao {
    
    // CREATE
    public Venue createVenue(Venue venueObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(venueObj);
            tr.commit();
            ss.close();
            return venueObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // UPDATE
    public Venue updateVenue(Venue venueObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(venueObj);
            tr.commit();
            ss.close();
            return venueObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // DELETE
    public Venue deleteVenue(Venue venueObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(venueObj);
            tr.commit();
            ss.close();
            return venueObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by ID
    public Venue findVenueById(Venue venueObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Venue venue = ss.get(Venue.class, venueObj.getVenueId());
            ss.close();
            return venue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find by ID (int)
    public Venue findVenueById(int venueId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Venue venue = ss.get(Venue.class, venueId);
            ss.close();
            return venue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find all venues
    public List<Venue> findAllVenues() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Venue> query = ss.createQuery("FROM Venue ORDER BY venueName", Venue.class);
            List<Venue> venues = query.list();
            ss.close();
            return venues;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // READ - Find available venues
    public List<Venue> findAvailableVenues() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query<Venue> query = ss.createQuery(
                "FROM Venue WHERE availabilityStatus = 'available' ORDER BY venueName", 
                Venue.class
            );
            List<Venue> venues = query.list();
            ss.close();
            return venues;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

