package project.coffeeshop;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

public class CartController implements Initializable {
    @FXML
    private VBox cartItemsBox;
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnClear;

    // Reference to the shared cart from MenuController
    private List<CartItem> cart;
    private Connection dbConnection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cart = MenuController.getCart();
        connectDB();
        renderCartItems();
    }

    private void connectDB() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/coffeeshop", "root", "toor");
        } catch (SQLException e) {
            showAlert("Database Error", "Could not connect to the database.", Alert.AlertType.ERROR);
        }
    }

    private void renderCartItems() {
        cartItemsBox.getChildren().clear();
        if (cart == null || cart.isEmpty()) {
            cartItemsBox.getChildren().add(new Text("Your cart is empty."));
            return;
        }
        for (CartItem item : cart) {
            HBox row = new HBox(20);
            row.getChildren().addAll(
                    new Text(item.getName()),
                    new Text("Qty: " + item.getQuantity()),
                    new Text("Rs. " + item.getPrice()),
                    new Text("Total: Rs. " + item.getTotal())
            );
            cartItemsBox.getChildren().add(row);
        }
    }

    @FXML
    private void handleOrder() {
        if (cart == null || cart.isEmpty()) {
            showAlert("Cart Empty", "No items to order.", Alert.AlertType.WARNING);
            return;
        }
        try {
            for (CartItem item : cart) {
                // Get menu_id from menu table
                String menuQuery = "SELECT id FROM menu WHERE coffee_name = ?";
                try (PreparedStatement menuStmt = dbConnection.prepareStatement(menuQuery)) {
                    menuStmt.setString(1, item.getName());
                    ResultSet menuRs = menuStmt.executeQuery();
                    int menuId;
                    if (menuRs.next()) {
                        menuId = menuRs.getInt("id");
                    } else {
                        continue; // skip if not found
                    }
                    menuRs.close();
                    int currentUserId = LoginController.userId;
                    String sql = "INSERT INTO orders (menu_id, user_id, quantity, total_amount, payment_status, delivery_status) " +
                            "VALUES (?, ?, ?, ?, NULL, 0)";
                    try (PreparedStatement ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, menuId);
                        ps.setInt(2, currentUserId);
                        ps.setInt(3, item.getQuantity());
                        ps.setInt(4, item.getTotal());
                        ps.executeUpdate();
                    }
                }
            }
            cart.clear();
            renderCartItems();
            // Update button colors in MenuController
            MenuController.updateMenuButtonColors();
            showAlert("Order Successful", "Your order(s) have been placed!", Alert.AlertType.INFORMATION);

            // Navigate to Payment page
            openWindow("/project/coffeeshop/Payment.fxml", "Payment");
            closeCurrentWindow(btnOrder);
        } catch (SQLException e) {
            showAlert("Order Error", "Failed to place orders.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        if (cart != null) {
            cart.clear();
        }
        renderCartItems();
        // Update button colors in MenuController
        MenuController.updateMenuButtonColors();
    }

    @FXML
    private void handleLogout() {
        openWindow("/project/coffeeshop/Menu.fxml", "Menu");
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
