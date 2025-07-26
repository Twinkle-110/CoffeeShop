package project.coffeeshop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;

public class MenuController implements Initializable {

    public static int amount = 0;
    public static int orderId; // Store order ID for payment page
    private static final List<CartItem> cart = new ArrayList<>();
    public static List<CartItem> getCart() { return cart; }
    // Map to store coffee types and their respective prices
    private final Map<String, Integer> coffeePrices = new HashMap<>();
    @FXML
    private TextField txtquantity;
    @FXML
    private ComboBox<String> CboxType;
    @FXML
    private CheckBox whippedCream;
    @FXML
    private CheckBox chocolate;
    @FXML
    private CheckBox vanilla;
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnLogout;
    // Add fx:id for new Add to Cart buttons
    @FXML private Button btnAddCart1, btnAddCart2, btnAddCart3, btnAddCart4, btnAddCart5, btnAddCart6, btnAddCart7, btnAddCart8, btnAddCart9;
    @FXML private Button btnOrderNow, btnClearCart;

    // Array to easily manage all cart buttons for color updates
    private Button[] cartButtons;
    private String[] coffeeNames = {"Espresso", "Cappuccino", "Latte", "Americano", "Chocolate Milk", "Caramel Frape", "Iced Coffee", "Flat White", "Macchiato"};
    private static MenuController instance; // Static reference for updating colors from other controllers
    private Connection dbConnection;
    private PreparedStatement preparedStatement;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this; // Set static reference
        connectDB();
        populateCoffeePrices();
        CboxType.getItems().addAll(coffeePrices.keySet());
        initializeCartButtons();
        updateButtonColors();
    }

    private void initializeCartButtons() {
        cartButtons = new Button[]{btnAddCart1, btnAddCart2, btnAddCart3, btnAddCart4, btnAddCart5, btnAddCart6, btnAddCart7, btnAddCart8, btnAddCart9};
    }

    private void connectDB() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/coffeeshop", "root", "toor");
            System.out.println("Database successfully connected for Menu page!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Connection Error", "Failed to connect to the database. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void populateCoffeePrices() {
        coffeePrices.put("Cappuccino (Rs. 170)", 170);
        coffeePrices.put("Espresso (Rs. 120)", 120);
        coffeePrices.put("Mocha (Rs. 160)", 160);
        coffeePrices.put("Latte (Rs. 150)", 150);
        coffeePrices.put("Hot Chocolate (Rs. 130)", 130);
        coffeePrices.put("Americano (Rs. 135)", 135);
        coffeePrices.put("Caramel Frape (Rs. 220)", 220);
        coffeePrices.put("Flat White (Rs. 210)", 210);
        coffeePrices.put("Macchiato (Rs. 175)", 175);
    }

    @FXML
    public void order() {
        // Validate inputs
        String quantityText = txtquantity.getText();
        String selectedCoffee = CboxType.getValue();

        if (selectedCoffee == null || quantityText.isEmpty()) {
            showAlert("Input Error", "Please select a coffee type and enter the quantity.", Alert.AlertType.ERROR);
            return;
        }

        if (!quantityText.matches("\\d+")) {
            showAlert("Input Error", "Quantity must be a valid number.", Alert.AlertType.ERROR);
            return;
        }

        int quantity = Integer.parseInt(quantityText);
        int wc = whippedCream.isSelected() ? 1 : 0;
        int c = chocolate.isSelected() ? 1 : 0;
        int v = vanilla.isSelected() ? 1 : 0;

        // Get the base price of the selected coffee type
        int basePrice = coffeePrices.get(selectedCoffee);

        // Calculate total amount
        amount = (basePrice + (wc * 10) + (c * 10) + (v * 10)) * quantity;

        try {
            // Extract the coffee name (assuming the format "CoffeeName (Rs. Price)")
            String coffeeName = selectedCoffee.split(" \\(")[0];

            // Query the menu table to retrieve the menu id for the selected coffee type
            String menuQuery = "SELECT id FROM menu WHERE coffee_name = ?";
            try (PreparedStatement menuStmt = dbConnection.prepareStatement(menuQuery)) {
                menuStmt.setString(1, coffeeName);
                ResultSet menuRs = menuStmt.executeQuery();
                int menuId;
                if (menuRs.next()) {
                    menuId = menuRs.getInt("id");
                } else {
                    showAlert("Order Error", "Selected coffee type does not exist in the menu.", Alert.AlertType.ERROR);
                    return;
                }
                menuRs.close();

                // IMPORTANT: Supply the current user's ID.
                // Assumes that LoginController.userId stores the logged-in user's id.
                int currentUserId = LoginController.userId; // <-- Ensure this is set during login

                // Now insert order into orders table using menu_id and user_id as foreign keys
                String sql = "INSERT INTO orders (menu_id, user_id, quantity, whipped_cream, chocolate, vanilla, total_amount, payment_status, delivery_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NULL, 0)";
                preparedStatement = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, menuId);
                preparedStatement.setInt(2, currentUserId);
                preparedStatement.setInt(3, quantity);
                preparedStatement.setInt(4, wc);
                preparedStatement.setInt(5, c);
                preparedStatement.setInt(6, v);
                preparedStatement.setInt(7, amount);
                preparedStatement.executeUpdate();

                // Fetch the generated order ID
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
                rs.close();

                // Pass orderId to PaymentController
                PaymentController.orderId = orderId;

                showAlert("Order Successful", "Your order has been placed successfully!", Alert.AlertType.INFORMATION);

                // Open Payment Page
                openWindow("/project/coffeeshop/Payment.fxml", "Payment");

                // Reset fields
                clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Order Error", "Failed to place the order. Please try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void clear() {
        txtquantity.clear();
        whippedCream.setSelected(false);
        chocolate.setSelected(false);
        vanilla.setSelected(false);
        CboxType.setValue(null);
        amount = 0;
    }

    @FXML
    public void logout() {
        openWindow("/project/coffeeshop/Login.fxml", "Login");
        closeCurrentWindow(btnLogout);
    }

    private void openWindow(String fxmlFile, String title) {
        try {
            Stage stage = new Stage();
            Pane root = FXMLLoader.load(getClass().getResource(fxmlFile));
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open the " + title + " window.", Alert.AlertType.ERROR);
        }
    }

    private void closeCurrentWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handler methods for Add to Cart buttons
    @FXML
    private void handleAddToCart1() { addCartItem("Espresso", 210); } // btnAddCart1 is Col 1, Row 0 = Espresso
    @FXML
    private void handleAddToCart2() { addCartItem("Cappuccino", 150); } // btnAddCart2 is Col 0, Row 0 = Cappuccino
    @FXML
    private void handleAddToCart3() { addCartItem("Latte", 200); } // btnAddCart3 is Col 0, Row 1 = Latte
    @FXML
    private void handleAddToCart4() { addCartItem("Americano", 175); } // btnAddCart4 is Col 0, Row 2 = Americano
    @FXML
    private void handleAddToCart5() { addCartItem("Chocolate Milk", 175); } // btnAddCart5 is Col 2, Row 2 = Chocolate Milk
    @FXML
    private void handleAddToCart6() { addCartItem("Caramel Frape", 220); } // btnAddCart6 is Col 2, Row 1 = Caramel Frape
    @FXML
    private void handleAddToCart7() { addCartItem("Iced Coffee", 100); } // btnAddCart7 is Col 2, Row 0 = Iced Coffee
    @FXML
    private void handleAddToCart8() { addCartItem("Flat White", 150); } // btnAddCart8 is Col 1, Row 2 = Flat White
    @FXML
    private void handleAddToCart9() { addCartItem("Macchiato", 120); } // btnAddCart9 is Col 1, Row 1 = Macchiato

    private void addCartItem(String name, int price) {
        // Prompt for quantity
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add to Cart");
        dialog.setHeaderText("Enter quantity for " + name + ":");
        dialog.setContentText("Quantity:");
        int quantity = 1;
        try {
            dialog.showAndWait();
            String result = dialog.getResult();
            if (result == null || !result.matches("\\d+") || Integer.parseInt(result) <= 0) {
                showAlert("Invalid Quantity", "Please enter a valid positive number.", Alert.AlertType.ERROR);
                return;
            }
            quantity = Integer.parseInt(result);
        } catch (Exception e) {
            showAlert("Error", "Invalid input.", Alert.AlertType.ERROR);
            return;
        }
        // Merge if item exists
        for (CartItem item : cart) {
            if (item.getName().equals(name)) {
                item.setQuantity(item.getQuantity() + quantity);
                showAlert("Cart Updated", name + " quantity updated.", Alert.AlertType.INFORMATION);
                updateButtonColors(); // Update button colors after updating quantity
                return;
            }
        }
        cart.add(new CartItem(name, price, quantity));
        showAlert("Cart Updated", name + " added to cart.", Alert.AlertType.INFORMATION);
        updateButtonColors(); // Update button colors after adding to cart
    }

    // Method to update button colors based on cart contents
    private void updateButtonColors() {
        if (cartButtons == null) return;

        for (int i = 0; i < cartButtons.length && i < coffeeNames.length; i++) {
            boolean inCart = false;
            for (CartItem item : cart) {
                if (item.getName().equals(coffeeNames[i])) {
                    inCart = true;
                    break;
                }
            }

            if (inCart) {
                cartButtons[i].setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Green for items in cart
            } else {
                cartButtons[i].setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black;"); // Default color
            }
        }
    }

    // Navigation method: Order Now button opens Cart page
    @FXML
    private void handleOrderNow() {
        if (cart.isEmpty()) {
            showAlert("Cart Empty", "Please add items to cart before proceeding to order.", Alert.AlertType.WARNING);
            return;
        }
        openWindow("/project/coffeeshop/Cart.fxml", "Cart");
        closeCurrentWindow(btnOrderNow);
    }

    // Clear cart functionality
    @FXML
    private void handleClearCart() {
        cart.clear();
        updateButtonColors();
        showAlert("Cart Cleared", "All items have been removed from the cart.", Alert.AlertType.INFORMATION);
    }

    // Static method to update button colors from other controllers
    public static void updateMenuButtonColors() {
        if (instance != null) {
            instance.updateButtonColors();
        }
    }
}
