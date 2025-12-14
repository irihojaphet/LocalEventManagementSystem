package util;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.UserService;
import service.EventService;
import service.BookingService;
import service.VenueService;

/**
 * RMI Client Utility for connecting to remote services
 * 
 * @author 27066
 */
public class RMIClientUtil {
    
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;
    
    private static Registry registry;
    private static UserService userService;
    private static EventService eventService;
    private static BookingService bookingService;
    private static VenueService venueService;
    
    static {
        try {
            registry = LocateRegistry.getRegistry(SERVER_HOST, SERVER_PORT);
            userService = (UserService) registry.lookup("userService");
            eventService = (EventService) registry.lookup("eventService");
            bookingService = (BookingService) registry.lookup("bookingService");
            venueService = (VenueService) registry.lookup("venueService");
        } catch (Exception e) {
            System.err.println("Failed to connect to RMI server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static UserService getUserService() {
        return userService;
    }
    
    public static EventService getEventService() {
        return eventService;
    }
    
    public static BookingService getBookingService() {
        return bookingService;
    }
    
    public static VenueService getVenueService() {
        return venueService;
    }
    
    public static boolean isConnected() {
        return registry != null && userService != null && eventService != null 
               && bookingService != null && venueService != null;
    }
    
    public static void reconnect() {
        try {
            registry = LocateRegistry.getRegistry(SERVER_HOST, SERVER_PORT);
            userService = (UserService) registry.lookup("userService");
            eventService = (EventService) registry.lookup("eventService");
            bookingService = (BookingService) registry.lookup("bookingService");
            venueService = (VenueService) registry.lookup("venueService");
        } catch (Exception e) {
            System.err.println("Failed to reconnect to RMI server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

