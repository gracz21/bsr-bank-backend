package pl.poznan.put.bsr.bank.exceptions;

/**
 * Parameter validation exceptions class
 * @author Kamil Walkowiak
 */
public class ValidationException extends Exception {
    /**
     * Creates new validation exception
     * @param message exception explanation message
     */
    public ValidationException(String message) {
        super(message);
    }
}
