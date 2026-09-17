package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.Work;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.DealService;
import com.example.basa_prof.service.ObjectEntityService;
import com.example.basa_prof.service.ReportService;
import com.example.basa_prof.service.WorkService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class WorkFrame extends JFrame {

    private WorkService workService;
    private ClientService clientService;
    private ObjectEntityService objectService;
    private DealService dealService;
    private ReportService reportService;

    private JTable workTable;
    private DefaultTableModel tableModel;

    private Long currentWorkId;

    private JTextField tfName, tfWorkType, tfEstimatedCost,
            tfPrice, tfPayment, tfPaymentDate;
    private JTextField tfStartDate, tfEndDate;
    private JTextField tfStatus;
    private JTextArea tfDescription;
    private JCheckBox cbDetailedDescription;
    private JComboBox<Client> cbClient;
    private JComboBox<ObjectEntity> cbObject;
    private JComboBox<Deal> cbDeal;

    public WorkFrame(WorkService workService, ClientService clientService, ObjectEntityService objectService, DealService dealService, ReportService reportService) {
        this.workService = workService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        this.reportService = reportService;
        initialize();
        loadWorks();
        loadClients();
    }



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
        cbDetailedDescription.addActionListener(e -> {
            if (cbDetailedDescription.isSelected()) {
                DescriptionFrame descFrame = new DescriptionFrame(WorkFrame.this, tfDescription.getText());
                descFrame.setVisible(true);
                tfDescription.setText(descFrame.getDescription());
            }
        });

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

        cbClient.addActionListener(e -> {
            loadObjectsForClient();
            loadDealsForClient();
        });

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить как новую");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");
        JButton btnReport = new JButton("Сформировать отчёт");
        JButton btnSaveMerge = new JButton("Сохранить изменения");

        btnSave.addActionListener(e -> saveWork());
        btnDelete.addActionListener(e -> deleteWork());
        btnRefresh.addActionListener(e -> loadWorks());
        btnReport.addActionListener(e -> generateReport());
        btnSaveMerge.addActionListener(e -> saveWorkMerge());

        cbClient.addActionListener(e -> loadWorksForClient());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnReport);
        buttonPanel.add(btnSaveMerge);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Клиент", "Объект", "Название", "Тип", "Оценка", "Цена", "Оплата", "Статус", "Договор"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        workTable = new JTable(tableModel){
            @Override
            public void removeRowSelectionInterval(int index0, int index1) {
                if (hasFocus()) {
                    super.removeRowSelectionInterval(index0, index1);
                }
            }
        };
        workTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = workTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    workTable.setRowSelectionInterval(row, row);
                    fillForm();
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
            String clientName = work.getObjectEntity() != null && work.getObjectEntity().getClient() != null
                    ? work.getObjectEntity().getClient().getFullName() : "—";
            String objectName = work.getObjectEntity() != null ? work.getObjectEntity().getName() : "—";
            tableModel.addRow(new Object[]{
                    work.getId(),
                    clientName,
                    objectName,
                    work.getName(),
                    work.getWorkType(),
                    work.getEstimatedCost(),
                    work.getPrice(),
                    work.getPayment() != null ? work.getPayment() : "—",
                    work.getStatus() != null ? work.getStatus() : "—",
                    work.getDeal() != null ? work.getDeal().getContractNumber() : "—"
            });
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

    private void loadWorksForClient() {
        tableModel.setRowCount(0);
        Client selectedClient = (Client) cbClient.getSelectedItem();

        if (selectedClient == null) {
            loadWorks();
            return;
        }

        List<Work> works = workService.findAll();
        for (Work work : works) {
            if (work.getObjectEntity() != null && work.getObjectEntity().getClient() != null
                    && work.getObjectEntity().getClient().getId().equals(selectedClient.getId())) {
                String clientName = work.getObjectEntity().getClient().getFullName();
                String objectName = work.getObjectEntity().getName();
                tableModel.addRow(new Object[]{
                        work.getId(),
                        clientName,
                        objectName,
                        work.getName(),
                        work.getWorkType(),
                        work.getEstimatedCost(),
                        work.getPrice(),
                        work.getPayment() != null ? work.getPayment() : "—",
                        work.getStatus() != null ? work.getStatus() : "—",
                        work.getDeal() != null ? work.getDeal().getContractNumber() : "—"
                });
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
        int selectedRow = workTable.getSelectedRow();
        if (selectedRow >= 0) {
            currentWorkId = (Long) tableModel.getValueAt(selectedRow, 0);
            Work work = workService.findById(currentWorkId).orElse(null);
            if (work != null) {
                if (work.getObjectEntity() != null && work.getObjectEntity().getClient() != null) {
                    cbClient.setSelectedItem(work.getObjectEntity().getClient());
                    loadObjectsForClient();
                    cbObject.setSelectedItem(work.getObjectEntity());
                }

                tfName.setText(work.getName());
                tfWorkType.setText(work.getWorkType());
                tfDescription.setText(work.getDescription() != null ? work.getDescription() : "");
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
                if (work.getStatus() != null) {
                    tfStatus.setText(work.getStatus());
                }

                if (work.getDeal() != null && work.getDeal().getClient() != null) {
                    cbClient.setSelectedItem(work.getDeal().getClient());
                    loadObjectsForClient();
                    loadDealsForClient();
                    cbDeal.setSelectedItem(work.getDeal());
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

            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient != null) {
                ObjectEntity selectedObject = (ObjectEntity) cbObject.getSelectedItem();
                if (selectedObject != null) {
                    work.setObjectEntity(selectedObject);
                }
            }

            work.setName(tfName.getText());
            work.setWorkType(tfWorkType.getText());
            work.setDescription(tfDescription.getText().isBlank() ? null : tfDescription.getText());

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

            work.setStatus(tfStatus.getText());

            Deal selectedDeal = (Deal) cbDeal.getSelectedItem();
            if (selectedDeal != null) {
                work.setDeal(selectedDeal);
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

    private void saveWorkMerge() {
        try {
            if (currentWorkId == null) {
                JOptionPane.showMessageDialog(this, "Сначала выберите работу в таблице для редактирования!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Название обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Work existingWork = workService.findById(currentWorkId).orElse(null);
            
            if (existingWork == null) {
                JOptionPane.showMessageDialog(this, "Работа не найдена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Название обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            existingWork.setName(tfName.getText());
            existingWork.setWorkType(tfWorkType.getText());
            existingWork.setDescription(tfDescription.getText().isBlank() ? null : tfDescription.getText());

            if (!tfEstimatedCost.getText().isBlank()) {
                existingWork.setEstimatedCost(Double.parseDouble(tfEstimatedCost.getText()));
            } else {
                existingWork.setEstimatedCost(null);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            if (!tfStartDate.getText().isBlank()) {
                try {
                    existingWork.setStartDate(LocalDate.parse(tfStartDate.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты начала (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                existingWork.setStartDate(null);
            }

            if (!tfEndDate.getText().isBlank()) {
                try {
                    existingWork.setEndDate(LocalDate.parse(tfEndDate.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты окончания (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                existingWork.setEndDate(null);
            }

            if (!tfPrice.getText().isBlank()) {
                existingWork.setPrice(Double.parseDouble(tfPrice.getText()));
            } else {
                existingWork.setPrice(null);
            }

            existingWork.setPayment(tfPayment.getText());

            if (!tfPaymentDate.getText().isBlank()) {
                try {
                    existingWork.setPaymentDate(LocalDate.parse(tfPaymentDate.getText(), formatter));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты оплаты (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                existingWork.setPaymentDate(null);
            }

            existingWork.setStatus(tfStatus.getText());

            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient != null) {
                ObjectEntity selectedObject = (ObjectEntity) cbObject.getSelectedItem();
                if (selectedObject != null) {
                    existingWork.setObjectEntity(selectedObject);
                }
            }

            Deal selectedDeal = (Deal) cbDeal.getSelectedItem();
            if (selectedDeal != null) {
                existingWork.setDeal(selectedDeal);
            }

            workService.save(existingWork);
            currentWorkId = null;
            loadWorks();
            clearForm();
            JOptionPane.showMessageDialog(this, "Работа обновлена!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка обновления: " + e.getMessage());
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
        cbClient.setSelectedIndex(-1);
        cbObject.removeAllItems();
        tfName.setText("");
        tfWorkType.setText("");
        tfDescription.setText("");
        cbDetailedDescription.setSelected(false);
        tfEstimatedCost.setText("");
        tfStartDate.setText("");
        tfEndDate.setText("");
        tfPrice.setText("");
        tfPayment.setText("");
        tfPaymentDate.setText("");
        tfStatus.setText("");
        cbDeal.removeAllItems();
    }

    private void generateReport() {
        Client selectedClient = (Client) cbClient.getSelectedItem();
        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, "Выберите клиента!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ObjectEntity selectedObject = (ObjectEntity) cbObject.getSelectedItem();

        List<Work> works = workService.findAll();
        List<Work> filteredWorks = works.stream()
                .filter(w -> w.getObjectEntity() != null && w.getObjectEntity().getClient() != null
                        && w.getObjectEntity().getClient().getId().equals(selectedClient.getId()))
                .filter(w -> selectedObject == null || w.getObjectEntity() != null
                        && w.getObjectEntity().getId().equals(selectedObject.getId()))
                .toList();

        if (filteredWorks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Работы не найдены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String saveDir = System.getProperty("user.home") + "\\Documents\\Reports";
        String filePath = reportService.generateWorkReport(selectedClient, selectedObject, filteredWorks, saveDir);
        if (filePath != null) {
            try {
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(this, "Отчет успешно создан!", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }
}
