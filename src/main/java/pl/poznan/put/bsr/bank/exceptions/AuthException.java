package pl.poznan.put.bsr.bank.exceptions;

/**
 * @author Kamil Walkowiak
 */
public class AuthException extends Exception {
    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }
}
