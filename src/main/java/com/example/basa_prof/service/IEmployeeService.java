package com.example.basa_prof.service;

import com.example.basa_prof.entity.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с сотрудниками.
 */
public interface IEmployeeService {
    List<Employee> findAll();
    Optional<Employee> findById(Long id);
    Employee save(Employee employee);
    void deleteById(Long id);
}
