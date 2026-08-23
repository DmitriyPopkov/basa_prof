package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientFrame extends JFrame {

    private ClientService clientService;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField tfFullName, tfPhone, tfEmail, tfAddress;

    public ClientFrame(ClientService clientService) {
        this.clientService = clientService;
        initialize();
        loadClients();
    }

    private void initialize() {
        setTitle("Управление клиентами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные клиента"));

        formPanel.add(new JLabel("ФИО:"));
        tfFullName = new JTextField();
        formPanel.add(tfFullName);

        formPanel.add(new JLabel("Телефон:"));
        tfPhone = new JTextField();
        formPanel.add(tfPhone);

        formPanel.add(new JLabel("Email:"));
        tfEmail = new JTextField();
        formPanel.add(tfEmail);

        formPanel.add(new JLabel("Адрес:"));
        tfAddress = new JTextField();
        formPanel.add(tfAddress);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveClient());
        btnDelete.addActionListener(e -> deleteClient());
        btnRefresh.addActionListener(e -> loadClients());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "ФИО", "Телефон", "Email", "Адрес"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientTable = new JTable(tableModel);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(clientTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список клиентов"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadClients() {
        tableModel.setRowCount(0);
        List<Client> clients = clientService.findAll();
        for (Client client : clients) {
            tableModel.addRow(new Object[]{
                client.getId(),
                client.getFullName(),
                client.getPhone(),
                client.getEmail(),
                client.getAddress()
            });
        }
    }

    private void fillForm() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0) {
            tfFullName.setText((String) tableModel.getValueAt(selectedRow, 1));
            tfPhone.setText((String) tableModel.getValueAt(selectedRow, 2));
            tfEmail.setText((String) tableModel.getValueAt(selectedRow, 3));
            tfAddress.setText((String) tableModel.getValueAt(selectedRow, 4));
        }
    }

    private void saveClient() {
        Client client = new Client(
            tfFullName.getText(),
            tfPhone.getText(),
            tfEmail.getText(),
            tfAddress.getText(),
            "",
            "",
            ""
        );
        clientService.save(client);
        loadClients();
        clearForm();
    }

    private void deleteClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            clientService.deleteById(id);
            loadClients();
            clearForm();
        }
    }

    private void clearForm() {
        tfFullName.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
        tfAddress.setText("");
    }
}
