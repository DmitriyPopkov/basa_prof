package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Material;
import com.example.basa_prof.entity.Supplier;
import com.example.basa_prof.service.MaterialService;
import com.example.basa_prof.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class MaterialFrame extends JFrame {

    private MaterialService materialService;
    private SupplierService supplierService;

    private JTable materialTable;
    private DefaultTableModel tableModel;

    private JTextField tfName, tfUnit, tfPrice, tfPurchasePrice, tfQuantity, tfDescription;
    private JComboBox<Supplier> cbSupplier;

    public MaterialFrame(MaterialService materialService, SupplierService supplierService) {
        this.materialService = materialService;
        this.supplierService = supplierService;
        initialize();
        loadMaterials();
        loadSuppliers();
    }

    private void initialize() {
        setTitle("Управление материалами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные материала"));

        cbSupplier = new JComboBox<>();
        tfName = new JTextField();
        tfUnit = new JTextField();
        tfPrice = new JTextField();
        tfPurchasePrice = new JTextField();
        tfQuantity = new JTextField();
        tfDescription = new JTextField();

        formPanel.add(new JLabel("Поставщик:"));
        formPanel.add(cbSupplier);

        formPanel.add(new JLabel("Название:"));
        formPanel.add(tfName);

        formPanel.add(new JLabel("Ед. измерения:"));
        formPanel.add(tfUnit);

        formPanel.add(new JLabel("Цена продажи:"));
        formPanel.add(tfPrice);

        formPanel.add(new JLabel("Цена закупки:"));
        formPanel.add(tfPurchasePrice);

        formPanel.add(new JLabel("Количество:"));
        formPanel.add(tfQuantity);

        formPanel.add(new JLabel("Описание:"));
        formPanel.add(tfDescription);

        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveMaterial());
        btnDelete.addActionListener(e -> deleteMaterial());
        btnRefresh.addActionListener(e -> loadMaterials());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Название", "Ед.изм.", "Цена продаж.", "Цена закупки", "Кол-во", "Поставщик"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        materialTable = new JTable(tableModel);
        materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(materialTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список материалов"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadMaterials() {
        tableModel.setRowCount(0);
        List<Material> materials = materialService.findAll();
        for (Material material : materials) {
            tableModel.addRow(new Object[]{
                    material.getId(),
                    material.getName(),
                    material.getUnit(),
                    material.getPrice(),
                    material.getPurchasePrice(),
                    material.getQuantity(),
                    material.getSupplier() != null ? material.getSupplier().getName() : ""
            });
        }
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = supplierService.findAll();
        cbSupplier.removeAllItems();
        for (Supplier supplier : suppliers) {
            cbSupplier.addItem(supplier);
        }
    }

    private void fillForm() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Material material = materialService.findById(id).orElse(null);
            if (material != null) {
                cbSupplier.setSelectedItem(material.getSupplier());
                tfName.setText(material.getName());
                tfUnit.setText(material.getUnit());
                tfPrice.setText(material.getPrice() != null ? material.getPrice().toPlainString() : "");
                tfPurchasePrice.setText(material.getPurchasePrice() != null ? material.getPurchasePrice().toPlainString() : "");
                tfQuantity.setText(material.getQuantity() != null ? material.getQuantity().toString() : "");
                tfDescription.setText(material.getDescription());
            }
        }
    }

    private void saveMaterial() {
        try {
            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Название обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Supplier selectedSupplier = (Supplier) cbSupplier.getSelectedItem();

            int selectedRow = materialTable.getSelectedRow();
            Material material;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                material = materialService.findById(id).orElse(new Material());
            } else {
                material = new Material();
            }

            material.setSupplier(selectedSupplier);
            material.setName(tfName.getText());
            material.setUnit(tfUnit.getText());

            if (!tfPrice.getText().isBlank()) {
                material.setPrice(new BigDecimal(tfPrice.getText()));
            } else {
                material.setPrice(null);
            }

            if (!tfPurchasePrice.getText().isBlank()) {
                material.setPurchasePrice(new BigDecimal(tfPurchasePrice.getText()));
            } else {
                material.setPurchasePrice(null);
            }

            if (!tfQuantity.getText().isBlank()) {
                material.setQuantity(Integer.parseInt(tfQuantity.getText()));
            } else {
                material.setQuantity(null);
            }

            material.setDescription(tfDescription.getText());

            materialService.save(material);
            loadMaterials();
            clearForm();
            JOptionPane.showMessageDialog(this, "Материал сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteMaterial() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить материал №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                materialService.deleteById(id);
                loadMaterials();
                clearForm();
                JOptionPane.showMessageDialog(this, "Материал удалён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        cbSupplier.setSelectedIndex(-1);
        tfName.setText("");
        tfUnit.setText("");
        tfPrice.setText("");
        tfPurchasePrice.setText("");
        tfQuantity.setText("");
        tfDescription.setText("");
    }
}