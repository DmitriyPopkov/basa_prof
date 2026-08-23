package com.example.basa_prof;

import com.example.basa_prof.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceInjectionTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private DealService dealService;

    @Autowired
    private ObjectEntityService objectService;

    @Autowired
    private WorkService workService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Все сервисы должны быть загружены Spring")
    void testAllServicesAreLoaded() {
        assertNotNull(clientService, "ClientService не загружен");
        assertNotNull(dealService, "DealService не загружен");
        assertNotNull(objectService, "ObjectEntityService не загружен");
        assertNotNull(workService, "WorkService не загружен");
        assertNotNull(materialService, "MaterialService не загружен");
        assertNotNull(employeeService, "EmployeeService не загружен");
        assertNotNull(supplierService, "SupplierService не загружен");
        assertNotNull(userService, "UserService не загружен");

        System.out.println("✅ Все сервисы загружены успешно!");
    }

    @Test
    @DisplayName("SupplierService должен возвращать список поставщиков")
    void testSupplierServiceWorks() {
        try {
            var suppliers = supplierService.findAll();
            assertNotNull(suppliers, "SupplierService.findAll() вернул null");
            System.out.println("✅ SupplierService работает. Поставщиков: " + suppliers.size());
        } catch (Exception e) {
            fail("Ошибка при вызове SupplierService.findAll(): " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Все сервисы должны возвращать не null")
    void testAllServicesNotNull() {
        assertDoesNotThrow(() -> {
            assertNotNull(clientService.findAll());
            assertNotNull(dealService.findAll());
            assertNotNull(objectService.findAll());
            assertNotNull(workService.findAll());
            assertNotNull(materialService.findAll());
            assertNotNull(employeeService.findAll());
            assertNotNull(supplierService.findAll());
            assertNotNull(userService.findAll());
        }, "Какой-то сервис не работает");
    }
}
