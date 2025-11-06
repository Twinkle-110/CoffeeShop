-- CoffeeShop Database Setup Script
-- Run this script to create the required database and tables

-- Create database
CREATE DATABASE IF NOT EXISTS coffeeshop;

-- Use the database
USE coffeeshop;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    user_password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_username (user_name),
    UNIQUE KEY unique_phone (phone_number)
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    coffee_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    whipped_cream BOOLEAN DEFAULT FALSE,
    chocolate BOOLEAN DEFAULT FALSE,
    vanilla BOOLEAN DEFAULT FALSE,
    total_amount INT NOT NULL,
    payment_status VARCHAR(50) DEFAULT 'Pending',
    delivery_status BOOLEAN DEFAULT FALSE,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Optional: Insert sample data
-- INSERT INTO users (user_name, phone_number, user_password) 
-- VALUES ('testuser', '1234567890', 'Test@123');

-- Display tables
SHOW TABLES;

-- Display table structures
DESCRIBE users;
DESCRIBE orders;
