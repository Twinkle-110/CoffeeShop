# CoffeeShop JavaFX Application

A JavaFX-based coffee shop management system with user authentication, menu ordering, payment processing, and admin panel.

## Features

- **User Authentication**: Login and signup functionality with validation
- **Admin Panel**: Admin access with username `admin` and password `admin`
- **Menu System**: Browse and order coffee with customizations (whipped cream, chocolate, vanilla)
- **Payment Processing**: Handle orders and payments
- **Order Management**: Track order status and delivery

## Prerequisites

- **Java 17** (Currently configured for Java 17)
- **Maven** (for building and running)
- **MySQL Server** (version 5.7 or higher)

## Database Setup

1. **Install MySQL** if not already installed

2. **Create the database**:
   ```sql
   CREATE DATABASE coffeeshop;
   ```

3. **Create the users table**:
   ```sql
   USE coffeeshop;
   
   CREATE TABLE users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       user_name VARCHAR(100) NOT NULL,
       phone_number VARCHAR(10) NOT NULL,
       user_password VARCHAR(100) NOT NULL,
       UNIQUE KEY unique_username (user_name),
       UNIQUE KEY unique_phone (phone_number)
   );
   ```

4. **Create the orders table**:
   ```sql
   CREATE TABLE orders (
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
       FOREIGN KEY (user_id) REFERENCES users(id)
   );
   ```

5. **Configure database connection**:
   
   The application connects to:
   - **Host**: `localhost`
   - **Port**: `3307` (change to `3306` if using default MySQL port)
   - **Database**: `coffeeshop`
   - **Username**: `root`
   - **Password**: `toor`
   
   To modify these settings, update the connection strings in:
   - `src/main/java/project/coffeeshop/LoginController.java` (line 97)
   - `src/main/java/project/coffeeshop/SignupController.java` (line 93)
   - `src/main/java/project/coffeeshop/MenuController.java`
   - `src/main/java/project/coffeeshop/AdminController.java`
   - `src/main/java/project/coffeeshop/PaymentController.java`

## Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Twinkle-110/CoffeeShop.git
   cd CoffeeShop
   ```

2. **Clean and compile**:
   ```bash
   mvn clean compile
   ```

3. **Package the application** (optional):
   ```bash
   mvn package
   ```

## Running the Application

### Using Maven:
```bash
mvn javafx:run
```

### Using Java directly:
```bash
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -jar target/CoffeeShop-1.0-SNAPSHOT.jar
```

## User Guide

### For Users:
1. **Sign Up**: 
   - Create a new account with username, phone number (10 digits), and password
   - Password requirements: 8-16 characters with uppercase, lowercase, and digit

2. **Login**: 
   - Use your username and password to log in
   - Browse the menu and add items to your cart
   - Customize your coffee with toppings
   - Proceed to payment

### For Admins:
1. **Login with admin credentials**:
   - Username: `admin`
   - Password: `admin`

2. **Admin Panel Features**:
   - View all orders
   - Update order status
   - Manage deliveries

## Project Structure

```
CoffeeShop/
├── src/
│   └── main/
│       ├── java/
│       │   └── project/
│       │       └── coffeeshop/
│       │           ├── Launcher.java         # Application entry point
│       │           ├── Main.java             # JavaFX application main class
│       │           ├── LoginController.java  # Login screen logic
│       │           ├── SignupController.java # Signup screen logic
│       │           ├── MenuController.java   # Menu/ordering logic
│       │           ├── PaymentController.java# Payment processing
│       │           ├── AdminController.java  # Admin panel logic
│       │           └── Model.java            # Order data model
│       └── resources/
│           └── project/
│               └── coffeeshop/
│                   ├── Login.fxml            # Login screen UI
│                   ├── Signup.fxml           # Signup screen UI
│                   ├── Menu.fxml             # Menu screen UI
│                   ├── Payment.fxml          # Payment screen UI
│                   ├── Admin.fxml            # Admin panel UI
│                   └── *.jpg/*.jpeg          # Image assets
├── pom.xml                                   # Maven configuration
└── README.md                                 # This file
```

## Dependencies

- JavaFX 21.0.5 (controls, fxml, web, swing)
- JFoenix 9.0.10 (Material Design components)
- MySQL Connector/J 8.3.0
- JUnit 5.10.2 (testing)

## Troubleshooting

### Database Connection Error
- Ensure MySQL is running on port 3307 (or update the port in the code)
- Verify username and password are correct (`root`/`toor`)
- Check if the `coffeeshop` database exists

### Compilation Errors
- Ensure Java 17 is installed: `java -version`
- Clean and rebuild: `mvn clean install`

### Application Won't Start
- Check if all FXML files are in the correct location
- Verify all dependencies are downloaded: `mvn dependency:resolve`

## Known Issues

- Large JAR file warning from GitHub (57.15 MB) - Consider using Git LFS for the artifacts

## License

This is an educational project.

## Contact

For issues or questions, please open an issue on GitHub.
