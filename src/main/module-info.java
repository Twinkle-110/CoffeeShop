module CoffeeShop {
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires maven.model;
    requires com.jfoenix;

    opens project.coffeeshop to javafx.fxml;
    exports project.coffeeshop;
}
