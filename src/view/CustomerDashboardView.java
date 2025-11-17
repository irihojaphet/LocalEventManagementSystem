package view;

import controller.BookingController;
import dao.EventDAO;
import model.Event;
import util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerDashboardView extends JFrame {
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private EventDAO eventDAO;
    private BookingController bookingController;
    
    public CustomerDashboardView() {
        eventDAO = new EventDAO();
        bookingController = new BookingController();
        initializeUI();
        loadEvents();
    }
    
    private void initializeUI() {
        setTitle("Customer Dashboard - Event Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu bookingsMenu = new JMenu("My Bookings");
        JMenuItem viewBookingsItem = new JMenuItem("View My Bookings");
        viewBookingsItem.addActionListener(e -> openMyBookings());
        bookingsMenu.add(viewBookingsItem);
        
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        
        menuBar.add(bookingsMenu);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome panel
        JPanel welcomePanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Welcome, " + SessionManager.getCurrentUser().getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomePanel.add(welcomeLabel);
        
        // Events table
        String[] columns = {"Event ID", "Event Name", "Date", "Time", "Venue", "Price", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        eventsTable = new JTable(tableModel);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Events"));
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton bookTicketButton = new JButton("Book Tickets");
        bookTicketButton.setBackground(new Color(60, 179, 113));
        bookTicketButton.setForeground(Color.BLACK);
        bookTicketButton.addActionListener(e -> bookTickets());
        
        buttonPanel.add(bookTicketButton);
        
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void loadEvents() {
        tableModel.setRowCount(0);
        List<Event> events = eventDAO.getScheduledEvents();
        
        for (Event event : events) {
            int available = eventDAO.getAvailableCapacity(event.getEventId());
            Object[] row = {
                event.getEventId(),
                event.getEventName(),
                event.getEventDate(),
                event.getEventTime(),
                event.getVenueName(),
                "RWF " + event.getTicketPrice(),
                available + " seats"
            };
            tableModel.addRow(row);
        }
    }
    
    private void bookTickets() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an event to book!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        String eventName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Simplified quantity selection
        String[] quantities = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String quantity = (String) JOptionPane.showInputDialog(this,
            "How many tickets for " + eventName + "?",
            "Select Quantity",
            JOptionPane.QUESTION_MESSAGE,
            null,
            quantities,
            "1");
        
        if (quantity != null) {
            int numTickets = Integer.parseInt(quantity);
            if (bookingController.createBooking(eventId, SessionManager.getCurrentUser().getUserId(), numTickets)) {
                JOptionPane.showMessageDialog(this, 
                    "Booking confirmed! Your ticket number has been generated.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadEvents(); // Refresh available capacity
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Booking failed. Insufficient tickets available or system error.", 
                    "Booking Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openMyBookings() {
        new BookingManagementView();
    }
    
    private void logout() {
        SessionManager.logout();
        dispose();
        new LoginView();
    }
}