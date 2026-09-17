package com.example.basa_prof.service;

import com.example.basa_prof.entity.Employee;
import com.example.basa_prof.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> searchByName(String name) {
        return employeeRepository.findByFullNameContainingIgnoreCase(name);
    }

    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public List<Employee> findByPosition(String position) {
        return employeeRepository.findByPosition(position);
    }
}
