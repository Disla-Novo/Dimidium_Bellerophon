package maindeveloper.core;

public class BellerophonException extends RuntimeException {
    public final int line;

    public BellerophonException(int line, String message) {
        super(message);
        this.line = line;
    }
}