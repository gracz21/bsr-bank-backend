package pl.poznan.put.bsr.bank.exceptions;

/**
 * Bank service runtime exceptions class
 * @author Kamil Walkowiak
 */
public class BankServiceException extends Exception {
    public BankServiceException() {
    }

    public BankServiceException(String message) {
        super(message);
    }


}
