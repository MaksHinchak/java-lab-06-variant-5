import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.security.SecureRandom;

final class PasswordPanel extends JPanel {
    private static final String DIGITS = "0123456789";
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String SYMBOLS = "!@#$%&*+-_?";
    private static final SecureRandom RANDOM = new SecureRandom();

    PasswordPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField length = new JTextField("12");
        JComboBox<String> mode = new JComboBox<>(new String[]{
                "Тільки цифри", "Букви та цифри", "Зі спецсимволами"
        });
        JTextField result = new JTextField();
        result.setEditable(false);
        JButton generate = new JButton("Згенерувати");
        JLabel explanation = new JLabel("Допустима довжина пароля — від 4 до 64 символів.");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Довжина"));
        form.add(length);
        form.add(new JLabel("Складність"));
        form.add(mode);
        form.add(new JLabel("Пароль"));
        form.add(result);
        form.add(new JLabel());
        form.add(generate);
        add(form, BorderLayout.NORTH);
        explanation.setVerticalAlignment(SwingConstants.TOP);
        add(explanation, BorderLayout.CENTER);

        generate.addActionListener(event -> {
            try {
                int requested = Integer.parseInt(length.getText().trim());
                String password = generatePassword(requested, mode.getSelectedIndex());
                result.setText(password);
                explanation.setText("4 ≤ " + requested + " ≤ 64. Створено " + password.length()
                        + " символів. Режим — " + mode.getSelectedItem() + ".");
            } catch (IllegalArgumentException error) {
                result.setText("");
                explanation.setText("Допустима довжина пароля — від 4 до 64 символів.");
                JOptionPane.showMessageDialog(this, "Введіть цілу довжину від 4 до 64.",
                        "Некоректна довжина", JOptionPane.ERROR_MESSAGE);
            }
        });
        length.addActionListener(event -> generate.doClick());
    }

    static String generatePassword(int length, int mode) {
        if (length < 4 || length > 64 || mode < 0 || mode > 2) {
            throw new IllegalArgumentException();
        }
        String alphabet = switch (mode) {
            case 0 -> DIGITS;
            case 1 -> DIGITS + LETTERS;
            default -> DIGITS + LETTERS + SYMBOLS;
        };
        char[] password = new char[length];
        for (int i = 0; i < password.length; i++) {
            password[i] = pick(alphabet);
        }
        password[0] = pick(DIGITS);
        if (mode >= 1) {
            password[1] = pick(LETTERS);
        }
        if (mode == 2) {
            password[2] = pick(SYMBOLS);
        }
        for (int i = password.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char previous = password[i];
            password[i] = password[j];
            password[j] = previous;
        }
        return new String(password);
    }

    private static char pick(String alphabet) {
        return alphabet.charAt(RANDOM.nextInt(alphabet.length()));
    }
}
