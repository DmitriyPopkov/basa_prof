package com.example.basa_prof.ui;

import com.example.basa_prof.entity.*;
import com.example.basa_prof.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class AnaliticaFrame extends JFrame {

    private ClientService clientService;
    private DealService dealService;
    private MaterialService materialService;
    private ObjectEntityService objectEntityService;
    private WorkService workService;

    private JComboBox<Client> cbClient;
    private JTable dealsTable;
    private DefaultTableModel dealsTableModel;
    private JTable materialsTable;
    private DefaultTableModel materialsTableModel;

    public AnaliticaFrame(WorkHourService workHourService, EmployeeService employeeService,
                          ClientService clientService, ObjectEntityService objectEntityService,
                          DealService dealService, MaterialService materialService, WorkService workService) {
        this.clientService = clientService;
        this.objectEntityService = objectEntityService;
        this.dealService = dealService;
        this.materialService = materialService;
        this.workService = workService;
        initialize();
        loadClients();
    }

    private void initialize() {
        setTitle("Аналитика по клиентам");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель выбора клиента
        JPanel clientSelectionPanel = new JPanel(new BorderLayout(5, 0));
        clientSelectionPanel.setBorder(BorderFactory.createTitledBorder("Выбор клиента"));

        JPanel clientFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientFormPanel.add(new JLabel("Клиент:"));
        cbClient = new JComboBox<>();
        cbClient.setPreferredSize(new Dimension(300, 25));
        cbClient.addActionListener(e -> loadClientData());
        clientFormPanel.add(cbClient);

        clientSelectionPanel.add(clientFormPanel, BorderLayout.WEST);
        mainPanel.add(clientSelectionPanel, BorderLayout.NORTH);

        // Центральная панель с двумя таблицами
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Таблица сделок
        JPanel dealsPanel = new JPanel(new BorderLayout());
        dealsPanel.setBorder(BorderFactory.createTitledBorder("Сделки клиента"));

        String[] dealColumns = {"ID", "Номер договора", "Тип", "Сумма", "Статус", "Дата сделки", "Исполнитель", "Примечания"};
        dealsTableModel = new DefaultTableModel(dealColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dealsTable = new JTable(dealsTableModel);
        dealsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dealsTable.setRowHeight(25);
        dealsTable.getTableHeader().setReorderingAllowed(false);

        // Подсветка выбранных строк
        dealsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(100, 149, 237));
                    c.setForeground(java.awt.Color.WHITE);
                } else {
                    c.setBackground(java.awt.Color.WHITE);
                    c.setForeground(java.awt.Color.BLACK);
                }
                return c;
            }
        });

        // Обработка выбора строки
        dealsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadMaterialsForSelectedDeal();
            }
        });

        JScrollPane dealsScrollPane = new JScrollPane(dealsTable);
        dealsPanel.add(dealsScrollPane, BorderLayout.CENTER);

        // Таблица материалов
        JPanel materialsPanel = new JPanel(new BorderLayout());
        materialsPanel.setBorder(BorderFactory.createTitledBorder("Закупленные материалы"));

        String[] materialColumns = {"ID", "Название", "Ед. изм.", "Цена", "Закуп. цена", "Количество", "Объект", "Поставщик", "Статус"};
        materialsTableModel = new DefaultTableModel(materialColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        materialsTable = new JTable(materialsTableModel);
        materialsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialsTable.setRowHeight(25);
        materialsTable.getTableHeader().setReorderingAllowed(false);

        materialsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(100, 149, 237));
                    c.setForeground(java.awt.Color.WHITE);
                } else {
                    c.setBackground(java.awt.Color.WHITE);
                    c.setForeground(java.awt.Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane materialsScrollPane = new JScrollPane(materialsTable);
        materialsPanel.add(materialsScrollPane, BorderLayout.CENTER);

        // Размещаем таблицы вертикально
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dealsPanel, materialsPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);

        centerPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadClients() {
        List<Client> clients = clientService.findAll();
        for (Client client : clients) {
            cbClient.addItem(client);
        }
    }

    private void loadClientData() {
        dealsTableModel.setRowCount(0);
        materialsTableModel.setRowCount(0);

        Client selectedClient = (Client) cbClient.getSelectedItem();
        if (selectedClient == null) {
            return;
        }

        // Загрузка сделок клиента
        List<Deal> deals = dealService.findByClientOrderByDealDateDesc(selectedClient);
        for (Deal deal : deals) {
            dealsTableModel.addRow(new Object[]{
                    deal.getId(),
                    deal.getContractNumber() != null ? deal.getContractNumber() : "—",
                    deal.getDealType() != null ? deal.getDealType() : "—",
                    deal.getAmount() != null ? deal.getAmount().toPlainString() : "0.00",
                    deal.getStatus() != null ? deal.getStatus() : "—",
                    deal.getDealDate() != null ? deal.getDealDate().toString() : "—",
                    deal.getContractor() != null ? deal.getContractor().getOrganization() : "—",
                    deal.getNotes() != null ? deal.getNotes() : "—"
            });
        }

        // Загрузка материалов для всех сделок клиента
        loadMaterialsForClient(selectedClient);
    }

    private void loadMaterialsForClient(Client client) {
        List<Deal> deals = dealService.findByClientOrderByDealDateDesc(client);
        List<Material> allMaterials = java.util.Collections.emptyList();

        // Собираем материалы из всех сделок клиента
        for (Deal deal : deals) {
            List<Material> dealMaterials = materialService.findByDealId(deal.getId());
            if (allMaterials.isEmpty()) {
                allMaterials = dealMaterials;
            } else {
                allMaterials.addAll(dealMaterials);
            }
        }

        // Отображаем материалы
        for (Material material : allMaterials) {
            materialsTableModel.addRow(new Object[]{
                    material.getId(),
                    material.getName() != null ? material.getName() : "—",
                    material.getUnit() != null ? material.getUnit() : "—",
                    material.getPrice() != null ? material.getPrice().toPlainString() : "0.00",
                    material.getPurchasePrice() != null ? material.getPurchasePrice().toPlainString() : "0.00",
                    material.getQuantity() != null ? material.getQuantity() : 0,
                    material.getObjectEntity() != null ? material.getObjectEntity().getName() : "—",
                    material.getSupplier() != null ? material.getSupplier().getName() : "—",
                    material.getStatus() != null ? material.getStatus() : "—"
            });
        }
    }

    private void loadMaterialsForSelectedDeal() {
        materialsTableModel.setRowCount(0);

        int selectedRow = dealsTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        Long dealId = (Long) dealsTableModel.getValueAt(selectedRow, 0);
        Deal deal = dealService.findById(dealId).orElse(null);
        if (deal == null) {
            return;
        }

        // Загрузка материалов для выбранной сделки
        List<Material> materials = materialService.findByDealId(dealId);
        for (Material material : materials) {
            materialsTableModel.addRow(new Object[]{
                    material.getId(),
                    material.getName() != null ? material.getName() : "—",
                    material.getUnit() != null ? material.getUnit() : "—",
                    material.getPrice() != null ? material.getPrice().toPlainString() : "0.00",
                    material.getPurchasePrice() != null ? material.getPurchasePrice().toPlainString() : "0.00",
                    material.getQuantity() != null ? material.getQuantity() : 0,
                    material.getObjectEntity() != null ? material.getObjectEntity().getName() : "—",
                    material.getSupplier() != null ? material.getSupplier().getName() : "—",
                    material.getStatus() != null ? material.getStatus() : "—"
            });
        }
    }
}
