package pl.poznan.put.bsr.bank.exceptions;

/**
 * Bank operation execution exception class
 * @author Kamil Walkowiak
 */
public class BankOperationException extends Exception {
    /**
     * Creates new bank operation exception
     * @param message exception explanation message
     */
    public BankOperationException(String message) {
        super(message);
    }
}
