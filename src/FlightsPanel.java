import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

final class FlightsPanel extends JPanel {
    private static final int STATUS_COLUMN = 4;
    private static final String ON_TIME = "Вчасно";
    private static final String DELAYED = "Запізнюється";
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[][]{
                    {"101", "Чернівці → Київ", "08:30", "1", ON_TIME},
                    {"204", "Чернівці → Львів", "10:15", "2", DELAYED},
                    {"315", "Чернівці → Івано-Франківськ", "12:00", "3", ON_TIME}
            }, new String[]{"Номер рейсу", "Напрямок", "Час відправлення", "Платформа", "Статус"}) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final JLabel explanation = new JLabel("Оберіть рейс для зміни статусу або застосуйте фільтр у меню.");
    private final JMenuBar menu = new JMenuBar();

    FlightsPanel() {
        super(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        sorter.setSortsOnUpdates(true);

        JMenu file = new JMenu("Файл");
        JMenuItem clear = new JMenuItem("Очистити таблицю");
        clear.addActionListener(event -> {
            model.setRowCount(0);
            explanation.setText("Таблицю очищено.");
        });
        JMenuItem exit = new JMenuItem("Вихід");
        exit.addActionListener(event -> SwingUtilities.getWindowAncestor(this).dispose());
        file.add(clear);
        file.add(exit);

        JMenu filter = new JMenu("Фільтр");
        filter.add(filterItem("Усі рейси", null));
        filter.add(filterItem(ON_TIME, ON_TIME));
        filter.add(filterItem(DELAYED, DELAYED));

        JMenu help = new JMenu("Довідка");
        JMenuItem about = new JMenuItem("Про програму");
        about.addActionListener(event -> JOptionPane.showMessageDialog(this,
                "Автор — Максим Гінчак\nНавчальна лабораторна №6, варіант 5.",
                "Про програму", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);
        menu.add(file);
        menu.add(filter);
        menu.add(help);

        JComboBox<String> status = new JComboBox<>(new String[]{ON_TIME, DELAYED});
        JButton change = new JButton("Змінити статус");
        change.addActionListener(event -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                explanation.setText("Спочатку оберіть рейс у таблиці.");
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            Object number = model.getValueAt(row, 0);
            Object previous = model.getValueAt(row, STATUS_COLUMN);
            String next = (String) status.getSelectedItem();
            model.setValueAt(next, row, STATUS_COLUMN);
            String filtered = table.convertRowIndexToView(row) < 0 ? " Поточний фільтр приховав цей рейс." : "";
            explanation.setText("Рейс " + number + ". Статус був «" + previous + "», став «" + next + "»." + filtered);
        });
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Новий статус"));
        controls.add(status);
        controls.add(change);

        add(controls, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(explanation, BorderLayout.SOUTH);
    }

    JMenuBar menuBar() {
        return menu;
    }

    private JMenuItem filterItem(String title, String status) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(event -> {
            sorter.setRowFilter(status == null ? null : RowFilter.regexFilter("^" + status + "$", STATUS_COLUMN));
            explanation.setText(status == null ? "Показано всі рейси. Рядків — " + table.getRowCount()
                    : "Порівнюємо статус із «" + status + "». Збігів — " + table.getRowCount() + ".");
        });
        return item;
    }
}
