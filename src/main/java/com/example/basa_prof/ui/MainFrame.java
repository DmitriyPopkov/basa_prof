package com.example.basa_prof.ui;

import com.example.basa_prof.service.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(
            ClientService clientService,
            DealService dealService,
            ObjectEntityService objectService,
            WorkService workService,
            MaterialService materialService,
            EmployeeService employeeService,
            SupplierService supplierService,
            UserService userService
    ) {
        initialize();

        JMenuBar menuBar = new JMenuBar();
        JMenu menuClients = new JMenu("Клиенты");
        JMenu menuObjects = new JMenu("Объекты");
        JMenu menuDeals = new JMenu("Сделки");
        JMenu menuWorks = new JMenu("Работы");
        JMenu menuMaterials = new JMenu("Материалы");
        JMenu menuEmployees = new JMenu("Сотрудники");
        JMenu menuSuppliers = new JMenu("Поставщики");
        JMenu menuUsers = new JMenu("Пользователи");

        JMenuItem menuItem1 = new JMenuItem("Управление клиентами");
        menuItem1.addActionListener(e -> new ClientFrame(clientService).setVisible(true));
        menuClients.add(menuItem1);

        JMenuItem menuItem2 = new JMenuItem("Управление объектами");
        menuItem2.addActionListener(e -> new ObjectFrame(clientService, objectService, workService, materialService).setVisible(true));
        menuObjects.add(menuItem2);

        JMenuItem menuItem3 = new JMenuItem("Управление сделками");
        menuItem3.addActionListener(e -> new DealFrame(dealService, clientService, objectService).setVisible(true));
        menuDeals.add(menuItem3);

        JMenuItem menuItem4 = new JMenuItem("Управление работами");
        menuItem4.addActionListener(e -> new WorkFrame(workService).setVisible(true));
        menuWorks.add(menuItem4);

        JMenuItem menuItem5 = new JMenuItem("Управление материалами");
        menuItem5.addActionListener(e -> new MaterialFrame(materialService, supplierService).setVisible(true));
        menuMaterials.add(menuItem5);

        JMenuItem menuItem6 = new JMenuItem("Управление сотрудниками");
        menuItem6.addActionListener(e -> new EmployeeFrame(employeeService).setVisible(true));
        menuEmployees.add(menuItem6);

        JMenuItem menuItem7 = new JMenuItem("Управление поставщиками");
        menuItem7.addActionListener(e -> new SupplierFrame(supplierService).setVisible(true));
        menuSuppliers.add(menuItem7);

        JMenuItem menuItem8 = new JMenuItem("Управление пользователями");
        menuItem8.addActionListener(e -> new UserFrame(userService).setVisible(true));
        menuUsers.add(menuItem8);

        menuBar.add(menuClients);
        menuBar.add(menuObjects);
        menuBar.add(menuDeals);
        menuBar.add(menuWorks);
        menuBar.add(menuMaterials);
        menuBar.add(menuEmployees);
        menuBar.add(menuSuppliers);
        menuBar.add(menuUsers);

        setJMenuBar(menuBar);
    }

    private void initialize() {
        setTitle("База данных - Управление");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Выберите раздел в меню", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        setContentPane(label);
    }
}
