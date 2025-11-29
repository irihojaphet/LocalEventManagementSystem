package view;

import controller.EventController;
import dao.EventDAO;
import dao.VenueDAO;
import model.Event;
import model.Venue;
import util.SessionManager;
import util.ValidationUtil;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class EventManagementPanel extends JPanel {
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
    private JRadioButton singlePriceRadio;
    private JRadioButton categoryPriceRadio;
    private JTextField vvipPriceField;
    private JTextField vipPriceField;
    private JTextField casualPriceField;
    private JPanel categoryPricePanel;
    private JPanel categoryContainer;
    
    public EventManagementPanel() {
        eventDAO = new EventDAO();
        venueDAO = new VenueDAO();
        eventController = new EventController();
        setupMenuBar();
        initializeUI();
        loadEvents();
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = Theme.createStyledMenuBar();
        
        JMenu navigationMenu = Theme.createStyledMenu("Navigation");
        JMenuItem backToDashboardItem = Theme.createStyledMenuItem("Back to Dashboard");
        backToDashboardItem.addActionListener(e -> {
            if (SessionManager.isAdmin()) {
                MainApplicationFrame.getInstance().showCard(MainApplicationFrame.ADMIN_DASHBOARD_CARD);
            } else {
                MainApplicationFrame.getInstance().showCard(MainApplicationFrame.CUSTOMER_DASHBOARD_CARD);
            }
        });
        navigationMenu.add(backToDashboardItem);
        
        menuBar.add(navigationMenu);
        MainApplicationFrame.getInstance().setJMenuBar(menuBar);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        
        // Title panel with permanent background
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(true);
        titlePanel.setBackground(Theme.PRIMARY_COLOR);
        titlePanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        titlePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 55));
        JLabel titleLabel = new JLabel("Event Details");
        titleLabel.setFont(Theme.getSubheadingFont());
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel panel = Theme.createCardPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(Theme.createStyledLabel("Event Name:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        nameField = Theme.createStyledTextField(20);
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
        venueCombo = Theme.createStyledComboBox();
        loadVenues();
        JPanel venuePanel = new JPanel(new BorderLayout(5, 0));
        venuePanel.add(venueCombo, BorderLayout.CENTER);
        JButton addVenueButton = new JButton("Add Venue");
        addVenueButton.addActionListener(e -> openAddVenueDialog());
        venuePanel.add(addVenueButton, BorderLayout.EAST);
        panel.add(venuePanel, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 3;
        capacityField = new JTextField(10);
        panel.add(capacityField, gbc);
        
        // Row 4 - Pricing Type
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Pricing Type:"), gbc);
        gbc.gridx = 1;
        JPanel pricingTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pricingTypePanel.setOpaque(false);
        singlePriceRadio = new JRadioButton("Single Price", true);
        singlePriceRadio.setFont(Theme.getBodyFont());
        singlePriceRadio.setForeground(Theme.TEXT_PRIMARY);
        categoryPriceRadio = new JRadioButton("Category Pricing (VVIP/VIP/Casual)");
        categoryPriceRadio.setFont(Theme.getBodyFont());
        categoryPriceRadio.setForeground(Theme.TEXT_PRIMARY);
        ButtonGroup pricingGroup = new ButtonGroup();
        pricingGroup.add(singlePriceRadio);
        pricingGroup.add(categoryPriceRadio);
        singlePriceRadio.addActionListener(e -> updatePricingFields());
        categoryPriceRadio.addActionListener(e -> updatePricingFields());
        pricingTypePanel.add(singlePriceRadio);
        pricingTypePanel.add(categoryPriceRadio);
        panel.add(pricingTypePanel, gbc);
        
        gbc.gridx = 2;
        panel.add(Theme.createStyledLabel("Status:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 3;
        statusCombo = Theme.createStyledComboBox();
        statusCombo.addItem("scheduled");
        statusCombo.addItem("ongoing");
        statusCombo.addItem("completed");
        statusCombo.addItem("cancelled");
        panel.add(statusCombo, gbc);
        
        // Row 5 - Single Price
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(Theme.createStyledLabel("Ticket Price:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        priceField = Theme.createStyledTextField(10);
        panel.add(priceField, gbc);
        
        // Row 6 - Category Prices
        gbc.gridx = 0; gbc.gridy = 5;
        categoryContainer = new JPanel(new BorderLayout());
        categoryContainer.setOpaque(false);
        
        // Category title panel with permanent background
        JPanel categoryTitlePanel = new JPanel(new BorderLayout());
        categoryTitlePanel.setOpaque(true);
        categoryTitlePanel.setBackground(Theme.PRIMARY_COLOR);
        categoryTitlePanel.setBorder(new EmptyBorder(12, 20, 12, 20));
        categoryTitlePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel categoryTitleLabel = new JLabel("Category Prices");
        categoryTitleLabel.setFont(Theme.getSubheadingFont());
        categoryTitleLabel.setForeground(Color.WHITE);
        categoryTitlePanel.add(categoryTitleLabel, BorderLayout.WEST);
        
        categoryPricePanel = new JPanel(new GridBagLayout());
        categoryPricePanel.setOpaque(false);
        categoryPricePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.insets = new Insets(8, 8, 8, 8);
        
        cgbc.gridx = 0; cgbc.gridy = 0;
        categoryPricePanel.add(Theme.createStyledLabel("VVIP Price:", Theme.getBodyFont(), Theme.TEXT_PRIMARY), cgbc);
        cgbc.gridx = 1;
        vvipPriceField = Theme.createStyledTextField(10);
        categoryPricePanel.add(vvipPriceField, cgbc);
        
        cgbc.gridx = 2;
        categoryPricePanel.add(Theme.createStyledLabel("VIP Price:", Theme.getBodyFont(), Theme.TEXT_PRIMARY), cgbc);
        cgbc.gridx = 3;
        vipPriceField = Theme.createStyledTextField(10);
        categoryPricePanel.add(vipPriceField, cgbc);
        
        cgbc.gridx = 4;
        categoryPricePanel.add(Theme.createStyledLabel("Casual Price:", Theme.getBodyFont(), Theme.TEXT_PRIMARY), cgbc);
        cgbc.gridx = 5;
        casualPriceField = Theme.createStyledTextField(10);
        categoryPricePanel.add(casualPriceField, cgbc);
        
        categoryContainer.add(categoryTitlePanel, BorderLayout.NORTH);
        categoryContainer.add(categoryPricePanel, BorderLayout.CENTER);
        
        categoryContainer.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        panel.add(categoryContainer, gbc);
        
        container.add(titlePanel, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }
    
    private JPanel createTablePanel() {
        // Container for title and table
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        
        // Title panel with permanent background
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(true);
        titlePanel.setBackground(Theme.PRIMARY_COLOR);
        titlePanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        titlePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 55));
        JLabel titleLabel = new JLabel("Events List");
        titleLabel.setFont(Theme.getSubheadingFont());
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel panel = Theme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 20, 20, 20)); // Remove top padding
        
        String[] columns = {"ID", "Name", "Date", "Time", "Venue", "Organizer", "Capacity", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        eventsTable = new JTable(tableModel);
        Theme.styleTable(eventsTable);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add listener to load event data when row is selected
        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEventToForm();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        container.add(titlePanel, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton addButton = Theme.createSuccessButton("Add Event");
        JButton updateButton = Theme.createPrimaryButton("Update Event");
        JButton deleteButton = Theme.createDangerButton("Delete Event");
        JButton refreshButton = Theme.createInfoButton("Refresh");
        
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
        Venue previouslySelected = (Venue) (venueCombo != null ? venueCombo.getSelectedItem() : null);
        venueCombo.removeAllItems();
        List<Venue> venues = venueDAO.getAvailableVenues();
        for (Venue venue : venues) {
            venueCombo.addItem(venue);
            if (previouslySelected != null && venue.getVenueId() == previouslySelected.getVenueId()) {
                venueCombo.setSelectedItem(venue);
            }
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
            
            // Handle pricing
            String description = descriptionArea.getText().trim();
            // Remove any old JSON pricing info if it exists
            description = description.replaceAll("\\[PRICING_INFO\\].*?\\[/PRICING_INFO\\]", "").trim();
            
            if (categoryPriceRadio.isSelected()) {
                double vvipPrice = Double.parseDouble(vvipPriceField.getText().trim());
                double vipPrice = Double.parseDouble(vipPriceField.getText().trim());
                double casualPrice = Double.parseDouble(casualPriceField.getText().trim());
                
                event.setPricingType("category");
                event.setVvipPrice(vvipPrice);
                event.setVipPrice(vipPrice);
                event.setCasualPrice(casualPrice);
                event.setTicketPrice(casualPrice); // Use casual as base price for database
            } else {
                event.setPricingType("single");
                event.setTicketPrice(Double.parseDouble(priceField.getText().trim()));
            }
            
            event.setEventDescription(description);
            event.setEventDate(Date.valueOf(dateField.getText().trim()));
            event.setEventTime(Time.valueOf(timeField.getText().trim() + ":00"));
            
            Venue selectedVenue = (Venue) venueCombo.getSelectedItem();
            event.setVenueId(selectedVenue.getVenueId());
            event.setOrganizerId(SessionManager.getCurrentUser().getUserId());
            event.setCapacity(Integer.parseInt(capacityField.getText().trim()));
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
            
            // Handle pricing
            String description = descriptionArea.getText().trim();
            // Remove any old JSON pricing info if it exists
            description = description.replaceAll("\\[PRICING_INFO\\].*?\\[/PRICING_INFO\\]", "").trim();
            
            if (categoryPriceRadio.isSelected()) {
                double vvipPrice = Double.parseDouble(vvipPriceField.getText().trim());
                double vipPrice = Double.parseDouble(vipPriceField.getText().trim());
                double casualPrice = Double.parseDouble(casualPriceField.getText().trim());
                
                event.setPricingType("category");
                event.setVvipPrice(vvipPrice);
                event.setVipPrice(vipPrice);
                event.setCasualPrice(casualPrice);
                event.setTicketPrice(casualPrice); // Use casual as base price for database
            } else {
                event.setPricingType("single");
                event.setTicketPrice(Double.parseDouble(priceField.getText().trim()));
            }
            
            event.setEventDescription(description);
            event.setEventDate(Date.valueOf(dateField.getText().trim()));
            event.setEventTime(Time.valueOf(timeField.getText().trim() + ":00"));
            
            Venue selectedVenue = (Venue) venueCombo.getSelectedItem();
            event.setVenueId(selectedVenue.getVenueId());
            event.setCapacity(Integer.parseInt(capacityField.getText().trim()));
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
        
        // Validate pricing based on type
        if (categoryPriceRadio.isSelected()) {
            try {
                double vvipPrice = Double.parseDouble(vvipPriceField.getText().trim());
                double vipPrice = Double.parseDouble(vipPriceField.getText().trim());
                double casualPrice = Double.parseDouble(casualPriceField.getText().trim());
                
                if (vvipPrice < 0 || vipPrice < 0 || casualPrice < 0) {
                    JOptionPane.showMessageDialog(this, "Prices cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                
                if (vvipPrice < vipPrice || vipPrice < casualPrice) {
                    JOptionPane.showMessageDialog(this, "VVIP price should be highest, then VIP, then Casual!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "All category prices must be valid numbers!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else {
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
        }
        
        // Business rule: Event date must be future
        Date eventDate = Date.valueOf(dateField.getText().trim());
        if (eventDate.before(new Date(System.currentTimeMillis()))) {
            JOptionPane.showMessageDialog(this, "Event date cannot be in the past!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void updatePricingFields() {
        boolean showCategory = categoryPriceRadio.isSelected();
        if (categoryContainer != null) {
            categoryContainer.setVisible(showCategory);
        }
        priceField.setEnabled(!showCategory);
        if (showCategory) {
            priceField.setText("");
        } else {
            vvipPriceField.setText("");
            vipPriceField.setText("");
            casualPriceField.setText("");
        }
        revalidate();
        repaint();
    }
    
    private void loadSelectedEventToForm() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        try {
            int eventId = (int) tableModel.getValueAt(selectedRow, 0);
            Event event = eventDAO.getEventById(eventId);
            
            if (event != null) {
                nameField.setText(event.getEventName());
                
                // Clean description (remove old JSON pricing info if exists)
                String description = event.getEventDescription();
                if (description != null) {
                    description = description.replaceAll("\\[PRICING_INFO\\].*?\\[/PRICING_INFO\\]", "").trim();
                }
                descriptionArea.setText(description != null ? description : "");
                
                dateField.setText(event.getEventDate().toString());
                timeField.setText(event.getEventTime().toString().substring(0, 5)); // HH:MM format
                capacityField.setText(String.valueOf(event.getCapacity()));
                statusCombo.setSelectedItem(event.getStatus());
                
                // Set venue
                for (int i = 0; i < venueCombo.getItemCount(); i++) {
                    Venue venue = venueCombo.getItemAt(i);
                    if (venue.getVenueId() == event.getVenueId()) {
                        venueCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Handle pricing
                if ("category".equals(event.getPricingType()) && event.getVvipPrice() > 0) {
                    categoryPriceRadio.setSelected(true);
                    vvipPriceField.setText(String.format("%.2f", event.getVvipPrice()));
                    vipPriceField.setText(String.format("%.2f", event.getVipPrice()));
                    casualPriceField.setText(String.format("%.2f", event.getCasualPrice()));
                } else {
                    singlePriceRadio.setSelected(true);
                    priceField.setText(String.format("%.2f", event.getTicketPrice()));
                }
                updatePricingFields();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        dateField.setText("");
        timeField.setText("");
        capacityField.setText("");
        priceField.setText("");
        vvipPriceField.setText("");
        vipPriceField.setText("");
        casualPriceField.setText("");
        statusCombo.setSelectedIndex(0);
        singlePriceRadio.setSelected(true);
        updatePricingFields();
    }

    private void openAddVenueDialog() {
        JTextField venueNameField = Theme.createStyledTextField(20);
        JTextField locationField = Theme.createStyledTextField(20);
        JTextField venueCapacityField = Theme.createStyledTextField(10);
        JTextField contactPhoneField = Theme.createStyledTextField(15);
        JTextField rentalCostField = Theme.createStyledTextField(10);
        JTextArea facilitiesArea = Theme.createStyledTextArea(3, 20);
        JComboBox<String> availabilityCombo = Theme.createStyledComboBox();
        availabilityCombo.addItem("available");
        availabilityCombo.addItem("booked");
        availabilityCombo.addItem("maintenance");

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(Theme.BACKGROUND_WHITE);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.add(Theme.createStyledLabel("Venue Name:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY));
        form.add(venueNameField);
        form.add(Theme.createStyledLabel("Location:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY));
        form.add(locationField);
        form.add(Theme.createStyledLabel("Capacity:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY));
        form.add(venueCapacityField);
        form.add(Theme.createStyledLabel("Contact Phone:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY));
        form.add(contactPhoneField);
        form.add(Theme.createStyledLabel("Rental Cost:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY));
        form.add(rentalCostField);
        form.add(Theme.createStyledLabel("Facilities:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY));
        JScrollPane facilitiesScroll = new JScrollPane(facilitiesArea);
        facilitiesScroll.setBorder(null);
        facilitiesScroll.setOpaque(false);
        facilitiesScroll.getViewport().setOpaque(false);
        form.add(facilitiesScroll);
        form.add(Theme.createStyledLabel("Availability:", Theme.getSubheadingFont(), Theme.TEXT_PRIMARY));
        form.add(availabilityCombo);

        int result = JOptionPane.showConfirmDialog(this, form, "Add Venue",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String venueName = venueNameField.getText().trim();
            String location = locationField.getText().trim();
            String capacityText = venueCapacityField.getText().trim();
            String rentalText = rentalCostField.getText().trim();

            if (venueName.isEmpty() || location.isEmpty() || capacityText.isEmpty() || rentalText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int venueCapacity;
            try {
                venueCapacity = Integer.parseInt(capacityText);
                if (!ValidationUtil.isValidCapacity(venueCapacity)) {
                    JOptionPane.showMessageDialog(this, "Capacity must be between 10 and 10,000.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Capacity must be a number.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double rentalCost;
            try {
                rentalCost = Double.parseDouble(rentalText);
                if (rentalCost <= 0) {
                    JOptionPane.showMessageDialog(this, "Rental cost must be greater than zero.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Rental cost must be a valid number.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Venue venue = new Venue();
            venue.setVenueName(venueName);
            venue.setLocation(location);
            venue.setCapacity(venueCapacity);
            venue.setContactPhone(contactPhoneField.getText().trim());
            venue.setRentalCost(rentalCost);
            venue.setFacilities(facilitiesArea.getText().trim());
            venue.setAvailabilityStatus((String) availabilityCombo.getSelectedItem());

            if (venueDAO.createVenue(venue)) {
                JOptionPane.showMessageDialog(this, "Venue added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadVenues();
                selectVenueByName(venueName);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add venue.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectVenueByName(String venueName) {
        for (int i = 0; i < venueCombo.getItemCount(); i++) {
            Venue venue = venueCombo.getItemAt(i);
            if (venue.getVenueName().equalsIgnoreCase(venueName)) {
                venueCombo.setSelectedIndex(i);
                break;
            }
        }
    }
}

