package com.example.destoktakip.controller;

import com.example.destoktakip.config.HibernateUtil;
import com.example.destoktakip.model.MovementStatus;
import com.example.destoktakip.model.Sample;
import com.example.destoktakip.model.StockMovement;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class StokEntryExitController {

    @FXML private TextField nameCodeOut;
    @FXML private DatePicker entryDatePick;
    @FXML private Button saveButton;
    @FXML private TextField nameCodeEnter;
    @FXML private ImageView homeIcon;
    @FXML private ImageView infoIcon1;
    @FXML private ImageView infoIcon2;

    @FXML private TableView<InOutRow> inOutTable;
    @FXML private TableColumn<InOutRow, String> sampleName;
    @FXML private TableColumn<InOutRow, LocalDate> entryDate;
    @FXML private TableColumn<InOutRow, LocalDate> outDate;

    private final ObservableList<InOutRow> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        sampleName.setCellValueFactory(cellData -> cellData.getValue().sampleNameProperty());
        entryDate.setCellValueFactory(cellData -> cellData.getValue().entryDateProperty());
        outDate.setCellValueFactory(cellData -> cellData.getValue().outDateProperty());
        inOutTable.setItems(tableData);

        inOutTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameCodeOut.setText(newSelection.getSampleName());
                nameCodeOut.setEditable(false);
                entryDatePick.setValue(newSelection.getOutDate());
                saveButton.setText("Tarihi Güncelle");
            } else {
                clearForm();
            }
        });

        loadTableData(null);
        setupFilter();
        setupTooltips();
    }

    private void clearForm() {
        nameCodeOut.clear();
        nameCodeOut.setEditable(true);
        entryDatePick.setValue(null);
        saveButton.setText("Yeni Çıkış Kaydet");
        inOutTable.getSelectionModel().clearSelection();
    }


    private void setupTooltips() {

        Tooltip tooltip1 = new Tooltip("Listeyi filtrelemek için numune adını ya da kodunu giriniz.");
        tooltip1.setFont(Font.font("System", 16));
        tooltip1.setShowDelay(Duration.millis(150));
        tooltip1.setHideDelay(Duration.millis(100));
        Tooltip.install(infoIcon1, tooltip1);

        // İkinci ikon için özel Tooltip
        Tooltip tooltip2 = new Tooltip("Numune çıkışı yapmak için adını ya da kodunu giriniz.");
        tooltip2.setFont(Font.font("System", 16));
        tooltip2.setShowDelay(Duration.millis(150));
        tooltip2.setHideDelay(Duration.millis(100));
        Tooltip.install(infoIcon2, tooltip2);
    }

    private void setupFilter() {
        nameCodeEnter.textProperty().addListener((obs, oldVal, newVal) -> loadTableData(newVal));
    }

    private void loadTableData(String filterText) {
        tableData.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT sm.movementId, s.sampleCode, s.entryDate, sm.movementDate " +
                    "FROM StockMovement sm JOIN sm.sample s " +
                    "WHERE sm.movementStatus = :status ";
            if (filterText != null && !filterText.trim().isEmpty()) {
                hql += "AND lower(s.sampleCode) LIKE :filter ";
            }
            hql += "ORDER BY sm.movementDate DESC";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("status", MovementStatus.out);
            if (filterText != null && !filterText.trim().isEmpty()) {
                query.setParameter("filter", "%" + filterText.toLowerCase().trim() + "%");
            }

            List<Object[]> results = query.getResultList();
            for (Object[] row : results) {
                tableData.add(new InOutRow((Integer) row[0], (String) row[1], toLocalDate(row[2]), toLocalDate(row[3])));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Veriler yüklenirken hata oluştu.");
        }
    }

    @FXML
    void handleSaveButton() {
        InOutRow selectedRow = inOutTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            updateMovementDate(selectedRow);
        } else {
            createNewExitMovement();
        }
    }

    private void updateMovementDate(InOutRow selectedRow) {
        LocalDate newDate = entryDatePick.getValue();
        if (newDate == null) {
            showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen geçerli bir çıkış tarihi seçin.");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            String hql = "UPDATE StockMovement SET movementDate = :date WHERE movementId = :id";
            session.createQuery(hql)
                    .setParameter("date", Timestamp.valueOf(newDate.atStartOfDay()))
                    .setParameter("id", selectedRow.getMovementId())
                    .executeUpdate();
            tx.commit();

            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Çıkış tarihi güncellendi.");
            loadTableData(nameCodeEnter.getText());
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Güncelleme Hatası", "Kayıt güncellenirken bir hata oluştu.");
        }
    }

    private void createNewExitMovement() {
        String sampleCode = nameCodeOut.getText();
        LocalDate exitDate = entryDatePick.getValue();
        if (sampleCode == null || sampleCode.trim().isEmpty() || exitDate == null) {
            showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Numune Kodu ve Çıkış Tarihi zorunludur.");
            return;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Sample sampleToMove = session.createQuery("FROM Sample WHERE sampleCode = :code", Sample.class)
                    .setParameter("code", sampleCode.trim()).uniqueResult();
            if (sampleToMove == null) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Bu koda sahip bir numune bulunamadı: " + sampleCode);
                transaction.rollback(); return;
            }
            Long existingMovements = session.createQuery("SELECT count(*) FROM StockMovement WHERE sample = :sample AND movementStatus = :status", Long.class)
                    .setParameter("sample", sampleToMove).setParameter("status", MovementStatus.out).getSingleResult();
            if (existingMovements > 0) {
                showAlert(Alert.AlertType.WARNING, "Zaten İşlem Yapılmış", "Bu numunenin zaten stoktan çıkışı yapılmış.");
                transaction.rollback(); return;
            }
            StockMovement movement = new StockMovement();
            movement.setSample(sampleToMove);
            movement.setMovementStatus(MovementStatus.out);
            movement.setMovementDate(Timestamp.valueOf(exitDate.atStartOfDay()));
            movement.setQuantity(sampleToMove.getWeightKg() != null ? sampleToMove.getWeightKg() : java.math.BigDecimal.ZERO);
            movement.setNotes("Stoktan çıkış yapıldı.");
            session.save(movement);
            transaction.commit();
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "'" + sampleCode + "' kodlu numune stoktan düşüldü.");
            loadTableData(null);
            clearForm();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kayıt Hatası", "İşlem sırasında beklenmedik bir hata oluştu.");
        }
    }

    @FXML
    private void goToMainMenu(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            homeIcon.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof java.sql.Date) return ((java.sql.Date) value).toLocalDate();
        if (value instanceof Timestamp) return ((Timestamp) value).toLocalDateTime().toLocalDate();
        if (value instanceof java.util.Date) return ((java.util.Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class InOutRow {
        private final IntegerProperty movementId;
        private final StringProperty sampleName;
        private final ObjectProperty<LocalDate> entryDate;
        private final ObjectProperty<LocalDate> outDate;
        public InOutRow(int movementId, String sampleName, LocalDate entryDate, LocalDate outDate) {
            this.movementId = new SimpleIntegerProperty(movementId);
            this.sampleName = new SimpleStringProperty(sampleName);
            this.entryDate = new SimpleObjectProperty<>(entryDate);
            this.outDate = new SimpleObjectProperty<>(outDate);
        }
        public int getMovementId() { return movementId.get(); }
        public IntegerProperty movementIdProperty() { return movementId; }
        public String getSampleName() { return sampleName.get(); }
        public StringProperty sampleNameProperty() { return sampleName; }
        public LocalDate getEntryDate() { return entryDate.get(); }
        public ObjectProperty<LocalDate> entryDateProperty() { return entryDate; }
        public LocalDate getOutDate() { return outDate.get(); }
        public ObjectProperty<LocalDate> outDateProperty() { return outDate; }
    }
}