import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

final class PcPanel extends JPanel {
    private final JComboBox<Part> cpu = new JComboBox<>(new Part[]{
            new Part("Intel Core i3", 3500), new Part("AMD Ryzen 5", 6000), new Part("Intel Core i7", 11000)
    });
    private final JComboBox<Part> gpu = new JComboBox<>(new Part[]{
            new Part("RTX 3050", 8500), new Part("RTX 4060", 13000), new Part("RX 7700 XT", 20000)
    });
    private final JRadioButton ram16 = new JRadioButton("16 ГБ · 1500 грн", true);
    private final JRadioButton ram32 = new JRadioButton("32 ГБ · 2800 грн");
    private final JRadioButton ram64 = new JRadioButton("64 ГБ · 5200 грн");
    private final JCheckBox monitor = new JCheckBox("Монітор · 4000 грн");
    private final JCheckBox mouse = new JCheckBox("Миша · 400 грн");
    private final JCheckBox keyboard = new JCheckBox("Клавіатура · 800 грн");
    private final JTextArea breakdown = new JTextArea(7, 36);

    PcPanel() {
        super(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        ButtonGroup memory = new ButtonGroup();
        memory.add(ram16);
        memory.add(ram32);
        memory.add(ram64);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.PAGE_AXIS));
        form.add(row(new JLabel("Процесор"), cpu));
        form.add(row(new JLabel("Відеокарта"), gpu));
        form.add(row(new JLabel("Оперативна пам’ять"), ram16, ram32, ram64));
        form.add(row(new JLabel("Периферія"), monitor, mouse, keyboard));
        add(form, BorderLayout.NORTH);

        breakdown.setEditable(false);
        add(new JScrollPane(breakdown), BorderLayout.CENTER);

        JButton calculate = new JButton("Підсумок");
        JButton reset = new JButton("Скинути");
        add(row(calculate, reset), BorderLayout.SOUTH);

        for (AbstractButton option : new AbstractButton[]{ram16, ram32, ram64, monitor, mouse, keyboard}) {
            option.addActionListener(event -> refresh());
        }
        cpu.addActionListener(event -> refresh());
        gpu.addActionListener(event -> refresh());
        calculate.addActionListener(event -> JOptionPane.showMessageDialog(this, breakdown.getText(),
                "Специфікація ПК", JOptionPane.INFORMATION_MESSAGE));
        reset.addActionListener(event -> {
            cpu.setSelectedIndex(0);
            gpu.setSelectedIndex(0);
            ram16.setSelected(true);
            monitor.setSelected(false);
            mouse.setSelected(false);
            keyboard.setSelected(false);
            refresh();
        });
        refresh();
    }

    private void refresh() {
        Part processor = (Part) cpu.getSelectedItem();
        Part graphics = (Part) gpu.getSelectedItem();
        int ram = ram64.isSelected() ? 5200 : ram32.isSelected() ? 2800 : 1500;
        String capacity = ram64.isSelected() ? "64 ГБ" : ram32.isSelected() ? "32 ГБ" : "16 ГБ";
        int total = processor.price() + graphics.price() + ram;
        StringBuilder text = new StringBuilder("Умовні ціни в гривнях\n")
                .append(processor).append('\n')
                .append(graphics).append('\n')
                .append(capacity).append(" ОЗП · ").append(ram).append(" грн\n");
        if (monitor.isSelected()) {
            text.append("Монітор · 4000 грн\n");
            total += 4000;
        }
        if (mouse.isSelected()) {
            text.append("Миша · 400 грн\n");
            total += 400;
        }
        if (keyboard.isSelected()) {
            text.append("Клавіатура · 800 грн\n");
            total += 800;
        }
        breakdown.setText(text.append("Разом — ").append(total).append(" грн").toString());
    }

    private static JPanel row(JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        for (JComponent component : components) {
            panel.add(component);
        }
        return panel;
    }

    private record Part(String name, int price) {
        @Override
        public String toString() {
            return name + " · " + price + " грн";
        }
    }
}
