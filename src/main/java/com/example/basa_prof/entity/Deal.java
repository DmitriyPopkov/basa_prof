package com.example.basa_prof.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deals", schema = "myschema")
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)  // ✅ ИЗМЕНЕНО с LAZY на EAGER
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "deal_objects",
            schema = "myschema",
            joinColumns = @JoinColumn(name = "deal_id"),
            inverseJoinColumns = @JoinColumn(name = "object_id")
    )
    private List<ObjectEntity> objects = new ArrayList<>();

    // ... остальные поля

    @Column(name = "deal_type")
    private String dealType;

    @Column(name = "contract_number")
    private String contractNumber;

    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @Column(name = "deal_date")
    private LocalDateTime dealDate;

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.dealDate == null) {
            this.dealDate = LocalDateTime.now();
        }
    }

    public Deal() {}

    public Deal(Client client, String dealType,
                String contractNumber, BigDecimal amount, String status, String notes) {
        this.client = client;
        this.dealType = dealType;
        this.contractNumber = contractNumber;
        this.amount = amount;
        this.status = status;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public java.util.List<ObjectEntity> getObjects() {
        return objects;
    }

    public void setObjects(java.util.List<ObjectEntity> objects) {
        this.objects = objects;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDealDate() {
        return dealDate;
    }

    public void setDealDate(LocalDateTime dealDate) {
        this.dealDate = dealDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
