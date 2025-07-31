package com.example.destoktakip.controller;

import com.example.destoktakip.config.HibernateUtil;
import com.example.destoktakip.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;

public class UserLoginController {

    @FXML private Label nameSurname;
    @FXML private Label email;
    @FXML private PasswordField oldPass;
    @FXML private PasswordField newPass;
    @FXML private PasswordField newPass2;
    @FXML private Label messageSuccess;
    @FXML private Button backButton;
    @FXML private Button changeButton;

    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            nameSurname.setText(user.getFull_name());
            email.setText(user.getEmail());
        }
    }

    @FXML
    public void initialize() {
        messageSuccess.setText("");
        messageSuccess.setVisible(false);
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        if (currentUser == null) {
            messageSuccess.setText("Kullanıcı bilgisi bulunamadı. Lütfen tekrar giriş yapın.");
            messageSuccess.setStyle("-fx-text-fill: #9E2A2B;");
            messageSuccess.setVisible(true);
            return;
        }

        String oldPassword = oldPass.getText();
        String newPassword1 = newPass.getText();
        String newPassword2 = newPass2.getText();

        messageSuccess.setVisible(true);

        if (oldPassword.isEmpty() || newPassword1.isEmpty() || newPassword2.isEmpty()) {
            messageSuccess.setText("Lütfen tüm alanları doldurun.");
            messageSuccess.setStyle("-fx-text-fill: #9E2A2B;");
            return;
        }

        if (!currentUser.getPassword().equals(oldPassword)) {
            messageSuccess.setText("Eski şifreniz yanlış.");
            messageSuccess.setStyle("-fx-text-fill: #9E2A2B;");
            return;
        }

        if (!newPassword1.equals(newPassword2)) {
            messageSuccess.setText("Yeni şifreler uyuşmuyor.");
            messageSuccess.setStyle("-fx-text-fill: #9E2A2B;");
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User userToUpdate = session.get(User.class, currentUser.getUser_id());
            userToUpdate.setPassword(newPassword1);
            session.merge(userToUpdate);
            tx.commit();

            this.currentUser.setPassword(newPassword1);

            messageSuccess.setText("Şifreniz başarıyla değiştirildi!");
            messageSuccess.setStyle("-fx-text-fill: #2C6E49;");

            oldPass.clear();
            newPass.clear();
            newPass2.clear();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            messageSuccess.setText("Veritabanı hatası oluştu.");
            messageSuccess.setStyle("-fx-text-fill: #9E2A2B;");
        }
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();

            dashboardController.initData(this.currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.setTitle("Kullanıcı Paneli");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}