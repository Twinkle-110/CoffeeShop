package project.coffeeshop;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SignupController implements Initializable {

    private Connection dbConnection;
    private PreparedStatement preparedStatement;

    @FXML
    private TextField txtname;
    @FXML
    private TextField txtphone;
    @FXML
    private PasswordField txtpassword;
    @FXML
    private Button btnsignup;
    @FXML
    private Button btnlogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connectDB();
    }

    public void signup() {
        String name = txtname.getText();
        String phone = txtphone.getText();
        String password = txtpassword.getText();

        // Validate input fields
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
            return;
        }

        if (!Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}").matcher(password).matches()) {
            showAlert("Invalid Password!", "Password must contain an uppercase letter, a lowercase letter, and a digit (8-16 characters).", Alert.AlertType.ERROR);
            return;
        }

        if (!Pattern.compile("\\d{10}").matcher(phone).matches()) {
            showAlert("Invalid Phone Number!", "Phone number must be exactly 10 digits.", Alert.AlertType.ERROR);
            return;
        }

        // Insert user into the database
        String sql = "INSERT INTO users (phone_number, user_name, user_password) VALUES (?, ?, ?)";
        try {
            preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, phone);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();

            showAlert("User Created", "Your account has been created! Please login to continue!", Alert.AlertType.INFORMATION);

            // Open login screen
            openWindow("/project/coffeeshop/Login.fxml", "Login");
            closeCurrentWindow(btnsignup);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating your account. Please try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void login() {
        // Open the login screen
        openWindow("/project/coffeeshop/Login.fxml", "Login");
        closeCurrentWindow(btnlogin);
    }

    private void connectDB() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3307/coffeeshop", "root", "toor");
            System.out.println("Database successfully connected for signup page!");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (button.getScene() != null) {
            Stage stage = (Stage) button.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Scene is not set yet!");
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
