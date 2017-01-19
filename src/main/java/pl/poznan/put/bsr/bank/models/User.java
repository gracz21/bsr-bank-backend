package pl.poznan.put.bsr.bank.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User class
 * @author Kamil Walkowiak
 */
@Entity("users")
public class User {
    @Id
    @XmlTransient
    private ObjectId id;
    @NotNull
    @Indexed(name = "userName", unique = true)
    private String userName;
    @NotNull
    @XmlTransient
    private byte[] password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @Reference
    private List<BankAccount> bankAccounts;

    /**
     * Empty constructor for ORM
     */
    public User() {
    }

    /**
     * Creates new user object
     * @param userName user login
     * @param password user password
     * @param firstName user first name
     * @param lastName user last name
     */
    public User(String userName, String password, String firstName, String lastName) {
        this.userName = userName;
        this.password = Base64.getEncoder().encode(password.getBytes());
        this.firstName = firstName;
        this.lastName = lastName;
        bankAccounts = new ArrayList<>();
    }

    /**
     * Gets unmodifiable list of user bank accounts
     * @return unmodifiable list of bank accounts
     */
    public List<BankAccount> getBankAccounts() {
        return Collections.unmodifiableList(bankAccounts);
    }

    /**
     * Adds bank account to user bank accounts list
     * @param bankAccount bank account object which should be added
     */
    public void addBankAccount(BankAccount bankAccount) {
        bankAccounts.add(bankAccount);
    }

    /**
     * Checks if user is owner of bank account with given no
     * @param accountNo account no of which should be checked
     * @return true if user owns given account, false otherwise
     */
    public boolean containsBankAccount(String accountNo) {
        return bankAccounts.stream().filter(bankAccount -> bankAccount.getAccountNo().equals(accountNo)).count() > 0;
    }

    /**
     * Removes bank account given by ist no from user bank accounts list
     * @param accountNo no of bank account which should be removed
     */
    public void removeBankAccount(String accountNo) {
        BankAccount bankAccount = bankAccounts.stream()
                .filter(account -> account.getAccountNo().equals(accountNo))
                .collect(Collectors.toList())
                .get(0);
        bankAccounts.remove(bankAccount);
    }

    /*
    Getter and setter methods for user class
     */

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
