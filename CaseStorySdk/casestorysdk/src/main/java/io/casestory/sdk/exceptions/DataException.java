package io.casestory.sdk.exceptions;

public class DataException extends Exception {
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    String message;
    Throwable cause;

    public DataException() {
        super();
    }

    public DataException(String message, Throwable cause)
    {
        super(message, cause);

        this.cause = cause;
        this.message = message;
    }
}
