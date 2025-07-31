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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", nullable = false)
    private Sample sample;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_status", nullable = false)
    private MovementStatus movementStatus;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "movement_date", nullable = false)
    private Timestamp movementDate;

    @Column(name = "notes")
    private String notes;

    public int getMovementId() { return movementId; }
    public void setMovementId(int movementId) { this.movementId = movementId; }

    public Sample getSample() { return sample; }
    public void setSample(Sample sample) { this.sample = sample; }

    public MovementStatus getMovementStatus() { return movementStatus; }
    public void setMovementStatus(MovementStatus movementStatus) { this.movementStatus = movementStatus; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public Timestamp getMovementDate() { return movementDate; }
    public void setMovementDate(Timestamp movementDate) { this.movementDate = movementDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}