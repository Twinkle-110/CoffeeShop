# CoffeeShop - Quick Start Guide

## 🚀 Get Started in 3 Steps

### Prerequisites Check
- ✅ Java 17 installed
- ✅ Maven installed
- ✅ MySQL Server running on port 3307

---

## Step 1: Setup Database (One-time)

Open MySQL command line or workbench and run:

```bash
mysql -u root -ptoor -P 3307 < database_setup.sql
```

Or manually:
```sql
CREATE DATABASE coffeeshop;
USE coffeeshop;
-- Copy the rest from database_setup.sql
```

---

## Step 2: Build the Project

Open terminal in project directory and run:

```bash
mvn clean compile
```

Wait for "BUILD SUCCESS" message.

---

## Step 3: Run the Application

```bash
mvn javafx:run
```

The login window should appear!

---

## 🎯 Test the Application

### Option 1: Admin Login
- Username: `admin`
- Password: `admin`
- Access: Admin panel with order management

### Option 2: Create User Account
1. Click "Sign Up" button
2. Fill in details:
   - Username: Any name
   - Phone: 10 digits (e.g., 9876543210)
   - Password: 8-16 chars with Upper+Lower+Digit (e.g., Test@123)
3. Click "Sign Up"
4. Login with your credentials
5. Browse menu and place orders!

---

## 🔧 Troubleshooting

### Can't connect to database?
```bash
# Check if MySQL is running
mysql -u root -ptoor -P 3307 -e "SHOW DATABASES;"

# If port is wrong, update these files:
# - LoginController.java line 97
# - SignupController.java line 93
# Change 3307 to 3306 or your MySQL port
```

### Compilation errors?
```bash
# Clean everything and rebuild
mvn clean install

# Check Java version
java -version
# Should show Java 17
```

### Application won't start?
```bash
# Check if JavaFX dependencies downloaded
mvn dependency:resolve

# Try running with verbose output
mvn javafx:run -X
```

---

## 📁 File Locations

- **Source Code**: `src/main/java/project/coffeeshop/`
- **UI Files**: `src/main/resources/project.coffeeshop/`
- **Database Script**: `database_setup.sql`
- **Built JAR**: `target/CoffeeShop-1.0-SNAPSHOT.jar`

---

## 🎓 What You Can Do

### As User:
- Sign up and create account
- Login with credentials
- Browse coffee menu
- Add items to cart
- Customize with toppings (whipped cream, chocolate, vanilla)
- Place and pay for orders

### As Admin:
- View all orders
- Update order status
- Manage deliveries
- Track payments

---

## 📝 Quick Commands Reference

| Command | Purpose |
|---------|---------|
| `mvn clean` | Remove built files |
| `mvn compile` | Compile source code |
| `mvn package` | Create JAR file |
| `mvn javafx:run` | Run the application |
| `mvn test` | Run unit tests |

---

## 💡 Tips

1. **First Run**: Always setup database first
2. **Development**: Keep MySQL running while developing
3. **Testing**: Use admin credentials for quick access
4. **Issues**: Check console output for error messages

---

## 🌐 Resources

- **GitHub**: https://github.com/Twinkle-110/CoffeeShop
- **Full Documentation**: See README.md
- **Project Status**: See PROJECT_STATUS.md

---

## ⚠️ Important Notes

- Database username: `root`
- Database password: `toor`
- Database port: `3307` (not standard 3306)
- Admin credentials are hardcoded (change in production!)
- Passwords stored in plaintext (add hashing for production!)

---

**Need Help?** Check the detailed README.md or create an issue on GitHub.

**Ready to start?** Run `mvn javafx:run` and enjoy! ☕
