package pl.poznan.put.bsr.bank.models.bankOperations;

import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Transfer bank operation class
 * @author Kamil Walkowiak
 */
@XmlRootElement(name = "transfer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transfer extends BankOperation {
    /**
     * Defines transfer object direction
     * (IN - object represents incoming operation, OUT - object represents outgoing operation)
     */
    public enum TransferDirection {
        IN, OUT
    }

    @XmlElement(name = "sender_account")
    @NotNull
    private String sourceAccountNo;
    @XmlTransient
    private TransferDirection direction;

    /**
     * Empty constructor for ORM
     */
    public Transfer() {
    }

    /**
     * Creates new transfer object
     * @param title transfer title
     * @param amount transfer amount
     * @param sourceAccountNo transfer source account
     * @param targetAccountNo transfer target account
     * @param direction transfer direction (IN or OUT)
     */
    public Transfer(String title, double amount, String sourceAccountNo, String targetAccountNo, TransferDirection direction) {
        super(title, amount, targetAccountNo);
        this.sourceAccountNo = sourceAccountNo;
        this.direction = direction;
    }

    /*
    Getter and setter methods for transfer class
     */

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
