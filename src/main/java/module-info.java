module CoffeeShop {
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires javafx.swing;
    requires javafx.controls;

    requires com.jfoenix;

    opens project.coffeeshop to javafx.fxml;
    exports project.coffeeshop;
}
