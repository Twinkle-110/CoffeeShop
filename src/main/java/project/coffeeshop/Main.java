package project.coffeeshop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/project/coffeeshop/Login.fxml"));

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Coffee Shop Login");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading Login.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
