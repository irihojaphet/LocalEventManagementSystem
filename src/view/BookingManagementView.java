package view;

import dao.BookingDAO;
import model.Booking;
import util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingManagementView extends JFrame {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private BookingDAO bookingDAO;
    
    public BookingManagementView() {
        bookingDAO = new BookingDAO();
        initializeUI();
        loadBookings();
    }
    
    private void initializeUI() {
        setTitle("Booking Management");
        setSize(900, 500);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"Booking ID", "Event", "Customer", "Date", "Tickets", "Amount", "Status", "Ticket#"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bookings"));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        if (SessionManager.isAdmin()) {
            JButton updateStatusButton = new JButton("Update Payment Status");
            updateStatusButton.setBackground(new Color(70, 130, 180));
            updateStatusButton.setForeground(Color.BLACK);
            updateStatusButton.addActionListener(e -> updatePaymentStatus());
            buttonPanel.add(updateStatusButton);
        }
        
        if (SessionManager.isCustomer()) {
            JButton cancelButton = new JButton("Cancel Booking");
            cancelButton.setBackground(new Color(220, 20, 60));
            cancelButton.setForeground(Color.BLACK);
            cancelButton.addActionListener(e -> cancelBooking());
            buttonPanel.add(cancelButton);
        }
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadBookings());
        buttonPanel.add(refreshButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
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
            Object[] row = {
                booking.getBookingId(),
                booking.getEventName(),
                booking.getUserName(),
                booking.getBookingDate(),
                booking.getNumberOfTickets(),
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
            tableModel.getValueAt(selectedRow, 6));
        
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
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking? This action cannot be undone.",
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
}