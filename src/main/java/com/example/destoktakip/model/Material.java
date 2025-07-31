package com.example.destoktakip.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "material_code", nullable = false)
    private String materialCode;

    @Column(name = "cabinet", nullable = false)
    private String cabinet;

    @Column(name = "shelf")
    private String shelf;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "description")
    private String description;

    public Material() {
    }

    public Material(String materialCode, String cabinet, String shelf, LocalDate entryDate, Integer quantity, String description) {
        this.materialCode = materialCode;
        this.cabinet = cabinet;
        this.shelf = shelf;
        this.entryDate = entryDate;
        this.quantity = quantity;
        this.description = description;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getCabinet() {
        return cabinet;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}