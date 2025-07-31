package com.example.destoktakip;

import com.example.destoktakip.config.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("DEStokTakip Giriş");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        HibernateUtil.getSessionFactory().openSession().close();
        launch();
    }
}
