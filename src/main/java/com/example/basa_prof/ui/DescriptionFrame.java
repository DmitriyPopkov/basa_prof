package com.example.basa_prof.ui;

import javax.swing.*;
import java.awt.*;

public class DescriptionFrame extends JDialog {

    private JTextArea descriptionArea;
    private String currentDescription;

    public DescriptionFrame(JFrame parent, String description) {
        super(parent, "Подробное описание", true);
        this.currentDescription = description != null ? description : "";
        initialize();
    }

    private void initialize() {
        setSize(600, 400);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Текстовое поле для описания
        descriptionArea = new JTextArea(15, 40);
        descriptionArea.setText(currentDescription);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Описание"));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSave = new JButton("Сохранить");
        JButton btnCancel = new JButton("Отмена");

        btnSave.addActionListener(e -> {
            currentDescription = descriptionArea.getText();
            dispose();
        });

        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public String getDescription() {
        return currentDescription;
    }
}
