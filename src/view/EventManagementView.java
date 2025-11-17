package view;

import controller.EventController;
import dao.EventDAO;
import dao.VenueDAO;
import model.Event;
import model.Venue;
import util.SessionManager;
import util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventManagementView extends JFrame {
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private EventDAO eventDAO;
    private VenueDAO venueDAO;
    private EventController eventController;
    
    // Form fields
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<Venue> venueCombo;
    private JTextField capacityField;
    private JTextField priceField;
    private JComboBox<String> statusCombo;
    
    public EventManagementView() {
        eventDAO = new EventDAO();
        venueDAO = new VenueDAO();
        eventController = new EventController();
        initializeUI();
        loadEvents();
    }
    
    private void initializeUI() {
        setTitle("Event Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Event Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Event Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        dateField = new JTextField(10);
        panel.add(dateField, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 3;
        timeField = new JTextField(10);
        panel.add(timeField, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 1;
        venueCombo = new JComboBox<>();
        loadVenues();
        panel.add(venueCombo, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 3;
        capacityField = new JTextField(10);
        panel.add(capacityField, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Ticket Price:"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(10);
        panel.add(priceField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        statusCombo = new JComboBox<>(new String[]{"scheduled", "ongoing", "completed", "cancelled"});
        panel.add(statusCombo, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Name", "Date", "Time", "Venue", "Organizer", "Capacity", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        eventsTable = new JTable(tableModel);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Events List"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Event");
        JButton updateButton = new JButton("Update Event");
        JButton deleteButton = new JButton("Delete Event");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.setBackground(new Color(60, 179, 113));
        addButton.setForeground(Color.BLACK);
        updateButton.setBackground(new Color(70, 130, 180));
        updateButton.setForeground(Color.BLACK);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.BLACK);
        
        addButton.addActionListener(e -> addEvent());
        updateButton.addActionListener(e -> updateEvent());
        deleteButton.addActionListener(e -> deleteEvent());
        refreshButton.addActionListener(e -> loadEvents());
        
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private void loadVenues() {
        venueCombo.removeAllItems();
        List<Venue> venues = venueDAO.getAvailableVenues();
        for (Venue venue : venues) {
            venueCombo.addItem(venue);
        }
    }
    
    private void loadEvents() {
        tableModel.setRowCount(0);
        List<Event> events;
        
        if (SessionManager.isAdmin()) {
            events = eventDAO.getAllEvents();
        } else {
            events = eventDAO.getEventsByOrganizer(SessionManager.getCurrentUser().getUserId());
        }
        
        for (Event event : events) {
            Object[] row = {
                event.getEventId(),
                event.getEventName(),
                event.getEventDate(),
                event.getEventTime(),
                event.getVenueName(),
                event.getOrganizerName(),
                event.getCapacity(),
                event.getTicketPrice(),
                event.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addEvent() {
        if (!validateForm()) return;
        
        try {
            Event event = new Event();
            event.setEventName(nameField.getText().trim());
            event.setEventDescription(descriptionArea.getText().trim());
            event.setEventDate(Date.valueOf(dateField.getText().trim()));
            event.setEventTime(Time.valueOf(timeField.getText().trim() + ":00"));
            
            Venue selectedVenue = (Venue) venueCombo.getSelectedItem();
            event.setVenueId(selectedVenue.getVenueId());
            event.setOrganizerId(SessionManager.getCurrentUser().getUserId());
            event.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            event.setTicketPrice(Double.parseDouble(priceField.getText().trim()));
            event.setStatus((String) statusCombo.getSelectedItem());
            
            if (eventController.createEvent(event)) {
                JOptionPane.showMessageDialog(this, 
                    "Event created successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create event. Please check your inputs.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid input format. Please check date/time/numeric fields.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an event to update!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) return;
        
        try {
            int eventId = (int) tableModel.getValueAt(selectedRow, 0);
            
            Event event = new Event();
            event.setEventId(eventId);
            event.setEventName(nameField.getText().trim());
            event.setEventDescription(descriptionArea.getText().trim());
            event.setEventDate(Date.valueOf(dateField.getText().trim()));
            event.setEventTime(Time.valueOf(timeField.getText().trim() + ":00"));
            
            Venue selectedVenue = (Venue) venueCombo.getSelectedItem();
            event.setVenueId(selectedVenue.getVenueId());
            event.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            event.setTicketPrice(Double.parseDouble(priceField.getText().trim()));
            event.setStatus((String) statusCombo.getSelectedItem());
            
            if (eventDAO.updateEvent(event)) {
                JOptionPane.showMessageDialog(this, 
                    "Event updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update event.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid input format.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an event to delete!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this event?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int eventId = (int) tableModel.getValueAt(selectedRow, 0);
            
            if (eventDAO.deleteEvent(eventId)) {
                JOptionPane.showMessageDialog(this, 
                    "Event deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cannot delete event with existing paid bookings!", 
                    "Delete Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Event name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Date.valueOf(dateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Time.valueOf(timeField.getText().trim() + ":00");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid time format! Use HH:MM", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (!ValidationUtil.isValidCapacity(capacity)) {
                JOptionPane.showMessageDialog(this, "Capacity must be between 10 and 10,000!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a valid number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (!ValidationUtil.isValidPrice(price)) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Business rule: Event date must be future
        Date eventDate = Date.valueOf(dateField.getText().trim());
        if (eventDate.before(new Date(System.currentTimeMillis()))) {
            JOptionPane.showMessageDialog(this, "Event date cannot be in the past!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        dateField.setText("");
        timeField.setText("");
        capacityField.setText("");
        priceField.setText("");
        statusCombo.setSelectedIndex(0);
    }
}