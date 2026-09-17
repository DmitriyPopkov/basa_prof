package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.Work;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * View для управления работами.
 * Отвечает только за отображение UI-элементов.
 * Реализует принцип Single Responsibility (S).
 */
public class WorkView extends JFrame {

    // UI-компоненты
    private JTable workTable;
    private DefaultTableModel tableModel;

    private JTextField tfName, tfWorkType, tfEstimatedCost;
    private JTextField tfPrice, tfPayment, tfPaymentDate;
    private JTextField tfStartDate, tfEndDate;
    private JTextField tfStatus;
    private JTextArea tfDescription;
    private JCheckBox cbDetailedDescription;
    private JComboBox<Client> cbClient;
    private JComboBox<ObjectEntity> cbObject;
    private JComboBox<Deal> cbDeal;

    // Состояние
    private Long currentWorkId;

    public WorkView() {
        initialize();
    }

    /**
     * Инициализация UI-компонентов.
     */
    private void initialize() {
        setTitle("Управление работами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(14, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные работы"));

        tfName = new JTextField();
        tfWorkType = new JTextField();
        tfEstimatedCost = new JTextField();
        tfStartDate = new JTextField();
        tfEndDate = new JTextField();
        tfPrice = new JTextField();
        tfPayment = new JTextField();
        tfPaymentDate = new JTextField();
        tfStatus = new JTextField();
        cbClient = new JComboBox<>();
        cbObject = new JComboBox<>();
        cbDeal = new JComboBox<>();

        // Описание с чекбоксом
        JPanel descriptionPanel = new JPanel(new BorderLayout(5, 5));
        descriptionPanel.add(new JLabel("Описание:"), BorderLayout.WEST);
        tfDescription = new JTextArea(3, 20);
        tfDescription.setLineWrap(true);
        tfDescription.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(tfDescription);
        descriptionPanel.add(descriptionScroll, BorderLayout.CENTER);

        cbDetailedDescription = new JCheckBox("Подробное описание");

        formPanel.add(new JLabel("Клиент:"));
        formPanel.add(cbClient);
        formPanel.add(new JLabel("Объект:"));
        formPanel.add(cbObject);
        formPanel.add(new JLabel("Название:"));
        formPanel.add(tfName);
        formPanel.add(new JLabel("Тип работы:"));
        formPanel.add(tfWorkType);
        formPanel.add(descriptionPanel);
        formPanel.add(cbDetailedDescription);
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
        formPanel.add(new JLabel("Статус:"));
        formPanel.add(tfStatus);
        formPanel.add(new JLabel("Договор:"));
        formPanel.add(cbDeal);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить как новую");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");
        JButton btnReport = new JButton("Сформировать отчёт");
        JButton btnSaveMerge = new JButton("Сохранить изменения");
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnReport);
        buttonPanel.add(btnSaveMerge);

        // Таблица
        String[] columns = {"ID", "Клиент", "Объект", "Название", "Тип", "Оценка", "Цена", "Оплата", "Статус", "Договор"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Исправлено LSP: используем композицию вместо наследования
        workTable = new JTable(tableModel);
        workTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(workTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список работ"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // Сохраняем ссылки на кнопки для контроллера
        this.btnSave = btnSave;
        this.btnDelete = btnDelete;
        this.btnRefresh = btnRefresh;
        this.btnReport = btnReport;
        this.btnSaveMerge = btnSaveMerge;
        this.contentPanel = mainPanel;
    }

    public String getTitle() {
        return "Управление работами";
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    private JButton btnSave, btnDelete, btnRefresh, btnReport, btnSaveMerge;
    private JPanel contentPanel;

    // Геттеры для UI-компонентов (для Controller)
    public JTable getWorkTable() { return workTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTfName() { return tfName; }
    public JTextField getTfWorkType() { return tfWorkType; }
    public JTextArea getTfDescription() { return tfDescription; }
    public JTextField getTfEstimatedCost() { return tfEstimatedCost; }
    public JTextField getTfStartDate() { return tfStartDate; }
    public JTextField getTfEndDate() { return tfEndDate; }
    public JTextField getTfPrice() { return tfPrice; }
    public JTextField getTfPayment() { return tfPayment; }
    public JTextField getTfPaymentDate() { return tfPaymentDate; }
    public JTextField getTfStatus() { return tfStatus; }
    public JCheckBox getCbDetailedDescription() { return cbDetailedDescription; }
    public JComboBox<Client> getCbClient() { return cbClient; }
    public JComboBox<ObjectEntity> getCbObject() { return cbObject; }
    public JComboBox<Deal> getCbDeal() { return cbDeal; }
    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnRefresh() { return btnRefresh; }
    public JButton getBtnReport() { return btnReport; }
    public JButton getBtnSaveMerge() { return btnSaveMerge; }

    // Геттеры/сеттеры для состояния
    public Long getCurrentWorkId() { return currentWorkId; }
    public void setCurrentWorkId(Long currentWorkId) { this.currentWorkId = currentWorkId; }

    // Методы для обновления UI (вызываются Controller)
    public void setTableData(List<Object[]> rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    public void selectRowByWorkId(Long workId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long id = (Long) tableModel.getValueAt(i, 0);
            if (id != null && id.equals(workId)) {
                workTable.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Подтверждение", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    public void openDescriptionDialog(String currentDescription, java.util.function.Consumer<String> callback) {
        DescriptionFrame descFrame = new DescriptionFrame(this, currentDescription);
        descFrame.setVisible(true);
        callback.accept(descFrame.getDescription());
    }

    public String getSaveDir() {
        return System.getProperty("user.home") + "\\Documents\\Reports";
    }
}
