package pl.poznan.put.bsr.bank.exceptions;

/**
 * Bank service runtime exceptions class
 * @author Kamil Walkowiak
 */
public class BankServiceException extends Exception {
    /**
     * Creates new bank service exception
     * @param message exception explanation message
     */
    public BankServiceException(String message) {
        super(message);
    }
}
