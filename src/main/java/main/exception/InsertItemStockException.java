package main.exception;

public class InsertItemStockException extends RuntimeException {

    public enum Reason {
        DUPLICATE_KEY,
        CONNECTION_FAILURE,
        UNKNOWN
    }

    private final Reason reason;

    public InsertItemStockException(String message, Reason reason) {
        super(message);
        this.reason = reason;
    }

    public InsertItemStockException(String message, Reason reason, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
