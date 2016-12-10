package pl.poznan.put.bsr.bank.exceptions;

/**
 * @author Kamil Walkowiak
 */
public class BankServiceException extends Exception {
    public BankServiceException() {
    }

    public BankServiceException(String message) {
        super(message);
    }


}
