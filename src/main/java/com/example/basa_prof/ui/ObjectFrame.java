package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.Work;
import com.example.basa_prof.entity.Material;
import com.example.basa_prof.service.ClientService;
import com.example.basa_prof.service.ObjectEntityService;
import com.example.basa_prof.service.WorkService;
import com.example.basa_prof.service.MaterialService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ObjectFrame extends JFrame {

    private ClientService clientService;
    private ObjectEntityService objectService;
    private WorkService workService;
    private MaterialService materialService;

    private JComboBox<Client> cbClient;
    private JTable objectTable;
    private DefaultTableModel tableModel;

    private JTextField tfName, tfObjectType, tfAddress, tfArea, tfPrice, tfDescription;
    private JTextField tfStartDate, tfEndDate, tfDocuments;
    private JTextField tfClientFullName, tfClientPhone, tfClientEmail, tfClientAddress, tfClientContact, tfClientJob;

    private ObjectEntity selectedObject;

    public ObjectFrame(ClientService clientService, ObjectEntityService objectService,
                       WorkService workService, MaterialService materialService) {
        this.clientService = clientService;
        this.objectService = objectService;
        this.workService = workService;
        this.materialService = materialService;
        initialize();
        loadClients();
    }



    private void initialize() {
        setTitle("Управление объектами клиентов");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setDividerLocation(300);

        // Верхняя панель - выбор клиента
        JPanel clientPanel = new JPanel(new BorderLayout(10, 10));
        clientPanel.setBorder(BorderFactory.createTitledBorder("Выберите клиента"));
        clientPanel.setPreferredSize(new Dimension(1200, 250));

        JPanel clientSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientSelectPanel.add(new JLabel("Клиент:"));
        cbClient = new JComboBox<>();
        cbClient.addActionListener(e -> loadClientObjects());
        clientSelectPanel.add(cbClient);

        JButton btnRefreshClients = new JButton("Обновить список");
        btnRefreshClients.addActionListener(e -> loadClients());
        clientSelectPanel.add(btnRefreshClients);

        clientPanel.add(clientSelectPanel, BorderLayout.NORTH);

        // Информация о клиенте
        JPanel clientInfoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        clientInfoPanel.setBorder(BorderFactory.createTitledBorder("Информация о клиенте"));
        clientInfoPanel.setPreferredSize(new Dimension(1200, 180));

        JLabel lblFullName = new JLabel("ФИО:");
        tfClientFullName = new JTextField();
        tfClientFullName.setEditable(false);
        tfClientPhone = new JTextField();
        tfClientPhone.setEditable(false);
        tfClientEmail = new JTextField();
        tfClientEmail.setEditable(false);
        tfClientAddress = new JTextField();
        tfClientAddress.setEditable(false);
        tfClientContact = new JTextField();
        tfClientContact.setEditable(false);
        tfClientJob = new JTextField();
        tfClientJob.setEditable(false);

        clientInfoPanel.add(lblFullName);
        clientInfoPanel.add(tfClientFullName);
        clientInfoPanel.add(new JLabel("Телефон:"));
        clientInfoPanel.add(tfClientPhone);
        clientInfoPanel.add(new JLabel("Email:"));
        clientInfoPanel.add(tfClientEmail);
        clientInfoPanel.add(new JLabel("Адрес:"));
        clientInfoPanel.add(tfClientAddress);
        clientInfoPanel.add(new JLabel("Контактное лицо:"));
        clientInfoPanel.add(tfClientContact);
        clientInfoPanel.add(new JLabel("Должность:"));
        clientInfoPanel.add(tfClientJob);

        clientPanel.add(clientInfoPanel, BorderLayout.CENTER);

        // Нижняя панель - объекты
        JPanel objectPanel = new JPanel(new BorderLayout(10, 10));
        objectPanel.setBorder(BorderFactory.createTitledBorder("Объекты клиента"));

        // Форма объекта
        JPanel objectForm = new JPanel(new GridLayout(9, 2, 5, 5));
        objectForm.setPreferredSize(new Dimension(1200, 200));

        tfName = new JTextField();
        tfObjectType = new JTextField();
        tfAddress = new JTextField();
        tfArea = new JTextField();
        tfPrice = new JTextField();
        tfDescription = new JTextField();
        tfStartDate = new JTextField();
        tfEndDate = new JTextField();
        tfDocuments = new JTextField();

        objectForm.add(new JLabel("Название:"));
        objectForm.add(tfName);
        objectForm.add(new JLabel("Тип:"));
        objectForm.add(tfObjectType);
        objectForm.add(new JLabel("Адрес:"));
        objectForm.add(tfAddress);
        objectForm.add(new JLabel("Площадь:"));
        objectForm.add(tfArea);
        objectForm.add(new JLabel("Цена:"));
        objectForm.add(tfPrice);
        objectForm.add(new JLabel("Описание:"));
        objectForm.add(tfDescription);
        objectForm.add(new JLabel("Дата начала (дд.мм.гггг чч:мм):"));
        objectForm.add(tfStartDate);
        objectForm.add(new JLabel("Дата окончания (дд.мм.гггг чч:мм):"));
        objectForm.add(tfEndDate);
        objectForm.add(new JLabel("Документы:"));
        objectForm.add(tfDocuments);

        // Кнопки для объекта
        JPanel objectButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSaveObject = new JButton("Сохранить объект");
        JButton btnDeleteObject = new JButton("Удалить объект");
        JButton btnRefreshObjects = new JButton("Обновить");

        btnSaveObject.addActionListener(e -> saveObject());
        btnDeleteObject.addActionListener(e -> deleteObject());
        btnRefreshObjects.addActionListener(e -> loadClientObjects());

        objectButtons.add(btnSaveObject);
        objectButtons.add(btnDeleteObject);
        objectButtons.add(btnRefreshObjects);

        objectPanel.add(objectForm, BorderLayout.NORTH);
        objectPanel.add(objectButtons, BorderLayout.CENTER);

        // Таблица объектов
        String[] columns = {"ID", "Название", "Тип", "Адрес", "Площадь", "Цена", "Начало", "Окончание"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        objectTable = new JTable(tableModel);
        objectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        objectTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedObject();
            }
        });

        JScrollPane tableScroll = new JScrollPane(objectTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Список объектов"));
        objectPanel.add(tableScroll, BorderLayout.SOUTH);

        mainSplit.setLeftComponent(clientPanel);
        mainSplit.setRightComponent(objectPanel);

        setContentPane(mainSplit);
    }

    private void loadClients() {
        cbClient.removeAllItems();
        List<Client> clients = clientService.findAll();
        for (Client client : clients) {
            cbClient.addItem(client);
        }
    }

    private void loadClientObjects() {
        tableModel.setRowCount(0);
        Client selectedClient = (Client) cbClient.getSelectedItem();

        if (selectedClient != null) {
            // Заполняем информацию о клиенте
            tfClientFullName.setText(selectedClient.getFullName() != null ? selectedClient.getFullName() : "");
            tfClientPhone.setText(selectedClient.getPhone() != null ? selectedClient.getPhone() : "");
            tfClientEmail.setText(selectedClient.getEmail() != null ? selectedClient.getEmail() : "");
            tfClientAddress.setText(selectedClient.getAddress() != null ? selectedClient.getAddress() : "");
            tfClientContact.setText(selectedClient.getContactPerson() != null ? selectedClient.getContactPerson() : "");
            tfClientJob.setText(selectedClient.getJobTitle() != null ? selectedClient.getJobTitle() : "");

            // Загружаем объекты
            List<ObjectEntity> objects = selectedClient.getObjects();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            for (ObjectEntity object : objects) {
                tableModel.addRow(new Object[]{
                        object.getId(),
                        object.getName() != null ? object.getName() : "",
                        object.getObjectType() != null ? object.getObjectType() : "",
                        object.getAddress() != null ? object.getAddress() : "",
                        object.getArea(),
                        object.getPrice(),
                        object.getStartDate() != null ? object.getStartDate().format(formatter) : "—",
                        object.getEndDate() != null ? object.getEndDate().format(formatter) : "—"
                });
            }
        } else {
            // Очищаем информацию о клиенте и форме объекта
            tfClientFullName.setText("");
            tfClientPhone.setText("");
            tfClientEmail.setText("");
            tfClientAddress.setText("");
            tfClientContact.setText("");
            tfClientJob.setText("");
            clearForm();
        }
    }

    private void loadSelectedObject() {
        int selectedRow = objectTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Client selectedClient = (Client) cbClient.getSelectedItem();

            if (selectedClient != null) {
                selectedObject = selectedClient.getObjects().stream()
                        .filter(o -> o.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (selectedObject != null) {
                    tfName.setText(selectedObject.getName() != null ? selectedObject.getName() : "");
                    tfObjectType.setText(selectedObject.getObjectType() != null ? selectedObject.getObjectType() : "");
                    tfAddress.setText(selectedObject.getAddress() != null ? selectedObject.getAddress() : "");
                    tfArea.setText(selectedObject.getArea() != null ? selectedObject.getArea().toString() : "");
                    tfPrice.setText(selectedObject.getPrice() != null ? selectedObject.getPrice().toString() : "");
                    tfDescription.setText(selectedObject.getDescription() != null ? selectedObject.getDescription() : "");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    if (selectedObject.getStartDate() != null) {
                        tfStartDate.setText(selectedObject.getStartDate().format(formatter));
                    }
                    if (selectedObject.getEndDate() != null) {
                        tfEndDate.setText(selectedObject.getEndDate().format(formatter));
                    }
                    tfDocuments.setText(selectedObject.getDocuments() != null ? selectedObject.getDocuments() : "");
                }
            }
        }
    }

    private void saveObject() {
        try {
            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Название обязательно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client selectedClient = (Client) cbClient.getSelectedItem();
            if (selectedClient == null) {
                JOptionPane.showMessageDialog(this, "Выберите клиента!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = objectTable.getSelectedRow();
            ObjectEntity object;

            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                object = selectedClient.getObjects().stream()
                        .filter(o -> o.getId().equals(id))
                        .findFirst()
                        .orElse(new ObjectEntity());
            } else {
                object = new ObjectEntity();
                object.setClient(selectedClient);
            }

            object.setName(tfName.getText());
            object.setObjectType(tfObjectType.getText());
            object.setAddress(tfAddress.getText());

            if (!tfArea.getText().isBlank()) {
                object.setArea(Double.parseDouble(tfArea.getText()));
            } else {
                object.setArea(null);
            }

            if (!tfPrice.getText().isBlank()) {
                object.setPrice(Double.parseDouble(tfPrice.getText()));
            } else {
                object.setPrice(null);
            }

            object.setDescription(tfDescription.getText());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            if (!tfStartDate.getText().isBlank()) {
                try {
                    object.setStartDate(java.time.LocalDateTime.parse(tfStartDate.getText(), formatter));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты (дд.мм.гггг чч:мм)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                object.setStartDate(null);
            }

            if (!tfEndDate.getText().isBlank()) {
                try {
                    object.setEndDate(java.time.LocalDateTime.parse(tfEndDate.getText(), formatter));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат даты (дд.мм.гггг чч:мм)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                object.setEndDate(null);
            }

            object.setDocuments(tfDocuments.getText());

            objectService.save(object);
            loadClientObjects();
            clearForm();
            JOptionPane.showMessageDialog(this, "Объект сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteObject() {
        int selectedRow = objectTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить объект №" + id + "?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                objectService.deleteById(id);
                loadClientObjects();
                clearForm();
                JOptionPane.showMessageDialog(this, "Объект удалён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfObjectType.setText("");
        tfAddress.setText("");
        tfArea.setText("");
        tfPrice.setText("");
        tfDescription.setText("");
        tfStartDate.setText("");
        tfEndDate.setText("");
        tfDocuments.setText("");
    }
}
