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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bank account class
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

    /**
     * Empty constructor for ORM
     */
    public BankAccount() {
        if (history == null) {
            history = new ArrayList<>();
        }
    }

    /**
     * Creates new bank account object
     * @param name bank account name
     */
    public BankAccount(String name) {
        this.name = name;
        accountNo = generateFullAccountNo();
        balance = 0.0;
        history = new ArrayList<>();
    }

    /**
     * Gets unmodifiable list of bank operations history of this bank account
     * @return unmodifiable list of bank operations history
     */
    @XmlElementWrapper(name = "history")
    @XmlElementRef()
    public List<BankOperation> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Sets bank account history
     * @param history bank account history
     */
    public void setHistory(List<BankOperation> history) {
        this.history = history;
    }

    /**
     * Adds new bank operation to bank account history
     * @param bankOperation bank operation which should be added
     */
    public void addBankOperation(BankOperation bankOperation) {
        history.add(bankOperation);
    }

    /**
     * Validate checksum of given full bank account no
     * @param fullAccountNo full bank account no which checksum should be validated
     * @return true if checksum is valid, false otherwise
     */
    public static boolean validateCheckSum(String fullAccountNo) {
        String accountNo = fullAccountNo.substring(2);
        String resultFullAccountNo = generateCheckSum(accountNo) + accountNo;

        return fullAccountNo.equals(resultFullAccountNo);
    }

    /**
     * Rounds bank account balance to two decimal places using half even rounding method
     */
    public void roundBalanceToTwoDecimal() {
        balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    /*
    Getter and setter methods for bank account class
     */

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

    private String generateFullAccountNo() {
        String baseAccountNo = generateAccountNo();
        accountNo = generateCheckSum(baseAccountNo) + baseAccountNo;
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
}
