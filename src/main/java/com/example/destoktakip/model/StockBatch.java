package com.example.destoktakip.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_batches")
public class StockBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private int batchId;

    @ManyToOne
    @JoinColumn(name = "definition_id", nullable = false)
    private ItemDefinition itemDefinition;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "current_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentQuantity;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "project_info")
    private String projectInfo;

    @Column(name = "archive_duration_months")
    private Integer archiveDurationMonths;

    @Column(name = "disposal_date")
    private LocalDate disposalDate;


    public StockBatch() {
    }

    public StockBatch(ItemDefinition itemDefinition, Location location, BigDecimal currentQuantity,
                      LocalDate entryDate, String projectInfo, Integer archiveDurationMonths, LocalDate disposalDate) {
        this.itemDefinition = itemDefinition;
        this.location = location;
        this.currentQuantity = currentQuantity;
        this.entryDate = entryDate;
        this.projectInfo = projectInfo;
        this.archiveDurationMonths = archiveDurationMonths;
        this.disposalDate = disposalDate;
    }

    // --- Getters & Setters ---

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(String projectInfo) {
        this.projectInfo = projectInfo;
    }

    public Integer getArchiveDurationMonths() {
        return archiveDurationMonths;
    }

    public void setArchiveDurationMonths(Integer archiveDurationMonths) {
        this.archiveDurationMonths = archiveDurationMonths;
    }

    public LocalDate getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(LocalDate disposalDate) {
        this.disposalDate = disposalDate;
    }
}
