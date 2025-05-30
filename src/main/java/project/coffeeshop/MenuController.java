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

public class MenuController implements Initializable {

    public static int amount = 0;
    public static int orderId; // Store order ID for payment page
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
    private Connection dbConnection;
    private PreparedStatement preparedStatement;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connectDB();
        populateCoffeePrices();
        CboxType.getItems().addAll(coffeePrices.keySet());
    }

    private void connectDB() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3307/coffeeshop", "root", "toor");
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
}
