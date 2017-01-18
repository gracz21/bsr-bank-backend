package pl.poznan.put.bsr.bank.exceptions;

/**
 * Authorization exceptions class
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
