package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.Employee;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.WorkHour;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.DealService;
import com.example.basa_prof.service.EmployeeService;
import com.example.basa_prof.service.ObjectEntityService;
import com.example.basa_prof.service.WorkHourService;
import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerNumberModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class WorkHourFrame extends JFrame {

    private WorkHourService workHourService;
    private EmployeeService employeeService;
    private ClientService clientService;
    private ObjectEntityService objectService;
    private DealService dealService;

    private JTable workHourTable;
    private DefaultTableModel tableModel;

    private JTextField tfWorkDate, tfHours, tfHourlyRate;
    private JComboBox<Employee> cbEmployee;
    private JComboBox<Client> cbClient;
    private JComboBox<ObjectEntity> cbObject;
    private JComboBox<Deal> cbDeal;
    private JComboBox<String> cbFilterType;
    private JSpinner spYear;
    private JSpinner spMonth;

    public WorkHourFrame(WorkHourService workHourService, EmployeeService employeeService,
                         ClientService clientService, ObjectEntityService objectService,
                         DealService dealService) {
        this.workHourService = workHourService;
        this.employeeService = employeeService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        initialize();
        loadWorkHours();
        loadEmployees();
        loadClients();
    }

    private void initialize() {
        setTitle("Учет рабочего времени");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма ввода
        JPanel formPanel = new JPanel(new GridLayout(11, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Данные учета рабочего времени"));

        tfWorkDate = new JTextField();
        tfHours = new JTextField();
        tfHourlyRate = new JTextField();
        cbEmployee = new JComboBox<>();
        cbClient = new JComboBox<>();
        cbObject = new JComboBox<>();
        cbDeal = new JComboBox<>();

        // Фильтры для табеля
        cbFilterType = new JComboBox<>(new String[]{
                "Без фильтра", "По клиенту", "По договору", "По месяцу", "По году"
        });
        spYear = new JSpinner(new SpinnerNumberModel(java.time.Year.now().getValue(), 2000, 2100, 1));
        spMonth = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));

        formPanel.add(new JLabel("Фильтр табеля:"));
        formPanel.add(cbFilterType);

        formPanel.add(new JLabel("Сотрудник:"));
        formPanel.add(cbEmployee);

        formPanel.add(new JLabel("Дата работы (дд.мм.гггг):"));
        formPanel.add(tfWorkDate);

        formPanel.add(new JLabel("Количество часов:"));
        formPanel.add(tfHours);

        formPanel.add(new JLabel("Стоимость в час:"));
        formPanel.add(tfHourlyRate);

        formPanel.add(new JLabel("Клиент:"));
        formPanel.add(cbClient);

        formPanel.add(new JLabel("Объект:"));
        formPanel.add(cbObject);

        formPanel.add(new JLabel("Договор:"));
        formPanel.add(cbDeal);

        formPanel.add(new JLabel("Год:"));
        formPanel.add(spYear);

        formPanel.add(new JLabel("Месяц:"));
        formPanel.add(spMonth);

        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        cbClient.addActionListener(e -> {
            loadObjectsForClient();
            loadDealsForClient();
        });

        cbEmployee.addActionListener(e -> onEmployeeChanged());

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");
        JButton btnTimesheet = new JButton("Вывести табель");

        btnSave.addActionListener(e -> saveWorkHour());
        btnDelete.addActionListener(e -> deleteWorkHour());
        btnRefresh.addActionListener(e -> loadWorkHours());
        btnTimesheet.addActionListener(e -> generateTimesheet());

        cbFilterType.addActionListener(e -> loadWorkHoursForEmployee());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnTimesheet);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Таблица
        String[] columns = {"ID", "Сотрудник", "Дата", "Часы", "Ставка", "Клиент", "Объект", "Договор"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        workHourTable = new JTable(tableModel) {
            @Override
            public void removeRowSelectionInterval(int index0, int index1) {
                if (hasFocus()) {
                    super.removeRowSelectionInterval(index0, index1);
                }
            }
        };
        workHourTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workHourTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = workHourTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    workHourTable.setRowSelectionInterval(row, row);
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

        JScrollPane scrollPane = new JScrollPane(workHourTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Список учета рабочего времени"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadWorkHours() {
        tableModel.setRowCount(0);
        List<WorkHour> workHours = workHourService.findAll();
        for (WorkHour wh : workHours) {
            tableModel.addRow(new Object[]{
                    wh.getId(),
                    wh.getEmployee() != null ? wh.getEmployee().getFullName() : "—",
                    wh.getWorkDate() != null ? wh.getWorkDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "—",
                    wh.getHours() != null ? wh.getHours().toString() : "—",
                    wh.getHourlyRate() != null ? wh.getHourlyRate().toString() : "—",
                    wh.getObjectEntity() != null && wh.getObjectEntity().getClient() != null
                            ? wh.getObjectEntity().getClient().getFullName() : "—",
                    wh.getObjectEntity() != null ? wh.getObjectEntity().getName() : "—",
                    wh.getDeal() != null ? wh.getDeal().getContractNumber() : "—"
            });
        }
    }

    private void loadEmployees() {
        cbEmployee.removeAllItems();
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            cbEmployee.addItem(employee);
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
        int selectedRow = workHourTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            WorkHour workHour = workHourService.findById(id).orElse(null);
            if (workHour != null) {
                if (workHour.getEmployee() != null) {
                    cbEmployee.setSelectedItem(workHour.getEmployee());
                }

                if (workHour.getWorkDate() != null) {
                    tfWorkDate.setText(workHour.getWorkDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }

                if (workHour.getHours() != null) {
                    tfHours.setText(workHour.getHours().toString());
                }

                if (workHour.getHourlyRate() != null) {
                    tfHourlyRate.setText(workHour.getHourlyRate().toString());
                }

                if (workHour.getObjectEntity() != null && workHour.getObjectEntity().getClient() != null) {
                    cbClient.setSelectedItem(workHour.getObjectEntity().getClient());
                    loadObjectsForClient();
                    loadDealsForClient();
                    cbObject.setSelectedItem(workHour.getObjectEntity());
                }

                if (workHour.getDeal() != null && workHour.getDeal().getClient() != null) {
                    cbClient.setSelectedItem(workHour.getDeal().getClient());
                    loadDealsForClient();
                    cbDeal.setSelectedItem(workHour.getDeal());
                }
            }
        }
    }

    private List<WorkHour> filterWorkHours() {
        Employee selectedEmployee = (Employee) cbEmployee.getSelectedItem();
        if (selectedEmployee == null) {
            return Collections.emptyList();
        }

        String filterType = (String) cbFilterType.getSelectedItem();
        List<WorkHour> allWorkHours = workHourService.findAll();

        // Фильтр по сотруднику
        List<WorkHour> employeeWorkHours = allWorkHours.stream()
                .filter(wh -> wh.getEmployee() != null && wh.getEmployee().getId().equals(selectedEmployee.getId()))
                .toList();

        switch (filterType) {
            case "Без фильтра":
                return employeeWorkHours;

            case "По клиенту":
                Client selectedClient = (Client) cbClient.getSelectedItem();
                if (selectedClient == null) {
                    return employeeWorkHours;
                }
                return employeeWorkHours.stream()
                        .filter(wh -> wh.getObjectEntity() != null && wh.getObjectEntity().getClient() != null
                                && wh.getObjectEntity().getClient().getId().equals(selectedClient.getId()))
                        .toList();

            case "По договору":
                Deal selectedDeal = (Deal) cbDeal.getSelectedItem();
                if (selectedDeal == null) {
                    return employeeWorkHours;
                }
                return employeeWorkHours.stream()
                        .filter(wh -> wh.getDeal() != null && wh.getDeal().getId().equals(selectedDeal.getId()))
                        .toList();

            case "По месяцу":
                int year = (Integer) spYear.getValue();
                int month = (Integer) spMonth.getValue();
                return employeeWorkHours.stream()
                        .filter(wh -> wh.getWorkDate() != null
                                && wh.getWorkDate().getYear() == year
                                && wh.getWorkDate().getMonthValue() == month)
                        .toList();

            case "По году":
                int selectedYear = (Integer) spYear.getValue();
                return employeeWorkHours.stream()
                        .filter(wh -> wh.getWorkDate() != null
                                && wh.getWorkDate().getYear() == selectedYear)
                        .toList();

            default:
                return employeeWorkHours;
        }
    }

    private void generateTimesheet() {
        Employee selectedEmployee = (Employee) cbEmployee.getSelectedItem();
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Выберите сотрудника!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<WorkHour> filteredWorkHours = filterWorkHours();

        if (filteredWorkHours.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Записи учета рабочего времени не найдены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            generateTimesheetDocument(selectedEmployee, filteredWorkHours);
            JOptionPane.showMessageDialog(this, "Табель успешно создан!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка создания табеля: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generateTimesheetDocument(Employee employee, List<WorkHour> workHours) {
        try (XWPFDocument document = new XWPFDocument()) {

            // Заголовок
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setText("ТАБЕЛЬ УЧЕТА РАБОЧЕГО ВРЕМЕНИ");

            XWPFParagraph subtitle = document.createParagraph();
            subtitle.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun subtitleRun = subtitle.createRun();
            subtitleRun.setFontSize(12);
            subtitleRun.setText("Фамилия И.О.: " + employee.getFullName());

            XWPFParagraph positionPara = document.createParagraph();
            positionPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun positionRun = positionPara.createRun();
            positionRun.setText("Должность: " + (employee.getPosition() != null ? employee.getPosition() : "—"));

            // Таблица табеля
            List<String> dates = workHours.stream()
                    .map(WorkHour::getWorkDate)
                    .filter(d -> d != null)
                    .distinct()
                    .sorted()
                    .map(d -> d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .toList();

            // 4 фиксированные колонки (Сотрудник, Клиент, Объект, Договор) + даты + 3 итого (Часы, Ставка, Сумма)
            int dataColumns = dates.size() + 3; // Часы + Ставка + Сумма
            int totalColumns = 4 + dataColumns; // Сотрудник + Клиент + Объект + Договор + даты + Часы + Ставка + Сумма

            XWPFTable table = document.createTable(1 + workHours.size() + 1, totalColumns);

            // Заголовки
            XWPFTableRow headerRow = table.getRow(0);
            setCellText(headerRow.getCell(0), "Сотрудник", true, true);
            setCellText(headerRow.getCell(1), "Клиент", true, true);
            setCellText(headerRow.getCell(2), "Объект", true, true);
            setCellText(headerRow.getCell(3), "Договор", true, true);

            for (int i = 0; i < dates.size(); i++) {
                setCellText(headerRow.getCell(i + 4), dates.get(i), true, true);
            }

            setCellText(headerRow.getCell(dates.size() + 4), "Часы", true, true);
            setCellText(headerRow.getCell(dates.size() + 5), "Ставка", true, true);
            setCellText(headerRow.getCell(dates.size() + 6), "Сумма", true, true);

            // Данные
            double totalHours = 0;
            double totalAmount = 0;

            for (int i = 0; i < workHours.size(); i++) {
                WorkHour wh = workHours.get(i);

                XWPFTableRow row = table.getRow(i + 1);
                if (row == null) break;

                // Сотрудник, Клиент, Объект, Договор
                setCellText(row.getCell(0), wh.getEmployee() != null ? wh.getEmployee().getFullName() : "—", false, false);

                String clientName = "—";
                String objectName = "—";
                String dealName = "—";

                if (wh.getObjectEntity() != null) {
                    objectName = wh.getObjectEntity().getName();
                    if (wh.getObjectEntity().getClient() != null) {
                        clientName = wh.getObjectEntity().getClient().getFullName();
                    }
                }

                if (wh.getDeal() != null) {
                    dealName = wh.getDeal().getContractNumber();
                }

                setCellText(row.getCell(1), clientName, false, false);
                setCellText(row.getCell(2), objectName, false, false);
                setCellText(row.getCell(3), dealName, false, false);

                // Заполняем даты
                for (int d = 0; d < dates.size(); d++) {
                    String cellValue = "";
                    if (wh.getWorkDate() != null && wh.getWorkDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).equals(dates.get(d))) {
                        cellValue = wh.getHours() != null ? wh.getHours().toString() : "0";
                    }
                    XWPFTableCell cell = row.getCell(d + 4);
                    if (cell != null) {
                        setCellText(cell, cellValue, false, true);
                    }
                }

                double hours = wh.getHours() != null ? wh.getHours() : 0;
                double rate = wh.getHourlyRate() != null ? wh.getHourlyRate() : 0;
                double amount = hours * rate;

                totalHours += hours;
                totalAmount += amount;

                XWPFTableCell hoursCell = row.getCell(dates.size() + 4);
                XWPFTableCell rateCell = row.getCell(dates.size() + 5);
                XWPFTableCell amountCell = row.getCell(dates.size() + 6);

                if (hoursCell != null) setCellText(hoursCell, String.valueOf(hours), false, true);
                if (rateCell != null) setCellText(rateCell, String.valueOf(rate), false, true);
                if (amountCell != null) setCellText(amountCell, String.valueOf(amount), false, true);
            }

            // Итого
            XWPFTableRow totalRow = table.getRow(workHours.size() + 1);
            if (totalRow != null) {
                // Объединяем первые 4 ячейки для "Итого"
                setCellText(totalRow.getCell(0), "ИТОГО", true, true);
                setCellText(totalRow.getCell(1), "—", true, true);
                setCellText(totalRow.getCell(2), "—", true, true);
                setCellText(totalRow.getCell(3), "—", true, true);

                XWPFTableCell totalHoursCell = totalRow.getCell(dates.size() + 4);
                XWPFTableCell totalAmountCell = totalRow.getCell(dates.size() + 6);

                if (totalHoursCell != null) setCellText(totalHoursCell, String.valueOf(totalHours), true, true);
                if (totalAmountCell != null) setCellText(totalAmountCell, String.valueOf(totalAmount), true, true);
            }

            // Сохранение
            DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = java.time.LocalDateTime.now().format(timestampFormatter);
            String safeName = employee.getFullName().replaceAll("[\\\\/:*?\"<>|]", "_");
            String fileName = "Табель_" + safeName + "_" + timestamp + ".docx";

            String saveDir = System.getProperty("user.home") + "\\Documents\\Reports";
            Path dir = Paths.get(saveDir);
            if (!java.nio.file.Files.exists(dir)) {
                java.nio.file.Files.createDirectories(dir);
            }

            Path filePath = dir.resolve(fileName);
            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                document.write(out);
            }

            System.out.println("Табель успешно создан: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Ошибка создания табеля: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold, boolean center) {
        if (cell == null) return;
        cell.removeParagraph(0);
        XWPFParagraph para = cell.addParagraph();
        if (center) {
            para.setAlignment(ParagraphAlignment.CENTER);
        }
        XWPFRun run = para.createRun();
        run.setBold(bold);
        run.setText(text);
    }


    private void saveWorkHour() {
        try {
            if (tfWorkDate.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Дата обязательна!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tfHours.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Количество часов обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tfHourlyRate.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Стоимость в час обязательна!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Employee selectedEmployee = (Employee) cbEmployee.getSelectedItem();
            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Выберите сотрудника!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = workHourTable.getSelectedRow();
            WorkHour workHour;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                workHour = workHourService.findById(id).orElse(new WorkHour());
            } else {
                workHour = new WorkHour();
            }

            workHour.setEmployee(selectedEmployee);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            try {
                workHour.setWorkDate(LocalDate.parse(tfWorkDate.getText(), formatter));
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Неверный формат даты (дд.мм.гггг)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            workHour.setHours(Double.parseDouble(tfHours.getText()));
            workHour.setHourlyRate(Double.parseDouble(tfHourlyRate.getText()));

            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient != null) {
                ObjectEntity selectedObject = (ObjectEntity) cbObject.getSelectedItem();
                if (selectedObject != null) {
                    workHour.setObjectEntity(selectedObject);
                }
            }

            Deal selectedDeal = (Deal) cbDeal.getSelectedItem();
            if (selectedDeal != null) {
                workHour.setDeal(selectedDeal);
            }

            workHourService.save(workHour);
            loadWorkHours();
            clearForm();
            JOptionPane.showMessageDialog(this, "Запись сохранена!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteWorkHour() {
        int selectedRow = workHourTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить запись №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                workHourService.deleteById(id);
                loadWorkHours();
                clearForm();
                JOptionPane.showMessageDialog(this, "Запись удалена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        cbEmployee.setSelectedIndex(-1);
        cbClient.setSelectedIndex(-1);
        cbObject.removeAllItems();
        cbDeal.removeAllItems();
        tableModel.setRowCount(0);
        tfWorkDate.setText("");
        tfHours.setText("");
        tfHourlyRate.setText("");
    }

    private void onEmployeeChanged() {
        tableModel.setRowCount(0);
        tfWorkDate.setText("");
        tfHours.setText("");
        tfHourlyRate.setText("");

        Employee selectedEmployee = (Employee) cbEmployee.getSelectedItem();
        if (selectedEmployee == null) {
            loadWorkHours();
            return;
        }

        loadWorkHoursForEmployee();
    }

    private void loadWorkHoursForEmployee() {
        List<WorkHour> filteredWorkHours = filterWorkHours();
        for (WorkHour wh : filteredWorkHours) {
            tableModel.addRow(new Object[]{
                    wh.getId(),
                    wh.getEmployee() != null ? wh.getEmployee().getFullName() : "—",
                    wh.getWorkDate() != null ? wh.getWorkDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "—",
                    wh.getHours() != null ? wh.getHours().toString() : "—",
                    wh.getHourlyRate() != null ? wh.getHourlyRate().toString() : "—",
                    wh.getObjectEntity() != null && wh.getObjectEntity().getClient() != null
                            ? wh.getObjectEntity().getClient().getFullName() : "—",
                    wh.getObjectEntity() != null ? wh.getObjectEntity().getName() : "—",
                    wh.getDeal() != null ? wh.getDeal().getContractNumber() : "—"
            });
        }
    }
}
