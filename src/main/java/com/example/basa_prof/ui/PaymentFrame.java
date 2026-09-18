package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.Payment;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.DealService;
import com.example.basa_prof.service.ObjectEntityService;
import com.example.basa_prof.service.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class PaymentFrame extends JFrame {

    private PaymentService paymentService;
    private ClientService clientService;
    private ObjectEntityService objectService;
    private DealService dealService;
    private Client preselectedClient;
    private ObjectEntity preselectedObject;
    private Deal preselectedDeal;
    private boolean readOnly;
    private boolean fromDealFrame;

    private JTable paymentTable;
    private DefaultTableModel tableModel;

    private JSpinner spPaymentDate;
    private JTextField tfPaymentName, tfAmount, tfNotes;
    private JComboBox<Client> cbClient;
    private JComboBox<ObjectEntity> cbObject;
    private JComboBox<Deal> cbDeal;

    public PaymentFrame(PaymentService paymentService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService) {
        this(paymentService, clientService, objectService, dealService, null, null, false);
    }

    public PaymentFrame(PaymentService paymentService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        boolean readOnly) {
        this(paymentService, clientService, objectService, dealService, null, null, readOnly);
    }

    public PaymentFrame(PaymentService paymentService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Client preselectedClient) {
        this(paymentService, clientService, objectService, dealService, preselectedClient, null);
    }

    public PaymentFrame(PaymentService paymentService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Client preselectedClient, ObjectEntity preselectedObject) {
        this(paymentService, clientService, objectService, dealService, preselectedClient, preselectedObject, false);
    }

    public PaymentFrame(PaymentService paymentService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Client preselectedClient, ObjectEntity preselectedObject, boolean readOnly) {
        this.paymentService = paymentService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        this.preselectedClient = preselectedClient;
        this.preselectedObject = preselectedObject;
        this.readOnly = readOnly;
        initialize();
        loadPayments();
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

    public PaymentFrame(PaymentService paymentService, ClientService clientService,
                        ObjectEntityService objectService, DealService dealService,
                        Deal preselectedDeal) {
        this.paymentService = paymentService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        this.preselectedDeal = preselectedDeal;
        this.preselectedClient = preselectedDeal != null ? preselectedDeal.getClient() : null;
        this.preselectedObject = null;
        this.fromDealFrame = preselectedDeal != null;
        this.readOnly = false;
        initialize();
        loadPayments();
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
        setTitle("Управление оплатами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(450);

        // Левая панель - форма ввода
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContent = new JPanel(new GridLayout(8, 1, 5, 5));
        formContent.setBorder(BorderFactory.createTitledBorder("Данные оплаты"));

        // Дата оплаты
        JPanel panelDate = new JPanel(new BorderLayout(5, 5));
        panelDate.add(new JLabel("Дата (дд.мм.гггг):"), BorderLayout.WEST);
        spPaymentDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spPaymentDate, "dd.MM.yyyy");
        spPaymentDate.setEditor(dateEditor);
        panelDate.add(spPaymentDate, BorderLayout.CENTER);
        formContent.add(panelDate);

        // Наименование оплаты
        JPanel panelName = new JPanel(new BorderLayout(5, 5));
        panelName.add(new JLabel("Наименование оплаты:"), BorderLayout.WEST);
        tfPaymentName = new JTextField();
        panelName.add(tfPaymentName, BorderLayout.CENTER);
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
        btnSave.addActionListener(e -> savePayment());
        btnDelete.addActionListener(e -> deletePayment());
        btnRefresh.addActionListener(e -> loadPayments());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // Блокировка полей формы в режиме только для чтения
        if (readOnly) {
            spPaymentDate.setEnabled(false);
            tfPaymentName.setEnabled(false);
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

        paymentTable = new JTable(tableModel) {
            @Override
            public void removeRowSelectionInterval(int index0, int index1) {
                if (hasFocus()) {
                    super.removeRowSelectionInterval(index0, index1);
                }
            }
        };
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.setRowHeight(25);
        paymentTable.getTableHeader().setReorderingAllowed(false);

        // Кастомный рендерер для подсветки выделения
        paymentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        paymentTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = paymentTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    paymentTable.setRowSelectionInterval(row, row);
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

        JScrollPane tableScroll = new JScrollPane(paymentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Список оплат"));

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

    private void loadPayments() {
        tableModel.setRowCount(0);
        List<Payment> payments;

        // Фильтрация по клиенту и договору из DealFrame
        if (fromDealFrame && preselectedDeal != null) {
            payments = paymentService.findByClientIdAndDealId(preselectedDeal.getClient().getId(), preselectedDeal.getId());
        } else if (preselectedClient != null) {
            if (preselectedObject != null) {
                payments = paymentService.findByClientIdAndObjectId(preselectedClient.getId(), preselectedObject.getId());
            } else {
                payments = paymentService.findByClientId(preselectedClient.getId());
            }
        } else {
            payments = paymentService.findAll();
        }

        for (Payment payment : payments) {
            tableModel.addRow(new Object[]{
                    payment.getId(),
                    payment.getPaymentDate() != null ? payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                    payment.getPaymentName() != null ? payment.getPaymentName() : "",
                    payment.getAmount() != null ? payment.getAmount().toPlainString() : "",
                    payment.getObjectEntity() != null && payment.getObjectEntity().getClient() != null
                            ? payment.getObjectEntity().getClient().getFullName() : "—",
                    payment.getObjectEntity() != null ? payment.getObjectEntity().getName() : "—",
                    payment.getDeal() != null ? payment.getDeal().getContractNumber() : "—",
                    payment.getNotes() != null ? payment.getNotes() : ""
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
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Payment payment = paymentService.findById(id).orElse(null);
            if (payment != null) {
                if (payment.getPaymentDate() != null) {
                    Date date = Date.from(payment.getPaymentDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    spPaymentDate.setValue(date);
                }

                tfPaymentName.setText(payment.getPaymentName() != null ? payment.getPaymentName() : "");
                tfAmount.setText(payment.getAmount() != null ? payment.getAmount().toPlainString() : "");
                tfNotes.setText(payment.getNotes() != null ? payment.getNotes() : "");

                if (payment.getObjectEntity() != null && payment.getObjectEntity().getClient() != null) {
                    cbClient.setSelectedItem(payment.getObjectEntity().getClient());
                    loadObjectsForClient();
                    loadDealsForClient();
                    cbObject.setSelectedItem(payment.getObjectEntity());
                }

                if (payment.getDeal() != null && payment.getDeal().getClient() != null) {
                    cbClient.setSelectedItem(payment.getDeal().getClient());
                    loadDealsForClient();
                    cbDeal.setSelectedItem(payment.getDeal());
                }
            }
        }
    }

    private void savePayment() {
        try {
            if (tfPaymentName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Наименование оплаты обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tfAmount.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Сумма обязательна!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = paymentTable.getSelectedRow();
            Payment payment;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                payment = paymentService.findById(id).orElse(new Payment());
            } else {
                payment = new Payment();
            }

            if (spPaymentDate.getValue() instanceof Date) {
                Date spinnerDate = (Date) spPaymentDate.getValue();
                LocalDate localDate = spinnerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                payment.setPaymentDate(localDate);
            }

            payment.setPaymentName(tfPaymentName.getText());

            try {
                payment.setAmount(new BigDecimal(tfAmount.getText()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Неверный формат суммы!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient != null) {
                ObjectEntity selectedObject = (ObjectEntity) cbObject.getSelectedItem();
                if (selectedObject != null) {
                    payment.setObjectEntity(selectedObject);
                }

                Deal selectedDeal = (Deal) cbDeal.getSelectedItem();
                if (selectedDeal != null) {
                    payment.setDeal(selectedDeal);
                }
            }

            payment.setNotes(tfNotes.getText().isBlank() ? null : tfNotes.getText());

            paymentService.save(payment);
            loadPayments();
            clearForm();
            JOptionPane.showMessageDialog(this, "Оплата сохранена!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deletePayment() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить оплату №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                paymentService.deleteById(id);
                loadPayments();
                clearForm();
                JOptionPane.showMessageDialog(this, "Оплата удалена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        spPaymentDate.setValue(new Date());
        cbClient.setSelectedIndex(-1);
        cbObject.removeAllItems();
        cbDeal.removeAllItems();
        tfPaymentName.setText("");
        tfAmount.setText("");
        tfNotes.setText("");
    }
}
