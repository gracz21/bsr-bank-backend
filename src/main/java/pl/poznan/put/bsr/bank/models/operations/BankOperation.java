package pl.poznan.put.bsr.bank.models.operations;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import pl.poznan.put.bsr.bank.models.BankAccount;

import javax.validation.constraints.NotNull;

/**
 * @author Kamil Walkowiak
 */
public abstract class BankOperation {
    @NotNull
    private String title;
    @NotNull
    private double amount;
    @NotNull
    private double balanceAfter;
    @NotNull
    private String targetAccountNo;

    public BankOperation() {
    }

    BankOperation(String title, double amount, String targetAccountNo) {
        this.title = title;
        this.amount = amount;
        this.targetAccountNo = targetAccountNo;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getTargetAccountNo() {
        return targetAccountNo;
    }

    public void doOperation(BankAccount bankAccount) {

    }
}
