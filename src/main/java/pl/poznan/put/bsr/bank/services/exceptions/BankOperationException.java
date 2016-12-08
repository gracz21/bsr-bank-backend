package pl.poznan.put.bsr.bank.services.exceptions;

/**
 * @author Kamil Walkowiak
 */
public class BankOperationException extends Exception {
    public BankOperationException() {
    }

    public BankOperationException(String message) {
        super(message);
    }
}
