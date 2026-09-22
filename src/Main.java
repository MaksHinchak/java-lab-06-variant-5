import javax.swing.*; // JFrame, JPanel, JButton, JLabel, JTextField, JTable та допоміжні компоненти.
import javax.swing.table.DefaultTableModel; // Модель зберігає редаговані клітинки матриць.
import java.awt.BorderLayout; // Розміщення панелей зверху, в центрі та знизу.
import java.awt.GridLayout; // Розміщення матриць A і B поруч однакового розміру.
import java.awt.event.WindowAdapter; // Обробляємо закриття вікна без реалізації всіх методів слухача.
import java.awt.event.WindowEvent; // Подія життєвого циклу вікна.
import java.io.IOException; // Перший стандартний тип винятку для демонстрації.
import java.nio.file.Path; // Перетворюємо текст шляху з поля у Path.
import java.util.Arrays; // Форматуємо вектор результату.
// class описує тип об’єктів; new створює конкретний об’єкт і викликає його конструктор. public робить клас
// доступним ззовні; final у заголовку класу, якщо він є, забороняє створювати підкласи, але сам по собі не робить
// поля незмінними.
public class Main { // Вікно містить дві вкладки для двох завдань лабораторної.
    // private забороняє прямий доступ до поля з інших класів. final дозволяє присвоїти поле лише один раз; для
    // посилання це заборона замінити об’єкт, а не заборона змінювати його вміст, якщо сам об’єкт змінний.
    private final JTable tableA = new JTable(); // Редагована таблиця першої матриці.
    private final JTable tableB = new JTable(); // Редагована таблиця другої матриці.
    private final JTextField size = new JTextField("2", 3); // Поле розміру квадратних матриць.
    private final JTextField path = new JTextField("data/matrices.txt", 24); // Поле шляху до демонстраційного файлу.
    private final JLabel result = new JLabel("Вектор X: ще не обчислено"); // Область відображення результату.
    private void fill(JTable table, double[][] values) { // Замінюємо модель таблиці перевіреними даними.
        // .length — незмінне поле масиву, тому пишеться без дужок, аналог len(a). Останній індекс — length - 1. Не
        // плутати: для String потрібен метод length(), а для ArrayList — size().
        int n = values.length; // Обчислюємо кількість рядків і стовпців.
        // JTable показує дані моделі, а DefaultTableModel зберігає клітинки як об’єкти. Після заміни моделі
        // таблиця сама отримує повідомлення про зміну; окремо перемальовувати кожну клітинку не потрібно.
        DefaultTableModel model = new DefaultTableModel(n, n); // Клітинки за замовчуванням доступні для редагування.
        // У for спочатку один раз задається лічильник, перед кожним проходом перевіряється умова, а після проходу
        // виконується i++ або j++. Умова i < n відповідає Python range(n): значення n вже не входить у цикл.
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) model.setValueAt(values[i][j], i, j); // Копіюємо матрицю в модель.
        table.setModel(model); // JTable автоматично оновлює відображення.
    }
    private double[][] readTable(JTable table) { // Збираємо поточні значення з редагованої таблиці.
        // Поки користувач набирає текст, він може бути ще в редакторі клітинки, а не в моделі JTable. Завершення
        // редагування перед читанням потрібне, щоб обчислення взяло останнє введене значення.
        if (table.isEditing()) table.getCellEditor().stopCellEditing(); // Завершуємо редагування, щоб останнє число потрапило в модель.
        int n = table.getRowCount(); // Визначаємо фактичний розмір матриці.
        // new тип[...] створює масив фіксованої довжини: додати елемент через append, як у Python list, не можна.
        // Числові клітинки без явних значень спочатку дорівнюють 0, boolean — false, а посилання на об’єкти —
        // null. Індекси починаються з 0; вихід за межі спричиняє виняток.
        // double[][] — масив рядків, кожен рядок теж масив; new double[n][n] створює окремий рядок для кожного
        // індексу. a[i][j] означає рядок i, стовпець j. На відміну від Python [[0]*n]*n, тут рядки не посилаються
        // на один спільний список.
        double[][] values = new double[n][n]; // Виділяємо окрему числову матрицю.
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) { // Обходимо кожну клітинку.
            // Double.parseDouble перетворює рядок у 64-бітне дробове число double, близький аналог float(text) у
            // Python. Десятковий роздільник має бути крапкою; replace коми, де він є, робить введення зручнішим.
            // Клітинка JTable повертає Object: це може бути число або введений рядок. String.valueOf приводить
            // його до текстового представлення; null перетвориться на "null", яке parseDouble відхилить, тому
            // незаповнена клітинка не стане мовчки нулем.
            double value = Double.parseDouble(String.valueOf(table.getValueAt(i, j)).replace(',', '.')); // Другий стандартний виняток: NumberFormatException.
            // Double.isFinite(...) повертає true лише для звичайного скінченного числа; ! заперечує результат.
            // double може містити NaN («не число») і ±Infinity, тому успішний parseDouble ще не гарантує
            // придатність числа для формули. У Python аналог — math.isfinite(x); ця перевірка не перевіряє
            // точність округлення.
            // throw — аналог raise у Python: негайно припиняємо звичайний хід методу й передаємо об’єкт помилки
            // найближчому відповідному catch. new створює виняток, а текст конструктора пояснює причину
            // користувачу.
            if (!Double.isFinite(value)) throw new NumberFormatException("Клітинки мають містити скінченні числа."); // Відхиляємо NaN та нескінченності.
            values[i][j] = value; // Зберігаємо перевірене число.
        }
        return values; // Повертаємо дані для чистого алгоритму.
    }
    private void error(Exception e) { // Єдиний спосіб показувати тип і причину помилки.
        // getClass() повертає фактичний клас об’єкта, а getSimpleName() — його коротку назву без пакета. Це
        // близько до type(obj).__name__ у Python; так можна відрізнити Ellipse від Hyperbola навіть через змінну
        // базового типу.
        JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), "Помилка даних", JOptionPane.ERROR_MESSAGE); // Користувач може виправити дані, вікно залишається відкритим.
    }
    private JPanel matrices() { // Будуємо вкладку з матрицями та елементами керування.
        // BorderLayout розподіляє компоненти по областях: NORTH — зверху, SOUTH — знизу, CENTER — решта місця. Це
        // менеджер розміщення: позиції перебудовуються при зміні розміру вікна.
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
        // GridLayout ділить місце на сітку однакових клітинок: перші два числа — рядки й стовпці, наступні, якщо
        // задані, — проміжки. Додавання компонентів іде послідовно зліва направо й зверху вниз.
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
        // addActionListener реєструє дію на майбутнє натискання або вибір. event -> ... — лямбда, приблизно
        // callback у Python: код виконається під час події, а не в момент реєстрації. Swing викликає такі
        // обробники на своєму потоці EDT.
        resize.addActionListener(event -> { // Обробник кнопки працює на EDT.
            try { // Перевіряємо число до створення нового масиву.
                // Integer.parseInt перетворює текст у примітивний int, подібно до int(text) у Python. Некоректний
                // текст або число за межами -2147483648..2147483647 спричиняє NumberFormatException; тип змінної в
                // Java при цьому залишається фіксованим.
                // trim() повертає рядок без звичайних крайніх пробілів і символів із кодами до U+0020;
                // оригінальний String не змінюється. Це близько до strip() у Python, але набір прибраних символів
                // не цілком однаковий.
                int n = Integer.parseInt(size.getText().trim()); // Неправильний формат породжує стандартний виняток.
                MatrixData.checkSize(n); // n=0 або n=16 демонструє власний виняток.
                fill(tableA, new double[n][n]); // Створюємо нову нульову A.
                fill(tableB, new double[n][n]); // Створюємо нову нульову B.
                result.setText("Вектор X: заповніть таблиці"); // Прибираємо застарілий результат.
            // catch — аналог except у Python: ця гілка виконується лише після відповідної помилки в try. e —
            // об’єкт винятку, getMessage() дає його пояснення; вертикальна риска між типами дозволяє одним блоком
            // обробити кілька видів помилок.
            } catch (NumberFormatException | MatrixSizeException e) { error(e); } // Обидва види помилок мають видиме пояснення.
        });
        load.addActionListener(event -> { // Читання демонстраційного або власного файлу.
            try { // Всі дані перевіряємо до оновлення таблиць.
                // Path — об’єкт шляху, приблизно pathlib.Path у Python. Path.of тільки розбирає запис шляху, а не
                // створює файл; відносний шлях рахується від робочої папки запущеної програми.
                double[][][] values = MatrixData.read(Path.of(path.getText())); // IOException, NumberFormatException або власний виняток.
                fill(tableA, values[0]); // Після успішної перевірки оновлюємо A.
                fill(tableB, values[1]); // Оновлюємо B тією самою розмірністю.
                size.setText(Integer.toString(values[0].length)); // Узгоджуємо поле n з фактичною таблицею.
                result.setText("Файл прочитано. Натисніть «Обчислити X»."); // Підказуємо наступну дію.
            } catch (IOException | NumberFormatException | MatrixSizeException | java.nio.file.InvalidPathException e) { error(e); } // Обробляємо всі заявлені сценарії помилок.
        });
        calculate.addActionListener(event -> { // Обчислення використовує актуальні клітинки обох таблиць.
            // Arrays.toString будує читабельний рядок на кшталт [1.0, 2.0]. Звичайний друк масиву в Java не
            // показує його елементи, як Python print(list), тому тут потрібен окремий метод.
            try { result.setText("Вектор X = " + Arrays.toString(MatrixData.compare(readTable(tableA), readTable(tableB)))); } // Показуємо результат під таблицями.
            catch (NumberFormatException e) { error(e); } // Текст замість числа не закриває програму.
        });
        return panel; // Повертаємо зібрану вкладку.
    }
    // public дозволяє Java знайти точку входу; static означає виклик без new Main(); void означає, що метод не
    // повертає значення. String[] args — масив аргументів запуску без назви програми (на відміну від Python
    // sys.argv). Тут починається виконання, приблизно як у блоці if __name__ == "__main__" у Python.
    // static означає, що метод належить класу: його можна викликати без створення об’єкта. public дозволяє виклик
    // з інших класів, private обмежує використання цим класом; тип перед назвою задає результат, а void означає
    // відсутність значення для повернення.
    public static void main(String[] args) { // JVM запускає main поза EDT.
        // invokeLater ставить дію в чергу EDT — спеціального потоку, який обробляє події та малює Swing. () ->
        // {...} — лямбда без параметрів. Створення й зміни віджетів тримаємо в цьому потоці; довге очікування в
        // ньому зробило б вікно нечутливим.
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
            // DISPOSE_ON_CLOSE закриває й звільняє це вікно, але не завершує примусово всі потоки програми. Тому
            // windowClosed окремо зупиняє таймер і, де є, робочі потоки.
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Закриваємо це вікно без примусового System.exit.
            // new WindowAdapter() {...} створює анонімний підклас прямо тут. Перевизначаємо лише потрібну подію
            // windowClosed; решту методів уже реалізовано порожніми в адаптері.
            frame.addWindowListener(new WindowAdapter() { // При закритті зупиняємо джерело подій анімації.
                // @Override просить компілятор перевірити, що метод справді замінює успадкований метод або
                // реалізує метод інтерфейсу. Якщо помилитися в назві чи параметрах, Java повідомить про це ще до
                // запуску.
                @Override public void windowClosed(WindowEvent event) { rotation.stop(); } // Після закриття таймер більше не утримує процес.
            });
            frame.setLocationRelativeTo(null); // Центруємо вікно на екрані.
            frame.setVisible(true); // Відображаємо повністю зібране вікно.
        });
    }
}
