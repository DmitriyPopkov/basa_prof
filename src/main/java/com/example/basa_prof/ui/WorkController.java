package com.example.basa_prof.ui;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.Work;
import com.example.basa_prof.service.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller для управления работами.
 * Обрабатывает события UI и вызывает сервисы.
 * Реализует принцип Single Responsibility (S).
 */
public class WorkController {

    private final WorkView view;
    private final IWorkService workService;
    private final IClientService clientService;
    private final IObjectEntityService objectService;
    private final IDealService dealService;
    private final ReportService reportService;

    public WorkController(WorkView view, IWorkService workService, IClientService clientService,
                         IObjectEntityService objectService, IDealService dealService, ReportService reportService) {
        this.view = view;
        this.workService = workService;
        this.clientService = clientService;
        this.objectService = objectService;
        this.dealService = dealService;
        this.reportService = reportService;
        bindEvents();
        loadData();
    }

    /**
     * Привязка событий UI к методам контроллера.
     */
    private void bindEvents() {
        // Клик по строке таблицы
        view.getWorkTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getWorkTable().rowAtPoint(e.getPoint());
                if (row >= 0) {
                    view.getWorkTable().setRowSelectionInterval(row, row);
                    fillForm();
                }
            }
        });

        // Выбор клиента
        view.getCbClient().addActionListener(e -> {
            loadObjectsForClient();
            loadDealsForClient();
        });

        // Кнопки
        view.getBtnSave().addActionListener(e -> saveWork());
        view.getBtnDelete().addActionListener(e -> deleteWork());
        view.getBtnRefresh().addActionListener(e -> loadWorks());
        view.getBtnReport().addActionListener(e -> generateReport());
        view.getBtnSaveMerge().addActionListener(e -> saveWorkMerge());

        // Чекбокс подробного описания
        view.getCbDetailedDescription().addActionListener(e -> {
            if (view.getCbDetailedDescription().isSelected()) {
                view.openDescriptionDialog(view.getTfDescription().getText(), newText -> {
                    view.getTfDescription().setText(newText);
                    view.getCbDetailedDescription().setSelected(false);
                });
            }
        });
    }

    /**
     * Загрузка всех данных.
     */
    private void loadData() {
        loadWorks();
        loadClients();
    }

    /**
     * Загрузка работ в таблицу.
     */
    private void loadWorks() {
        List<Work> works = workService.findAll();
        List<Object[]> rows = works.stream().map(work -> {
            String clientName = work.getObjectEntity() != null && work.getObjectEntity().getClient() != null
                    ? work.getObjectEntity().getClient().getFullName() : "—";
            String objectName = work.getObjectEntity() != null ? work.getObjectEntity().getName() : "—";
            return new Object[]{
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
            };
        }).toList();
        view.setTableData(rows);
    }

    /**
     * Загрузка клиентов в комбо-бокс.
     */
    private void loadClients() {
        List<Client> clients = clientService.findAll();
        view.getCbClient().removeAllItems();
        for (Client client : clients) {
            view.getCbClient().addItem(client);
        }
    }

    /**
     * Загрузка объектов для выбранного клиента.
     */
    private void loadObjectsForClient() {
        view.getCbObject().removeAllItems();
        Client selectedClient = (Client) view.getCbClient().getSelectedItem();
        if (selectedClient != null) {
            List<ObjectEntity> objects = selectedClient.getObjects();
            for (ObjectEntity object : objects) {
                view.getCbObject().addItem(object);
            }
        }
    }

    /**
     * Загрузка договоров для выбранного клиента.
     */
    private void loadDealsForClient() {
        view.getCbDeal().removeAllItems();
        Client selectedClient = (Client) view.getCbClient().getSelectedItem();
        if (selectedClient != null) {
            List<Deal> deals = dealService.findByClientId(selectedClient.getId());
            for (Deal deal : deals) {
                view.getCbDeal().addItem(deal);
            }
        }
    }

    /**
     * Заполнение формы данными из выбранной работы.
     */
    private void fillForm() {
        int selectedRow = view.getWorkTable().getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) view.getTableModel().getValueAt(selectedRow, 0);
            view.setCurrentWorkId(id);
            Work work = workService.findById(id).orElse(null);
            if (work != null) {
                if (work.getObjectEntity() != null && work.getObjectEntity().getClient() != null) {
                    view.getCbClient().setSelectedItem(work.getObjectEntity().getClient());
                    loadObjectsForClient();
                    view.getCbObject().setSelectedItem(work.getObjectEntity());
                }

                view.getTfName().setText(work.getName());
                view.getTfWorkType().setText(work.getWorkType());
                view.getTfDescription().setText(work.getDescription() != null ? work.getDescription() : "");
                view.getTfEstimatedCost().setText(work.getEstimatedCost() != null ? work.getEstimatedCost().toString() : "");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                if (work.getStartDate() != null) {
                    view.getTfStartDate().setText(work.getStartDate().format(formatter));
                }
                if (work.getEndDate() != null) {
                    view.getTfEndDate().setText(work.getEndDate().format(formatter));
                }
                if (work.getPrice() != null) {
                    view.getTfPrice().setText(work.getPrice().toString());
                }
                if (work.getPayment() != null) {
                    view.getTfPayment().setText(work.getPayment());
                }
                if (work.getPaymentDate() != null) {
                    view.getTfPaymentDate().setText(work.getPaymentDate().format(formatter));
                }
                if (work.getStatus() != null) {
                    view.getTfStatus().setText(work.getStatus());
                }
                if (work.getDeal() != null && work.getDeal().getClient() != null) {
                    view.getCbClient().setSelectedItem(work.getDeal().getClient());
                    loadObjectsForClient();
                    loadDealsForClient();
                    view.getCbDeal().setSelectedItem(work.getDeal());
                }
            }
        }
    }

    /**
     * Применение данных из формы к объекту Work (DRY).
     * @return true если валидация прошла успешно, false если была ошибка
     */
    private boolean applyFormToWork(Work work) {
        work.setName(view.getTfName().getText());
        work.setWorkType(view.getTfWorkType().getText());
        work.setDescription(view.getTfDescription().getText().isBlank() ? null : view.getTfDescription().getText());

        if (!view.getTfEstimatedCost().getText().isBlank()) {
            work.setEstimatedCost(Double.parseDouble(view.getTfEstimatedCost().getText()));
        } else {
            work.setEstimatedCost(null);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (!view.getTfStartDate().getText().isBlank()) {
            try {
                work.setStartDate(LocalDate.parse(view.getTfStartDate().getText(), formatter));
            } catch (DateTimeParseException e) {
                view.showErrorMessage("Неверный формат даты начала (дд.мм.гггг)!");
                return false;
            }
        } else {
            work.setStartDate(null);
        }

        if (!view.getTfEndDate().getText().isBlank()) {
            try {
                work.setEndDate(LocalDate.parse(view.getTfEndDate().getText(), formatter));
            } catch (DateTimeParseException e) {
                view.showErrorMessage("Неверный формат даты окончания (дд.мм.гггг)!");
                return false;
            }
        } else {
            work.setEndDate(null);
        }

        if (!view.getTfPrice().getText().isBlank()) {
            work.setPrice(Double.parseDouble(view.getTfPrice().getText()));
        } else {
            work.setPrice(null);
        }

        work.setPayment(view.getTfPayment().getText());

        if (!view.getTfPaymentDate().getText().isBlank()) {
            try {
                work.setPaymentDate(LocalDate.parse(view.getTfPaymentDate().getText(), formatter));
            } catch (DateTimeParseException e) {
                view.showErrorMessage("Неверный формат даты оплаты (дд.мм.гггг)!");
                return false;
            }
        } else {
            work.setPaymentDate(null);
        }

        work.setStatus(view.getTfStatus().getText());

        Client selectedClient = (Client) view.getCbClient().getSelectedItem();
        if (selectedClient != null) {
            ObjectEntity selectedObject = (ObjectEntity) view.getCbObject().getSelectedItem();
            if (selectedObject != null) {
                work.setObjectEntity(selectedObject);
            }
        }

        Deal selectedDeal = (Deal) view.getCbDeal().getSelectedItem();
        if (selectedDeal != null) {
            work.setDeal(selectedDeal);
        }
        return true;
    }

    /**
     * Сохранение работы как новой.
     */
    private void saveWork() {
        if (view.getTfName().getText().isBlank()) {
            view.showErrorMessage("Название обязательно!");
            return;
        }

        Work work = new Work();
        if (!applyFormToWork(work)) return;

        workService.save(work);
        loadWorks();
        clearForm();
        view.showInfoMessage("Работа сохранена!");
    }

    /**
     * Обновление существующей работы.
     */
    private void saveWorkMerge() {
        Long currentId = view.getCurrentWorkId();
        if (currentId == null) {
            view.showErrorMessage("Сначала выберите работу в таблице для редактирования!");
            return;
        }

        if (view.getTfName().getText().isBlank()) {
            view.showErrorMessage("Название обязательно!");
            return;
        }

        Work existingWork = workService.findById(currentId).orElse(null);
        if (existingWork == null) {
            view.showErrorMessage("Работа не найдена!");
            return;
        }

        if (!applyFormToWork(existingWork)) return;

        workService.save(existingWork);
        view.setCurrentWorkId(null);
        loadWorks();
        clearForm();
        view.showInfoMessage("Работа обновлена!");
    }

    /**
     * Удаление работы.
     */
    private void deleteWork() {
        int selectedRow = view.getWorkTable().getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) view.getTableModel().getValueAt(selectedRow, 0);
            if (view.showConfirmDialog("Удалить работу №" + id + "?")) {
                workService.deleteById(id);
                loadWorks();
                clearForm();
                view.showInfoMessage("Работа удалена!");
            }
        }
    }

    /**
     * Генерация отчёта.
     */
    private void generateReport() {
        Client selectedClient = (Client) view.getCbClient().getSelectedItem();
        if (selectedClient == null) {
            view.showErrorMessage("Выберите клиента!");
            return;
        }

        ObjectEntity selectedObject = (ObjectEntity) view.getCbObject().getSelectedItem();

        List<Work> works = workService.findAll();
        List<Work> filteredWorks = works.stream()
                .filter(w -> w.getObjectEntity() != null && w.getObjectEntity().getClient() != null
                        && w.getObjectEntity().getClient().getId().equals(selectedClient.getId()))
                .filter(w -> selectedObject == null || w.getObjectEntity() != null
                        && w.getObjectEntity().getId().equals(selectedObject.getId()))
                .toList();

        if (filteredWorks.isEmpty()) {
            view.showErrorMessage("Работы не найдены!");
            return;
        }

        String filePath = reportService.generateWorkReport(selectedClient, selectedObject, filteredWorks, view.getSaveDir());
        if (filePath != null) {
            try {
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception e) {
                view.showErrorMessage("Не удалось открыть файл: " + e.getMessage());
            }
        }
        view.showInfoMessage("Отчет успешно создан!");
    }

    /**
     * Очистка формы.
     */
    private void clearForm() {
        view.getCbClient().setSelectedIndex(-1);
        view.getCbObject().removeAllItems();
        view.getTfName().setText("");
        view.getTfWorkType().setText("");
        view.getTfDescription().setText("");
        view.getCbDetailedDescription().setSelected(false);
        view.getTfEstimatedCost().setText("");
        view.getTfStartDate().setText("");
        view.getTfEndDate().setText("");
        view.getTfPrice().setText("");
        view.getTfPayment().setText("");
        view.getTfPaymentDate().setText("");
        view.getTfStatus().setText("");
        view.getCbDeal().removeAllItems();
        view.setCurrentWorkId(null);
    }
}
