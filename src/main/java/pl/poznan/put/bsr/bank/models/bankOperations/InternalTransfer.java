package pl.poznan.put.bsr.bank.models.bankOperations;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;

import javax.validation.constraints.NotNull;

/**
 * @author Kamil Walkowiak
 */
public class InternalTransfer extends BankOperation {
    @NotNull
    private String sourceAccountNo;

    public InternalTransfer() {
    }

    InternalTransfer(String title, double amount, String sourceAccountNo, String targetAccountNo) {
        super(title, amount, targetAccountNo);
        this.sourceAccountNo = sourceAccountNo;
    }

    public String getSourceAccountNo() {
        return sourceAccountNo;
    }

    public void setSourceAccountNo(String sourceAccountNo) {
        this.sourceAccountNo = sourceAccountNo;
    }

    @Override
    protected void execute(Datastore datastore) throws BankOperationException {
        BankAccount sourceBankAccount = datastore.find(BankAccount.class)
                .field("accountNo").equal(sourceAccountNo).get();
        BankAccount targetBankAccount = datastore.find(BankAccount.class)
                .field("accountNo").equal(targetAccountNo).get();

        if(sourceBankAccount == null) {
            throw new BankOperationException("Source bank account does not exist");
        }
        if(targetBankAccount == null) {
            throw new BankOperationException("Target bank account does not exist");
        }

        sourceBankAccount.setBalance(sourceBankAccount.getBalance() - amount);
        targetBankAccount.setBalance(targetBankAccount.getBalance() + amount);
    }
}
