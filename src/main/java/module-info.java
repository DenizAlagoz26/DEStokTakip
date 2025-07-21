module com.example.destoktakip {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.example.destoktakip.app to javafx.fxml;
    opens com.example.destoktakip.controller to javafx.fxml;

    exports com.example.destoktakip.app;
    exports com.example.destoktakip.controller;
}
