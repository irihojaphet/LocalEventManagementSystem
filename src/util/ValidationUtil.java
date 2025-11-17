package util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Email validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Username validation: 4-20 alphanumeric characters
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9]{4,20}$");
    
    // Rwanda phone number validation
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(\\+250|0)[7][0-9]{8}$");
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidCapacity(int capacity) {
        return capacity >= 10 && capacity <= 10000;
    }
    
    public static boolean isValidPrice(double price) {
        return price >= 0;
    }
    
    public static boolean isValidTicketQuantity(int quantity) {
        return quantity >= 1 && quantity <= 10;
    }
}