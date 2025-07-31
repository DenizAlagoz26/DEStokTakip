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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "entry_date", nullable = false)
    private Date entryDate;

    @Column(name = "grain_size")
    private String grainSize;

    @Column(name = "prepared_1kg_count")
    private Integer prepared1kgCount;

    @Column(name = "prepared_2kg_count")
    private Integer prepared2kgCount;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "analysis_source")
    private String analysisSource;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "project")
    private Project project;


    public int getSampleId() { return sampleId; }
    public void setSampleId(int sampleId) { this.sampleId = sampleId; }

    public String getSampleCode() { return sampleCode; }
    public void setSampleCode(String sampleCode) { this.sampleCode = sampleCode; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public Date getEntryDate() { return entryDate; }
    public void setEntryDate(Date entryDate) { this.entryDate = entryDate; }

    public String getGrainSize() { return grainSize; }
    public void setGrainSize(String grainSize) { this.grainSize = grainSize; }

    public Integer getPrepared1kgCount() { return prepared1kgCount; }
    public void setPrepared1kgCount(Integer prepared1kgCount) { this.prepared1kgCount = prepared1kgCount; }

    public Integer getPrepared2kgCount() { return prepared2kgCount; }
    public void setPrepared2kgCount(Integer prepared2kgCount) { this.prepared2kgCount = prepared2kgCount; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public String getAnalysisSource() { return analysisSource; }
    public void setAnalysisSource(String analysisSource) { this.analysisSource = analysisSource; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}