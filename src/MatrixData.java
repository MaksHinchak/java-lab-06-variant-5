import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
public final class MatrixData {
    public static void checkSize(int n) {
        if (n < 1 || n > 15) throw new MatrixSizeException("Розмір n має бути від 1 до 15.");
    }
    public static int[] compare(double[][] a, double[][] b) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = 1;
            for (int j = 0; j < a.length; j++) if (a[i][j] <= b[i][j]) result[i] = 0;
        }
        return result;
    }
    public static double[][][] read(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8).trim();
        if (text.isEmpty()) throw new NumberFormatException("Файл порожній.");
        String[] parts = text.split("\\s+");
        int n = Integer.parseInt(parts[0]);
        checkSize(n);
        if (parts.length != 1 + 2 * n * n) throw new NumberFormatException("Після n потрібно рівно " + 2 * n * n + " чисел.");
        double[][][] values = new double[2][n][n];
        int index = 1;
        for (int matrix = 0; matrix < 2; matrix++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double value = Double.parseDouble(parts[index++].replace(',', '.'));
                    if (!Double.isFinite(value)) throw new NumberFormatException("NaN та Infinity заборонені.");
                    values[matrix][i][j] = value;
                }
            }
        }
        return values;
    }
}
