package pl.poznan.put.bsr.bank.exceptions;

/**
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
