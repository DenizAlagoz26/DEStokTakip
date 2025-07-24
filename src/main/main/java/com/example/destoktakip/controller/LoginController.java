package com.example.destoktakip.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

import com.example.destoktakip.util.DBUtil;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("E-posta ve şifre zorunludur.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                errorLabel.setText("Giriş başarılı!");
                // buraya ana sayfaya geçiş kodu gelir
            } else {
                errorLabel.setText("Geçersiz e-posta veya şifre.");
            }

        } catch (SQLException e) {
            errorLabel.setText("Veritabanı hatası!");
            e.printStackTrace();
        }
    }
}
