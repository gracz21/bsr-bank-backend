package pl.poznan.put.bsr.bank.exceptions;

/**
 * Bank operation execution exception class
 * @author Kamil Walkowiak
 */
public class BankOperationException extends Exception {
    public BankOperationException() {
    }

    public BankOperationException(String message) {
        super(message);
    }
}
