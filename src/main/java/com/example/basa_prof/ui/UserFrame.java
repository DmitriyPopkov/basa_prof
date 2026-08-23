package com.example.basa_prof.ui;

import com.example.basa_prof.entity.User;
import com.example.basa_prof.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserFrame extends JFrame {

    private UserService userService;

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField tfUsername, tfEmail, tfPassword, tfPhone, tfFirstName, tfLastName;
    private JTextField tfRole;
    private JCheckBox cbActive;

    public UserFrame(UserService userService) {
        this.userService = userService;
        initialize();
        loadUsers();
    }

    private void initialize() {
        setTitle("Управление пользователями");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные пользователя"));

        tfUsername = new JTextField();
        tfEmail = new JTextField();
        tfPassword = new JTextField();
        tfPhone = new JTextField();
        tfFirstName = new JTextField();
        tfLastName = new JTextField();
        tfRole = new JTextField();
        cbActive = new JCheckBox("Активен", true);

        formPanel.add(new JLabel("Логин:"));
        formPanel.add(tfUsername);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(tfEmail);

        formPanel.add(new JLabel("Пароль:"));
        formPanel.add(tfPassword);

        formPanel.add(new JLabel("Телефон:"));
        formPanel.add(tfPhone);

        formPanel.add(new JLabel("Имя:"));
        formPanel.add(tfFirstName);

        formPanel.add(new JLabel("Фамилия:"));
        formPanel.add(tfLastName);

        formPanel.add(new JLabel("Роль:"));
        formPanel.add(tfRole);

        formPanel.add(new JLabel("Статус:"));
        formPanel.add(cbActive);

        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnRefresh.addActionListener(e -> loadUsers());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Логин", "Email", "Роль", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список пользователей"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.findAll();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getActive() ? "Активен" : "Не активен"
            });
        }
    }

    private void fillForm() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            User user = userService.findById(id).orElse(null);
            if (user != null) {
                tfUsername.setText(user.getUsername());
                tfEmail.setText(user.getEmail());
                tfPassword.setText(user.getPassword());
                tfPhone.setText(user.getPhone());
                tfFirstName.setText(user.getFirstName());
                tfLastName.setText(user.getLastName());
                tfRole.setText(user.getRole());
                cbActive.setSelected(user.getActive());
            }
        }
    }

    private void saveUser() {
        try {
            if (tfUsername.getText().isBlank() || tfEmail.getText().isBlank() || tfPassword.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Заполните обязательные поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = userTable.getSelectedRow();
            User user;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                user = userService.findById(id).orElse(new User());
            } else {
                user = new User();
            }

            user.setUsername(tfUsername.getText());
            user.setEmail(tfEmail.getText());
            user.setPassword(tfPassword.getText());
            user.setPhone(tfPhone.getText());
            user.setFirstName(tfFirstName.getText());
            user.setLastName(tfLastName.getText());
            user.setRole(tfRole.getText());
            user.setActive(cbActive.isSelected());

            userService.save(user);
            loadUsers();
            clearForm();
            JOptionPane.showMessageDialog(this, "Пользователь сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить пользователя №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                userService.deleteById(id);
                loadUsers();
                clearForm();
                JOptionPane.showMessageDialog(this, "Пользователь удалён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfUsername.setText("");
        tfEmail.setText("");
        tfPassword.setText("");
        tfPhone.setText("");
        tfFirstName.setText("");
        tfLastName.setText("");
        tfRole.setText("");
        cbActive.setSelected(true);
    }
}