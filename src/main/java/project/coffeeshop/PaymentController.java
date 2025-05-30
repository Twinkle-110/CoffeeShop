package project.coffeeshop;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    private static final String DB_URL = "jdbc:mysql://localhost:3307/coffeeshop";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "toor";
    // Variables to hold the current order ID and total amount
    public static int orderId;
    public static int totalAmount;
    @FXML
    private Text txtamount;  // Text to display the total amount
    @FXML
    private ComboBox<String> Cboxpay;  // Dropdown for selecting payment method
    @FXML
    private Button btnconfirm;  // Button to confirm payment
    private Connection dbConnection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectDB();
        initializeUI();
        fetchAndDisplayAmount();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        Cboxpay.getItems().addAll("Cash", "Card", "UPI");
        Cboxpay.setPromptText("Select Payment Method");
        btnconfirm.setDisable(true);

        // Enable the confirm button only when a payment method is selected
        Cboxpay.setOnAction(event -> btnconfirm.setDisable(Cboxpay.getValue() == null));
    }

    /**
     * Fetches the updated amount for the current order and displays it.
     */
    private void fetchAndDisplayAmount() {
        // Note: using "amount" column name as inserted in the orders table.
        String sql = "SELECT total_amount FROM orders WHERE order_id = ?";

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                totalAmount = resultSet.getInt("total_amount");
                txtamount.setText("Total Amount: Rs. " + totalAmount);
            } else {
                showErrorAlert("Error", "Order not found. Please try again.");
            }
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to fetch order details. Please try again later.");
            e.printStackTrace();
        }
    }

    /**
     * Confirms the payment and updates the database.
     * This method is now renamed to "confirm" so that it matches the FXML onAction attribute.
     */
    @FXML
    private void confirm() {
        String selectedPaymentMethod = Cboxpay.getValue();

        if (selectedPaymentMethod == null) {
            showErrorAlert("Invalid Input", "Please select a valid payment method.");
            return;
        }

        String sql = "UPDATE orders SET payment_status = ? WHERE order_id = ?";

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatement.setString(1, selectedPaymentMethod);
            preparedStatement.setInt(2, orderId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                showInformationAlert("Payment Successful", "Your payment has been successfully recorded!");

                // Close the current stage (window)
                Stage stage = (Stage) btnconfirm.getScene().getWindow();
                stage.close();
            } else {
                showErrorAlert("Payment Error", "Failed to update payment. Please try again.");
            }
        } catch (SQLException e) {
            showErrorAlert("Database Error", "An error occurred while processing your payment. Please try again later.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes a database connection.
     */
    private void connectDB() {
        try {
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully for Payment page!");
        } catch (SQLException e) {
            showErrorAlert("Database Connection Error", "Could not connect to the database. Please try again later.");
            e.printStackTrace();
        }
    }

    /**
     * Shows an informational alert.
     *
     * @param title   Title of the alert.
     * @param message Message to display in the alert.
     */
    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert.
     *
     * @param title   Title of the alert.
     * @param message Message to display in the alert.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
