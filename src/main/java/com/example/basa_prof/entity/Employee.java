package com.example.basa_prof.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "employees", schema = "myschema")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "phone")
    private String phone;

    private String email;

    @Column(name = "position")
    private String position;

    @Column(name = "specialization")
    private String specialization;

    private BigDecimal salary;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "date_of_work")
    private LocalDate dateOfWork;

    @Column(name = "hours_worked")
    private Double hoursWorked;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "employee_objects",
        schema = "myschema",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "object_id")
    )
    private List<ObjectEntity> objects = new ArrayList<>();

    public Employee() {}

    public Employee(String fullName, String phone, String email,
                    String position, String specialization,
                    BigDecimal salary, LocalDate hireDate) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.specialization = specialization;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getDateOfWork() {
        return dateOfWork;
    }

    public void setDateOfWork(LocalDate dateOfWork) {
        this.dateOfWork = dateOfWork;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public java.util.List<ObjectEntity> getObjects() {
        return objects;
    }

    public void setObjects(java.util.List<ObjectEntity> objects) {
        this.objects = objects;
    }
}
