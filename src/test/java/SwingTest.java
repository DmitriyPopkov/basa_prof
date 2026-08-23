import java.awt.*;
import javax.swing.*;

public class SwingTest {
    public static void main(String[] args) {
        System.out.println("Headless: " + GraphicsEnvironment.isHeadless());
        System.out.println("DisplayDevice: " + GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Swing Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 200);
                frame.add(new JLabel("Если вы видите это — Swing работает!", SwingConstants.CENTER));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                System.out.println("Swing UI создан успешно");
            } catch (Exception e) {
                System.err.println("Ошибка создания Swing UI: " + e.getMessage());
                e.printStackTrace();
            }
        });

        System.out.println("Ожидание 5 секунд...");
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
    }
}
