-- Create users table
CREATE TABLE users (
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
CREATE TABLE venues (
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
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    event_description TEXT,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    venue_id INTEGER REFERENCES venues(venue_id),
    organizer_id INTEGER REFERENCES users(user_id),
    capacity INTEGER NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL CHECK (ticket_price >= 0),
    status VARCHAR(20) CHECK (status IN ('scheduled', 'ongoing', 'completed', 'cancelled')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create bookings table
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    event_id INTEGER REFERENCES events(event_id),
    user_id INTEGER REFERENCES users(user_id),
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) CHECK (payment_status IN ('pending', 'paid', 'refunded', 'cancelled')),
    ticket_number VARCHAR(50) UNIQUE,
    number_of_tickets INTEGER DEFAULT 1 CHECK (number_of_tickets >= 1 AND number_of_tickets <= 10),
    total_amount DECIMAL(10,2),
    check_in_status BOOLEAN DEFAULT FALSE
);

-- Create payments table (optional but included)
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    booking_id INTEGER REFERENCES bookings(booking_id),
    payment_method VARCHAR(20) CHECK (payment_method IN ('cash', 'mobile_money', 'bank_transfer', 'card')),
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_reference VARCHAR(100) UNIQUE,
    status VARCHAR(20) CHECK (status IN ('successful', 'failed', 'pending'))
);
-- Insert default admin user (password: Admin123)
INSERT INTO users (username, password, email, full_name, user_role, account_status) 
VALUES ('admin', '482c811da5d5b4bc6d497ffa98491e38', 'admin@events.com', 'System Administrator', 'admin', 'active');

s

ALTER TABLE events ADD COLUMN vvip_price DECIMAL(10,2);
ALTER TABLE events ADD COLUMN vip_price DECIMAL(10,2);
ALTER TABLE events ADD COLUMN casual_price DECIMAL(10,2);
ALTER TABLE events ADD COLUMN pricing_type VARCHAR(20) DEFAULT 'single';

ALTER TABLE bookings ADD COLUMN ticket_category VARCHAR(20);