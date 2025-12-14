# Validation Rules Documentation

## Overview
This document categorizes all validation rules implemented in the Local Event Management System into **Business Rules** and **Technical Rules**.

---

## BUSINESS VALIDATION RULES (5 Unique Rules)

Business rules enforce domain-specific logic and business policies.

### 1. Event Date Must Be in the Future
- **Location**: `EventController.java`, `EventManagementPanel.java`
- **Rule**: Events cannot be created with dates in the past
- **Implementation**: `event.getEventDate().before(new Date(System.currentTimeMillis()))`
- **Message**: "Event date cannot be in the past!" / "Cannot create events in the past."

### 2. Capacity Range Validation
- **Location**: `ValidationUtil.java`, `EventManagementPanel.java`
- **Rule**: Event/Venue capacity must be between 10 and 10,000
- **Implementation**: `capacity >= 10 && capacity <= 10000`
- **Message**: "Capacity must be between 10 and 10,000!"

### 3. Ticket Quantity Limit
- **Location**: `ValidationUtil.java`
- **Rule**: Number of tickets per booking must be between 1 and 10
- **Implementation**: `quantity >= 1 && quantity <= 10`
- **Message**: Used in booking validation

### 4. Category Pricing Hierarchy
- **Location**: `EventManagementPanel.java`
- **Rule**: VVIP price must be highest, then VIP, then Casual (VVIP > VIP > Casual)
- **Implementation**: `vvipPrice < vipPrice || vipPrice < casualPrice`
- **Message**: "VVIP price should be highest, then VIP, then Casual!"

### 5. Rental Cost Must Be Positive
- **Location**: `EventManagementPanel.java`
- **Rule**: Venue rental cost must be greater than zero
- **Implementation**: `rentalCost <= 0`
- **Message**: "Rental cost must be greater than zero."

---

## TECHNICAL VALIDATION RULES (5 Unique Rules)

Technical rules enforce data format, type, and structural constraints.

### 1. Email Format Validation
- **Location**: `ValidationUtil.java`, `RegistrationPanel.java`
- **Rule**: Email must match standard email format (regex pattern)
- **Implementation**: `Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")`
- **Message**: "Please enter a valid email address!"

### 2. Username Format Validation
- **Location**: `ValidationUtil.java`, `RegistrationPanel.java`
- **Rule**: Username must be 4-20 alphanumeric characters only
- **Implementation**: `Pattern.compile("^[a-zA-Z0-9]{4,20}$")`
- **Message**: "Username must be 4-20 alphanumeric characters only!"

### 3. Phone Number Format Validation (Rwanda)
- **Location**: `ValidationUtil.java`, `RegistrationPanel.java`
- **Rule**: Phone number must match Rwanda format (+250 or 0 followed by 7XXXXXXXX)
- **Implementation**: `Pattern.compile("^(\\+250|0)[7][0-9]{8}$")`
- **Message**: "Phone number must be in format: +250XXXXXXXXX or 07XXXXXXXX"

### 4. Password Strength Validation
- **Location**: `PasswordUtil.java`, `RegistrationPanel.java`
- **Rule**: Password must be at least 8 characters with uppercase, lowercase, and digit
- **Implementation**: Checks length >= 8, has uppercase, has lowercase, has digit
- **Message**: "Password must be at least 8 characters with 1 uppercase and 1 number!"

### 5. Date/Time Format Validation
- **Location**: `EventManagementPanel.java`
- **Rule**: Date must be in YYYY-MM-DD format, Time must be in HH:MM format
- **Implementation**: `Date.valueOf(dateField.getText())` and `Time.valueOf(timeField.getText() + ":00")`
- **Message**: "Invalid date format! Use YYYY-MM-DD" / "Invalid time format! Use HH:MM"

---

## ADDITIONAL VALIDATION RULES (Supporting Rules)

These are supporting validations that complement the main rules:

### Supporting Business Rules:
- **Price Cannot Be Negative**: `price >= 0` (applies to all price fields)
- **Required Fields Validation**: All mandatory fields must be filled
- **Password Confirmation Match**: Password and confirm password must match

### Supporting Technical Rules:
- **Numeric Field Validation**: Capacity, prices, quantities must be valid numbers
- **Required Field Validation**: All required fields must not be empty
- **Connection Validation**: RMI server connection must be available

---

## Summary

- **Business Rules**: 5 unique rules (Event date future, Capacity range, Ticket quantity, Pricing hierarchy, Rental cost positive)
- **Technical Rules**: 5 unique rules (Email format, Username format, Phone format, Password strength, Date/Time format)

All validation rules are properly implemented with user-friendly error messages displayed via `JOptionPane`.

