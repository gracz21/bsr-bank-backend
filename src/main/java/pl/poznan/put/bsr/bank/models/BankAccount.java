package pl.poznan.put.bsr.bank.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import pl.poznan.put.bsr.bank.models.operations.Operation;
import pl.poznan.put.bsr.bank.utils.ConstantsUtil;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Kamil Walkowiak
 */
@Entity("accounts")
public class BankAccount {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(name = "accountNo", unique = true)
    private String accountNo;
    @NotNull
    private BigDecimal balance;
    private List<Operation> history;

    public BankAccount() {
        accountNo = "48" + ConstantsUtil.BANK_ID + UUID.randomUUID().hashCode();
        balance = new BigDecimal(0.0);
        history = new ArrayList<>();
    }

    public String getAccountNo() {
        return accountNo;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Operation> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
