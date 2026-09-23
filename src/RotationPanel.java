import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class RotationPanel extends JPanel {
    private double time = 0;
    private long previous = System.nanoTime(); // Монотонний годинник для вимірювання інтервалів.
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final Timer timer;
    public RotationPanel() { // Ініціалізуємо панель і механізм оновлення.
        setBackground(Color.WHITE);
        // Swing Timer викликає обробник на EDT; затримка задається в мілісекундах.
        // new Клас(...) створює об’єкт і викликає його конструктор.
        timer = new Timer(16, event -> { // Приблизно 60 оновлень на секунду без окремого робочого потоку.
            long now = System.nanoTime();
            time += (now - previous) / 1_000_000_000.0;
            previous = now;
            repaint();
        });
        timer.start();
    }
    public void stop() { timer.stop(); } // Зупиняємо таймер, коли вікно закривається.
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override protected void paintComponent(Graphics graphics) { // Swing викликає метод на EDT, коли панель треба оновити.
        super.paintComponent(graphics);
        // (Graphics2D) уточнює тип копії графічного контексту; копію потім звільняє dispose().
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int left = 150;
        int right = Math.max(left, getWidth() - 150);
        int cy = getHeight() / 2;
        double fraction = (1 + Math.sin(time * 0.7)) / 2;
        double cx = left + (right - left) * fraction;
        double angle = time;
        double dx = 110 * Math.cos(angle);
        double dy = 110 * Math.sin(angle);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(left, cy, right, cy);
        g.setStroke(new BasicStroke(4));
        g.setColor(new Color(30, 95, 180));
        // (int) відкидає дробову частину до нуля; int обмежений 32 бітами.
        g.drawLine((int) (cx - dx), (int) (cy - dy), (int) (cx + dx), (int) (cy + dy));
        g.setColor(new Color(220, 70, 50));
        g.fillOval((int) cx - 6, cy - 6, 12, 12);
        g.drawString("Червона точка рухається по сірому відрізку; синій обертається навколо неї.", 24, 35);
        g.dispose();
    }
}
