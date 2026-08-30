package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Supplier;
import com.example.basa_prof.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierSearchFrame extends JFrame {

    private final SupplierService supplierService;

    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;

    public SupplierSearchFrame(SupplierService supplierService) {
        this.supplierService = supplierService;
        initialize();
        loadSuppliers();
    }

    private void initialize() {
        setTitle("Поиск поставщика");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель поиска
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Поиск"));
        tfSearch = new JTextField();
        tfSearch.setToolTipText("Введите текст для поиска по любому полю");
        searchPanel.add(new JLabel("Фильтр:"), BorderLayout.WEST);
        searchPanel.add(tfSearch, BorderLayout.CENTER);

        // Фильтрация при вводе
        tfSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterTable();
            }
        });

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Таблица
        String[] columns = {"ID", "Название", "Контактное лицо", "Телефон", "Email", "Адрес", "Описание"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Скрыть колонку ID
        supplierTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        supplierTable.getColumnModel().getColumn(0).setMaxWidth(0);
        supplierTable.getColumnModel().getColumn(0).setMinWidth(0);

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
                    supplier.getAddress(),
                    supplier.getDescription() != null ? supplier.getDescription() : "—"
            });
        }
    }

    private void filterTable() {
        String searchText = tfSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        List<Supplier> allSuppliers = supplierService.findAll();

        for (Supplier supplier : allSuppliers) {
            if (searchText.isEmpty()) {
                tableModel.addRow(new Object[]{
                        supplier.getId(),
                        supplier.getName(),
                        supplier.getContactPerson(),
                        supplier.getPhone(),
                        supplier.getEmail(),
                        supplier.getAddress(),
                        supplier.getDescription() != null ? supplier.getDescription() : "—"
                });
            } else {
                // Поиск по любому полю
                if (matchesSearch(supplier, searchText)) {
                    tableModel.addRow(new Object[]{
                            supplier.getId(),
                            supplier.getName(),
                            supplier.getContactPerson(),
                            supplier.getPhone(),
                            supplier.getEmail(),
                            supplier.getAddress(),
                            supplier.getDescription() != null ? supplier.getDescription() : "—"
                    });
                }
            }
        }
    }

    private boolean matchesSearch(Supplier supplier, String searchText) {
        // Поиск по названию
        if (supplier.getName() != null && supplier.getName().toLowerCase().contains(searchText)) return true;
        // Поиск по контактному лицу
        if (supplier.getContactPerson() != null && supplier.getContactPerson().toLowerCase().contains(searchText)) return true;
        // Поиск по телефону
        if (supplier.getPhone() != null && supplier.getPhone().toLowerCase().contains(searchText)) return true;
        // Поиск по email
        if (supplier.getEmail() != null && supplier.getEmail().toLowerCase().contains(searchText)) return true;
        // Поиск по адресу
        if (supplier.getAddress() != null && supplier.getAddress().toLowerCase().contains(searchText)) return true;
        // Поиск по описанию
        if (supplier.getDescription() != null && supplier.getDescription().toLowerCase().contains(searchText)) return true;
        return false;
    }
}
