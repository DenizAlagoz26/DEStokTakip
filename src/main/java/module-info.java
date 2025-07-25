module com.example.destoktakip {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires transitive javafx.graphics;
    requires transitive jakarta.persistence;
    requires transitive org.hibernate.orm.core;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.destoktakip.app to javafx.fxml;
    opens com.example.destoktakip.controller to javafx.fxml;
    opens com.example.destoktakip.model to org.hibernate.orm.core, javafx.base;

    exports com.example.destoktakip.app;
    exports com.example.destoktakip.controller;
    exports com.example.destoktakip.model;
}
