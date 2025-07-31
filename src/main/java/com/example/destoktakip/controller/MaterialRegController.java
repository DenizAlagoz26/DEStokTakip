package com.example.destoktakip.controller;

import com.example.destoktakip.config.HibernateUtil;
import com.example.destoktakip.model.Cabinet;
import com.example.destoktakip.model.Material;
import com.example.destoktakip.model.Shelf;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MaterialRegController {

    @FXML private TextField materialCode;
    @FXML private ComboBox<Cabinet> cabinetComboBox;
    @FXML private ComboBox<Shelf> shelfComboBox;
    @FXML private DatePicker entryDate;
    @FXML private TextField quantity;
    @FXML private TextArea description;
    @FXML private TextField searchField;

    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private Button deleteButton;
    @FXML private Button homePage;

    @FXML private TableView<Material> materialTable;
    @FXML private TableColumn<Material, String> materialCodeCol;
    @FXML private TableColumn<Material, String> cabinetCol;
    @FXML private TableColumn<Material, String> shelfCol;
    @FXML private TableColumn<Material, LocalDate> entryDateCol;
    @FXML private TableColumn<Material, Integer> quantityCol;
    @FXML private TableColumn<Material, String> descriptionCol;
    //endregion

    private final ObservableList<Material> materialList = FXCollections.observableArrayList();
    private FilteredList<Material> filteredMaterialList;
    private Material selectedMaterial = null;

    @FXML
    public void initialize() {
        setupTableColumns();
        populateComboBoxes();
        loadMaterialData();
        setupFilterListener();

        materialTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleTableSelection(newVal));
        cabinetComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleCabinetSelection(newVal));
    }

    private void setupTableColumns() {
        materialCodeCol.setCellValueFactory(new PropertyValueFactory<>("materialCode"));
        cabinetCol.setCellValueFactory(new PropertyValueFactory<>("cabinet"));
        shelfCol.setCellValueFactory(new PropertyValueFactory<>("shelf"));
        entryDateCol.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void loadMaterialData() {
        materialList.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Material> materials = session.createQuery("FROM Material", Material.class).list();
            materialList.setAll(materials);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Materyal verileri yüklenirken bir hata oluştu.");
        }
    }

    private void setupFilterListener() {
        filteredMaterialList = new FilteredList<>(materialList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMaterialList.setPredicate(material -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (material.getMaterialCode().toLowerCase().contains(lowerCaseFilter)) return true;
                if (material.getCabinet().toLowerCase().contains(lowerCaseFilter)) return true;
                return material.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });
        materialTable.setItems(filteredMaterialList);
    }

    private void populateComboBoxes() {
        cabinetComboBox.setItems(FXCollections.observableArrayList(Cabinet.values()));
        shelfComboBox.setDisable(true);
    }

    private void handleTableSelection(Material material) {
        selectedMaterial = material;
        if (material != null) {
            displayMaterialDetails(material);
        } else {
            handleClearForm();
        }
    }

    private void handleCabinetSelection(Cabinet selectedCabinet) {
        if (selectedCabinet != null) {
            if (selectedCabinet.hasShelves()) {
                shelfComboBox.setItems(FXCollections.observableArrayList(Shelf.values()));
                shelfComboBox.setDisable(false);
            } else {
                shelfComboBox.getItems().clear();
                shelfComboBox.setDisable(true);
            }
        } else {
            shelfComboBox.getItems().clear();
            shelfComboBox.setDisable(true);
        }
    }

    private void displayMaterialDetails(Material material) {
        materialCode.setText(material.getMaterialCode());
        quantity.setText(String.valueOf(material.getQuantity()));
        description.setText(material.getDescription());
        entryDate.setValue(material.getEntryDate());

        Arrays.stream(Cabinet.values())
                .filter(c -> c.getCode().equals(material.getCabinet()))
                .findFirst()
                .ifPresent(cabinet -> {
                    cabinetComboBox.setValue(cabinet);
                    if (cabinet.hasShelves()) {
                        Arrays.stream(Shelf.values())
                                .filter(s -> s.toString().equals(material.getShelf()))
                                .findFirst()
                                .ifPresent(shelfComboBox::setValue);
                    }
                });
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (materialCode.getText().isEmpty() || quantity.getText().isEmpty() || cabinetComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Eksik Bilgi", "Malzeme Kodu, Miktar ve Dolap alanları zorunludur.");
            return;
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Material materialToSave = (selectedMaterial != null) ? session.get(Material.class, selectedMaterial.getId()) : new Material();

            materialToSave.setMaterialCode(materialCode.getText());
            materialToSave.setQuantity(Integer.parseInt(quantity.getText()));
            materialToSave.setDescription(description.getText());
            materialToSave.setEntryDate(entryDate.getValue());

            Cabinet selectedCabinet = cabinetComboBox.getValue();
            Shelf selectedShelf = shelfComboBox.getValue();

            materialToSave.setCabinet(selectedCabinet.getCode());
            materialToSave.setShelf(selectedCabinet.hasShelves() ? selectedShelf.toString() : "-");

            session.saveOrUpdate(materialToSave);
            transaction.commit();
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kayıt başarıyla kaydedildi.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Geçersiz Değer", "Miktar alanına geçerli bir sayı giriniz.");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Kayıt işlemi sırasında bir hata oluştu.");
        } finally {
            if (session != null && session.isOpen()) session.close();
        }

        loadMaterialData();
        handleClearForm();
    }

    @FXML
    private void handleDeleteSelected(ActionEvent event) {
        if (selectedMaterial == null) {
            showAlert(Alert.AlertType.WARNING, "Seçim Yok", "Lütfen silmek için bir kayıt seçin.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Bu kaydı silmek istediğinizden emin misiniz?", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Silme Onayı");
        confirmation.setHeaderText(null);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            Session session = null;
            Transaction transaction = null;
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                transaction = session.beginTransaction();
                session.remove(selectedMaterial);
                transaction.commit();
                showAlert(Alert.AlertType.INFORMATION, "Silindi", "Kayıt başarıyla silindi.");
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) transaction.rollback();
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Hata", "Kayıt silinirken bir hata oluştu.");
            } finally {
                if (session != null && session.isOpen()) session.close();
            }
            loadMaterialData();
        }
    }

    @FXML
    private void handleClearForm() {
        materialTable.getSelectionModel().clearSelection();
        selectedMaterial = null;
        materialCode.clear();
        cabinetComboBox.setValue(null);
        shelfComboBox.setValue(null);
        shelfComboBox.getItems().clear();
        shelfComboBox.setDisable(true);
        entryDate.setValue(null);
        quantity.clear();
        description.clear();
        materialCode.requestFocus();
    }

    @FXML
    private void handleGoToDashboard(ActionEvent event) {
        loadScene("/fxml/dashboard.fxml", "Ana Panel", (Button) event.getSource());
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
            showAlert(Alert.AlertType.ERROR,"Hata", "Sayfa yüklenirken bir sorun oluştu.");
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