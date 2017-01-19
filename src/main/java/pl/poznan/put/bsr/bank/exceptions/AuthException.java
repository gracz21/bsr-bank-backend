package pl.poznan.put.bsr.bank.exceptions;

/**
 * Authorization exceptions class
 * @author Kamil Walkowiak
 */
public class AuthException extends Exception {
    /**
     * Creates new authentication exception
     * @param message exception explanation message
     */
    public AuthException(String message) {
        super(message);
    }
}
