package pl.poznan.put.bsr.bank.models.bankOperations;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import pl.poznan.put.bsr.bank.services.exceptions.BankOperationException;
import javax.validation.constraints.NotNull;

/**
 * @author Kamil Walkowiak
 */
@Embedded
public abstract class BankOperation {
    @Id
    protected ObjectId id;
    @NotNull
    protected String title;
    @NotNull
    protected double amount;
    @NotNull
    protected double balanceAfter;
    @NotNull
    protected String targetAccountNo;
    @NotNull
    protected boolean executed;

    public BankOperation() {
    }

    BankOperation(String title, double amount, String targetAccountNo) {
        this.title = title;
        this.amount = amount;
        this.targetAccountNo = targetAccountNo;
        this.executed = false;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public void doOperation() throws BankOperationException {
        if(executed) {
            throw new BankOperationException("Operation has been already executed");
        }
    }
}
