package com.example.basa_prof.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "objects", schema = "myschema")
public class ObjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "object_type")
    private String objectType;

    private String address;

    private Double area;

    private Double price;

    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    private String documents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToMany(mappedBy = "objects", fetch = FetchType.LAZY)
    private List<Deal> deals = new ArrayList<>();

    @OneToMany(mappedBy = "object", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Work> works = new ArrayList<>();

    @OneToMany(mappedBy = "object", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Material> materials = new ArrayList<>();

    @ManyToMany(mappedBy = "objects", fetch = FetchType.LAZY)
    private java.util.List<Employee> employees = new ArrayList<>();



    public ObjectEntity() {}

    public ObjectEntity(String name, String objectType, String address,
                        Double area, Double price, String description,
                        LocalDateTime startDate, LocalDateTime endDate, String documents) {
        this.name = name;
        this.objectType = objectType;
        this.address = address;
        this.area = area;
        this.price = price;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.documents = documents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public java.util.List<Deal> getDeals() {
        return deals;
    }

    public void setDeals(java.util.List<Deal> deals) {
        this.deals = deals;
    }

    public java.util.List<Work> getWorks() {
        return works;
    }

    public void setWorks(java.util.List<Work> works) {
        this.works = works;
    }

    public java.util.List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(java.util.List<Material> materials) {
        this.materials = materials;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public java.util.List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(java.util.List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return name != null ? name : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectEntity that = (ObjectEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
