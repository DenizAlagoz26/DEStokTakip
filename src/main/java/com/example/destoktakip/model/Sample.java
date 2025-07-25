package com.example.destoktakip.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "samples")
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_id")
    private int sampleId;

    @Column(name = "sample_code", nullable = false, unique = true)
    private String sampleCode;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "entry_date", nullable = false)
    private Date entryDate;

    @Column(name = "grain_size", nullable = false)
    private String grainSize;

    @Column(name = "prepared_1kg_count")
    private Integer prepared1kgCount;

    @Column(name = "prepared_2kg_count")
    private Integer prepared2kgCount;

    @Column(name = "weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "au_tenor_ppm", nullable = false, precision = 10, scale = 2)
    private BigDecimal auTenorPpm;

    @Column(name = "s_tenor_ppm", nullable = false, precision = 10, scale = 2)
    private BigDecimal sTenorPpm;

    @Column(name = "analysis_source")
    private String analysisSource;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "registered_by_user_id", nullable = false)
    private User registeredBy;

    // Getter ve Setter metodları

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public String getGrainSize() {
        return grainSize;
    }

    public void setGrainSize(String grainSize) {
        this.grainSize = grainSize;
    }

    public Integer getPrepared1kgCount() {
        return prepared1kgCount;
    }

    public void setPrepared1kgCount(Integer prepared1kgCount) {
        this.prepared1kgCount = prepared1kgCount;
    }

    public Integer getPrepared2kgCount() {
        return prepared2kgCount;
    }

    public void setPrepared2kgCount(Integer prepared2kgCount) {
        this.prepared2kgCount = prepared2kgCount;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getAuTenorPpm() {
        return auTenorPpm;
    }

    public void setAuTenorPpm(BigDecimal auTenorPpm) {
        this.auTenorPpm = auTenorPpm;
    }

    public BigDecimal getSTenorPpm() {
        return sTenorPpm;
    }

    public void setSTenorPpm(BigDecimal sTenorPpm) {
        this.sTenorPpm = sTenorPpm;
    }

    public String getAnalysisSource() {
        return analysisSource;
    }

    public void setAnalysisSource(String analysisSource) {
        this.analysisSource = analysisSource;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public User getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(User registeredBy) {
        this.registeredBy = registeredBy;
    }
}
