package com.example.basa_prof.service;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.entity.Work;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {

    public void generateWorkReport(Client client, ObjectEntity object, List<Work> works, String saveDir) {
        try (XWPFDocument document = new XWPFDocument()) {

            // Заголовок документа
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setText("ОТЧЕТ ПО РАБОТАМ");

            // Подзаголовок
            XWPFParagraph subtitle = document.createParagraph();
            subtitle.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun subtitleRun = subtitle.createRun();
            subtitleRun.setFontSize(12);
            subtitleRun.setText("Клиент и объект");

            // Разделитель
            document.createParagraph();

            // Информация о клиенте
            XWPFParagraph infoPara = document.createParagraph();
            infoPara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun infoRun = infoPara.createRun();
            infoRun.setBold(true);
            infoRun.setText("Клиент:");

            XWPFParagraph clientNamePara = document.createParagraph();
            clientNamePara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun clientNameRun = clientNamePara.createRun();
            clientNameRun.setText("ФИО: " + client.getFullName());

            XWPFParagraph clientPhonePara = document.createParagraph();
            clientPhonePara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun clientPhoneRun = clientPhonePara.createRun();
            clientPhoneRun.setText("Телефон: " + client.getPhone());

            XWPFParagraph clientEmailPara = document.createParagraph();
            clientEmailPara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun clientEmailRun = clientEmailPara.createRun();
            clientEmailRun.setText("Email: " + (client.getEmail() != null ? client.getEmail() : "—"));

            // Разделитель
            document.createParagraph();

            // Информация об объекте
            if (object != null) {
                XWPFParagraph objInfoPara = document.createParagraph();
                objInfoPara.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun objInfoRun = objInfoPara.createRun();
                objInfoRun.setBold(true);
                objInfoRun.setText("Объект:");

                XWPFParagraph objNamePara = document.createParagraph();
                objNamePara.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun objNameRun = objNamePara.createRun();
                objNameRun.setText("Название: " + object.getName());

                XWPFParagraph objTypePara = document.createParagraph();
                objTypePara.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun objTypeRun = objTypePara.createRun();
                objTypeRun.setText("Тип: " + (object.getObjectType() != null ? object.getObjectType() : "—"));

                XWPFParagraph objAddressPara = document.createParagraph();
                objAddressPara.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun objAddressRun = objAddressPara.createRun();
                objAddressRun.setText("Адрес: " + (object.getAddress() != null ? object.getAddress() : "—"));
            }

            // Разделитель
            document.createParagraph();

            // Заголовок таблицы работ
            XWPFParagraph worksTitle = document.createParagraph();
            worksTitle.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun worksTitleRun = worksTitle.createRun();
            worksTitleRun.setBold(true);
            worksTitleRun.setFontSize(14);
            worksTitleRun.setText("Перечень работ:");

            // Таблица
            if (works != null && !works.isEmpty()) {
                XWPFTable table = document.createTable(works.size() + 1, 12);

                // Заголовки таблицы
                String[] headers = {"№", "Название", "Тип", "Описание", "Оценка стоимости", "Цена", "Дата начала", "Дата окончания", "Оплата", "Дата оплаты", "Статус", "Договор"};
                XWPFTableRow headerRow = table.getRow(0);
                for (int i = 0; i < headers.length; i++) {
                    XWPFTableCell cell = headerRow.getCell(i);
                    cell.setWidth("10%");
                    setCellText(cell, headers[i], true, false);
                }

                // Данные
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                for (int i = 0; i < works.size(); i++) {
                    Work work = works.get(i);
                    XWPFTableRow row = table.getRow(i + 1);
                    setCellText(row.getCell(0), String.valueOf(i + 1), false, false);
                    setCellText(row.getCell(1), work.getName() != null ? work.getName() : "—", false, false);
                    setCellText(row.getCell(2), work.getWorkType() != null ? work.getWorkType() : "—", false, false);
                    setCellText(row.getCell(3), work.getDescription() != null ? work.getDescription() : "—", false, false);
                    setCellText(row.getCell(4), work.getEstimatedCost() != null ? work.getEstimatedCost().toString() : "—", false, false);
                    setCellText(row.getCell(5), work.getPrice() != null ? work.getPrice().toString() : "—", false, false);
                    setCellText(row.getCell(6), work.getStartDate() != null ? work.getStartDate().format(formatter) : "—", false, false);
                    setCellText(row.getCell(7), work.getEndDate() != null ? work.getEndDate().format(formatter) : "—", false, false);
                    setCellText(row.getCell(8), work.getPayment() != null ? work.getPayment() : "—", false, false);
                    setCellText(row.getCell(9), work.getPaymentDate() != null ? work.getPaymentDate().format(formatter) : "—", false, false);
                    setCellText(row.getCell(10), work.getStatus() != null ? work.getStatus() : "—", false, false);
                    setCellText(row.getCell(11), work.getDeal() != null ? work.getDeal().getContractNumber() : "—", false, false);
                }
            } else {
                XWPFParagraph noWorks = document.createParagraph();
                noWorks.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun noWorksRun = noWorks.createRun();
                noWorksRun.setText("Работы не найдены");
            }

            // Дата формирования отчёта
            document.createParagraph();
            XWPFParagraph datePara = document.createParagraph();
            datePara.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun dateRun = datePara.createRun();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            dateRun.setText("Дата формирования: " + java.time.LocalDate.now().format(dateFormatter));

            // Сохранение файла
            String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "Отчет_" + (client != null ? client.getFullName().replaceAll("[\\\\/:*?\"<>|]", "_") : "Клиент")
                    + (object != null ? "_" + object.getName().replaceAll("[\\\\/:*?\"<>|]", "_") : "")
                    + "_" + timestamp + ".docx";

            Path dir = Paths.get(saveDir);
            if (!java.nio.file.Files.exists(dir)) {
                java.nio.file.Files.createDirectories(dir);
            }

            Path filePath = dir.resolve(fileName);
            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                document.write(out);
            }

            System.out.println("Отчет успешно создан: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Ошибка создания отчета: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold, boolean center) {
        cell.removeParagraph(0);
        XWPFParagraph para = cell.addParagraph();
        if (center) {
            para.setAlignment(ParagraphAlignment.CENTER);
        }
        XWPFRun run = para.createRun();
        run.setBold(bold);
        run.setText(text);
    }
}
