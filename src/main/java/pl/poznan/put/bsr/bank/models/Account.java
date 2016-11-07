package pl.poznan.put.bsr.bank.models;

import pl.poznan.put.bsr.bank.utils.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Kamil Walkowiak
 */
public class Account {
    private String accountNo;
    private BigDecimal balance;
    private List<Operation> history;

    public Account() {
        accountNo = "48" + Constants.BANK_ID + UUID.randomUUID().hashCode();
        balance = new BigDecimal(0.0);
        history = new ArrayList<>();
    }
}
