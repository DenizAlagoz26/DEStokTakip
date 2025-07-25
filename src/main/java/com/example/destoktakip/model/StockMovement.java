package com.example.destoktakip.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private int movementId;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private StockBatch stockBatch;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_status", nullable = false)
    private MovementStatus movementStatus;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "movement_date", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp movementDate;

    @Column(name = "notes")
    private String notes;

    // --- Constructors ---

    public StockMovement() {
    }

    public StockMovement(StockBatch stockBatch, User user, MovementStatus movementStatus,
                         BigDecimal quantity, Timestamp movementDate, String notes) {
        this.stockBatch = stockBatch;
        this.user = user;
        this.movementStatus = movementStatus;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.notes = notes;
    }

    // --- Getters & Setters ---

    public int getMovementId() {
        return movementId;
    }

    public void setMovementId(int movementId) {
        this.movementId = movementId;
    }

    public StockBatch getStockBatch() {
        return stockBatch;
    }

    public void setStockBatch(StockBatch stockBatch) {
        this.stockBatch = stockBatch;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MovementStatus getMovementStatus() {
        return movementStatus;
    }

    public void setMovementStatus(MovementStatus movementStatus) {
        this.movementStatus = movementStatus;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Timestamp getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(Timestamp movementDate) {
        this.movementDate = movementDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
