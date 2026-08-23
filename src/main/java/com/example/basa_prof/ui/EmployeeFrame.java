package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Employee;
import com.example.basa_prof.service.EmployeeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
public class EmployeeFrame extends JFrame {

    private EmployeeService employeeService;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private JTextField tfFullName, tfPhone, tfEmail, tfPosition, tfSpecialization,
            tfSalary, tfHireDate, tfDateOfWork, tfHoursWorked;

    public EmployeeFrame(EmployeeService employeeService) {
        this.employeeService = employeeService;
        initialize();
        loadEmployees();
    }

    private void initialize() {
        setTitle("Управление сотрудниками");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные сотрудника"));

        tfFullName = new JTextField();
        tfPhone = new JTextField();
        tfEmail = new JTextField();
        tfPosition = new JTextField();
        tfSpecialization = new JTextField();
        tfSalary = new JTextField();
        tfHireDate = new JTextField();
        tfDateOfWork = new JTextField();
        tfHoursWorked = new JTextField();

        formPanel.add(new JLabel("ФИО:"));
        formPanel.add(tfFullName);

        formPanel.add(new JLabel("Телефон:"));
        formPanel.add(tfPhone);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(tfEmail);

        formPanel.add(new JLabel("Должность:"));
        formPanel.add(tfPosition);

        formPanel.add(new JLabel("Специализация:"));
        formPanel.add(tfSpecialization);

        formPanel.add(new JLabel("Зарплата:"));
        formPanel.add(tfSalary);

        formPanel.add(new JLabel("Дата найма (дд.мм.гггг):"));
        formPanel.add(tfHireDate);

        formPanel.add(new JLabel("Дата работы (дд.мм.гггг):"));
        formPanel.add(tfDateOfWork);

        formPanel.add(new JLabel("Часы работы:"));
        formPanel.add(tfHoursWorked);

        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnRefresh.addActionListener(e -> loadEmployees());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "ФИО", "Телефон", "Должность", "Специализация", "Зарплата"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список сотрудников"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            tableModel.addRow(new Object[]{
                    employee.getId(),
                    employee.getFullName(),
                    employee.getPhone(),
                    employee.getPosition(),
                    employee.getSpecialization(),
                    employee.getSalary()
            });
        }
    }

    private void fillForm() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Employee employee = employeeService.findById(id).orElse(null);
            if (employee != null) {
                tfFullName.setText(employee.getFullName());
                tfPhone.setText(employee.getPhone());
                tfEmail.setText(employee.getEmail());
                tfPosition.setText(employee.getPosition());
                tfSpecialization.setText(employee.getSpecialization());
                tfSalary.setText(employee.getSalary() != null ? employee.getSalary().toPlainString() : "");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                if (employee.getHireDate() != null) {
                    tfHireDate.setText(employee.getHireDate().format(formatter));
                }
                if (employee.getDateOfWork() != null) {
                    tfDateOfWork.setText(employee.getDateOfWork().format(formatter));
                }
                if (employee.getHoursWorked() != null) {
                    tfHoursWorked.setText(employee.getHoursWorked().toString());
                }
            }
        }
    }

    private void saveEmployee() {
        try {
            if (tfFullName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "ФИО обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = employeeTable.getSelectedRow();
            Employee employee;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                employee = employeeService.findById(id).orElse(new Employee());
            } else {
                employee = new Employee();
            }

            employee.setFullName(tfFullName.getText());
            employee.setPhone(tfPhone.getText());
            employee.setEmail(tfEmail.getText());
            employee.setPosition(tfPosition.getText());
            employee.setSpecialization(tfSpecialization.getText());
            // Зарплата
            if (!tfSalary.getText().isBlank()) {
                try {
                    employee.setSalary(new BigDecimal(tfSalary.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат зарплаты!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                employee.setSalary(null);
            }

            // Дата найма
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            if (!tfHireDate.getText().isBlank()) {
                try {
                    employee.setHireDate(LocalDate.parse(tfHireDate.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты найма (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                employee.setHireDate(null);
            }

            // Дата работы
            if (!tfDateOfWork.getText().isBlank()) {
                try {
                    employee.setDateOfWork(LocalDate.parse(tfDateOfWork.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты работы (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                employee.setDateOfWork(null);
            }

            // Отработанные часы
            if (!tfHoursWorked.getText().isBlank()) {
                try {
                    employee.setHoursWorked(Double.parseDouble(tfHoursWorked.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат часов!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                employee.setHoursWorked(null);
            }

            // Сохранение и обновление
            employeeService.save(employee);
            loadEmployees();
            clearForm();
            JOptionPane.showMessageDialog(this, "Сотрудник сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить сотрудника №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                employeeService.deleteById(id);
                loadEmployees();
                clearForm();
                JOptionPane.showMessageDialog(this, "Сотрудник удалён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfFullName.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
        tfPosition.setText("");
        tfSpecialization.setText("");
        tfSalary.setText("");
        tfHireDate.setText("");
        tfDateOfWork.setText("");
        tfHoursWorked.setText("");
    }
}

