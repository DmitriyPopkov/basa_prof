package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Contractor;
import com.example.basa_prof.service.ContractorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ContractorFrame extends JFrame {

    private ContractorService contractorService;

    private JTable contractorTable;
    private DefaultTableModel tableModel;

    private JTextField tfOrganization, tfPhone, tfContactPerson, tfEmail;
    private JTextArea tfDetails, tfDescription;

    public ContractorFrame(ContractorService contractorService) {
        this.contractorService = contractorService;
        initialize();
        loadContractors();
    }

    private void initialize() {
        setTitle("Управление исполнителями");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(400);

        // Левая панель - форма ввода
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContent = new JPanel(new GridLayout(7, 1, 5, 5));
        formContent.setBorder(BorderFactory.createTitledBorder("Данные исполнителя"));

        // Организация
        JPanel panelOrganization = new JPanel(new BorderLayout(5, 5));
        panelOrganization.add(new JLabel("Организация:"), BorderLayout.WEST);
        tfOrganization = new JTextField();
        panelOrganization.add(tfOrganization, BorderLayout.CENTER);
        formContent.add(panelOrganization);

        // Телефон
        JPanel panelPhone = new JPanel(new BorderLayout(5, 5));
        panelPhone.add(new JLabel("Телефон:"), BorderLayout.WEST);
        tfPhone = new JTextField();
        panelPhone.add(tfPhone, BorderLayout.CENTER);
        formContent.add(panelPhone);

        // Контактное лицо
        JPanel panelContact = new JPanel(new BorderLayout(5, 5));
        panelContact.add(new JLabel("Контактное лицо:"), BorderLayout.WEST);
        tfContactPerson = new JTextField();
        panelContact.add(tfContactPerson, BorderLayout.CENTER);
        formContent.add(panelContact);

        // E-mail
        JPanel panelEmail = new JPanel(new BorderLayout(5, 5));
        panelEmail.add(new JLabel("E-mail:"), BorderLayout.WEST);
        tfEmail = new JTextField();
        panelEmail.add(tfEmail, BorderLayout.CENTER);
        formContent.add(panelEmail);

        // Реквизиты (многострочное)
        JPanel panelDetails = new JPanel(new BorderLayout(5, 5));
        panelDetails.add(new JLabel("Реквизиты:"), BorderLayout.NORTH);
        tfDetails = new JTextArea(3, 20);
        JScrollPane detailsScroll = new JScrollPane(tfDetails);
        panelDetails.add(detailsScroll, BorderLayout.CENTER);
        formContent.add(panelDetails);

        // Описание (многострочное)
        JPanel panelDescription = new JPanel(new BorderLayout(5, 5));
        panelDescription.add(new JLabel("Описание:"), BorderLayout.NORTH);
        tfDescription = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(tfDescription);
        panelDescription.add(descriptionScroll, BorderLayout.CENTER);
        formContent.add(panelDescription);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveContractor());
        btnDelete.addActionListener(e -> deleteContractor());
        btnRefresh.addActionListener(e -> loadContractors());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        formPanel.add(formContent, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Правая панель - таблица
        String[] columns = {"ID", "Организация", "Телефон", "Контактное лицо", "E-mail"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        contractorTable = new JTable(tableModel);
        contractorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contractorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane tableScroll = new JScrollPane(contractorTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Список исполнителей"));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        mainSplit.setLeftComponent(formPanel);
        mainSplit.setRightComponent(tablePanel);

        setContentPane(mainSplit);
    }

    private void loadContractors() {
        tableModel.setRowCount(0);
        List<Contractor> contractors = contractorService.findAll();
        for (Contractor contractor : contractors) {
            tableModel.addRow(new Object[]{
                    contractor.getId(),
                    contractor.getOrganization() != null ? contractor.getOrganization() : "",
                    contractor.getPhone() != null ? contractor.getPhone() : "",
                    contractor.getContactPerson() != null ? contractor.getContactPerson() : "",
                    contractor.getEmail() != null ? contractor.getEmail() : ""
            });
        }
    }

    private void fillForm() {
        int selectedRow = contractorTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Contractor contractor = contractorService.findById(id).orElse(null);
            if (contractor != null) {
                tfOrganization.setText(contractor.getOrganization() != null ? contractor.getOrganization() : "");
                tfPhone.setText(contractor.getPhone() != null ? contractor.getPhone() : "");
                tfContactPerson.setText(contractor.getContactPerson() != null ? contractor.getContactPerson() : "");
                tfEmail.setText(contractor.getEmail() != null ? contractor.getEmail() : "");
                tfDetails.setText(contractor.getDetails() != null ? contractor.getDetails() : "");
                tfDescription.setText(contractor.getDescription() != null ? contractor.getDescription() : "");
            }
        }
    }

    private void saveContractor() {
        try {
            if (tfOrganization.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Организация обязательна!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = contractorTable.getSelectedRow();
            Contractor contractor;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                contractor = contractorService.findById(id).orElse(new Contractor());
            } else {
                contractor = new Contractor();
            }

            contractor.setOrganization(tfOrganization.getText());
            contractor.setPhone(tfPhone.getText());
            contractor.setContactPerson(tfContactPerson.getText());
            contractor.setEmail(tfEmail.getText());
            contractor.setDetails(tfDetails.getText().isBlank() ? null : tfDetails.getText());
            contractor.setDescription(tfDescription.getText().isBlank() ? null : tfDescription.getText());

            contractorService.save(contractor);
            loadContractors();
            clearForm();
            JOptionPane.showMessageDialog(this, "Исполнитель сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteContractor() {
        int selectedRow = contractorTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить исполнителя №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                contractorService.deleteById(id);
                loadContractors();
                clearForm();
                JOptionPane.showMessageDialog(this, "Исполнитель удалён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfOrganization.setText("");
        tfPhone.setText("");
        tfContactPerson.setText("");
        tfEmail.setText("");
        tfDetails.setText("");
        tfDescription.setText("");
    }
}
