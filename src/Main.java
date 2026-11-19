import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showWindow);
    }

    private static void showWindow() {
        JFrame frame = new JFrame("Лабораторна 6 · Варіант 5");
        JTabbedPane tabs = new JTabbedPane();
        FlightsPanel flights = new FlightsPanel();
        RotationPanel rotation = new RotationPanel();

        tabs.addTab("1. Паролі", new PasswordPanel());
        tabs.addTab("2. Конфігуратор ПК", new PcPanel());
        tabs.addTab("3. Розклад рейсів", flights);
        tabs.addTab("4. Обертання", rotation);
        tabs.addChangeListener(event -> frame.setJMenuBar(
                tabs.getSelectedComponent() == flights ? flights.menuBar() : null));
        frame.add(tabs);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                rotation.stop();
            }
        });
        frame.setSize(800, 500);
        frame.setMinimumSize(new java.awt.Dimension(620, 420));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
