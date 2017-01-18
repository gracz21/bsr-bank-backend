package pl.poznan.put.bsr.bank.models.bankOperations;

import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * @author Kamil Walkowiak
 */
@XmlRootElement(name = "transfer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transfer extends BankOperation {
    public enum TransferDirection {
        IN, OUT
    }

    @XmlElement(name = "sender_account")
    @NotNull
    private String sourceAccountNo;
    @XmlTransient
    private TransferDirection direction;

    public Transfer() {
    }

    public Transfer(String title, double amount, String sourceAccountNo, String targetAccountNo, TransferDirection direction) {
        super(title, amount, targetAccountNo);
        this.sourceAccountNo = sourceAccountNo;
        this.direction = direction;
    }

    public String getSourceAccountNo() {
        return sourceAccountNo;
    }

    public void setSourceAccountNo(String sourceAccountNo) {
        this.sourceAccountNo = sourceAccountNo;
    }

    public TransferDirection getDirection() {
        return direction;
    }

    public void setDirection(TransferDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void execute(BankAccount bankAccount) throws BankOperationException {
        if (direction == TransferDirection.IN) {
            bankAccount.setBalance(bankAccount.getBalance() + amount);
        } else {
            if (amount > bankAccount.getBalance()) {
                throw new BankOperationException("Amount bigger than current account balance");
            }

            bankAccount.setBalance(bankAccount.getBalance() - amount);
        }
        this.balanceAfter = bankAccount.getBalance();
        bankAccount.addBankOperation(this);
    }
}
