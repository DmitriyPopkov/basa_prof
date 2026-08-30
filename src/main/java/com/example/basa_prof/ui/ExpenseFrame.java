package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.Expense;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.DealService;
import com.example.basa_prof.service.ExpenseService;
import com.example.basa_prof.service.ObjectEntityService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExpenseFrame extends JFrame {

    private ExpenseService expenseService;
    private ClientService clientService;
    private ObjectEntityService objectService;
    private DealService dealService;
    private Client preselectedClient;
    private ObjectEntity preselectedObject;
    private Deal preselectedDeal;
    private boolean readOnly;
    private boolean fromDealFrame;

    private JTable expenseTable;
    private DefaultTableModel tableModel;

    private JSpinner spExpenseDate;
    private JTextField tfExpenseName, tfAmount, tfNotes;
    private JComboBox<Client> cbClient;
    private JComboBox<ObjectEntity> cbObject;
    private JComboBox<Deal> cbDeal;

    public ExpenseFrame(ExpenseService expenseService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService) {
        this(expenseService, clientService, objectService, dealService, null, null, false);
    }

    public ExpenseFrame(ExpenseService expenseService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        boolean readOnly) {
        this(expenseService, clientService, objectService, dealService, null, null, readOnly);
    }

    public ExpenseFrame(ExpenseService expenseService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Client preselectedClient) {
        this(expenseService, clientService, objectService, dealService, preselectedClient, null);
    }

    public ExpenseFrame(ExpenseService expenseService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Client preselectedClient, ObjectEntity preselectedObject) {
        this(expenseService, clientService, objectService, dealService, preselectedClient, preselectedObject, false);
    }

    public ExpenseFrame(ExpenseService expenseService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Client preselectedClient, ObjectEntity preselectedObject, boolean readOnly) {
        this.expenseService = expenseService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        this.preselectedClient = preselectedClient;
        this.preselectedObject = preselectedObject;
        this.readOnly = readOnly;
        initialize();
        loadExpenses();
        loadClients();

        // Автозаполнение из DealFrame
        if (preselectedClient != null) {
            cbClient.setSelectedItem(preselectedClient);
            loadObjectsForClient();
            loadDealsForClient();
            // Блокировка изменения клиент, объект и договор
            cbClient.setEnabled(false);
            cbObject.setEnabled(false);
            cbDeal.setEnabled(false);
        }
    }

    public ExpenseFrame(ExpenseService expenseService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Deal preselectedDeal) {
        this.expenseService = expenseService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        this.preselectedDeal = preselectedDeal;
        this.preselectedClient = preselectedDeal != null ? preselectedDeal.getClient() : null;
        this.preselectedObject = null;
        this.fromDealFrame = preselectedDeal != null;
        this.readOnly = false;
        initialize();
        loadExpenses();
        loadClients();

        // Автозаполнение из DealFrame
        if (preselectedDeal != null) {
            cbClient.setSelectedItem(preselectedDeal.getClient());
            loadObjectsForClient();
            loadDealsForClient();
            // Подставляем договор
            cbDeal.setSelectedItem(preselectedDeal);
            // Подставляем объект из deal.getObjects()
            if (preselectedDeal.getObjects() != null && !preselectedDeal.getObjects().isEmpty()) {
                cbObject.setSelectedItem(preselectedDeal.getObjects().get(0));
            }
            // Блокировка изменения клиент и договор
            cbClient.setEnabled(false);
            cbDeal.setEnabled(false);
        }
    }

    private void initialize() {
        setTitle("Управление расходами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(450);

        // Левая панель - форма ввода
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContent = new JPanel(new GridLayout(8, 1, 5, 5));
        formContent.setBorder(BorderFactory.createTitledBorder("Данные расхода"));

        // Дата расхода
        JPanel panelDate = new JPanel(new BorderLayout(5, 5));
        panelDate.add(new JLabel("Дата (дд.мм.гггг):"), BorderLayout.WEST);
        spExpenseDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spExpenseDate, "dd.MM.yyyy");
        spExpenseDate.setEditor(dateEditor);
        panelDate.add(spExpenseDate, BorderLayout.CENTER);
        formContent.add(panelDate);

        // Наименование расхода
        JPanel panelName = new JPanel(new BorderLayout(5, 5));
        panelName.add(new JLabel("Наименование расхода:"), BorderLayout.WEST);
        tfExpenseName = new JTextField();
        panelName.add(tfExpenseName, BorderLayout.CENTER);
        formContent.add(panelName);

        // Сумма
        JPanel panelAmount = new JPanel(new BorderLayout(5, 5));
        panelAmount.add(new JLabel("Сумма:"), BorderLayout.WEST);
        tfAmount = new JTextField();
        panelAmount.add(tfAmount, BorderLayout.CENTER);
        formContent.add(panelAmount);

        // Клиент
        JPanel panelClient = new JPanel(new BorderLayout(5, 5));
        panelClient.add(new JLabel("Клиент:"), BorderLayout.WEST);
        cbClient = new JComboBox<>();
        cbClient.addActionListener(e -> {
            loadObjectsForClient();
            loadDealsForClient();
        });
        panelClient.add(cbClient, BorderLayout.CENTER);
        formContent.add(panelClient);

        // Объект
        JPanel panelObject = new JPanel(new BorderLayout(5, 5));
        panelObject.add(new JLabel("Объект:"), BorderLayout.WEST);
        cbObject = new JComboBox<>();
        panelObject.add(cbObject, BorderLayout.CENTER);
        formContent.add(panelObject);

        // Договор
        JPanel panelDeal = new JPanel(new BorderLayout(5, 5));
        panelDeal.add(new JLabel("Договор:"), BorderLayout.WEST);
        cbDeal = new JComboBox<>();
        panelDeal.add(cbDeal, BorderLayout.CENTER);
        formContent.add(panelDeal);

        // Примечания (многострочное)
        JPanel panelNotes = new JPanel(new BorderLayout(5, 5));
        panelNotes.add(new JLabel("Примечания:"), BorderLayout.NORTH);
        tfNotes = new JTextField();
        JScrollPane notesScroll = new JScrollPane(tfNotes);
        panelNotes.add(notesScroll, BorderLayout.CENTER);
        formContent.add(panelNotes);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnRefresh = new JButton("Обновить");
        btnRefresh.setVisible(!readOnly);

        btnSave.setEnabled(!readOnly);
        btnDelete.setEnabled(!readOnly);
        btnSave.addActionListener(e -> saveExpense());
        btnDelete.addActionListener(e -> deleteExpense());
        btnRefresh.addActionListener(e -> loadExpenses());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // Блокировка полей формы в режиме только для чтения
        if (readOnly) {
            spExpenseDate.setEnabled(false);
            tfExpenseName.setEnabled(false);
            tfAmount.setEnabled(false);
            cbClient.setEnabled(false);
            cbObject.setEnabled(false);
            cbDeal.setEnabled(false);
            tfNotes.setEnabled(false);
        }

        formPanel.add(formContent, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Правая панель - таблица
        String[] columns = {"ID", "Дата", "Наименование", "Сумма", "Клиент", "Объект", "Договор", "Примечания"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel) {
            @Override
            public void removeRowSelectionInterval(int index0, int index1) {
                if (hasFocus()) {
                    super.removeRowSelectionInterval(index0, index1);
                }
            }
        };
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.setRowHeight(25);
        expenseTable.getTableHeader().setReorderingAllowed(false);

        // Кастомный рендерер для подсветки выделения
        expenseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        expenseTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = expenseTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    expenseTable.setRowSelectionInterval(row, row);
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

        JScrollPane tableScroll = new JScrollPane(expenseTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Список расходов"));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        if (readOnly) {
            setContentPane(tablePanel);
        } else {
            mainSplit.setLeftComponent(formPanel);
            mainSplit.setRightComponent(tablePanel);
            setContentPane(mainSplit);
        }
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);
        List<Expense> expenses;

        // Фильтрация по клиенту и договору из DealFrame
        if (fromDealFrame && preselectedDeal != null) {
            expenses = expenseService.findByClientIdAndDealId(preselectedDeal.getClient().getId(), preselectedDeal.getId());
        } else if (preselectedClient != null) {
            if (preselectedObject != null) {
                expenses = expenseService.findByClientIdAndObjectId(preselectedClient.getId(), preselectedObject.getId());
            } else {
                expenses = expenseService.findByClientId(preselectedClient.getId());
            }
        } else {
            expenses = expenseService.findAll();
        }

        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                    expense.getId(),
                    expense.getExpenseDate() != null ? expense.getExpenseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                    expense.getExpenseName() != null ? expense.getExpenseName() : "",
                    expense.getAmount() != null ? expense.getAmount().toPlainString() : "",
                    expense.getObjectEntity() != null && expense.getObjectEntity().getClient() != null
                            ? expense.getObjectEntity().getClient().getFullName() : "—",
                    expense.getObjectEntity() != null ? expense.getObjectEntity().getName() : "—",
                    expense.getDeal() != null ? expense.getDeal().getContractNumber() : "—",
                    expense.getNotes() != null ? expense.getNotes() : ""
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
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Expense expense = expenseService.findById(id).orElse(null);
            if (expense != null) {
                if (expense.getExpenseDate() != null) {
                    Date date = Date.from(expense.getExpenseDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    spExpenseDate.setValue(date);
                }

                tfExpenseName.setText(expense.getExpenseName() != null ? expense.getExpenseName() : "");
                tfAmount.setText(expense.getAmount() != null ? expense.getAmount().toPlainString() : "");
                tfNotes.setText(expense.getNotes() != null ? expense.getNotes() : "");

                if (expense.getObjectEntity() != null && expense.getObjectEntity().getClient() != null) {
                    cbClient.setSelectedItem(expense.getObjectEntity().getClient());
                    loadObjectsForClient();
                    loadDealsForClient();
                    cbObject.setSelectedItem(expense.getObjectEntity());
                }

                if (expense.getDeal() != null && expense.getDeal().getClient() != null) {
                    cbClient.setSelectedItem(expense.getDeal().getClient());
                    loadDealsForClient();
                    cbDeal.setSelectedItem(expense.getDeal());
                }
            }
        }
    }

    private void saveExpense() {
        try {
            if (tfExpenseName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Наименование расхода обязат��льно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tfAmount.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Сумма обязательна!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = expenseTable.getSelectedRow();
            Expense expense;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                expense = expenseService.findById(id).orElse(new Expense());
            } else {
                expense = new Expense();
            }

            if (spExpenseDate.getValue() instanceof Date) {
                Date spinnerDate = (Date) spExpenseDate.getValue();
                LocalDate localDate = spinnerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                expense.setExpenseDate(localDate);
            }

            expense.setExpenseName(tfExpenseName.getText());

            try {
                expense.setAmount(new BigDecimal(tfAmount.getText()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Неверный формат суммы!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient != null) {
                ObjectEntity selectedObject = (ObjectEntity) cbObject.getSelectedItem();
                if (selectedObject != null) {
                    expense.setObjectEntity(selectedObject);
                }

                Deal selectedDeal = (Deal) cbDeal.getSelectedItem();
                if (selectedDeal != null) {
                    expense.setDeal(selectedDeal);
                }
            }

            expense.setNotes(tfNotes.getText().isBlank() ? null : tfNotes.getText());

            expenseService.save(expense);
            loadExpenses();
            clearForm();
            JOptionPane.showMessageDialog(this, "Расход сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить расход №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                expenseService.deleteById(id);
                loadExpenses();
                clearForm();
                JOptionPane.showMessageDialog(this, "Расход удалён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        spExpenseDate.setValue(new Date());
        cbClient.setSelectedIndex(-1);
        cbObject.removeAllItems();
        cbDeal.removeAllItems();
        tfExpenseName.setText("");
        tfAmount.setText("");
        tfNotes.setText("");
    }
}
