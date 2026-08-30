package com.example.basa_prof.config;

import com.example.basa_prof.entity.*;
import com.example.basa_prof.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DemoDataConfig {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectEntityRepository objectEntityRepository;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner loadDemoData() {

        return args -> {
            System.out.println("Загрузка демо-данных...");

            // Клиенты
            if (clientRepository.count() == 0) {
                Client c1 = new Client("Иванов Иван Иванович", "+79991111111", "ivanov@mail.ru", "Москва", "Иванов Иван", "Директор", "Основной клиент");
                Client c2 = new Client("Петров Пётр Петрович", "+79992222222", "petrov@mail.ru", "Санкт-Петербург", "Петров Пётр", "Менеджер", "Постоянный клиент");
                Client c3 = new Client("Сидорова Анна Сергеевна", "+79993333333", "sidorova@mail.ru", "Казань", "Сидорова Анна", "Заместитель", "Новый клиент");
                clientRepository.saveAll(java.util.List.of(c1, c2, c3));
                System.out.println("  - Клиенты загружены");
            }

            // Поставщики
            if (supplierRepository.count() == 0) {
                Supplier s1 = new Supplier("СтройОптом", "Смирнов Алексей", "+79994444444", "info@stroypitom.ru", "Москва, ул. Строителей 1", "Оптовые поставки строительных материалов");
                Supplier s2 = new Supplier("МеталлТрейд", "Козлов Дмитрий", "+79995555555", "info@metallotrade.ru", "Екатеринбург, ул. Промышленная 5", "Поставки металлопроката");
                s1 = supplierRepository.save(s1);
                s2 = supplierRepository.save(s2);
                System.out.println("  - Поставщики загружены (ID: " + s1.getId() + ", " + s2.getId() + ")");
            }

            // Объекты
            if (objectEntityRepository.count() == 0) {
                ObjectEntity o1 = new ObjectEntity("Жилой комплекс 'Солнечный'", "Жилой", "Москва, ул. Ленина 10", 150.5, 25000000.0, "Жилой комплекс 15 этажей",
                        LocalDateTime.of(2024, 1, 15, 9, 0), LocalDateTime.of(2025, 12, 31, 18, 0), "Договор №123, Разрешение на строительство");
                ObjectEntity o2 = new ObjectEntity("ТЦ 'Галактика'", "Коммерческий", "Москва, ул. Тверская 1", 300.0, 50000000.0, "Торговый центр 5 этажей",
                        LocalDateTime.of(2024, 3, 1, 8, 0), LocalDateTime.of(2026, 6, 30, 18, 0), "Договор №456, Проектная документация");
                objectEntityRepository.saveAll(java.util.List.of(o1, o2));
                System.out.println("  - Объекты загружены");
            }

            // Сотрудники
            if (employeeRepository.count() == 0) {
                Employee e1 = new Employee("Смирнов Алексей Николаевич", "+79996666666", "smirnov@staff.ru", "Прораб", "Строительство", new BigDecimal("120000"), LocalDate.of(2023, 6, 1));
                Employee e2 = new Employee("Козлова Мария Дмитриевна", "+79997777777", "kozlova@staff.ru", "Инженер", "Проектирование", new BigDecimal("95000"), LocalDate.of(2023, 9, 15));
                employeeRepository.saveAll(java.util.List.of(e1, e2));
                System.out.println("  - Сотрудники загружены");
            }

            // Материалы — привязываем к первому поставщику и первому объекту
            if (materialRepository.count() == 0) {
                List<Supplier> suppliers = supplierRepository.findAll();
                List<ObjectEntity> objects = objectEntityRepository.findAll();

                Supplier defaultSupplier = suppliers.isEmpty() ? null : suppliers.get(0);
                ObjectEntity defaultObject = objects.isEmpty() ? null : objects.get(0);

                Material m1 = new Material("Цемент М500", "тонна", new BigDecimal("5500"), new BigDecimal("4800"), 100, "Portland цемент высокой прочности");
                Material m2 = new Material("Арматура А500С", "тонна", new BigDecimal("62000"), new BigDecimal("58000"), 20, "Стальная арматура для бетона");
                Material m3 = new Material("Кирпич рядовой", "тысяча штук", new BigDecimal("12000"), new BigDecimal("10500"), 5, "Керамический кирпич М150");

                m1.setSupplier(defaultSupplier);
                m1.setObjectEntity(defaultObject);
                m2.setSupplier(defaultSupplier);
                m2.setObjectEntity(defaultObject);
                m3.setSupplier(defaultSupplier);
                m3.setObjectEntity(defaultObject);

                materialRepository.saveAll(java.util.List.of(m1, m2, m3));
                System.out.println("  - Материалы загружены (поставщик: " + (defaultSupplier != null ? defaultSupplier.getName() : "нет") + ", объект: " + (defaultObject != null ? defaultObject.getName() : "нет") + ")");
            }
            // Виды работ
            if (workRepository.count() == 0) {
                Work w1 = new Work("Фундаментные работы", "fundament", "Устройство ленточного фундамента", 2500000.0);
                w1.setStartDate(LocalDate.of(2024, 2, 1));
                w1.setEndDate(LocalDate.of(2024, 5, 31));
                w1.setPrice(2800000.0);
                w1.setPayment("bank_transfer");
                w1.setPaymentDate(LocalDate.of(2024, 6, 15));

                Work w2 = new Work("Возведение стен", "stennye", "Строительство наружных стен", 4000000.0);
                w2.setStartDate(LocalDate.of(2024, 6, 1));
                w2.setEndDate(LocalDate.of(2024, 10, 31));
                w2.setPrice(4500000.0);
                w2.setPayment("bank_transfer");
                w2.setPaymentDate(LocalDate.of(2024, 11, 15));

                Work w3 = new Work("Кровельные работы", "krovlya", "Монтаж кровли", 1200000.0);
                w3.setStartDate(LocalDate.of(2024, 11, 1));
                w3.setEndDate(LocalDate.of(2024, 12, 31));
                w3.setPrice(1400000.0);
                w3.setPayment("cash");
                w3.setPaymentDate(LocalDate.of(2025, 1, 10));

                workRepository.saveAll(java.util.List.of(w1, w2, w3));
                System.out.println("  - Виды работ загружены");
            }

            // Сделки
            if (dealRepository.count() == 0) {
                // Получаем клиентов для создания сделок
                java.util.List<Client> clients = clientRepository.findAll();
                java.util.List<ObjectEntity> objects = objectEntityRepository.findAll();

                if (!clients.isEmpty() && !objects.isEmpty()) {
                    Deal d1 = new Deal(clients.get(0), "sale", "Договор №Д-001", new BigDecimal("25000000"), "active", null);
                    Deal d2 = new Deal(clients.get(1), "rent", "Договор аренды №А-002", new BigDecimal("500000"), "active", "Ежемесячная аренда");
                    Deal d3 = new Deal(clients.get(2), "management", "Договор управления №У-003", new BigDecimal("1000000"), "pending", "Управление объектом");
                    dealRepository.saveAll(java.util.List.of(d1, d2, d3));
                    System.out.println("  - Сделки загружены");
                }
            }

            // Пользователи
            if (userRepository.count() == 0) {
                User u1 = new User("admin", "admin@basa.pro", "admin123", "+79990000000", "Администратор", "Системный", "ADMIN");
                User u2 = new User("manager1", "manager@basa.pro", "manager123", "+79991111111", "Менеджер", "Первый", "USER");
                userRepository.saveAll(java.util.List.of(u1, u2));
                System.out.println("  - Пользователи загружены");
            }

            System.out.println("Демо-данные загружены успешно!");
        };
    }

}