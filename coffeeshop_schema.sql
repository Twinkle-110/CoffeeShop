-- CoffeeShop Database Schema
-- Run this script in MySQL (port 3306) to set up the backend

CREATE DATABASE IF NOT EXISTS coffeeshop;
USE coffeeshop;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    user_name VARCHAR(100) NOT NULL,
    user_password VARCHAR(256) NOT NULL
);

-- Menu table
CREATE TABLE IF NOT EXISTS menu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    coffee_name VARCHAR(100) NOT NULL UNIQUE,
    price INT NOT NULL
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_id INT NOT NULL,
    user_id INT NOT NULL,
    quantity INT NOT NULL,
    whipped_cream BOOLEAN DEFAULT 0,
    chocolate BOOLEAN DEFAULT 0,
    vanilla BOOLEAN DEFAULT 0,
    total_amount INT NOT NULL,
    payment_status VARCHAR(50),
    delivery_status BOOLEAN DEFAULT 0,
    FOREIGN KEY (menu_id) REFERENCES menu(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert menu items
INSERT IGNORE INTO menu (coffee_name, price) VALUES
  ('Cappuccino', 170),
  ('Espresso', 120),
  ('Mocha', 160),
  ('Latte', 150),
  ('Hot Chocolate', 130),
  ('Americano', 135),
  ('Caramel Frape', 220),
  ('Flat White', 210),
  ('Macchiato', 175);
