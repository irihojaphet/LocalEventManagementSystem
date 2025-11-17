package util;

import model.User;

public class SessionManager {
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getUserRole());
    }
    
    public static boolean isOrganizer() {
        return currentUser != null && "organizer".equals(currentUser.getUserRole());
    }
    
    public static boolean isCustomer() {
        return currentUser != null && "customer".equals(currentUser.getUserRole());
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}