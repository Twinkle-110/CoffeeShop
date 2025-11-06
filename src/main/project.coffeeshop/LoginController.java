package project.coffeeshop;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public static String phoneNumber;
    public static int userId;

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnSignup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Login Controller Initialized.");
    }

    @FXML
    public void login() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed!", "Username and password cannot be empty!", Alert.AlertType.ERROR);
            return;
        }

        // Check Admin Login
        if (username.equals("admin") && password.equals("admin")) {
            openWindow("/project/coffeeshop/Admin.fxml", "Admin Panel");
            closeCurrentWindow(btnLogin);
            return;
        }

        // Updated query to fetch user_id along with phone_number and user_password.
        String sql = "SELECT id, phone_number, user_password FROM users WHERE user_name = ?";

        try (Connection dbConnection = connectDB();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                // Verify password (consider using hashed passwords in production)
                String storedPassword = result.getString("user_password");
                if (storedPassword.equals(password)) {
                    // Retrieve and store the user's id for later use (for example, in MenuController)
                    LoginController.userId = result.getInt("id");
                    phoneNumber = result.getString("phone_number");

                    openWindow("/project/coffeeshop/Menu.fxml", "Menu");
                    closeCurrentWindow(btnLogin);
                } else {
                    showAlert("Login Failed!", "Invalid username or password!", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Login Failed!", "Invalid username or password!", Alert.AlertType.ERROR);
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error!", Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void signup() {
        openWindow("/project/coffeeshop/Signup.fxml", "Sign Up");
        closeCurrentWindow(btnSignup);
    }

    private Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3307/coffeeshop", "root", "toor");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load window: " + title, Alert.AlertType.ERROR);
        }
    }

    private void closeCurrentWindow(Button button) {
        Platform.runLater(() -> {
            if (button != null && button.getScene() != null) {
                Stage stage = (Stage) button.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
