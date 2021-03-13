package pl.edu.pwr.ontologydatagenerator.infrastructure.exception;

public class IllegalArgumentAppException extends RuntimeException {

    public IllegalArgumentAppException(String message) {
        super(message);
    }

    public IllegalArgumentAppException(String message, Throwable cause) {
        super(message, cause);
    }

}
