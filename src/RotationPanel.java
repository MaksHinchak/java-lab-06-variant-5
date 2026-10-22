import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
public final class RotationPanel extends JPanel {
    private double time = 0;
    private long previous = System.nanoTime();
    private final Timer timer;
    public RotationPanel() {
        setBackground(Color.WHITE);
        timer = new Timer(16, event -> {
            long now = System.nanoTime();
            time += (now - previous) / 1_000_000_000.0;
            previous = now;
            repaint();
        });
        timer.start();
    }
    public void stop() { timer.stop(); }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
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
        g.drawLine((int) (cx - dx), (int) (cy - dy), (int) (cx + dx), (int) (cy + dy));
        g.setColor(new Color(220, 70, 50));
        g.fillOval((int) cx - 6, cy - 6, 12, 12);
        g.drawString("Червона точка рухається по сірому відрізку; синій обертається навколо неї.", 24, 35);
        g.dispose();
    }
}
