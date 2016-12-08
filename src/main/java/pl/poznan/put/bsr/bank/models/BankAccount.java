package pl.poznan.put.bsr.bank.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import pl.poznan.put.bsr.bank.models.operations.Operation;
import pl.poznan.put.bsr.bank.utils.ConstantsUtil;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Kamil Walkowiak
 */
@Entity("bankAccounts")
public class BankAccount {
    @Id
    private ObjectId id;
    @NotNull
    private String name;
    @NotNull
    @Indexed(name = "accountNo", unique = true)
    private String accountNo;
    @NotNull
    private double balance;
    private List<Operation> history;

    public BankAccount() {
    }

    public BankAccount(String name) {
        this.name = name;
        accountNo = "48" + ConstantsUtil.BANK_ID + UUID.randomUUID().hashCode();
        balance = 0.0;
        history = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Operation> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
