package com.example.destoktakip.controller;

import com.example.destoktakip.config.HibernateUtil;
import com.example.destoktakip.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen e-posta ve şifre alanlarını doldurun.");
            return;
        }
        User user = authenticate(email, password);

        if (user != null) {
            String fxmlPath;
            String title;

            if (user.getRole() == User.Role.admin) {
                fxmlPath = "/fxml/adminpanel.fxml";
                title = "Admin Paneli";
            } else {
                fxmlPath = "/fxml/dashboard.fxml";
                title = "Kullanıcı Paneli";
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

                Parent root = loader.load();

                if (user.getRole() == User.Role.admin) {

                } else {
                    DashboardController dashboardController = loader.getController();
                    dashboardController.initData(user);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
                stage.setTitle(title);
                stage.centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Arayüz Hatası", "Panel yüklenirken bir sorun oluştu.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı adı veya şifre yanlış.");
        }
    }

    private User authenticate(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();

            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Giriş yapılırken bir sorun oluştu.");
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}