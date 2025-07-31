package com.example.destoktakip.controller;

import com.example.destoktakip.config.HibernateUtil;
import com.example.destoktakip.model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StokRegController {

    @FXML private ComboBox<Isletme> isletmeComboBox;
    @FXML private ComboBox<Project> projectComboBox;
    @FXML private ComboBox<Cabinet> cabinetComboBox;
    @FXML private ComboBox<Shelf> shelfComboBox;
    @FXML private TextField materialCode, weight, unitSize, oneKg, twoKg, searchField;
    @FXML private DatePicker entryDatePicker;
    @FXML private TextArea description;
    @FXML private Button saveButton, clearButton, deleteButton, homePage;
    @FXML private TableView<Sample> materialTable;

    @FXML private TableColumn<Sample, String> isletmeCol, projectCol, materialCodeName, cabinetCol,
            shelfCol, weightCol, unitSizeCol, oneKgCol, twoKgCol, descriptionCol, importanceCol;
    @FXML private TableColumn<Sample, Date> entryDateCol;

    private final ObservableList<Sample> sampleList = FXCollections.observableArrayList();
    private FilteredList<Sample> filteredSampleList;
    private SortedList<Sample> sortedSampleList;
    private Sample selectedSample = null;

    @FXML
    public void initialize() {
        setupTableColumns();
        populateComboBoxes();
        loadSampleData();
        setupFilterAndSortListener();

        materialTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleTableSelection(newVal));
        isletmeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> populateProjectComboBox(newVal));
        cabinetComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleCabinetSelection(newVal));
    }

    private void setupTableColumns() {
        isletmeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getProject() != null ? cellData.getValue().getProject().getIsletme().toString() : ""));
        projectCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getProject() != null ? cellData.getValue().getProject().toString() : ""));
        materialCodeName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleCode()));

        entryDateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEntryDate()));
        entryDateCol.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.toLocalDate().format(formatter));
            }
        });

        cabinetCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getLocation() != null ? cellData.getValue().getLocation().getCabinetName() : ""));
        shelfCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getLocation() != null ? cellData.getValue().getLocation().getShelfNumber() : ""));
        weightCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getWeightKg() != null ? cellData.getValue().getWeightKg().toString() : "0"));
        unitSizeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrainSize()));
        oneKgCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPrepared1kgCount() != null ? cellData.getValue().getPrepared1kgCount().toString() : ""));
        twoKgCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPrepared2kgCount() != null ? cellData.getValue().getPrepared2kgCount().toString() : ""));
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotes()));

        importanceCol.setCellValueFactory(cellData -> {
            Sample sample = cellData.getValue();
            if (sample == null || sample.getEntryDate() == null) return new SimpleStringProperty("");
            long monthsOld = Period.between(sample.getEntryDate().toLocalDate(), LocalDate.now()).toTotalMonths();
            if (monthsOld <= 3) return new SimpleStringProperty("⭐⭐⭐⭐⭐");
            else if (monthsOld <= 6) return new SimpleStringProperty("⭐⭐⭐⭐");
            else if (monthsOld <= 9) return new SimpleStringProperty("⭐⭐⭐");
            else if (monthsOld <= 12) return new SimpleStringProperty("⭐⭐");
            else return new SimpleStringProperty("⭐");
        });
        importanceCol.setComparator(Comparator.comparingInt(String::length));
    }

    private void setupFilterAndSortListener() {
        filteredSampleList = new FilteredList<>(sampleList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredSampleList.setPredicate(sample -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (sample.getSampleCode().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (sample.getProject() != null && sample.getProject().toString().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (sample.getLocation() != null && sample.getLocation().getCabinetName().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (sample.getLocation() != null && sample.getLocation().getShelfNumber().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        sortedSampleList = new SortedList<>(filteredSampleList);
        sortedSampleList.comparatorProperty().bind(materialTable.comparatorProperty());
        materialTable.setItems(sortedSampleList);
    }

    private void loadSampleData() {
        sampleList.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Sample s LEFT JOIN FETCH s.location " +
                    "WHERE s.id NOT IN " +
                    "(SELECT sm.sample.id FROM StockMovement sm WHERE sm.movementStatus = :status)";

            Query<Sample> query = session.createQuery(hql, Sample.class);
            query.setParameter("status", MovementStatus.out);

            List<Sample> samples = query.getResultList();
            sampleList.setAll(samples);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Stoktaki numune verileri yüklenirken bir hata oluştu.");
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (projectComboBox.getValue() == null || materialCode.getText().isEmpty() || entryDatePicker.getValue() == null || cabinetComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Proje, Numune Kodu, Giriş Tarihi ve Dolap zorunludur.");
            return;
        }
        Cabinet selectedCabinet = cabinetComboBox.getValue();
        Shelf selectedShelf = shelfComboBox.getValue();
        if (selectedCabinet.hasShelves() && selectedShelf == null) {
            showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen bu dolap için bir raf seçin.");
            return;
        }

        saveButton.setDisable(true);

        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Sample sampleToSave = (selectedSample != null) ? session.get(Sample.class, selectedSample.getSampleId()) : new Sample();

            Location loc = findOrCreateLocation(session, selectedCabinet, selectedShelf);

            sampleToSave.setProject(projectComboBox.getValue());
            sampleToSave.setSampleCode(materialCode.getText());
            sampleToSave.setLocation(loc);
            sampleToSave.setEntryDate(Date.valueOf(entryDatePicker.getValue()));

            sampleToSave.setWeightKg(weight.getText().isEmpty() ? null : new BigDecimal(weight.getText().replace(',', '.')));
            sampleToSave.setGrainSize(unitSize.getText());
            sampleToSave.setPrepared1kgCount(oneKg.getText().isEmpty() ? null : Integer.parseInt(oneKg.getText()));
            sampleToSave.setPrepared2kgCount(twoKg.getText().isEmpty() ? null : Integer.parseInt(twoKg.getText()));
            sampleToSave.setNotes(description.getText());

            session.saveOrUpdate(sampleToSave);
            transaction.commit();
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Numune başarıyla kaydedildi.");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kayıt Hatası", "Numune kaydedilirken bir hata oluştu: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
            saveButton.setDisable(false);
        }
        loadSampleData();
    }

    private void handleTableSelection(Sample selectedValue) {
        selectedSample = selectedValue;
        if (selectedValue != null) {
            displaySampleDetails(selectedValue);
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

    private void displaySampleDetails(Sample sample) {
        if (sample.getProject() != null) {
            isletmeComboBox.setValue(sample.getProject().getIsletme());
            projectComboBox.setValue(sample.getProject());
        }
        materialCode.setText(sample.getSampleCode());
        entryDatePicker.setValue(sample.getEntryDate().toLocalDate());
        if (sample.getLocation() != null) {
            String cabinetCode = sample.getLocation().getCabinetName();
            String shelfDisplayName = sample.getLocation().getShelfNumber();
            Arrays.stream(Cabinet.values())
                    .filter(c -> c.getCode().equals(cabinetCode))
                    .findFirst()
                    .ifPresent(cabinet -> {
                        cabinetComboBox.setValue(cabinet);
                        if (cabinet.hasShelves()) {
                            Arrays.stream(Shelf.values())
                                    .filter(s -> s.toString().equals(shelfDisplayName))
                                    .findFirst()
                                    .ifPresent(shelfComboBox::setValue);
                        }
                    });
        }
        weight.setText(sample.getWeightKg() != null ? sample.getWeightKg().toPlainString() : "");
        unitSize.setText(sample.getGrainSize());
        oneKg.setText(sample.getPrepared1kgCount() != null ? sample.getPrepared1kgCount().toString() : "");
        twoKg.setText(sample.getPrepared2kgCount() != null ? sample.getPrepared2kgCount().toString() : "");
        description.setText(sample.getNotes());
    }

    @FXML
    void handleClearForm() {
        materialTable.getSelectionModel().clearSelection();
        selectedSample = null;
        isletmeComboBox.getSelectionModel().clearSelection();
        projectComboBox.getSelectionModel().clearSelection();
        projectComboBox.getItems().clear();
        cabinetComboBox.getSelectionModel().clearSelection();
        shelfComboBox.getSelectionModel().clearSelection();
        shelfComboBox.getItems().clear();
        shelfComboBox.setDisable(true);
        materialCode.clear();
        entryDatePicker.setValue(null);
        weight.clear();
        unitSize.clear();
        oneKg.clear();
        twoKg.clear();
        description.clear();
        materialCode.requestFocus();
    }

    @FXML
    void handleDeleteSelected(ActionEvent event) {
        if (selectedSample == null) {
            showAlert(Alert.AlertType.WARNING, "Seçim Yapılmadı", "Lütfen silmek için tablodan bir kayıt seçin.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "'" + selectedSample.getSampleCode() + "' kodlu numuneyi silmek istediğinizden emin misiniz?", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Silme Onayı");
        confirmation.setHeaderText("Kaydı Kalıcı Olarak Silmek Üzeresiniz");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Transaction transaction = null;
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    transaction = session.beginTransaction();
                    session.delete(selectedSample);
                    transaction.commit();
                    showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kayıt başarıyla silindi.");
                    loadSampleData();
                } catch (Exception e) {
                    if (transaction != null) transaction.rollback();
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Silme Hatası", "Kayıt silinirken bir hata oluştu.");
                }
            }
        });
    }

    @FXML
    private void handleGoToDashboard(ActionEvent event) {
        loadScene("/fxml/dashboard.fxml", "Ana Panel");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            Stage stage = (Stage) homePage.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Arayüz Hatası", "Yeni sayfa yüklenirken bir sorun oluştu: " + fxmlPath);
        }
    }

    private void populateComboBoxes() {
        isletmeComboBox.setItems(FXCollections.observableArrayList(Isletme.values()));
        cabinetComboBox.setItems(FXCollections.observableArrayList(Cabinet.values()));
        shelfComboBox.setDisable(true);
    }

    private void populateProjectComboBox(Isletme isletme) {
        if (isletme == null) {
            projectComboBox.getItems().clear();
            return;
        }
        projectComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(Project.values())
                        .filter(project -> project.getIsletme() == isletme)
                        .collect(Collectors.toList())));
    }

    private Location findOrCreateLocation(Session session, Cabinet cabinet, Shelf shelf) {
        String cabinetCode = cabinet.getCode();
        String shelfDisplayName = cabinet.hasShelves() ? shelf.toString() : "-";
        Query<Location> query = session.createQuery("FROM Location WHERE cabinetName = :cabinet AND shelfNumber = :shelf", Location.class);
        query.setParameter("cabinet", cabinetCode).setParameter("shelf", shelfDisplayName);
        Location loc = query.uniqueResult();
        if (loc == null) {
            String desc = cabinet.toString();
            Location.LocationType type = cabinet.hasShelves() ? Location.LocationType.cabinet : Location.LocationType.refrigerator;
            loc = new Location(cabinetCode, shelfDisplayName, desc, type);
            session.save(loc);
            session.flush();
        }
        return loc;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}