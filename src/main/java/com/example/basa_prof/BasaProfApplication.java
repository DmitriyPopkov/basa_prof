package com.example.basa_prof;

import com.example.basa_prof.service.*;
import com.example.basa_prof.ui.MainFrame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class BasaProfApplication {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Грузим приложение");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 200);
                frame.add(new JLabel("Если вы видите это — Приложение работает!", SwingConstants.CENTER));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                System.out.println("Swing UI создан успешно");

                //   MainFrame mainFrame = new MainFrame(
                //      clientService, dealService, objectService, workService,
                //     materialService, employeeService, supplierService, userService
                //  );
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Ошибка запуска приложения: " + e.getMessage());
                e.printStackTrace();
                // context.close();
            }
        });

         ConfigurableApplicationContext context = SpringApplication.run(BasaProfApplication.class, args);
        // Проверяем наличие графической оболочки
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Headless режим — Swing UI не запускается.");
             context.close();
            // Запуск Swing UI после инициализации Spring


            return;
        }




        // Запуск Swing UI после инициализации Spring
        SwingUtilities.invokeLater(() -> {
            try {
                ClientService clientService = context.getBean(ClientService.class);
                DealService dealService = context.getBean(DealService.class);
                ObjectEntityService objectService = context.getBean(ObjectEntityService.class);
                WorkService workService = context.getBean(WorkService.class);
                SupplierService supplierService = context.getBean(SupplierService.class);
                MaterialService materialService = context.getBean(MaterialService.class);
                EmployeeService employeeService = context.getBean(EmployeeService.class);

                UserService userService = context.getBean(UserService.class);
                WorkHourService workHourService = context.getBean(WorkHourService.class);
                ContractorService contractorService = context.getBean(ContractorService.class);
                ExpenseService expenseService = context.getBean(ExpenseService.class);
                PaymentService paymentService = context.getBean(PaymentService.class);

                  MainFrame mainFrame = new MainFrame(
                                          clientService, dealService, objectService, workService, materialService,
                          employeeService, supplierService, userService, workHourService, contractorService, expenseService, paymentService
                                  );
                mainFrame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Ошибка запуска приложения: " + e.getMessage());
                e.printStackTrace();
               // context.close();
            }
        });



        // Добавляем shutdown hook для корректного закрытия
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Приложение завершается...");
            context.close();
        }));


    }
}
