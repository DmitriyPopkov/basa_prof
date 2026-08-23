package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFullNameContainingIgnoreCase(String fullName);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByPosition(String position);

    List<Employee> findBySpecialization(String specialization);
}
