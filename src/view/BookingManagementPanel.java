package view;

import dao.BookingDAO;
import model.Booking;
import util.SessionManager;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingManagementPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private BookingDAO bookingDAO;
    
    public BookingManagementPanel() {
        bookingDAO = new BookingDAO();
        setupMenuBar();
        initializeUI();
        loadBookings();
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
        
        // Container for title and table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        
        // Title panel with permanent background
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(true);
        titlePanel.setBackground(Theme.PRIMARY_COLOR);
        titlePanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        titlePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 55));
        JLabel titleLabel = new JLabel("Bookings");
        titleLabel.setFont(Theme.getSubheadingFont());
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Table card
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(0, 20, 20, 20)); // Remove top padding
        
        String[] columns = {"Booking ID", "Event", "Customer", "Date", "Tickets", "Category", "Amount", "Status", "Ticket#"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        Theme.styleTable(bookingsTable);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
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
        
        if (SessionManager.isAdmin()) {
            JButton updateStatusButton = Theme.createPrimaryButton("Update Payment Status");
            updateStatusButton.addActionListener(e -> updatePaymentStatus());
            buttonPanel.add(updateStatusButton);
        }
        
        if (SessionManager.isCustomer()) {
            JButton cancelButton = Theme.createDangerButton("Cancel Booking");
            cancelButton.addActionListener(e -> cancelBooking());
            buttonPanel.add(cancelButton);
            
            JButton reactivateButton = Theme.createSuccessButton("Reactivate Booking");
            reactivateButton.addActionListener(e -> reactivateBooking());
            buttonPanel.add(reactivateButton);
        }
        
        JButton printButton = Theme.createInfoButton("Print Ticket");
        printButton.addActionListener(e -> printTicket());
        buttonPanel.add(printButton);
        
        JButton refreshButton = Theme.createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> loadBookings());
        buttonPanel.add(refreshButton);
        
        add(tableContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings;
        
        if (SessionManager.isAdmin()) {
            bookings = bookingDAO.getAllBookings();
        } else {
            bookings = bookingDAO.getBookingsByUser(SessionManager.getCurrentUser().getUserId());
        }
        
        for (Booking booking : bookings) {
            String categoryDisplay = booking.getTicketCategory() != null ? 
                booking.getTicketCategory().toUpperCase() : "Standard";
            Object[] row = {
                booking.getBookingId(),
                booking.getEventName(),
                booking.getUserName(),
                booking.getBookingDate(),
                booking.getNumberOfTickets(),
                categoryDisplay,
                "RWF " + booking.getTotalAmount(),
                booking.getPaymentStatus(),
                booking.getTicketNumber()
            };
            tableModel.addRow(row);
        }
    }
    
    private void updatePaymentStatus() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String[] statuses = {"pending", "paid", "refunded", "cancelled"};
        
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new payment status:",
            "Update Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            tableModel.getValueAt(selectedRow, 7));
        
        if (newStatus != null) {
            if (bookingDAO.updatePaymentStatus(bookingId, newStatus)) {
                JOptionPane.showMessageDialog(this, 
                    "Payment status updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update payment status.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to cancel!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        if ("cancelled".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, 
                "This booking is already cancelled!", 
                "Already Cancelled", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking? You can reactivate it later if you change your mind.",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            
            if (bookingDAO.cancelBooking(bookingId)) {
                JOptionPane.showMessageDialog(this, 
                    "Booking cancelled successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to cancel booking.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void reactivateBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to reactivate!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        if (!"cancelled".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, 
                "Only cancelled bookings can be reactivated!", 
                "Invalid Status", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reactivate this booking?",
            "Confirm Reactivation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            
            if (bookingDAO.reactivateBooking(bookingId)) {
                JOptionPane.showMessageDialog(this, 
                    "Booking reactivated successfully! Status changed to pending.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to reactivate booking.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void printTicket() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to print!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        Booking booking = bookingDAO.getBookingById(bookingId);
        
        if (booking == null) {
            JOptionPane.showMessageDialog(this, 
                "Failed to load booking details.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create printable ticket content
        StringBuilder ticketContent = new StringBuilder();
        ticketContent.append("========================================\n");
        ticketContent.append("        EVENT TICKET\n");
        ticketContent.append("========================================\n\n");
        ticketContent.append("Ticket Number: ").append(booking.getTicketNumber()).append("\n");
        ticketContent.append("Booking ID: ").append(booking.getBookingId()).append("\n\n");
        ticketContent.append("Event: ").append(booking.getEventName()).append("\n");
        if (booking.getEventDescription() != null && !booking.getEventDescription().isEmpty()) {
            ticketContent.append("Description: ").append(booking.getEventDescription()).append("\n");
        }
        ticketContent.append("Date: ").append(booking.getEventDate()).append("\n");
        if (booking.getEventTime() != null) {
            ticketContent.append("Time: ").append(booking.getEventTime()).append("\n");
        }
        ticketContent.append("Venue: ").append(booking.getVenueName()).append("\n");
        if (booking.getVenueLocation() != null) {
            ticketContent.append("Location: ").append(booking.getVenueLocation()).append("\n");
        }
        ticketContent.append("\n");
        ticketContent.append("Customer: ").append(booking.getUserName()).append("\n");
        if (booking.getUserEmail() != null) {
            ticketContent.append("Email: ").append(booking.getUserEmail()).append("\n");
        }
        if (booking.getUserPhone() != null) {
            ticketContent.append("Phone: ").append(booking.getUserPhone()).append("\n");
        }
        ticketContent.append("\n");
        ticketContent.append("Number of Tickets: ").append(booking.getNumberOfTickets()).append("\n");
        if (booking.getTicketCategory() != null) {
            ticketContent.append("Category: ").append(booking.getTicketCategory().toUpperCase()).append("\n");
        }
        ticketContent.append("Total Amount: RWF ").append(String.format("%.2f", booking.getTotalAmount())).append("\n");
        ticketContent.append("Status: ").append(booking.getPaymentStatus().toUpperCase()).append("\n");
        ticketContent.append("\n");
        ticketContent.append("========================================\n");
        ticketContent.append("Thank you for your booking!\n");
        ticketContent.append("========================================\n");
        
        // Show ticket in a dialog with print option
        JTextArea ticketArea = new JTextArea(ticketContent.toString());
        ticketArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        ticketArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ticketArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        int option = JOptionPane.showOptionDialog(this,
            scrollPane,
            "Ticket Details - " + booking.getTicketNumber(),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"Print", "Close"},
            "Print");
        
        if (option == 0) { // Print button clicked
            try {
                ticketArea.print();
                JOptionPane.showMessageDialog(this,
                    "Ticket sent to printer!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to print ticket: " + e.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

