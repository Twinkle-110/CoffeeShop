package project.coffeeshop;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private Connection connection;
    private final ObservableList<Model> oblist = FXCollections.observableArrayList();

    @FXML
    private TableView<Model> ordertable;
    @FXML
    private TableColumn<Model, Integer> colorderId;
    @FXML
    private TableColumn<Model, Integer> coluserId;
    @FXML
    private TableColumn<Model, String> coltype;
    @FXML
    private TableColumn<Model, Integer> colquantity;
    @FXML
    private TableColumn<Model, Boolean> colcream;
    @FXML
    private TableColumn<Model, Boolean> colchocolate;
    @FXML
    private TableColumn<Model, Boolean> colvanilla;
    @FXML
    private TableColumn<Model, Integer> colamount;
    @FXML
    private TableColumn<Model, String> colpayment;
    @FXML
    private TableColumn<Model, Boolean> coldelivery;
    @FXML
    private JFXButton btnclear, btnupdate, btndelete, btnlogout;
    @FXML
    private TextField txtorderId;
    @FXML
    private CheckBox delivered;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Database successfully connected for admin page!");
        connection = connectDB();
        loadTable();
    }

    private Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3307/coffeeshop", "root", "toor");
        } catch (SQLException e) {
            System.out.println("Database Connection Error: " + e.getMessage());
            return null;
        }
    }

    private void loadTable() {
        oblist.clear();
        String query = "SELECT o.order_id, o.user_id, m.coffee_name, o.quantity, o.whipped_cream, o.chocolate, o.vanilla, " +
                "o.total_amount, o.payment_status, o.delivery_status " +
                "FROM orders o " +
                "JOIN menu m ON o.menu_id = m.id";

        try (Statement stmt = connection.createStatement();
             ResultSet res = stmt.executeQuery(query)) {
            while (res.next()) {
                oblist.add(new Model(
                        res.getInt("order_id"),
                        res.getInt("user_id"),
                        res.getString("coffee_name"), // Fetch coffee_name instead of menu_id
                        res.getInt("quantity"),
                        res.getBoolean("whipped_cream"),
                        res.getBoolean("chocolate"),
                        res.getBoolean("vanilla"),
                        res.getInt("total_amount"),
                        res.getString("payment_status"),
                        res.getBoolean("delivery_status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Load Table Error: " + e.getMessage());
        }

        colorderId.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        coluserId.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        coltype.setCellValueFactory(new PropertyValueFactory<>("coffee_name")); // Updated to show coffee_name
        colquantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colcream.setCellValueFactory(new PropertyValueFactory<>("whipped_cream"));
        colchocolate.setCellValueFactory(new PropertyValueFactory<>("chocolate"));
        colvanilla.setCellValueFactory(new PropertyValueFactory<>("vanilla"));
        colamount.setCellValueFactory(new PropertyValueFactory<>("total_amount"));
        colpayment.setCellValueFactory(new PropertyValueFactory<>("payment_status"));
        coldelivery.setCellValueFactory(new PropertyValueFactory<>("delivery_status"));

        ordertable.setItems(oblist);
    }

    @FXML
    private void update() {
        String orderId = txtorderId.getText();
        if (orderId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Please enter a valid Order ID.");
            return;
        }

        String sql = "UPDATE orders SET delivery_status = ? WHERE order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, delivered.isSelected());
            pstmt.setInt(2, Integer.parseInt(orderId));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                loadTable();
                showAlert(Alert.AlertType.INFORMATION, "Order Updated", "The selected order has been updated!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Order not found!");
            }
        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }

    @FXML
    private void delete() {
        String orderId = txtorderId.getText();
        if (orderId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Please enter a valid Order ID.");
            return;
        }

        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(orderId));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                loadTable();
                showAlert(Alert.AlertType.INFORMATION, "Order Deleted", "The selected order has been deleted!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Order not found!");
            }
        } catch (SQLException e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
    }

    @FXML
    private void clear() {
        txtorderId.clear();
        delivered.setSelected(false);
    }

    @FXML
    private void logout() throws IOException {
        Stage stage = (Stage) btnlogout.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/project/coffeeshop/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

