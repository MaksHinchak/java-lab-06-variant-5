import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
public class Main {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    // new Клас(...) створює об’єкт і викликає його конструктор.
    private final JTable tableA = new JTable(); // Редагована таблиця першої матриці.
    private final JTable tableB = new JTable(); // Редагована таблиця другої матриці.
    private final JTextField size = new JTextField("2", 3); // Поле розміру квадратних матриць.
    private final JTextField path = new JTextField("data/matrices.txt", 24); // Поле шляху до демонстраційного файлу.
    private final JLabel result = new JLabel("Вектор X: ще не обчислено"); // Область відображення результату.
    private void fill(JTable table, double[][] values) { // Замінюємо модель таблиці перевіреними даними.
        // .length — довжина масиву без дужок; для String — length(), для колекції — size().
        int n = values.length;
        DefaultTableModel model = new DefaultTableModel(n, n);
        // for (початок; умова; крок); i++ збільшує лічильник після проходу.
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) model.setValueAt(values[i][j], i, j); // Копіюємо матрицю в модель.
        table.setModel(model);
    }
    private double[][] readTable(JTable table) { // Збираємо поточні значення з редагованої таблиці.
        if (table.isEditing()) table.getCellEditor().stopCellEditing(); // Завершуємо редагування, щоб останнє число потрапило в модель.
        int n = table.getRowCount();
        // new тип[n] — масив незмінної довжини; числові елементи спочатку 0, посилання — null.
        // double[][] — масив окремих рядків; індекси [рядок][стовпець].
        double[][] values = new double[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) { // Обходимо кожну клітинку.
            // parseInt/parseDouble перетворюють текст на число; помилка формату — NumberFormatException.
            double value = Double.parseDouble(String.valueOf(table.getValueAt(i, j)).replace(',', '.'));
            // Double.isFinite відкидає NaN та ±Infinity; ! заперечує перевірку.
            // throw передає помилку в catch; new створює об’єкт винятку з повідомленням.
            if (!Double.isFinite(value)) throw new NumberFormatException("Клітинки мають містити скінченні числа."); // Відхиляємо NaN та нескінченності.
            values[i][j] = value;
        }
        return values;
    }
    private void error(Exception e) { // Єдиний спосіб показувати тип і причину помилки.
        // getClass().getSimpleName() — коротка назва фактичного класу.
        JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), "Помилка даних", JOptionPane.ERROR_MESSAGE);
    }
    private JPanel matrices() { // Будуємо вкладку з матрицями та елементами керування.
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel controls = new JPanel();
        JButton resize = new JButton("Створити таблиці");
        JButton load = new JButton("Прочитати файл");
        JButton calculate = new JButton("Обчислити X");
        controls.add(new JLabel("n:"));
        controls.add(size);
        controls.add(resize);
        controls.add(path);
        controls.add(load);
        controls.add(calculate);
        panel.add(controls, BorderLayout.NORTH);
        JPanel tables = new JPanel(new GridLayout(1, 2, 12, 0));
        JScrollPane first = new JScrollPane(tableA);
        JScrollPane second = new JScrollPane(tableB);
        first.setBorder(BorderFactory.createTitledBorder("Матриця A"));
        second.setBorder(BorderFactory.createTitledBorder("Матриця B"));
        tables.add(first);
        tables.add(second);
        panel.add(tables, BorderLayout.CENTER);
        panel.add(result, BorderLayout.SOUTH);
        fill(tableA, new double[][]{{3, 5}, {2, 4}});
        fill(tableB, new double[][]{{1, 4}, {2, 1}});
        // event -> ... — лямбда-обробник; виконається при події на потоці Swing EDT.
        resize.addActionListener(event -> { // Обробник кнопки працює на EDT.
            try { // Перевіряємо число до створення нового масиву.
                int n = Integer.parseInt(size.getText().trim());
                MatrixData.checkSize(n);
                fill(tableA, new double[n][n]);
                fill(tableB, new double[n][n]);
                result.setText("Вектор X: заповніть таблиці");
            // catch (Тип1 | Тип2 e) — один обробник для кількох типів винятків.
            // catch (Тип e) перехоплює виняток із try; e.getMessage() повертає його повідомлення.
            } catch (NumberFormatException | MatrixSizeException e) { error(e); } // Обидва види помилок мають видиме пояснення.
        });
        load.addActionListener(event -> { // Читання демонстраційного або власного файлу.
            try { // Всі дані перевіряємо до оновлення таблиць.
                double[][][] values = MatrixData.read(Path.of(path.getText()));
                fill(tableA, values[0]);
                fill(tableB, values[1]);
                size.setText(Integer.toString(values[0].length));
                result.setText("Файл прочитано. Натисніть «Обчислити X».");
            } catch (IOException | NumberFormatException | MatrixSizeException | java.nio.file.InvalidPathException e) { error(e); } // Обробляємо всі заявлені сценарії помилок.
        });
        calculate.addActionListener(event -> { // Обчислення використовує актуальні клітинки обох таблиць.
            // Arrays.toString друкує елементи; звичайний println масиву їх не показує.
            try { result.setText("Вектор X = " + Arrays.toString(MatrixData.compare(readTable(tableA), readTable(tableB)))); } // Показуємо результат під таблицями.
            catch (NumberFormatException e) { error(e); } // Текст замість числа не закриває програму.
        });
        return panel;
    }
    // static — виклик без об’єкта; public — доступ ззовні, private — лише в класі; void — без результату.
    // main — точка входу; String[] args містить аргументи запуску без назви програми.
    public static void main(String[] args) { // JVM запускає main поза EDT.
        // invokeLater ставить лямбду () -> {...} у чергу EDT — потоку роботи з інтерфейсом Swing.
        SwingUtilities.invokeLater(() -> { // Створення всіх Swing-компонентів переносимо на EDT.
            Main app = new Main();
            JFrame frame = new JFrame("Лабораторна 6 • Варіант 5");
            RotationPanel rotation = new RotationPanel();
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("1. Обертання відрізка", rotation);
            tabs.addTab("2. Порівняння матриць", app.matrices());
            frame.add(tabs);
            frame.setSize(1050, 600);
            frame.setMinimumSize(new java.awt.Dimension(1000, 500));
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // new WindowAdapter() {...} — анонімний підклас із перевизначеним обробником.
            frame.addWindowListener(new WindowAdapter() {
                // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
                @Override public void windowClosed(WindowEvent event) { rotation.stop(); } // Після закриття таймер більше не утримує процес.
            });
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
