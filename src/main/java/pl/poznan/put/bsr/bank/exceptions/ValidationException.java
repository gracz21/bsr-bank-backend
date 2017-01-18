package pl.poznan.put.bsr.bank.exceptions;

/**
 * Parameter validation exceptions class
 * @author Kamil Walkowiak
 */
public class ValidationException extends Exception {
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }
}
