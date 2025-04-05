module gui {
    requires javafx.controls;
    requires javafx.graphics;
    requires transportAgency.networking;
    requires transportAgency.services;
    requires org.apache.logging.log4j;
    requires transportAgency.model;
    requires javafx.fxml;
    requires java.sql;
    exports gui;
}