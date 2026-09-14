public class LabChecks { // Автономні перевірки без зовнішніх бібліотек тестування.
    private static int count = 0; // Лічильник успішно перевірених умов.
    private static void check(boolean value) { // Допоміжний метод перетворює хибну умову на провал тесту.
        if (!value) throw new AssertionError("Перевірка " + (count + 1) + " не пройшла."); // Зупиняємо тест із ненульовим кодом процесу.
        count++; // Рахуємо лише успішні перевірки.
    }
    private static void near(double actual, double expected) { // Порівнюємо double з допуском на округлення.
        check(Math.abs(actual - expected) <= 1e-9 * Math.max(1, Math.abs(expected))); // Допуск враховує масштаб очікуваного числа.
    }
    private interface Action { void run() throws Exception; } // Лямбда тесту може породжувати і перевірювані винятки.
    private static void expect(Class<? extends Throwable> type, Action action) { // Перевіряємо, що помилкові дані дають саме потрібний вид помилки.
        try { action.run(); } // Виконуємо потенційно помилкову операцію.
        catch (Throwable error) { check(type.isInstance(error)); return; } // Неправильний тип винятку теж провалює тест.
        throw new AssertionError("Очікували " + type.getSimpleName()); // Відсутність потрібного винятку є помилкою реалізації.
    }
    public static void main(String[] args) throws Exception { // Метод запускає усі перевірки цієї лабораторної.
        double[][][] data = MatrixData.read(java.nio.file.Path.of("data/matrices.txt")); // Читаємо реальний демонстраційний файл.
        check(java.util.Arrays.equals(MatrixData.compare(data[0],data[1]),new int[]{1,0})); // Відомий результат перевіряє алгоритм і порядок читання матриць.
        expect(MatrixSizeException.class, () -> MatrixData.checkSize(0)); // Нижня межа власного винятку.
        expect(MatrixSizeException.class, () -> MatrixData.checkSize(16)); // Верхня межа обмеження n<=15.
        MatrixData.checkSize(1); MatrixData.checkSize(15); // Обидві включені межі мають бути дозволені.
        expect(NumberFormatException.class, () -> MatrixData.read(java.nio.file.Path.of("data/bad-format.txt"))); // Нечислове значення перевіряє стандартний виняток формату.
        expect(java.io.IOException.class, () -> MatrixData.read(java.nio.file.Path.of("data/missing-file.txt"))); // Відсутній файл перевіряє другий стандартний виняток.
        expect(MatrixSizeException.class, () -> MatrixData.read(java.nio.file.Path.of("data/bad-size.txt"))); // Власна помилка має працювати також під час читання файлу.
        System.out.println("OK: " + count + " перевірок"); // Видимий підсумок після успішного виконання.
    }
}
