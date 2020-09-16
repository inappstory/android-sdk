package io.casestory.sdk.exceptions;

public class InitException extends Exception {

    String message;
    Throwable cause;

    public InitException() {
        super();
    }

    public InitException(String message, Throwable cause)
    {
        super("Need to initialize first", cause);

        this.cause = cause;
        this.message = "Need to initialize first";
    }
}
