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
