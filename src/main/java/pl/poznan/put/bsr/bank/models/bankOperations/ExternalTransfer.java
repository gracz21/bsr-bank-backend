package pl.poznan.put.bsr.bank.models.bankOperations;

import pl.poznan.put.bsr.bank.exceptions.BankOperationException;

import javax.validation.constraints.NotNull;

/**
 * @author Kamil Walkowiak
 */
public class ExternalTransfer extends BankOperation {
    @NotNull
    private String sourceAccountNo;

    public ExternalTransfer() {
    }

    public ExternalTransfer(String title, double amount, String sourceAccountNo, String targetAccountNo) {
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
