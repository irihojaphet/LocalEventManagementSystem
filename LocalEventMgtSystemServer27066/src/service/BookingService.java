package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Booking;

/**
 * Remote interface for Booking Service
 * 
 * @author 27066
 */
public interface BookingService extends Remote {
    
    // CRUD operations
    Booking createBooking(Booking booking) throws RemoteException;
    Booking updateBooking(Booking booking) throws RemoteException;
    Booking deleteBooking(Booking booking) throws RemoteException;
    Booking findBookingById(Booking booking) throws RemoteException;
    List<Booking> findAllBookings() throws RemoteException;
    
    // Query operations
    List<Booking> findBookingsByUser(int userId) throws RemoteException;
    boolean cancelBooking(int bookingId) throws RemoteException;
    boolean updatePaymentStatus(int bookingId, String status) throws RemoteException;
    String generateTicketNumber() throws RemoteException;
}

