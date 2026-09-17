package com.example.basa_prof.ui;

import com.example.basa_prof.service.*;

import javax.swing.*;

/**
 * Frame для управления работами.
 * Использует паттерн MVC (Model-View-Controller).
 * Реализует принцип Single Responsibility (S).
 */
public class WorkFrame extends JFrame {

    private WorkView view;
    private WorkController controller;

    public WorkFrame(IWorkService workService, IClientService clientService,
                    IObjectEntityService objectService, IDealService dealService, ReportService reportService) {
        view = new WorkView();
        controller = new WorkController(view, workService, clientService, objectService, dealService, reportService);
        
        setTitle(view.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setContentPane(view.getContentPanel());
    }

    // Вся логика перенесена в WorkController
    // Этот класс теперь только обёртка для отображения
}
