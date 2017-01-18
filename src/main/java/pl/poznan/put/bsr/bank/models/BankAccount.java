package pl.poznan.put.bsr.bank.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import pl.poznan.put.bsr.bank.models.bankOperations.BankOperation;
import pl.poznan.put.bsr.bank.utils.ConstantsUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kamil Walkowiak
 */
@Entity("bankAccounts")
@XmlSeeAlso({BankOperation.class})
public class BankAccount {
    @Id
    @XmlTransient
    private ObjectId id;
    @NotNull
    private String name;
    @NotNull
    @Indexed(name = "accountNo", unique = true)
    private String accountNo;
    @NotNull
    private double balance;
    @Embedded
    private List<BankOperation> history;

    public BankAccount() {
        if (history == null) {
            history = new ArrayList<>();
        }
    }

    public BankAccount(String name) {
        this.name = name;
        accountNo = generateFullAccountNo();
        balance = 0.0;
        history = new ArrayList<>();
    }

    public static boolean validateCheckSum(String fullAccountNo) {
        String accountNo = fullAccountNo.substring(0, 16);
        String resultFullAccountNo = generateCheckSum(accountNo) + accountNo;

        return fullAccountNo.equals(resultFullAccountNo);
    }

    private String generateFullAccountNo() {
        accountNo = generateCheckSum(accountNo) + generateAccountNo();
        return accountNo;
    }

    private String generateAccountNo() {
        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        Query<Counter> query = datastore.find(Counter.class, "id", "accountNoCounter");
        UpdateOperations<Counter> operation = datastore.createUpdateOperations(Counter.class).inc("seq");
        long count = datastore.findAndModify(query, operation).getSeq();
        return ConstantsUtil.BANK_ID + String.format("%016d", count);
    }

    private static String generateCheckSum(String accountNo) {
        String tmpNo = accountNo + "252100";
        String part1 = tmpNo.substring(0, 15);
        String part2 = tmpNo.substring(15);
        long rest1 = Long.parseLong(part1) % 97;
        long rest2 = Long.parseLong(rest1 + part2) % 97;
        long checkSum = 98 - rest2;

        return String.format("%02d", checkSum);
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

    @XmlElementWrapper(name = "history")
    @XmlElementRef()
    public List<BankOperation> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void setHistory(List<BankOperation> history) {
        this.history = history;
    }

    public void addBankOperation(BankOperation bankOperation) {
        history.add(bankOperation);
    }
}
