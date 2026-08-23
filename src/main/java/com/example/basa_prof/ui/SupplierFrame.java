package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Supplier;
import com.example.basa_prof.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierFrame extends JFrame {

    private final SupplierService supplierService;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    private JTextField tfName, tfContactPerson, tfPhone, tfEmail, tfAddress, tfDescription;

    public SupplierFrame(SupplierService supplierService) {
        this.supplierService = supplierService;
        initialize();
        loadSuppliers();
    }

    private void initialize() {
        setTitle("Управление поставщиками");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные поставщика"));

        tfName = new JTextField();
        tfContactPerson = new JTextField();
        tfPhone = new JTextField();
        tfEmail = new JTextField();
        tfAddress = new JTextField();
        tfDescription = new JTextField();

        formPanel.add(new JLabel("Название:"));
        formPanel.add(tfName);

        formPanel.add(new JLabel("Контактное лицо:"));
        formPanel.add(tfContactPerson);

        formPanel.add(new JLabel("Телефон:"));
        formPanel.add(tfPhone);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(tfEmail);

        formPanel.add(new JLabel("Адрес:"));
        formPanel.add(tfAddress);

        formPanel.add(new JLabel("Описание:"));
        formPanel.add(tfDescription);

        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveSupplier());
        btnDelete.addActionListener(e -> deleteSupplier());
        btnRefresh.addActionListener(e -> loadSuppliers());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Название", "Контактное лицо", "Телефон", "Email", "Адрес"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список поставщиков"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = supplierService.findAll();
        for (Supplier supplier : suppliers) {
            tableModel.addRow(new Object[]{
                    supplier.getId(),
                    supplier.getName(),
                    supplier.getContactPerson(),
                    supplier.getPhone(),
                    supplier.getEmail(),
                    supplier.getAddress()
            });
        }
    }


    private void fillForm() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Supplier supplier = supplierService.findById(id).orElse(null);
            if (supplier != null) {
                tfName.setText(supplier.getName());
                tfContactPerson.setText(supplier.getContactPerson());
                tfPhone.setText(supplier.getPhone());
                tfEmail.setText(supplier.getEmail());
                tfAddress.setText(supplier.getAddress());
                tfDescription.setText(supplier.getDescription());
            }
        }
    }

    private void saveSupplier() {
        try {
            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Название обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = supplierTable.getSelectedRow();
            Supplier supplier;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                supplier = supplierService.findById(id).orElse(new Supplier());
            } else {
                supplier = new Supplier();
            }

            supplier.setName(tfName.getText());
            supplier.setContactPerson(tfContactPerson.getText());
            supplier.setPhone(tfPhone.getText());
            supplier.setEmail(tfEmail.getText());
            supplier.setAddress(tfAddress.getText());
            supplier.setDescription(tfDescription.getText());

            supplierService.save(supplier);
            loadSuppliers();
            clearForm();
            JOptionPane.showMessageDialog(this, "Поставщик сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить поставщика №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                supplierService.deleteById(id);
                loadSuppliers();
                clearForm();
                JOptionPane.showMessageDialog(this, "Поставщик удалён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfContactPerson.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
        tfAddress.setText("");
        tfDescription.setText("");
    }
}