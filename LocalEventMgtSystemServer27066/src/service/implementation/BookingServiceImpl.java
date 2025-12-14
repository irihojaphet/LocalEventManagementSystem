package service.implementation;

import dao.BookingDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Booking;
import model.Event;
import model.User;
import service.BookingService;
import util.NotificationService;

/**
 * Booking Service Implementation
 * 
 * @author 27066
 */
public class BookingServiceImpl extends UnicastRemoteObject implements BookingService {

    private BookingDao dao = new BookingDao();
    
    public BookingServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Booking createBooking(Booking booking) throws RemoteException {
        Booking result = dao.createBooking(booking);
        if (result != null) {
            // Get booking with relationships for notification
            Booking bookingWithRelations = dao.getBookingWithRelations(result.getBookingId());
            
            // Clear Hibernate relationships to avoid serialization issues
            if (result.getEvent() != null) {
                result.getEvent().setBookings(null);
                result.getEvent().setTags(null);
            }
            if (result.getUser() != null) {
                result.getUser().setBookings(null);
                result.getUser().setOrganizedEvents(null);
                result.getUser().setUserProfile(null);
            }
            
            // Send notification to admin about new booking
            if (bookingWithRelations != null && bookingWithRelations.getUser() != null && bookingWithRelations.getEvent() != null) {
                new Thread(() -> {
                    try {
                        NotificationService.getInstance().sendNewBookingNotification(
                            bookingWithRelations, bookingWithRelations.getUser(), bookingWithRelations.getEvent()
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to send new booking notification: " + e.getMessage());
                    }
                }).start();
            }
        }
        return result;
    }

    @Override
    public Booking updateBooking(Booking booking) throws RemoteException {
        Booking result = dao.updateBooking(booking);
        if (result != null) {
            if (result.getEvent() != null) {
                result.getEvent().setBookings(null);
                result.getEvent().setTags(null);
            }
            if (result.getUser() != null) {
                result.getUser().setBookings(null);
                result.getUser().setOrganizedEvents(null);
                result.getUser().setUserProfile(null);
            }
        }
        return result;
    }

    @Override
    public Booking deleteBooking(Booking booking) throws RemoteException {
        return dao.deleteBooking(booking);
    }

    @Override
    public Booking findBookingById(Booking booking) throws RemoteException {
        Booking result = dao.findBookingById(booking);
        if (result != null) {
            if (result.getEvent() != null) {
                result.getEvent().setBookings(null);
                result.getEvent().setTags(null);
            }
            if (result.getUser() != null) {
                result.getUser().setBookings(null);
                result.getUser().setOrganizedEvents(null);
                result.getUser().setUserProfile(null);
            }
        }
        return result;
    }

    @Override
    public List<Booking> findAllBookings() throws RemoteException {
        List<Booking> bookings = dao.findAllBookings();
        if (bookings != null) {
            // Detach Hibernate entities and clear relationships to avoid RMI serialization issues
            for (Booking booking : bookings) {
                detachBooking(booking);
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByUser(int userId) throws RemoteException {
        List<Booking> bookings = dao.findBookingsByUser(userId);
        if (bookings != null) {
            // Detach Hibernate entities and clear relationships
            for (Booking booking : bookings) {
                detachBooking(booking);
            }
        }
        return bookings;
    }
    
    // Helper method to detach booking from Hibernate session
    private void detachBooking(Booking booking) {
        try {
            // Clear Hibernate relationships to avoid serialization issues
            // The transient fields (eventName, userName, etc.) are already populated by DAO
            booking.setEvent(null); // Clear the relationship
            booking.setUser(null); // Clear the relationship
        } catch (Exception e) {
            // If any error occurs, continue - the transient fields should have the data
            System.err.println("Warning: Error detaching booking: " + e.getMessage());
        }
    }

    @Override
    public boolean cancelBooking(int bookingId) throws RemoteException {
        return dao.cancelBooking(bookingId);
    }

    @Override
    public boolean updatePaymentStatus(int bookingId, String status) throws RemoteException {
        // Get old status before update
        Booking oldBooking = dao.findBookingById(bookingId);
        String oldStatus = oldBooking != null ? oldBooking.getPaymentStatus() : null;
        
        boolean result = dao.updatePaymentStatus(bookingId, status);
        
        // If payment was confirmed (changed to 'paid'), send notification
        if (result && "paid".equalsIgnoreCase(status) && !"paid".equalsIgnoreCase(oldStatus)) {
            try {
                // Get booking with relationships loaded
                Booking booking = dao.getBookingWithRelations(bookingId);
                if (booking != null && booking.getUser() != null && booking.getEvent() != null) {
                    // Send notifications asynchronously to avoid blocking
                    new Thread(() -> {
                        try {
                            NotificationService.getInstance().sendPaymentConfirmationNotification(
                                booking, booking.getUser(), booking.getEvent()
                            );
                            NotificationService.getInstance().sendTicketReadyNotification(
                                booking, booking.getUser(), booking.getEvent()
                            );
                            NotificationService.getInstance().sendBookingApprovedNotification(
                                booking, booking.getUser(), booking.getEvent()
                            );
                        } catch (Exception e) {
                            System.err.println("Failed to send notification: " + e.getMessage());
                        }
                    }).start();
                }
            } catch (Exception e) {
                System.err.println("Failed to prepare notification: " + e.getMessage());
                // Don't fail the update if notification fails
            }
        }
        
        return result;
    }

    @Override
    public String generateTicketNumber() throws RemoteException {
        return dao.generateTicketNumber();
    }
}

