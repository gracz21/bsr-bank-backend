package pl.poznan.put.bsr.bank.models.operations;

import pl.poznan.put.bsr.bank.models.BankAccount;

import java.math.BigDecimal;

/**
 * @author Kamil Walkowiak
 */
public abstract class Operation {
    private String title;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String targetAccountNo;

    Operation(String title, BigDecimal amount, String targetAccountNo) {
        this.title = title;
        this.amount = amount;
        this.targetAccountNo = targetAccountNo;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getTargetAccountNo() {
        return targetAccountNo;
    }

    public void doOperation(BankAccount bankAccount) {

    }
}
