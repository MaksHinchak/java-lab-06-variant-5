import javax.swing.*; // JFrame, JPanel, JButton, JLabel, JTextField, JTable та допоміжні компоненти.
import javax.swing.table.DefaultTableModel; // Модель зберігає редаговані клітинки матриць.
import java.awt.BorderLayout; // Розміщення панелей зверху, в центрі та знизу.
import java.awt.GridLayout; // Розміщення матриць A і B поруч однакового розміру.
import java.awt.event.WindowAdapter; // Обробляємо закриття вікна без реалізації всіх методів слухача.
import java.awt.event.WindowEvent; // Подія життєвого циклу вікна.
import java.io.IOException; // Перший стандартний тип винятку для демонстрації.
import java.nio.file.Path; // Перетворюємо текст шляху з поля у Path.
import java.util.Arrays; // Форматуємо вектор результату.
public class Main { // Вікно містить дві вкладки для двох завдань лабораторної.
    private final JTable tableA = new JTable(); // Редагована таблиця першої матриці.
    private final JTable tableB = new JTable(); // Редагована таблиця другої матриці.
    private final JTextField size = new JTextField("2", 3); // Поле розміру квадратних матриць.
    private final JTextField path = new JTextField("data/matrices.txt", 24); // Поле шляху до демонстраційного файлу.
    private final JLabel result = new JLabel("Вектор X: ще не обчислено"); // Область відображення результату.
    private void fill(JTable table, double[][] values) { // Замінюємо модель таблиці перевіреними даними.
        int n = values.length; // Обчислюємо кількість рядків і стовпців.
        DefaultTableModel model = new DefaultTableModel(n, n); // Клітинки за замовчуванням доступні для редагування.
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) model.setValueAt(values[i][j], i, j); // Копіюємо матрицю в модель.
        table.setModel(model); // JTable автоматично оновлює відображення.
    }
    private double[][] readTable(JTable table) { // Збираємо поточні значення з редагованої таблиці.
        if (table.isEditing()) table.getCellEditor().stopCellEditing(); // Завершуємо редагування, щоб останнє число потрапило в модель.
        int n = table.getRowCount(); // Визначаємо фактичний розмір матриці.
        double[][] values = new double[n][n]; // Виділяємо окрему числову матрицю.
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) { // Обходимо кожну клітинку.
            double value = Double.parseDouble(String.valueOf(table.getValueAt(i, j)).replace(',', '.')); // Другий стандартний виняток: NumberFormatException.
            if (!Double.isFinite(value)) throw new NumberFormatException("Клітинки мають містити скінченні числа."); // Відхиляємо NaN та нескінченності.
            values[i][j] = value; // Зберігаємо перевірене число.
        }
        return values; // Повертаємо дані для чистого алгоритму.
    }
    private void error(Exception e) { // Єдиний спосіб показувати тип і причину помилки.
        JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), "Помилка даних", JOptionPane.ERROR_MESSAGE); // Користувач може виправити дані, вікно залишається відкритим.
    }
    private JPanel matrices() { // Будуємо вкладку з матрицями та елементами керування.
        JPanel panel = new JPanel(new BorderLayout(8, 8)); // Основне компонування вкладки.
        JPanel controls = new JPanel(); // Панель кнопок і текстових полів.
        JButton resize = new JButton("Створити таблиці"); // Перебудовує таблиці за полем n.
        JButton load = new JButton("Прочитати файл"); // Завантажує обидві матриці з файлу.
        JButton calculate = new JButton("Обчислити X"); // Запускає попарне порівняння рядків.
        controls.add(new JLabel("n:")); // JLabel підписує поле розміру.
        controls.add(size); // Додаємо поле n.
        controls.add(resize); // Додаємо кнопку зміни розміру.
        controls.add(path); // Додаємо поле шляху.
        controls.add(load); // Додаємо кнопку читання файлу.
        controls.add(calculate); // Додаємо кнопку обчислення.
        panel.add(controls, BorderLayout.NORTH); // Керування розміщуємо над таблицями.
        JPanel tables = new JPanel(new GridLayout(1, 2, 12, 0)); // Дві матриці поруч.
        JScrollPane first = new JScrollPane(tableA); // Прокрутка дозволяє бачити матриці до 15x15.
        JScrollPane second = new JScrollPane(tableB); // Незалежна прокрутка другої матриці.
        first.setBorder(BorderFactory.createTitledBorder("Матриця A")); // Підписуємо першу таблицю.
        second.setBorder(BorderFactory.createTitledBorder("Матриця B")); // Підписуємо другу таблицю.
        tables.add(first); // Ліва частина вкладки.
        tables.add(second); // Права частина вкладки.
        panel.add(tables, BorderLayout.CENTER); // Таблиці займають вільний простір.
        panel.add(result, BorderLayout.SOUTH); // Результат розміщуємо під таблицями.
        fill(tableA, new double[][]{{3, 5}, {2, 4}}); // Приклад A для негайного обчислення.
        fill(tableB, new double[][]{{1, 4}, {2, 1}}); // Приклад B дає X=[1,0].
        resize.addActionListener(event -> { // Обробник кнопки працює на EDT.
            try { // Перевіряємо число до створення нового масиву.
                int n = Integer.parseInt(size.getText().trim()); // Неправильний формат породжує стандартний виняток.
                MatrixData.checkSize(n); // n=0 або n=16 демонструє власний виняток.
                fill(tableA, new double[n][n]); // Створюємо нову нульову A.
                fill(tableB, new double[n][n]); // Створюємо нову нульову B.
                result.setText("Вектор X: заповніть таблиці"); // Прибираємо застарілий результат.
            } catch (NumberFormatException | MatrixSizeException e) { error(e); } // Обидва види помилок мають видиме пояснення.
        });
        load.addActionListener(event -> { // Читання демонстраційного або власного файлу.
            try { // Всі дані перевіряємо до оновлення таблиць.
                double[][][] values = MatrixData.read(Path.of(path.getText())); // IOException, NumberFormatException або власний виняток.
                fill(tableA, values[0]); // Після успішної перевірки оновлюємо A.
                fill(tableB, values[1]); // Оновлюємо B тією самою розмірністю.
                size.setText(Integer.toString(values[0].length)); // Узгоджуємо поле n з фактичною таблицею.
                result.setText("Файл прочитано. Натисніть «Обчислити X»."); // Підказуємо наступну дію.
            } catch (IOException | NumberFormatException | MatrixSizeException | java.nio.file.InvalidPathException e) { error(e); } // Обробляємо всі заявлені сценарії помилок.
        });
        calculate.addActionListener(event -> { // Обчислення використовує актуальні клітинки обох таблиць.
            try { result.setText("Вектор X = " + Arrays.toString(MatrixData.compare(readTable(tableA), readTable(tableB)))); } // Показуємо результат під таблицями.
            catch (NumberFormatException e) { error(e); } // Текст замість числа не закриває програму.
        });
        return panel; // Повертаємо зібрану вкладку.
    }
    public static void main(String[] args) { // JVM запускає main поза EDT.
        SwingUtilities.invokeLater(() -> { // Створення всіх Swing-компонентів переносимо на EDT.
            Main app = new Main(); // Створюємо модель керування вікном.
            JFrame frame = new JFrame("Лабораторна 6 • Варіант 5"); // Основне вікно програми.
            RotationPanel rotation = new RotationPanel(); // Окрема панель анімації першої задачі.
            JTabbedPane tabs = new JTabbedPane(); // Перемикач між двома задачами.
            tabs.addTab("1. Обертання відрізка", rotation); // Перша вкладка з анімацією.
            tabs.addTab("2. Порівняння матриць", app.matrices()); // Друга вкладка з таблицями.
            frame.add(tabs); // Додаємо вкладки у вікно.
            frame.setSize(1050, 600); // Початкові розміри вміщують елементи керування.
            frame.setMinimumSize(new java.awt.Dimension(1000, 500)); // Не дозволяємо сховати кнопки надмірним звуженням.
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Закриваємо це вікно без примусового System.exit.
            frame.addWindowListener(new WindowAdapter() { // При закритті зупиняємо джерело подій анімації.
                @Override public void windowClosed(WindowEvent event) { rotation.stop(); } // Після закриття таймер більше не утримує процес.
            });
            frame.setLocationRelativeTo(null); // Центруємо вікно на екрані.
            frame.setVisible(true); // Відображаємо повністю зібране вікно.
        });
    }
}
