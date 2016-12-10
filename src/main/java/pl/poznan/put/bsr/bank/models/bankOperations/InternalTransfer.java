package pl.poznan.put.bsr.bank.models.bankOperations;

import pl.poznan.put.bsr.bank.services.exceptions.BankOperationException;

import javax.validation.constraints.NotNull;

/**
 * @author Kamil Walkowiak
 */
public class InternalTransfer extends BankOperation {
    @NotNull
    private String sourceAccountNo;

    public InternalTransfer() {
    }

    InternalTransfer(String title, double amount, String sourceAccountNo, String targetAccountNo) {
        super(title, amount, targetAccountNo);
        this.sourceAccountNo = sourceAccountNo;
    }

    public String getSourceAccountNo() {
        return sourceAccountNo;
    }

    public void setSourceAccountNo(String sourceAccountNo) {
        this.sourceAccountNo = sourceAccountNo;
    }

    @Override
    public void doOperation() throws BankOperationException {
        super.doOperation();
    }
}
