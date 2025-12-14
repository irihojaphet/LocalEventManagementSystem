package dao;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

// Import all entity classes
import model.User;
import model.Venue;
import model.Event;
import model.Booking;
import model.UserProfile;
import model.EventTag;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 * Updated for Hibernate 6.x compatibility with JDK 21
 *
 * @author 27066
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final ServiceRegistry serviceRegistry;
    
    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file using modern Hibernate Configuration
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            
            // Explicitly add annotated classes (required for Hibernate 6 in JAR)
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Venue.class);
            configuration.addAnnotatedClass(Event.class);
            configuration.addAnnotatedClass(Booking.class);
            configuration.addAnnotatedClass(UserProfile.class);
            configuration.addAnnotatedClass(EventTag.class);
            
            System.out.println("Entity classes registered successfully");
            
            // Build service registry
            serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            
            // Build session factory
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            
            System.out.println("SessionFactory created successfully");
            
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed: " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        // Close caches and connection pools
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }
}

