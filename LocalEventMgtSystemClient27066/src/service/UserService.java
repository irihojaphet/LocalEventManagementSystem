package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.User;

/**
 * Remote interface for User Service (Client side)
 * 
 * @author 27066
 */
public interface UserService extends Remote {
    
    // Authentication
    User login(String username, String password) throws RemoteException;
    User register(User user) throws RemoteException;
    
    // CRUD operations
    User createUser(User user) throws RemoteException;
    User updateUser(User user) throws RemoteException;
    User deleteUser(User user) throws RemoteException;
    User findUserById(User user) throws RemoteException;
    List<User> findAllUsers() throws RemoteException;
    
    // Validation
    boolean isUsernameExists(String username) throws RemoteException;
    boolean isEmailExists(String email) throws RemoteException;
}

