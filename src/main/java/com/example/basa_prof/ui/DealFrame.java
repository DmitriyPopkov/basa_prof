package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.DealService;
import com.example.basa_prof.service.ObjectEntityService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DealFrame extends JFrame {

    private DealService dealService;
    private ClientService clientService;
    private ObjectEntityService objectService;

    private JTable dealTable;
    private DefaultTableModel tableModel;

    private JTextField tfContractNumber, tfAmount, tfDealType, tfStatus, tfNotes;
    private JComboBox<Client> cbClient;

    public DealFrame(DealService dealService, ClientService clientService, ObjectEntityService objectService) {
        this.dealService = dealService;
        this.clientService = clientService;
        this.objectService = objectService;
        initialize();
        loadDeals();
        loadClients();
    }

    private void initialize() {
        setTitle("Управление сделками");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные сделки"));

        formPanel.add(new JLabel("Клиент:"));
        cbClient = new JComboBox<>();
        formPanel.add(cbClient);

        formPanel.add(new JLabel("Номер договора:"));
        tfContractNumber = new JTextField();
        formPanel.add(tfContractNumber);

        formPanel.add(new JLabel("Сумма:"));
        tfAmount = new JTextField();
        formPanel.add(tfAmount);

        formPanel.add(new JLabel("Тип сделки:"));
        tfDealType = new JTextField();
        formPanel.add(tfDealType);

        formPanel.add(new JLabel("Статус:"));
        tfStatus = new JTextField();
        formPanel.add(tfStatus);

        formPanel.add(new JLabel("Примечания:"));
        tfNotes = new JTextField();
        formPanel.add(tfNotes);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveDeal());
        btnDelete.addActionListener(e -> deleteDeal());
        btnRefresh.addActionListener(e -> loadDeals());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Клиент", "Договор", "Сумма", "Тип", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dealTable = new JTable(tableModel);
        dealTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dealTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(dealTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список сделок"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadDeals() {
        tableModel.setRowCount(0);
        List<Deal> deals = dealService.findAll();
        for (Deal deal : deals) {
            tableModel.addRow(new Object[]{
                    deal.getId(),
                    deal.getClient() != null ? deal.getClient().getFullName() : "—",
                    deal.getContractNumber(),
                    deal.getAmount(),
                    deal.getDealType(),
                    deal.getStatus()
            });
        }
    }

    private void loadClients() {
        List<Client> clients = clientService.findAll();
        cbClient.removeAllItems();
        for (Client client : clients) {
            cbClient.addItem(client);
        }
    }

    private void fillForm() {
        int selectedRow = dealTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Deal deal = dealService.findById(id).orElse(null);
            if (deal != null) {
                cbClient.setSelectedItem(deal.getClient());
                tfContractNumber.setText(deal.getContractNumber());
                tfAmount.setText(deal.getAmount() != null ? deal.getAmount().toPlainString() : "");
                tfDealType.setText(deal.getDealType());
                tfStatus.setText(deal.getStatus());
                tfNotes.setText(deal.getNotes());
            }
        }
    }

    private void saveDeal() {
        try {
            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient == null) {
                JOptionPane.showMessageDialog(this, "Выберите клиента!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal amount = null;
            if (!tfAmount.getText().isBlank()) {
                amount = new BigDecimal(tfAmount.getText());
            }

            int selectedRow = dealTable.getSelectedRow();
            Deal deal;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                deal = dealService.findById(id).orElse(new Deal());
            } else {
                deal = new Deal();
            }

            deal.setClient(selectedClient);
            deal.setContractNumber(tfContractNumber.getText());
            deal.setAmount(amount);
            deal.setDealType(tfDealType.getText());
            deal.setStatus(tfStatus.getText());
            deal.setNotes(tfNotes.getText());

            dealService.save(deal);
            loadDeals();
            clearForm();
            JOptionPane.showMessageDialog(this, "Сделка сохранена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteDeal() {
        int selectedRow = dealTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить сделку №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dealService.deleteById(id);
                loadDeals();
                clearForm();
                JOptionPane.showMessageDialog(this, "Сделка удалена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        cbClient.setSelectedIndex(-1);
        tfContractNumber.setText("");
        tfAmount.setText("");
        tfDealType.setText("");
        tfStatus.setText("");
        tfNotes.setText("");
    }
}