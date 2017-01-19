package pl.poznan.put.bsr.bank.models.bankOperations;

import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Payment bank operation class
 * @author Kamil Walkowiak
 */
@XmlRootElement(name = "payment")
public class Payment extends BankOperation {
    /**
     * Empty constructor for ORM
     */
    public Payment() {
    }

    /**
     * Creates new payment bank operation object
     * @param title payment title
     * @param amount payment amount
     * @param targetAccountNo payment target account
     */
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
