package controller;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;
import util.SessionManager;
import view.MainApplicationFrame;

public class AuthController {
    private UserDAO userDAO;
    
    public AuthController() {
        userDAO = new UserDAO();
    }
    
    public boolean login(String username, String password) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = userDAO.login(username, hashedPassword);
        
        if (user != null) {
            SessionManager.setCurrentUser(user);
            return true;
        }
        return false;
    }
    
    public boolean register(String fullName, String username, String email, 
                           String phone, String password, String role) {
        // Check if username or email already exists
        if (userDAO.isUsernameExists(username) || userDAO.isEmailExists(email)) {
            return false;
        }
        
        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setUserRole(role);
        user.setAccountStatus("active");
        
        return userDAO.register(user);
    }
    
    public void openDashboard() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            switch (currentUser.getUserRole()) {
                case "admin":
                case "organizer":
                    MainApplicationFrame.getInstance().showCard(MainApplicationFrame.ADMIN_DASHBOARD_CARD);
                    break;
                case "customer":
                    MainApplicationFrame.getInstance().showCard(MainApplicationFrame.CUSTOMER_DASHBOARD_CARD);
                    break;
            }
        }
    }
}