package pl.poznan.put.bsr.bank.models.bankOperations;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;

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
    protected void execute(BankAccount bankAccount) throws BankOperationException {
        if(amount > bankAccount.getBalance()) {
            throw new BankOperationException("Amount bigger than current account balance");
        }

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        this.balanceAfter = bankAccount.getBalance();
        bankAccount.addBankOperation(this);
    }
}
