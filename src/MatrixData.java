import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
// final class — від цього класу не можна успадковуватися.
public final class MatrixData {
    // static — виклик без об’єкта; public — доступ ззовні, private — лише в класі; void — без результату.
    public static void checkSize(int n) { // Спільне правило допустимого розміру.
        // throw передає помилку в catch; new створює об’єкт винятку з повідомленням.
        // && — «і», || — «або»; праву умову перевіряють лише за потреби.
        if (n < 1 || n > 15) throw new MatrixSizeException("Розмір n має бути від 1 до 15."); // Демонструємо власний виняток на конкретних даних n=0 або n=16.
    }
    public static int[] compare(double[][] a, double[][] b) { // Алгоритм повторює лабораторну 1, завдання 3.
        // new тип[n] — масив незмінної довжини; числові елементи спочатку 0, посилання — null.
        // .length — довжина масиву без дужок; для String — length(), для колекції — size().
        int[] result = new int[a.length];
        // for (початок; умова; крок); i++ збільшує лічильник після проходу.
        for (int i = 0; i < a.length; i++) { // Перебираємо рядки квадратних матриць.
            result[i] = 1;
            for (int j = 0; j < a.length; j++) if (a[i][j] <= b[i][j]) result[i] = 0; // Рівність також порушує строгу нерівність.
        }
        return result;
    }
    // throws оголошує перевірюваний виняток: викликач мусить перехопити його або теж оголосити.
    public static double[][][] read(Path path) throws IOException { // Формат: n, потім n*n чисел A, потім n*n чисел B.
        String text = Files.readString(path, StandardCharsets.UTF_8).trim();
        if (text.isEmpty()) throw new NumberFormatException("Файл порожній."); // Порожній файл не містить навіть розміру.
        // String.split приймає regex: "\\s+" — пробіли, "\\." — крапка; -1 зберігає кінцеві порожні частини.
        String[] parts = text.split("\\s+");
        // parseInt/parseDouble перетворюють текст на число; помилка формату — NumberFormatException.
        int n = Integer.parseInt(parts[0]);
        checkSize(n);
        if (parts.length != 1 + 2 * n * n) throw new NumberFormatException("Після n потрібно рівно " + 2 * n * n + " чисел."); // Відхиляємо і недостатні, і зайві дані.
        // double[][] — масив окремих рядків; індекси [рядок][стовпець].
        // Три індекси: [номер матриці][рядок][стовпець].
        double[][][] values = new double[2][n][n];
        int index = 1;
        for (int matrix = 0; matrix < 2; matrix++) { // Послідовно заповнюємо дві матриці.
            for (int i = 0; i < n; i++) { // Перебираємо рядки.
                for (int j = 0; j < n; j++) { // Перебираємо стовпці.
                    // index++ спочатку використовує поточний індекс, потім збільшує його.
                    double value = Double.parseDouble(parts[index++].replace(',', '.'));
                    // Double.isFinite відкидає NaN та ±Infinity; ! заперечує перевірку.
                    if (!Double.isFinite(value)) throw new NumberFormatException("NaN та Infinity заборонені."); // Забороняємо спеціальні значення double.
                    values[matrix][i][j] = value;
                }
            }
        }
        return values;
    }
}
