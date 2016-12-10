package pl.poznan.put.bsr.bank.models.bankOperations;

import pl.poznan.put.bsr.bank.exceptions.BankOperationException;

/**
 * @author Kamil Walkowiak
 */
public class Withdrawal extends BankOperation {
    public Withdrawal() {
    }

    public Withdrawal(String title, double amount, String targetAccountNo) {
        super(title, amount, targetAccountNo);
    }

    @Override
    public void doOperation() throws BankOperationException {
        super.doOperation();
    }
}
