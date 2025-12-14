-- =====================================================
-- Local Event Management System Database Schema
-- =====================================================
-- This script is for REFERENCE ONLY
-- Hibernate will automatically create/update tables based on entity definitions
-- when hibernate.hbm2ddl.auto is set to "update"
-- =====================================================

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    user_role VARCHAR(20) CHECK (user_role IN ('admin', 'organizer', 'customer')),
    account_status VARCHAR(20) CHECK (account_status IN ('active', 'suspended', 'pending')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Create venues table
CREATE TABLE IF NOT EXISTS venues (
    venue_id SERIAL PRIMARY KEY,
    venue_name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity >= 10 AND capacity <= 10000),
    contact_phone VARCHAR(20),
    rental_cost DECIMAL(10,2) NOT NULL CHECK (rental_cost > 0),
    facilities TEXT,
    availability_status VARCHAR(20) CHECK (availability_status IN ('available', 'booked', 'maintenance'))
);

-- Create events table
CREATE TABLE IF NOT EXISTS events (
    event_id SERIAL PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    event_description TEXT,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    venue_id INTEGER REFERENCES venues(venue_id) ON DELETE RESTRICT,
    organizer_id INTEGER REFERENCES users(user_id) ON DELETE RESTRICT,
    capacity INTEGER NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL CHECK (ticket_price >= 0),
    vvip_price DECIMAL(10,2),
    vip_price DECIMAL(10,2),
    casual_price DECIMAL(10,2),
    pricing_type VARCHAR(20) DEFAULT 'single',
    status VARCHAR(20) CHECK (status IN ('scheduled', 'ongoing', 'completed', 'cancelled')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id SERIAL PRIMARY KEY,
    event_id INTEGER REFERENCES events(event_id) ON DELETE RESTRICT,
    user_id INTEGER REFERENCES users(user_id) ON DELETE RESTRICT,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) CHECK (payment_status IN ('pending', 'paid', 'refunded', 'cancelled')),
    ticket_number VARCHAR(50) UNIQUE,
    number_of_tickets INTEGER DEFAULT 1 CHECK (number_of_tickets >= 1 AND number_of_tickets <= 10),
    total_amount DECIMAL(10,2),
    check_in_status BOOLEAN DEFAULT FALSE,
    ticket_category VARCHAR(20)
);

-- Create payments table (optional but included)
CREATE TABLE IF NOT EXISTS payments (
    payment_id SERIAL PRIMARY KEY,
    booking_id INTEGER REFERENCES bookings(booking_id) ON DELETE RESTRICT,
    payment_method VARCHAR(20) CHECK (payment_method IN ('cash', 'mobile_money', 'bank_transfer', 'card')),
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_reference VARCHAR(100) UNIQUE,
    status VARCHAR(20) CHECK (status IN ('successful', 'failed', 'pending'))
);

-- =====================================================
-- NEW TABLES FOR HIBERNATE RELATIONSHIPS
-- =====================================================

-- Create user_profiles table (One-to-One relationship with users)
-- This table stores additional user profile information
CREATE TABLE IF NOT EXISTS user_profiles (
    profile_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    bio TEXT,
    preferences TEXT,
    notification_enabled BOOLEAN DEFAULT TRUE,
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE
);

-- Create event_tags table (for Many-to-Many relationship)
-- This table stores available tags that can be assigned to events
CREATE TABLE IF NOT EXISTS event_tags (
    tag_id SERIAL PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- Create event_event_tags join table (Many-to-Many relationship)
-- Hibernate will create this automatically via @JoinTable annotation
-- This table links events to tags
CREATE TABLE IF NOT EXISTS event_tags (
    event_id INTEGER NOT NULL REFERENCES events(event_id) ON DELETE CASCADE,
    tag_id INTEGER NOT NULL REFERENCES event_tags(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, tag_id)
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_events_organizer ON events(organizer_id);
CREATE INDEX IF NOT EXISTS idx_events_venue ON events(venue_id);
CREATE INDEX IF NOT EXISTS idx_events_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_bookings_user ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_event ON bookings(event_id);
CREATE INDEX IF NOT EXISTS idx_bookings_ticket_number ON bookings(ticket_number);

-- =====================================================
-- INITIAL DATA
-- =====================================================

-- Insert default admin user (password: Admin123)
-- MD5 hash: 482c811da5d5b4bc6d497ffa98491e38
INSERT INTO users (username, password, email, full_name, user_role, account_status) 
VALUES ('admin', '482c811da5d5b4bc6d497ffa98491e38', 'admin@events.com', 'System Administrator', 'admin', 'active')
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- NOTES:
-- =====================================================
-- 1. Hibernate will automatically create/update these tables when the server starts
-- 2. The hibernate.hbm2ddl.auto property is set to "update" in hibernate.cfg.xml
-- 3. This script is provided for reference and manual setup if needed
-- 4. Foreign key constraints use ON DELETE RESTRICT to prevent accidental data loss
-- 5. The event_tags join table name matches the @JoinTable annotation in Event entity
