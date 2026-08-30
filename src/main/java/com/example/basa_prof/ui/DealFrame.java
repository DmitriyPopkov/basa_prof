package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Contractor;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.Expense;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.ContractorService;
import com.example.basa_prof.service.DealService;
import com.example.basa_prof.service.ExpenseService;
import com.example.basa_prof.service.ObjectEntityService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.List;

public class DealFrame extends JFrame {

    private DealService dealService;
    private ClientService clientService;
    private ObjectEntityService objectService;
    private ContractorService contractorService;
    private ExpenseService expenseService;

    private JTable dealTable;
    private DefaultTableModel tableModel;

    private JTextField tfContractNumber, tfAmount, tfDealType, tfStatus;
    private JTextArea tfNotes;
    private JComboBox<Client> cbClient;
    private JComboBox<Contractor> cbContractor;
    private JButton btnExpenses;

    public DealFrame(DealService dealService, ClientService clientService, ObjectEntityService objectService, ContractorService contractorService, ExpenseService expenseService) {
        this.dealService = dealService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.contractorService = contractorService;
        this.expenseService = expenseService;
        initialize();
        loadDeals();
        loadClients();
        loadContractors();
    }

    private void initialize() {
        setTitle("Управление сделками");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные сделки"));

        formPanel.add(new JLabel("Клиент:"));
        cbClient = new JComboBox<>();
        formPanel.add(cbClient);

        formPanel.add(new JLabel("Исполнитель:"));
        cbContractor = new JComboBox<>();
        formPanel.add(cbContractor);

        formPanel.add(new JLabel("Номер договора:"));
        tfContractNumber = new JTextField( );
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
        tfNotes = new JTextArea(3, 20);
        JScrollPane notesScroll = new JScrollPane(tfNotes);
        formPanel.add(notesScroll);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");
        btnExpenses = new JButton("Расходы");
        btnExpenses.setEnabled(false);

        btnSave.addActionListener(e -> saveDeal());
        btnDelete.addActionListener(e -> deleteDeal());
        btnRefresh.addActionListener(e -> loadDeals());
        btnExpenses.addActionListener(e -> openExpenses());

        cbClient.addActionListener(e -> loadDealsForClient());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExpenses);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Клиент", "Исполнитель", "Договор", "Сумма", "Тип", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dealTable = new JTable(tableModel) {
            @Override
            public void removeRowSelectionInterval(int index0, int index1) {
                if (hasFocus()) {
                    super.removeRowSelectionInterval(index0, index1);
                }
            }
        };
        dealTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dealTable.setRowHeight(25);
        dealTable.getTableHeader().setReorderingAllowed(false);
        
        // Кастомный рендерер для подсветки выделения
        dealTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        // Обработка выбора строки при клике мыши
        dealTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = dealTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    dealTable.setRowSelectionInterval(row, row);
                    fillForm();
                    btnExpenses.setEnabled(true);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
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
                    deal.getContractor() != null ? deal.getContractor().getOrganization() : "—",
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

    private void loadContractors() {
        cbContractor.removeAllItems();
        List<Contractor> contractors = contractorService.findAll();
        for (Contractor contractor : contractors) {
            cbContractor.addItem(contractor);
        }
    }

    private void loadDealsForClient() {
        tableModel.setRowCount(0);
        Client selectedClient = (Client) cbClient.getSelectedItem();

        if (selectedClient == null) {
            loadDeals();
            return;
        }

        List<Deal> deals = dealService.findAll();
        for (Deal deal : deals) {
            if (deal.getClient() != null && deal.getClient().getId().equals(selectedClient.getId())) {
                tableModel.addRow(new Object[]{
                        deal.getId(),
                        deal.getClient() != null ? deal.getClient().getFullName() : "—",
                        deal.getContractor() != null ? deal.getContractor().getOrganization() : "—",
                        deal.getContractNumber(),
                        deal.getAmount(),
                        deal.getDealType(),
                        deal.getStatus()
                });
            }
        }
    }

    private void fillForm() {
        int selectedRow = dealTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Deal deal = dealService.findById(id).orElse(null);
            if (deal != null) {
                cbClient.setSelectedItem(deal.getClient());
                cbContractor.setSelectedItem(deal.getContractor());
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
            Contractor selectedContractor = (Contractor) cbContractor.getSelectedItem();
            deal.setContractor(selectedContractor);
            deal.setContractNumber(tfContractNumber.getText());
            deal.setAmount(amount);
            deal.setDealType(tfDealType.getText());
            deal.setStatus(tfStatus.getText());
            deal.setNotes(tfNotes.getText().trim().isEmpty() ? null : tfNotes.getText().trim());

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
        cbContractor.setSelectedIndex(-1);
        tableModel.setRowCount(0);
        tfContractNumber.setText("");
        tfAmount.setText("");
        tfDealType.setText("");
        tfStatus.setText("");
        tfNotes.setText("");
        btnExpenses.setEnabled(false);
    }

    private void openExpenses() {
        int selectedRow = dealTable.getSelectedRow();
        if (selectedRow < 0) return;

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        Deal deal = dealService.findByIdWithObjects(id).orElse(null);
        if (deal == null) return;

        // Открываем ExpenseFrame с уже выбранным клиентом и объектами из сделки
        Client client = deal.getClient();
        if (client == null) return;

        ExpenseFrame expenseFrame = new ExpenseFrame(expenseService, clientService, objectService, dealService, deal);
        expenseFrame.setVisible(true);
    }
}