package pl.poznan.put.bsr.bank.models.bankOperations;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

/**
 * @author Kamil Walkowiak
 */
public class Payment extends BankOperation {
    public Payment() {
    }

    public Payment(String title, double amount, String targetAccountNo) {
        super(title, amount, targetAccountNo);
    }

    @Override
    protected void execute(BankAccount bankAccount) throws BankOperationException {
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        this.balanceAfter = bankAccount.getBalance();
        bankAccount.addBankOperation(this);
    }
}
