module com.example.destoktakip {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Ekstra JavaFX UI Kütüphaneleri
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    // Hibernate & ORM
    requires org.hibernate.orm.core;
    requires java.sql;
    requires jakarta.persistence;
    requires java.naming;



    // Paketleri açma (reflection için)
    opens com.example.destoktakip to javafx.fxml;
    opens com.example.destoktakip.model to org.hibernate.orm.core;
    opens com.example.destoktakip.config to org.hibernate.orm.core;

    // Export edilenler
    exports com.example.destoktakip;
    exports com.example.destoktakip.model;
    exports com.example.destoktakip.config;
    exports com.example.destoktakip.controller;
    opens com.example.destoktakip.controller to javafx.fxml;
}
