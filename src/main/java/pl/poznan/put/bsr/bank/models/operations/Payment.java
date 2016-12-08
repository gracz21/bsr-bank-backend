package pl.poznan.put.bsr.bank.models.operations;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.services.exceptions.BankOperationException;
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
    public void doOperation() throws BankOperationException {
        super.doOperation();
        if(amount < 0) {
            throw new BankOperationException("Negative amount");
        }

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
        if(bankAccount == null) {
            throw new BankOperationException("Target bank account does not exist");
        }

        bankAccount.setBalance(bankAccount.getBalance() + amount);
        executed = true;
    }
}
