package service.implementation;

import dao.BookingDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Booking;
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
            // Clear Hibernate relationships to avoid RMI serialization issues
            for (Booking booking : bookings) {
                if (booking.getEvent() != null) {
                    booking.getEvent().setBookings(null);
                    booking.getEvent().setTags(null);
                }
                if (booking.getUser() != null) {
                    booking.getUser().setBookings(null);
                    booking.getUser().setOrganizedEvents(null);
                    booking.getUser().setUserProfile(null);
                }
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByUser(int userId) throws RemoteException {
        List<Booking> bookings = dao.findBookingsByUser(userId);
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking.getEvent() != null) {
                    booking.getEvent().setBookings(null);
                    booking.getEvent().setTags(null);
                }
                if (booking.getUser() != null) {
                    booking.getUser().setBookings(null);
                    booking.getUser().setOrganizedEvents(null);
                    booking.getUser().setUserProfile(null);
                }
            }
        }
        return bookings;
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

