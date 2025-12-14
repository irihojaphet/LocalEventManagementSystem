package view;

import dao.BookingDAO;
import model.Booking;
import util.SessionManager;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class BookingManagementPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private BookingDAO bookingDAO;
    
    public BookingManagementPanel() {
        bookingDAO = new BookingDAO();
        initializeUI();
        loadBookings();
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
        titlePanel.setBackground(Theme.PRIMARY);
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
        bookingsTable.setAutoCreateRowSorter(false);
        bookingsTable.setFillsViewportHeight(true);
        bookingsTable.setShowGrid(true);
        bookingsTable.setShowHorizontalLines(true);
        bookingsTable.setShowVerticalLines(true);
        
        // Ensure table header is visible and styled with dark blue background
        JTableHeader header = bookingsTable.getTableHeader();
        header.setVisible(true);
        header.setReorderingAllowed(false);
        header.setOpaque(true);
        header.setBackground(Theme.SIDEBAR_BG); // Same dark blue as sidebar
        header.setForeground(Theme.TEXT_WHITE);
        header.setFont(Theme.getSubheadingFont());
        
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
        
        try {
            if (SessionManager.isAdmin()) {
                bookings = bookingDAO.getAllBookings();
            } else {
                bookings = bookingDAO.getBookingsByUser(SessionManager.getCurrentUser().getUserId());
            }
            
            if (bookings == null || bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No bookings found.",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Booking booking : bookings) {
                String categoryDisplay = booking.getTicketCategory() != null ? 
                    booking.getTicketCategory().toUpperCase() : "Standard";
                
                // Get event name - use display field or fallback to ID
                String eventName = booking.getEventName();
                if (eventName == null || eventName.trim().isEmpty()) {
                    eventName = "Event #" + booking.getEventId();
                }
                
                // Get user name - use display field or fallback to ID
                String userName = booking.getUserName();
                if (userName == null || userName.trim().isEmpty()) {
                    userName = "User #" + booking.getUserId();
                }
                
                // Get event date - prefer eventDate (from event) over bookingDate
                // Combine date and time if both are available
                String eventDateStr = "N/A";
                if (booking.getEventDate() != null && !booking.getEventDate().trim().isEmpty()) {
                    eventDateStr = booking.getEventDate();
                    // If time is also available, append it
                    if (booking.getEventTime() != null && !booking.getEventTime().trim().isEmpty()) {
                        eventDateStr += " " + booking.getEventTime();
                    }
                } else if (booking.getBookingDate() != null) {
                    // Fallback to booking date if event date is not available
                    eventDateStr = booking.getBookingDate().toString();
                }
                
                Object[] row = {
                    booking.getBookingId(),
                    eventName,
                    userName,
                    eventDateStr,
                    booking.getNumberOfTickets(),
                    categoryDisplay,
                    "RWF " + String.format("%.2f", booking.getTotalAmount()),
                    booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "pending",
                    booking.getTicketNumber() != null ? booking.getTicketNumber() : "N/A"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading bookings: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
        
        // Check if payment is approved (status = 'paid')
        if (!"paid".equalsIgnoreCase(booking.getPaymentStatus())) {
            String message = "Ticket cannot be printed!\n\n";
            message += "Payment Status: " + booking.getPaymentStatus().toUpperCase() + "\n";
            message += "Only bookings with PAID status can be printed.\n\n";
            
            if ("pending".equalsIgnoreCase(booking.getPaymentStatus())) {
                message += "Please wait for admin to approve your payment.";
            } else if ("cancelled".equalsIgnoreCase(booking.getPaymentStatus())) {
                message += "This booking has been cancelled.";
            } else if ("refunded".equalsIgnoreCase(booking.getPaymentStatus())) {
                message += "This booking has been refunded.";
            }
            
            JOptionPane.showMessageDialog(this, 
                message,
                "Payment Not Approved", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Create HTML ticket
            String html = generateTicketHTML(booking);
            
            // Save to temp file
            java.io.File tempFile = java.io.File.createTempFile("ticket_" + booking.getTicketNumber(), ".html");
            java.io.FileWriter writer = new java.io.FileWriter(tempFile);
            writer.write(html);
            writer.close();
            
            // Open in browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(tempFile.toURI());
                JOptionPane.showMessageDialog(this,
                    "Ticket opened in browser. Use Ctrl+P or Cmd+P to print.",
                    "Print Ready",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Ticket saved to: " + tempFile.getAbsolutePath(),
                    "Ticket Saved",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to generate ticket: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private String generateTicketHTML(Booking booking) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>Ticket - ").append(booking.getTicketNumber()).append("</title>\n");
        html.append("<style>\n");
        html.append("@media print { .no-print { display: none; } }\n");
        html.append("body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 20px; }\n");
        html.append(".ticket { border: 3px solid #6366F1; border-radius: 15px; padding: 30px; background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%); }\n");
        html.append(".header { text-align: center; border-bottom: 2px dashed #6366F1; padding-bottom: 20px; margin-bottom: 20px; }\n");
        html.append(".header h1 { color: #6366F1; margin: 0; font-size: 32px; }\n");
        html.append(".ticket-number { background: #6366F1; color: white; padding: 10px 20px; border-radius: 25px; display: inline-block; margin: 10px 0; font-size: 18px; font-weight: bold; }\n");
        html.append(".section { margin: 20px 0; }\n");
        html.append(".section-title { color: #6366F1; font-size: 18px; font-weight: bold; margin-bottom: 10px; border-bottom: 1px solid #e5e7eb; padding-bottom: 5px; }\n");
        html.append(".info-row { display: flex; padding: 8px 0; }\n");
        html.append(".info-label { font-weight: bold; width: 180px; color: #4b5563; }\n");
        html.append(".info-value { color: #111827; }\n");
        html.append(".total { background: #f3f4f6; padding: 15px; border-radius: 8px; text-align: right; font-size: 20px; font-weight: bold; color: #6366F1; margin-top: 20px; }\n");
        html.append(".footer { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 2px dashed #6366F1; color: #6b7280; }\n");
        html.append(".print-btn { background: #6366F1; color: white; border: none; padding: 12px 30px; border-radius: 8px; font-size: 16px; cursor: pointer; margin: 20px auto; display: block; }\n");
        html.append(".print-btn:hover { background: #4f46e5; }\n");
        html.append("</style>\n");
        html.append("<script>\n");
        html.append("window.onload = function() { setTimeout(function(){ window.print(); }, 500); };\n");
        html.append("</script>\n");
        html.append("</head>\n<body>\n");
        
        html.append("<div class='ticket'>\n");
        html.append("<div class='header'>\n");
        html.append("<h1>🎫 EVENT TICKET</h1>\n");
        html.append("<div class='ticket-number'>").append(booking.getTicketNumber()).append("</div>\n");
        html.append("</div>\n");
        
        // Event Details
        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>📅 Event Details</div>\n");
        html.append("<div class='info-row'><div class='info-label'>Event:</div><div class='info-value'>").append(booking.getEventName()).append("</div></div>\n");
        if (booking.getEventDescription() != null && !booking.getEventDescription().isEmpty()) {
            html.append("<div class='info-row'><div class='info-label'>Description:</div><div class='info-value'>").append(booking.getEventDescription()).append("</div></div>\n");
        }
        html.append("<div class='info-row'><div class='info-label'>Date:</div><div class='info-value'>").append(booking.getEventDate()).append("</div></div>\n");
        if (booking.getEventTime() != null) {
            html.append("<div class='info-row'><div class='info-label'>Time:</div><div class='info-value'>").append(booking.getEventTime()).append("</div></div>\n");
        }
        html.append("</div>\n");
        
        // Venue Details
        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>📍 Venue Information</div>\n");
        html.append("<div class='info-row'><div class='info-label'>Venue:</div><div class='info-value'>").append(booking.getVenueName()).append("</div></div>\n");
        if (booking.getVenueLocation() != null) {
            html.append("<div class='info-row'><div class='info-label'>Location:</div><div class='info-value'>").append(booking.getVenueLocation()).append("</div></div>\n");
        }
        html.append("</div>\n");
        
        // Customer Details
        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>👤 Customer Information</div>\n");
        html.append("<div class='info-row'><div class='info-label'>Name:</div><div class='info-value'>").append(booking.getUserName()).append("</div></div>\n");
        if (booking.getUserEmail() != null) {
            html.append("<div class='info-row'><div class='info-label'>Email:</div><div class='info-value'>").append(booking.getUserEmail()).append("</div></div>\n");
        }
        if (booking.getUserPhone() != null) {
            html.append("<div class='info-row'><div class='info-label'>Phone:</div><div class='info-value'>").append(booking.getUserPhone()).append("</div></div>\n");
        }
        html.append("</div>\n");
        
        // Ticket Details
        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>🎟️ Ticket Information</div>\n");
        html.append("<div class='info-row'><div class='info-label'>Booking ID:</div><div class='info-value'>#").append(booking.getBookingId()).append("</div></div>\n");
        html.append("<div class='info-row'><div class='info-label'>Number of Tickets:</div><div class='info-value'>").append(booking.getNumberOfTickets()).append("</div></div>\n");
        if (booking.getTicketCategory() != null) {
            html.append("<div class='info-row'><div class='info-label'>Category:</div><div class='info-value'>").append(booking.getTicketCategory().toUpperCase()).append("</div></div>\n");
        }
        html.append("<div class='info-row'><div class='info-label'>Status:</div><div class='info-value' style='color: #10b981; font-weight: bold;'>").append(booking.getPaymentStatus().toUpperCase()).append("</div></div>\n");
        html.append("</div>\n");
        
        // Total
        html.append("<div class='total'>Total Amount: RWF ").append(String.format("%.2f", booking.getTotalAmount())).append("</div>\n");
        
        // Footer
        html.append("<div class='footer'>\n");
        html.append("<p><strong>Thank you for your booking!</strong></p>\n");
        html.append("<p>Please present this ticket at the venue entrance.</p>\n");
        html.append("<p style='font-size: 12px; color: #9ca3af;'>Generated on ").append(new java.util.Date()).append("</p>\n");
        html.append("</div>\n");
        
        html.append("</div>\n");
        html.append("<button class='print-btn no-print' onclick='window.print()'>🖨️ Print Ticket</button>\n");
        html.append("</body>\n</html>");
        
        return html.toString();
    }
}

