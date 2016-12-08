package pl.poznan.put.bsr.bank.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import pl.poznan.put.bsr.bank.models.operations.BankOperation;
import pl.poznan.put.bsr.bank.utils.ConstantsUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<BankOperation> history;

    public BankAccount() {
    }

    public BankAccount(String name) {
        this.name = name;
        accountNo = generateAccountNo();
        balance = 0.0;
        history = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    private String generateAccountNo() {
        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        Query<Counter> query = datastore.find(Counter.class, "id", "accountNoCounter");
        UpdateOperations<Counter> operation = datastore.createUpdateOperations(Counter.class).inc("seq");
        long count = datastore.findAndModify(query, operation).getSeq();

        String accountNo = ConstantsUtil.BANK_ID + String.format("%016d", count);

        String tmpNo = accountNo + "252100";
        String part1 = tmpNo.substring(0, 15);
        String part2 = tmpNo.substring(15);
        long rest1 = Long.parseLong(part1)%97;
        long rest2 = Long.parseLong(rest1 + part2)%97;
        long checkSum = 98 - rest2;

        accountNo = String.format("%02d", checkSum) + accountNo;
        return accountNo;
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

    public List<BankOperation> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
