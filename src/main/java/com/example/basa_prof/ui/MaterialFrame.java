package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.Material;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.Supplier;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.DealService;
import com.example.basa_prof.service.MaterialService;
import com.example.basa_prof.service.ObjectEntityService;
import com.example.basa_prof.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.List;

public class MaterialFrame extends JFrame {

    private MaterialService materialService;
    private SupplierService supplierService;
    private ClientService clientService;
    private ObjectEntityService objectService;
    private DealService dealService;

    private JTable materialTable;
    private DefaultTableModel tableModel;

    private JTextField tfName, tfUnit, tfPrice, tfPurchasePrice, tfQuantity, tfDescription;
    private JTextField tfStatus;
    private JComboBox<Supplier> cbSupplier;
    private JComboBox<Client> cbClient;
    private JComboBox<ObjectEntity> cbObject;
    private JComboBox<Deal> cbDeal;

    public MaterialFrame(MaterialService materialService, SupplierService supplierService, ClientService clientService, ObjectEntityService objectService, DealService dealService) {
        this.materialService = materialService;
        this.supplierService = supplierService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        initialize();
        loadMaterials();
        loadSuppliers();
        loadClients();
    }

    private void initialize() {
        setTitle("Управление материалами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(14, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные материала"));

        cbSupplier = new JComboBox<>();
        tfName = new JTextField();
        tfUnit = new JTextField();
        tfPrice = new JTextField();
        tfPurchasePrice = new JTextField();
        tfQuantity = new JTextField();
        tfDescription = new JTextField();
        tfStatus = new JTextField();
        cbClient = new JComboBox<>();
        cbObject = new JComboBox<>();
        cbDeal = new JComboBox<>();

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

        formPanel.add(new JLabel("Клиент:"));
        formPanel.add(cbClient);

        formPanel.add(new JLabel("Объект:"));
        formPanel.add(cbObject);

        formPanel.add(new JLabel("Статус:"));
        formPanel.add(tfStatus);

        formPanel.add(new JLabel("Договор:"));
        formPanel.add(cbDeal);

        cbClient.addActionListener(e -> {
            loadObjectsForClient();
            loadDealsForClient();
        });

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
        String[] columns = {"ID", "Название", "Ед.изм.", "Цена продаж.", "Цена закупки", "Кол-во", "Поставщик", "Клиент", "Объект", "Статус", "Договор"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        materialTable = new JTable(tableModel){
        @Override
        public void removeRowSelectionInterval(int index0, int index1) {
            // if (hasFocus()) {
            //   super.removeRowSelectionInterval(index0, index1);
             }
        };
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
                    material.getSupplier() != null ? material.getSupplier().getName() : "",
                    material.getObjectEntity() != null && material.getObjectEntity().getClient() != null
                            ? material.getObjectEntity().getClient().getFullName() : "—",
                    material.getObjectEntity() != null ? material.getObjectEntity().getName() : "—",
                    material.getStatus() != null ? material.getStatus() : "—",
                    material.getDeal() != null ? material.getDeal().getContractNumber() : "—"
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

    private void loadClients() {
        cbClient.removeAllItems();
        List<Client> clients = clientService.findAll();
        for (Client client : clients) {
            cbClient.addItem(client);
        }
    }

    private void loadObjectsForClient() {
        cbObject.removeAllItems();
        Client selectedClient = (Client) cbClient.getSelectedItem();
        if (selectedClient != null) {
            List<ObjectEntity> objects = selectedClient.getObjects();
            for (ObjectEntity object : objects) {
                cbObject.addItem(object);
            }
        }
    }

    private void loadDealsForClient() {
        cbDeal.removeAllItems();
        Client selectedClient = (Client) cbClient.getSelectedItem();
        if (selectedClient != null) {
            List<Deal> deals = dealService.findByClientId(selectedClient.getId());
            for (Deal deal : deals) {
                cbDeal.addItem(deal);
            }
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

                if (material.getObjectEntity() != null && material.getObjectEntity().getClient() != null) {
                    cbClient.setSelectedItem(material.getObjectEntity().getClient());
                    loadObjectsForClient();
                    loadDealsForClient();
                    cbObject.setSelectedItem(material.getObjectEntity());
                }

                if (material.getStatus() != null) {
                    tfStatus.setText(material.getStatus());
                }

                if (material.getDeal() != null && material.getDeal().getClient() != null) {
                    cbClient.setSelectedItem(material.getDeal().getClient());
                    loadDealsForClient();
                    cbDeal.setSelectedItem(material.getDeal());
                }
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

            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient != null) {
                ObjectEntity selectedObject = (ObjectEntity) cbObject.getSelectedItem();
                if (selectedObject != null) {
                    material.setObjectEntity(selectedObject);
                }
            }

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
            material.setStatus(tfStatus.getText());

            Deal selectedDeal = (Deal) cbDeal.getSelectedItem();
            if (selectedDeal != null) {
                material.setDeal(selectedDeal);
            }

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
        cbClient.setSelectedIndex(-1);
        cbObject.removeAllItems();
        cbDeal.removeAllItems();
        tfName.setText("");
        tfUnit.setText("");
        tfPrice.setText("");
        tfPurchasePrice.setText("");
        tfQuantity.setText("");
        tfDescription.setText("");
        tfStatus.setText("");
    }
}