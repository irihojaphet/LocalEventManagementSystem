package dao;

import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DashboardDAO {
    private final Connection connection;

    public DashboardDAO() {
        connection = DatabaseConnection.getConnection();
    }

    public int getTotalEvents() {
        return getCount("SELECT COUNT(*) FROM events");
    }

    public int getUpcomingEventsCount() {
        String query = "SELECT COUNT(*) FROM events WHERE status = 'scheduled' AND event_date >= CURRENT_DATE";
        return getCount(query);
    }

    public int getTotalUsers() {
        return getCount("SELECT COUNT(*) FROM users");
    }

    public double getTotalRevenue() {
        String query = "SELECT COALESCE(SUM(total_amount), 0) AS revenue FROM bookings WHERE payment_status = 'paid'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getCount(String query) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

