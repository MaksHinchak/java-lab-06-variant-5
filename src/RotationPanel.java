import javax.swing.JPanel; // Панель є областю власного малювання.
import javax.swing.Timer; // Swing Timer викликає обробник у потоці EDT.
import java.awt.Color; // Колір напрямного та обертового відрізків.
import java.awt.Graphics; // Початковий графічний контекст Swing.
import java.awt.Graphics2D; // Розширений контекст підтримує якісне малювання.
import java.awt.BasicStroke; // Керуємо товщиною ліній.
import java.awt.RenderingHints; // Увімкнення згладжування контурів.
public final class RotationPanel extends JPanel { // Точка опори рухається по горизонтальному напрямному відрізку, синій відрізок обертається навколо неї.
    private double time = 0; // Накопичений час анімації у секундах.
    private long previous = System.nanoTime(); // Монотонний годинник для вимірювання інтервалів.
    private final Timer timer; // Таймер зберігається для коректної зупинки при закритті.
    public RotationPanel() { // Ініціалізуємо панель і механізм оновлення.
        setBackground(Color.WHITE); // Біле тло робить лінії та написи читабельними.
        timer = new Timer(16, event -> { // Приблизно 60 оновлень на секунду без окремого робочого потоку.
            long now = System.nanoTime(); // Зчитуємо поточний монотонний час.
            time += (now - previous) / 1_000_000_000.0; // Переводимо наносекунди в секунди незалежно від частоти кадрів.
            previous = now; // Зберігаємо початок наступного інтервалу.
            repaint(); // Просимо Swing перемалювати панель, не викликаючи paintComponent напряму.
        });
        timer.start(); // Запускаємо події таймера.
    }
    public void stop() { timer.stop(); } // Зупиняємо таймер, коли вікно закривається.
    @Override protected void paintComponent(Graphics graphics) { // Swing викликає метод на EDT, коли панель треба оновити.
        super.paintComponent(graphics); // Очищуємо попередній кадр і малюємо тло.
        Graphics2D g = (Graphics2D) graphics.create(); // Створюємо копію контексту, щоб локальні налаштування не витікали назовні.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Згладжуємо похилі лінії.
        int left = 150; // Ліва межа напрямного відрізка.
        int right = Math.max(left, getWidth() - 150); // Права межа враховує поточну ширину панелі.
        int cy = getHeight() / 2; // Напрямний відрізок проходить посередині панелі.
        double fraction = (1 + Math.sin(time * 0.7)) / 2; // Значення 0..1 задає плавний рух точки туди й назад.
        double cx = left + (right - left) * fraction; // Перетворюємо частку шляху на екранну координату x точки опори.
        double angle = time; // Кутова швидкість становить 1 радіан за секунду.
        double dx = 110 * Math.cos(angle); // Горизонтальна складова половини обертового відрізка.
        double dy = 110 * Math.sin(angle); // Вертикальна складова половини відрізка.
        g.setColor(Color.LIGHT_GRAY); // Напрямний шлях малюємо нейтральним кольором.
        g.drawLine(left, cy, right, cy); // Показуємо відрізок, по якому переміщується точка.
        g.setStroke(new BasicStroke(4)); // Обертову лінію робимо помітнішою.
        g.setColor(new Color(30, 95, 180)); // Вибираємо синій колір обертового відрізка.
        g.drawLine((int) (cx - dx), (int) (cy - dy), (int) (cx + dx), (int) (cy + dy)); // Обидва кінці обертаються навколо поточної точки (cx,cy).
        g.setColor(new Color(220, 70, 50)); // Точку опори виділяємо червоним.
        g.fillOval((int) cx - 6, cy - 6, 12, 12); // Малюємо коло радіуса 6 навколо точки опори.
        g.drawString("Червона точка рухається по сірому відрізку; синій обертається навколо неї.", 24, 35); // Пояснюємо прийняте трактування руху без технічних деталей.
        g.dispose(); // Звільняємо створену копію графічного контексту.
    }
}
