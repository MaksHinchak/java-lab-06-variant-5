import java.io.IOException; // Стандартний виняток для помилок читання файлу.
import java.nio.charset.StandardCharsets; // UTF-8 робить текстовий формат переносним.
import java.nio.file.Files; // Читаємо вхідний текст із вибраного шляху.
import java.nio.file.Path; // Представляємо ім'я файлу об'єктом Path.
public final class MatrixData { // Обчислення відокремлено від Swing для перевірки без графічного екрана.
    public static void checkSize(int n) { // Спільне правило допустимого розміру.
        if (n < 1 || n > 15) throw new MatrixSizeException("Розмір n має бути від 1 до 15."); // Демонструємо власний виняток на конкретних даних n=0 або n=16.
    }
    public static int[] compare(double[][] a, double[][] b) { // Алгоритм повторює лабораторну 1, завдання 3.
        int[] result = new int[a.length]; // Один результат для кожного рядка.
        for (int i = 0; i < a.length; i++) { // Перебираємо рядки квадратних матриць.
            result[i] = 1; // Початкова гіпотеза: всі елементи A строго більші.
            for (int j = 0; j < a.length; j++) if (a[i][j] <= b[i][j]) result[i] = 0; // Рівність також порушує строгу нерівність.
        }
        return result; // Повертаємо вектор нулів та одиниць.
    }
    public static double[][][] read(Path path) throws IOException { // Формат: n, потім n*n чисел A, потім n*n чисел B.
        String text = Files.readString(path, StandardCharsets.UTF_8).trim(); // Помилка відкриття природно породжує IOException.
        if (text.isEmpty()) throw new NumberFormatException("Файл порожній."); // Порожній файл не містить навіть розміру.
        String[] parts = text.split("\\s+"); // Рядки та пробіли рівнозначно відділяють числа.
        int n = Integer.parseInt(parts[0]); // Нечисловий розмір породжує NumberFormatException.
        checkSize(n); // Неприпустимий числовий розмір породжує MatrixSizeException.
        if (parts.length != 1 + 2 * n * n) throw new NumberFormatException("Після n потрібно рівно " + 2 * n * n + " чисел."); // Відхиляємо і недостатні, і зайві дані.
        double[][][] values = new double[2][n][n]; // Перший індекс обирає матрицю A або B.
        int index = 1; // Перший елемент уже використано для n.
        for (int matrix = 0; matrix < 2; matrix++) { // Послідовно заповнюємо дві матриці.
            for (int i = 0; i < n; i++) { // Перебираємо рядки.
                for (int j = 0; j < n; j++) { // Перебираємо стовпці.
                    double value = Double.parseDouble(parts[index++].replace(',', '.')); // Читаємо наступне число й пересуваємо індекс.
                    if (!Double.isFinite(value)) throw new NumberFormatException("NaN та Infinity заборонені."); // Забороняємо спеціальні значення double.
                    values[matrix][i][j] = value; // Записуємо число в потрібну клітинку.
                }
            }
        }
        return values; // Повертаємо пару повністю перевірених матриць.
    }
}
