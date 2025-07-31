package com.example.destoktakip.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "item_definitions")
public class ItemDefinition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "definition_id")
    private int id;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "importance_level")
    private ImportanceLevel importanceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_class")
    private WeightClass weightClass;

    @Column(name = "description")
    private String description;

    public ItemDefinition() {}

    public ItemDefinition(String itemName, Category category, String unit,
                          ImportanceLevel importanceLevel, WeightClass weightClass, String description) {
        this.itemName = itemName;
        this.category = category;
        this.unit = unit;
        this.importanceLevel = importanceLevel;
        this.weightClass = weightClass;
        this.description = description;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public ImportanceLevel getImportanceLevel() { return importanceLevel; }
    public void setImportanceLevel(ImportanceLevel importanceLevel) { this.importanceLevel = importanceLevel; }

    public WeightClass getWeightClass() { return weightClass; }
    public void setWeightClass(WeightClass weightClass) { this.weightClass = weightClass; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public enum ImportanceLevel {
        high, medium, low
    }

    public enum WeightClass {
        heavy, medium, light
    }
}