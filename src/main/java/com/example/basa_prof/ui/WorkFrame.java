package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Work;
import com.example.basa_prof.service.WorkService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class WorkFrame extends JFrame {

    private  WorkService workService;

    private JTable workTable;
    private DefaultTableModel tableModel;

    private JTextField tfName, tfWorkType, tfDescription, tfEstimatedCost,
            tfPrice, tfPayment, tfPaymentDate;
    private JTextField tfStartDate, tfEndDate;

    public WorkFrame(WorkService workService) {
        this.workService = workService;
        initialize();
        loadWorks();
    }



    private void initialize() {
        setTitle("Управление работами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные работы"));

        tfName = new JTextField();
        tfWorkType = new JTextField();
        tfDescription = new JTextField();
        tfEstimatedCost = new JTextField();
        tfStartDate = new JTextField();
        tfEndDate = new JTextField();
        tfPrice = new JTextField();
        tfPayment = new JTextField();
        tfPaymentDate = new JTextField();

        formPanel.add(new JLabel("Название:"));
        formPanel.add(tfName);

        formPanel.add(new JLabel("Тип работы:"));
        formPanel.add(tfWorkType);

        formPanel.add(new JLabel("Описание:"));
        formPanel.add(tfDescription);

        formPanel.add(new JLabel("Оценка стоимости:"));
        formPanel.add(tfEstimatedCost);

        formPanel.add(new JLabel("Дата начала (дд.мм.гггг):"));
        formPanel.add(tfStartDate);

        formPanel.add(new JLabel("Дата окончания (дд.мм.гггг):"));
        formPanel.add(tfEndDate);

        formPanel.add(new JLabel("Цена:"));
        formPanel.add(tfPrice);

        formPanel.add(new JLabel("Способ оплаты:"));
        formPanel.add(tfPayment);

        formPanel.add(new JLabel("Дата оплаты (дд.мм.гггг):"));
        formPanel.add(tfPaymentDate);

        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");

        btnSave.addActionListener(e -> saveWork());
        btnDelete.addActionListener(e -> deleteWork());
        btnRefresh.addActionListener(e -> loadWorks());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Название", "Тип", "Оценка", "Цена", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        workTable = new JTable(tableModel);
        workTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(workTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список работ"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadWorks() {
        tableModel.setRowCount(0);
        List<Work> works = workService.findAll();
        for (Work work : works) {
            tableModel.addRow(new Object[]{
                    work.getId(),
                    work.getName(),
                    work.getWorkType(),
                    work.getEstimatedCost(),
                    work.getPrice(),
                    work.getPayment() != null ? work.getPayment() : "—"
            });
        }
    }
    private void fillForm() {
        int selectedRow = workTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Work work = workService.findById(id).orElse(null);
            if (work != null) {
                tfName.setText(work.getName());
                tfWorkType.setText(work.getWorkType());
                tfDescription.setText(work.getDescription());
                tfEstimatedCost.setText(work.getEstimatedCost() != null ? work.getEstimatedCost().toString() : "");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                if (work.getStartDate() != null) {
                    tfStartDate.setText(work.getStartDate().format(formatter));
                }
                if (work.getEndDate() != null) {
                    tfEndDate.setText(work.getEndDate().format(formatter));
                }

                if (work.getPrice() != null) {
                    tfPrice.setText(work.getPrice().toString());
                }
                if (work.getPayment() != null) {
                    tfPayment.setText(work.getPayment());
                }
                if (work.getPaymentDate() != null) {
                    tfPaymentDate.setText(work.getPaymentDate().format(formatter));
                }
            }
        }
    }

    private void saveWork() {
        try {
            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Название обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = workTable.getSelectedRow();
            Work work;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                work = workService.findById(id).orElse(new Work());
            } else {
                work = new Work();
            }

            work.setName(tfName.getText());
            work.setWorkType(tfWorkType.getText());
            work.setDescription(tfDescription.getText());

            if (!tfEstimatedCost.getText().isBlank()) {
                work.setEstimatedCost(Double.parseDouble(tfEstimatedCost.getText()));
            } else {
                work.setEstimatedCost(null);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            if (!tfStartDate.getText().isBlank()) {
                try {
                    work.setStartDate(LocalDate.parse(tfStartDate.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                work.setStartDate(null);
            }

            if (!tfEndDate.getText().isBlank()) {
                try {
                    work.setEndDate(LocalDate.parse(tfEndDate.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                work.setEndDate(null);
            }

            if (!tfPrice.getText().isBlank()) {
                work.setPrice(Double.parseDouble(tfPrice.getText()));
            } else {
                work.setPrice(null);
            }

            work.setPayment(tfPayment.getText());

            if (!tfPaymentDate.getText().isBlank()) {
                try {
                    work.setPaymentDate(LocalDate.parse(tfPaymentDate.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                work.setPaymentDate(null);
            }

            workService.save(work);
            loadWorks();
            clearForm();
            JOptionPane.showMessageDialog(this, "Работа сохранена!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteWork() {
        int selectedRow = workTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить работу №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                workService.deleteById(id);
                loadWorks();
                clearForm();
                JOptionPane.showMessageDialog(this, "Работа удалена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfWorkType.setText("");
        tfDescription.setText("");
        tfEstimatedCost.setText("");
        tfStartDate.setText("");
        tfEndDate.setText("");
        tfPrice.setText("");
        tfPayment.setText("");
        tfPaymentDate.setText("");
    }
}
