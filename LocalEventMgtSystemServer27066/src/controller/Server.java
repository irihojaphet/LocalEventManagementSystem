package controller;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.implementation.UserServiceImpl;
import service.implementation.EventServiceImpl;
import service.implementation.BookingServiceImpl;
import service.implementation.VenueServiceImpl;

/**
 * RMI Server for Local Event Management System
 * 
 * @author 27066
 */
public class Server {
    public static void main(String[] args) {
        try {
            // Set properties
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            
            // Create registry on port 3000 (within required range 3000-4000)
            Registry registry = LocateRegistry.createRegistry(3000);
            
            // Register all services
            registry.rebind("userService", new UserServiceImpl());
            registry.rebind("eventService", new EventServiceImpl());
            registry.rebind("bookingService", new BookingServiceImpl());
            registry.rebind("venueService", new VenueServiceImpl());
            
            System.out.println("========================================");
            System.out.println("  Local Event Management System");
            System.out.println("  RMI Server Started");
            System.out.println("========================================");
            System.out.println("Server running on: localhost:3000");
            System.out.println("\nRegistered Services:");
            System.out.println("  - userService         (UserService)");
            System.out.println("  - eventService        (EventService)");
            System.out.println("  - bookingService      (BookingService)");
            System.out.println("  - venueService        (VenueService)");
            System.out.println("\nPress Ctrl+C to stop the server...");
            System.out.println("========================================\n");
            
            // Keep the server running - prevents JVM from exiting
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (Exception ex) {
            System.err.println("Server exception: " + ex.toString());
            ex.printStackTrace();
        }
    }
}

