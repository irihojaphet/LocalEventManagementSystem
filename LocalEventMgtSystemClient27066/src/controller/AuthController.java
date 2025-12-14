package controller;

import model.User;
import service.UserService;
import util.PasswordUtil;
import util.RMIClientUtil;
import util.SessionManager;
import view.MainApplicationFrame;
import javax.swing.JOptionPane;

/**
 * Authentication Controller using RMI services
 * 
 * @author 27066
 */
public class AuthController {
    private UserService userService;
    
    public AuthController() {
        userService = RMIClientUtil.getUserService();
    }
    
    public boolean login(String username, String password) {
        try {
            if (userService == null) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to server. Please ensure the server is running.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = userService.login(username, hashedPassword);
            
            if (user != null) {
                SessionManager.setCurrentUser(user);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Invalid username or password.", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error connecting to server: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean register(String fullName, String username, String email, 
                           String phone, String password, String role) {
        try {
            if (userService == null) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to server. Please ensure the server is running.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Check if username or email already exists
            if (userService.isUsernameExists(username) || userService.isEmailExists(email)) {
                JOptionPane.showMessageDialog(null, 
                    "Username or email already exists.", 
                    "Registration Failed", 
                    JOptionPane.ERROR_MESSAGE);
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
            
            User result = userService.register(user);
            if (result != null) {
                JOptionPane.showMessageDialog(null, 
                    "Registration successful!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Registration failed. Please try again.", 
                    "Registration Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error during registration: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
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

