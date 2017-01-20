package pl.poznan.put.bsr.bank.models.bankOperations;

import org.mongodb.morphia.annotations.Embedded;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.models.BankAccount;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Abstract parent bank operation class
 * @author Kamil Walkowiak
 */
@Embedded
@XmlRootElement(name = "bankOperation")
@XmlSeeAlso({Payment.class, Transfer.class, Withdrawal.class, Fee.class})
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
    @NotNull
    protected String timestamp;

    /**
     * Empty constructor for ORM
     */
    public BankOperation() {
    }

    /**
     * Creates new bank operation object
     * @param title bank operation title
     * @param amount bank operation amount
     * @param targetAccountNo bank operation target account
     */
    BankOperation(String title, double amount, String targetAccountNo) {
        this.title = title;
        this.amount = amount;
        this.targetAccountNo = targetAccountNo;
        this.executed = false;
    }

    /**
     * Execute this bank operation
     * @param bankAccount bank account on which this operation will be executed
     * @throws BankOperationException if bank operation parameters are invalid
     */
    public void doOperation(BankAccount bankAccount) throws BankOperationException {
        if (executed) {
            throw new BankOperationException("Operation has been already executed");
        }
        if (amount <= 0) {
            throw new BankOperationException("Amount cannot be less or equal to 0");
        }
        if (BigDecimal.valueOf(amount).scale() > 2) {
            throw new BankOperationException("Amount format is invalid");
        }

        execute(bankAccount);
        bankAccount.roundBalanceToTwoDecimal();
        this.balanceAfter = bankAccount.getBalance();
        bankAccount.addBankOperation(this);
        executed = true;
        timestamp = new Timestamp(System.currentTimeMillis()).toString();
    }

    /*
    Getter and setter methods for bank operation class
     */

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    protected abstract void execute(BankAccount bankAccount) throws BankOperationException;
}
