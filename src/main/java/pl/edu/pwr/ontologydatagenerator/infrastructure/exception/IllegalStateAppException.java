package pl.edu.pwr.ontologydatagenerator.infrastructure.exception;

public class IllegalStateAppException extends RuntimeException {

    public IllegalStateAppException(String message) {
        super(message);
    }

    public IllegalStateAppException(Throwable cause) {
        super(cause);
    }

    public IllegalStateAppException(String message, Throwable cause) {
        super(message, cause);
    }

}

