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
    protected void execute(Datastore datastore) throws BankOperationException {
        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
        if(bankAccount == null) {
            throw new BankOperationException("Target bank account does not exist");
        }

        bankAccount.setBalance(bankAccount.getBalance() - amount);
    }
}
