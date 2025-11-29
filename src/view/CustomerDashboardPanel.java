package view;

import controller.BookingController;
import dao.EventDAO;
import model.Event;
import util.SessionManager;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerDashboardPanel extends JPanel {
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private EventDAO eventDAO;
    private BookingController bookingController;
    
    public CustomerDashboardPanel() {
        eventDAO = new EventDAO();
        bookingController = new BookingController();
        setupMenuBar();
        initializeUI();
        loadEvents();
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = Theme.createStyledMenuBar();
        
        JMenu bookingsMenu = Theme.createStyledMenu("My Bookings");
        JMenuItem viewBookingsItem = Theme.createStyledMenuItem("View My Bookings");
        viewBookingsItem.addActionListener(e -> openMyBookings());
        bookingsMenu.add(viewBookingsItem);
        
        JMenu accountMenu = Theme.createStyledMenu("Account");
        JMenuItem logoutItem = Theme.createStyledMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        
        menuBar.add(bookingsMenu);
        menuBar.add(accountMenu);
        
        MainApplicationFrame.getInstance().setJMenuBar(menuBar);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Welcome panel
        JPanel welcomePanel = Theme.createCardPanel();
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        JLabel welcomeLabel = Theme.createStyledLabel(
            "Welcome, " + SessionManager.getCurrentUser().getFullName() + "!",
            Theme.getHeadingFont(), Theme.TEXT_PRIMARY);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Container for title and table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        
        // Title panel with permanent background
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(true);
        titlePanel.setBackground(Theme.PRIMARY_COLOR);
        titlePanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        titlePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 55));
        JLabel titleLabel = new JLabel("Available Events");
        titleLabel.setFont(Theme.getSubheadingFont());
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Events table card
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(0, 20, 20, 20)); // Remove top padding
        
        String[] columns = {"Event ID", "Event Name", "Date", "Time", "Venue", "Price", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        eventsTable = new JTable(tableModel);
        Theme.styleTable(eventsTable);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        tableCard.add(scrollPane, BorderLayout.CENTER);
        
        tableContainer.add(titlePanel, BorderLayout.NORTH);
        tableContainer.add(tableCard, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton bookTicketButton = Theme.createSuccessButton("Book Tickets");
        bookTicketButton.addActionListener(e -> bookTickets());
        buttonPanel.add(bookTicketButton);
        
        add(welcomePanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadEvents() {
        tableModel.setRowCount(0);
        List<Event> events = eventDAO.getScheduledEvents();
        
        for (Event event : events) {
            int available = eventDAO.getAvailableCapacity(event.getEventId());
            String priceDisplay;
            if ("category".equals(event.getPricingType())) {
                priceDisplay = String.format("VVIP: RWF %.0f | VIP: RWF %.0f | Casual: RWF %.0f", 
                    event.getVvipPrice(), event.getVipPrice(), event.getCasualPrice());
            } else {
                priceDisplay = "RWF " + String.format("%.2f", event.getTicketPrice());
            }
            
            Object[] row = {
                event.getEventId(),
                event.getEventName(),
                event.getEventDate(),
                event.getEventTime(),
                event.getVenueName(),
                priceDisplay,
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
        
        // Get event details to check pricing type
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            JOptionPane.showMessageDialog(this, 
                "Failed to load event details.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Category selection if event has category pricing
        String selectedCategory = null;
        if ("category".equals(event.getPricingType())) {
            // Create category selection dialog
            JPanel categoryPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            JLabel titleLabel = new JLabel("Select Ticket Category:");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            categoryPanel.add(titleLabel, gbc);
            
            ButtonGroup categoryGroup = new ButtonGroup();
            JRadioButton vvipRadio = new JRadioButton("VVIP - RWF " + String.format("%.2f", event.getVvipPrice()));
            JRadioButton vipRadio = new JRadioButton("VIP - RWF " + String.format("%.2f", event.getVipPrice()));
            JRadioButton casualRadio = new JRadioButton("Casual - RWF " + String.format("%.2f", event.getCasualPrice()));
            
            categoryGroup.add(vvipRadio);
            categoryGroup.add(vipRadio);
            categoryGroup.add(casualRadio);
            casualRadio.setSelected(true); // Default selection
            
            gbc.gridwidth = 1;
            gbc.gridy = 1;
            categoryPanel.add(vvipRadio, gbc);
            gbc.gridy = 2;
            categoryPanel.add(vipRadio, gbc);
            gbc.gridy = 3;
            categoryPanel.add(casualRadio, gbc);
            
            int categoryResult = JOptionPane.showConfirmDialog(this,
                categoryPanel,
                "Select Ticket Category for " + eventName,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (categoryResult != JOptionPane.OK_OPTION) {
                return; // User cancelled
            }
            
            if (vvipRadio.isSelected()) {
                selectedCategory = "vvip";
            } else if (vipRadio.isSelected()) {
                selectedCategory = "vip";
            } else {
                selectedCategory = "casual";
            }
        }
        
        // Quantity selection
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
            
            // Calculate total for confirmation
            double pricePerTicket;
            if (selectedCategory != null) {
                pricePerTicket = event.getPriceForCategory(selectedCategory);
            } else {
                pricePerTicket = event.getTicketPrice();
            }
            double totalAmount = pricePerTicket * numTickets;
            
            // Show confirmation
            String categoryText = selectedCategory != null ? 
                "Category: " + selectedCategory.toUpperCase() + "\n" : "";
            int confirm = JOptionPane.showConfirmDialog(this,
                "Booking Details:\n" +
                "Event: " + eventName + "\n" +
                categoryText +
                "Quantity: " + numTickets + "\n" +
                "Price per ticket: RWF " + String.format("%.2f", pricePerTicket) + "\n" +
                "Total Amount: RWF " + String.format("%.2f", totalAmount) + "\n\n" +
                "Confirm booking?",
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (bookingController.createBooking(eventId, SessionManager.getCurrentUser().getUserId(), numTickets, selectedCategory)) {
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
    }
    
    private void openMyBookings() {
        MainApplicationFrame.getInstance().showCard(MainApplicationFrame.BOOKING_MANAGEMENT_CARD);
    }
    
    private void logout() {
        SessionManager.logout();
        MainApplicationFrame.getInstance().setJMenuBar(null);
        MainApplicationFrame.getInstance().showCard(MainApplicationFrame.LOGIN_CARD);
    }
}

