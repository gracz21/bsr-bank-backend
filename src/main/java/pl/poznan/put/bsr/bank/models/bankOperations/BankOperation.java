package pl.poznan.put.bsr.bank.models.bankOperations;

import org.mongodb.morphia.annotations.Embedded;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * @author Kamil Walkowiak
 */
@Embedded
@XmlRootElement(name = "bankOperation")
@XmlSeeAlso({Payment.class, Transfer.class, Withdrawal.class})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BankOperation {
    @NotNull
    protected String title;
    @NotNull
    protected double amount;
    protected double balanceAfter;
    @XmlElement(name = "receiver_account")
    @NotNull
    protected String targetAccountNo;
    @XmlTransient
    protected boolean executed;

    public BankOperation() {
    }

    BankOperation(String title, double amount, String targetAccountNo) {
        this.title = title;
        this.amount = amount;
        this.targetAccountNo = targetAccountNo;
        this.executed = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getTargetAccountNo() {
        return targetAccountNo;
    }

    public void setTargetAccountNo(String targetAccountNo) {
        this.targetAccountNo = targetAccountNo;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public void doOperation(BankAccount bankAccount) throws BankOperationException {
        if(executed) {
            throw new BankOperationException("Operation has been already executed");
        }
        if(amount < 0) {
            throw new BankOperationException("Negative amount");
        }

        execute(bankAccount);
        executed = true;
    }

    protected abstract void execute(BankAccount bankAccount) throws BankOperationException;
}
