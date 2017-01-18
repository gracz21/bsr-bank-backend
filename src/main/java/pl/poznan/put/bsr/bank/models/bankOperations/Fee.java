package pl.poznan.put.bsr.bank.models.bankOperations;

import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.utils.ConstantsUtil;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Kamil Walkowiak
 */
@XmlRootElement(name = "fee")
public class Fee extends BankOperation {
    public Fee() {
    }

    public Fee(String targetAccountNo) {
        super("Bank fee", ConstantsUtil.feeAmount, targetAccountNo);
    }

    @Override
    protected void execute(BankAccount bankAccount) throws BankOperationException {
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        this.balanceAfter = bankAccount.getBalance();
        bankAccount.addBankOperation(this);
    }
}
