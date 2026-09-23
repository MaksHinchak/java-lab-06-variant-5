// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class MatrixSizeException extends ArithmeticException {
    // super(...) — виклик конструктора батьківського класу.
    public MatrixSizeException(String message) { super(message); }
}
