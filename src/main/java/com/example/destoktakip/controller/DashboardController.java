package com.example.destoktakip.controller;

import com.example.destoktakip.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private Button stokKayitButton;
    @FXML private Button stokGirisCikisButton;
    @FXML private Button stokListelemeButton;
    @FXML private Button userProfileButton;
    @FXML private Button logoutButton;

    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
    }

    @FXML
    void handleProfile(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı bilgisi bulunamadı! Lütfen tekrar giriş yapın.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userlogin.fxml"));
            Parent root = loader.load();

            UserLoginController userLoginController = loader.getController();


            userLoginController.initData(this.currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.setTitle("Kullanıcı Profili");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Profil sayfası yüklenirken bir sorun oluştu.");
        }
    }

    @FXML
    void handleStokKayit(ActionEvent event) {
        loadScene("/fxml/stokreg.fxml", "Numune Yönetim Ekranı", (Button) event.getSource());
    }

    @FXML
    void handleStokGirisCikis(ActionEvent event) {
        loadScene("/fxml/stokentryexit.fxml", "Stok Giriş/Çıkış", (Button) event.getSource());
    }

    @FXML
    void handleStokListeleme(ActionEvent event) {
        loadScene("/fxml/materialreg.fxml", "Malzeme Kayıt ve Listeleme", (Button) event.getSource());
    }

    @FXML
    void handleLogout(ActionEvent event) {
        loadScene("/fxml/login.fxml", "Giriş Ekranı", (Button) event.getSource());
    }

    @FXML
    void handleMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#D4AF37"));
        glow.setRadius(20);
        glow.setSpread(0.6);
        button.setEffect(glow);
        button.setScaleX(1.05);
        button.setScaleY(1.05);
    }

    @FXML
    void handleMouseExited(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setEffect(null);
        button.setScaleX(1.0);
        button.setScaleY(1.0);
    }
    private void loadScene(String fxmlPath, String title, Button sourceButton) {
        try {
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"Hata", "Sayfa yüklenirken bir sorun oluştu: " + fxmlPath);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}