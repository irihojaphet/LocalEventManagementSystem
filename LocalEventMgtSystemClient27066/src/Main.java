import view.MainApplicationFrame;
import util.NotificationListener;
import javax.swing.*;

/**
 * Main entry point for Local Event Management System Client
 * 
 * @author 27066
 */
public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start notification listener in background
        try {
            NotificationListener.getInstance().startListening();
        } catch (Exception e) {
            System.err.println("Warning: Could not start notification listener. ActiveMQ may not be running.");
            System.err.println("Notifications will not be available, but the application will continue to work.");
        }
        
        // Start application with main frame
        SwingUtilities.invokeLater(() -> MainApplicationFrame.getInstance());
    }
}

