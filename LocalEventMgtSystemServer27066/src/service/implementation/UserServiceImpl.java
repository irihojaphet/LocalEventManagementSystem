package service.implementation;

import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.User;
import service.UserService;

/**
 * User Service Implementation
 * 
 * @author 27066
 */
public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private UserDao dao = new UserDao();
    
    public UserServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public User login(String username, String password) throws RemoteException {
        User result = dao.login(username, password);
        if (result != null) {
            // Clear Hibernate collections to avoid serialization issues
            result.setBookings(null);
            result.setOrganizedEvents(null);
            result.setUserProfile(null);
        }
        return result;
    }

    @Override
    public User register(User user) throws RemoteException {
        User result = dao.createUser(user);
        if (result != null) {
            result.setBookings(null);
            result.setOrganizedEvents(null);
            result.setUserProfile(null);
        }
        return result;
    }

    @Override
    public User createUser(User user) throws RemoteException {
        User result = dao.createUser(user);
        if (result != null) {
            result.setBookings(null);
            result.setOrganizedEvents(null);
            result.setUserProfile(null);
        }
        return result;
    }

    @Override
    public User updateUser(User user) throws RemoteException {
        User result = dao.updateUser(user);
        if (result != null) {
            result.setBookings(null);
            result.setOrganizedEvents(null);
            result.setUserProfile(null);
        }
        return result;
    }

    @Override
    public User deleteUser(User user) throws RemoteException {
        return dao.deleteUser(user);
    }

    @Override
    public User findUserById(User user) throws RemoteException {
        User result = dao.findUserById(user);
        if (result != null) {
            result.setBookings(null);
            result.setOrganizedEvents(null);
            result.setUserProfile(null);
        }
        return result;
    }

    @Override
    public List<User> findAllUsers() throws RemoteException {
        List<User> users = dao.findAllUsers();
        if (users != null) {
            // Clear Hibernate collections to avoid RMI serialization issues
            for (User user : users) {
                user.setBookings(null);
                user.setOrganizedEvents(null);
                user.setUserProfile(null);
            }
        }
        return users;
    }

    @Override
    public boolean isUsernameExists(String username) throws RemoteException {
        return dao.isUsernameExists(username);
    }

    @Override
    public boolean isEmailExists(String email) throws RemoteException {
        return dao.isEmailExists(email);
    }
}

