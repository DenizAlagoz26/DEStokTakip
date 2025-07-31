package com.example.destoktakip.controller;

import com.example.destoktakip.config.HibernateUtil;
import com.example.destoktakip.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.List;

public class AdminPanelController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> fullNameCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TableColumn<User, Void> actionsCol;

    @FXML private TextField emailField;
    @FXML private TextField fullNameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;

    @FXML private Button addButton;
    @FXML private Button logoutButton;

    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private User currentUserForEdit = null;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("admin", "user");
        roleCombo.setValue("user");

        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("full_name"));

        roleCol.setCellValueFactory(data -> {
            User user = data.getValue();
            if (user != null && user.getRole() != null) {
                return new javafx.beans.property.SimpleStringProperty(user.getRole().name());
            }
            return new javafx.beans.property.SimpleStringProperty("ROL YOK");
        });

        setupActionsColumn();
        loadUsers();
    }

    private void setupActionsColumn() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Düzenle");
            private final Button deleteButton = new Button("Sil");
            private final HBox pane = new HBox(5, editButton, deleteButton);

            {
                pane.setAlignment(Pos.CENTER);
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    prepareFormForEdit(user);
                });
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void prepareFormForEdit(User user) {
        currentUserForEdit = user;
        emailField.setText(user.getEmail());
        emailField.setEditable(false);
        fullNameField.setText(user.getFull_name());
        passwordField.clear();
        roleCombo.setValue(user.getRole().name());
        addButton.setText("Kullanıcıyı Güncelle");
    }

    private void loadUsers() {
        userList.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            userList.addAll(users);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kullanıcılar yüklenirken hata oluştu: " + e.getMessage());
        }
        userTable.setItems(userList);
    }

    @FXML
    private void handleAddOrUpdateUser() {
        if (currentUserForEdit == null) {
            handleAddUser();
        } else {
            handleUpdateUser();
        }
    }

    private void handleAddUser() {
        String email = emailField.getText().trim();
        if (isEmailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Bu e-posta adresi zaten kayıtlı.");
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User newUser = new User();
            if (!updateUserFromForm(newUser)) return;

            session.persist(newUser);
            tx.commit();
            userList.add(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Kullanıcı başarıyla eklendi.");
            clearForm();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kullanıcı eklenirken hata: " + e.getMessage());
        }
    }

    private void handleUpdateUser() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (!updateUserFromForm(currentUserForEdit)) return;

            session.merge(currentUserForEdit);
            tx.commit();
            showAlert(Alert.AlertType.INFORMATION, "Kullanıcı başarıyla güncellendi.");
            userTable.refresh();
            clearForm();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kullanıcı güncellenirken hata: " + e.getMessage());
        }
    }

    private boolean updateUserFromForm(User user) {
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = passwordField.getText().trim();
        String roleStr = roleCombo.getValue();

        if (email.isEmpty() || fullName.isEmpty() || roleStr == null) {
            showAlert(Alert.AlertType.ERROR, "Email, Ad Soyad ve Rol alanları boş bırakılamaz.");
            return false;
        }
        if (user.getUser_id() == 0 && password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Yeni kullanıcı için şifre alanı zorunludur.");
            return false;
        }

        user.setEmail(email);
        user.setFull_name(fullName);
        user.setRole(User.Role.valueOf(roleStr));
        if (!password.isEmpty()) {
            user.setPassword(password);
        }
        return true;
    }

    private void handleDeleteUser(User user) {
        if (user == null) return;
        if ("admin@demirexport.com".equals(user.getEmail())) {
            showAlert(Alert.AlertType.WARNING, "Varsayılan admin kullanıcısı silinemez.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, user.getFull_name() + " adlı kullanıcıyı silmek istediğinizden emin misiniz?", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Silme Onayı");
        confirmation.setHeaderText(null);
        confirmation.showAndWait();

        if (confirmation.getResult() == ButtonType.YES) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                session.remove(user);
                tx.commit();
                userList.remove(user);
                showAlert(Alert.AlertType.INFORMATION, "Kullanıcı başarıyla silindi.");
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Kullanıcı silinirken hata: " + e.getMessage());
            }
        }
    }

    private boolean isEmailExists(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return !query.list().isEmpty();
        }
    }

    private void clearForm() {
        emailField.clear();
        fullNameField.clear();
        passwordField.clear();
        roleCombo.setValue("user");
        emailField.setEditable(true);
        addButton.setText("Kullanıcı Ekle");
        currentUserForEdit = null;
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Bilgilendirme");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {

            Stage stage = (Stage) logoutButton.getScene().getWindow();


            double currentWidth = stage.getScene().getWidth();
            double currentHeight = stage.getScene().getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Scene loginScene = new Scene(root, currentWidth, currentHeight);
            stage.setScene(loginScene);
            stage.setTitle("Giriş Ekranı");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Giriş ekranı yüklenemedi.");
        }
    }
}