package pl.poznan.put.bsr.bank.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kamil Walkowiak
 */
@Entity("users")
public class User {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(name = "userName", unique = true)
    private String userName;
    @NotNull
    private byte[] password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @Reference
    private List<BankAccount> bankAccounts;

    public User() {
    }

    public User(String userName, String password, String firstName, String lastName) {
        this.userName = userName;
        this.password = Base64.getEncoder().encode(password.getBytes());
        this.firstName = firstName;
        this.lastName = lastName;
        bankAccounts = new ArrayList<>();
    }

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

    public List<BankAccount> getBankAccounts() {
        return Collections.unmodifiableList(bankAccounts);
    }

    public void addBankAccount(BankAccount bankAccount) {
        bankAccounts.add(bankAccount);
    }

    public boolean containsBankAccount(String accountNo) {
        return bankAccounts.stream().filter(bankAccount -> bankAccount.getAccountNo().equals(accountNo)).count() > 0;
    }

    public void removeBankAccount(String accountNo) {
        BankAccount bankAccount = bankAccounts.stream()
                .filter(account -> account.getAccountNo().equals(accountNo))
                .collect(Collectors.toList())
                .get(0);
        bankAccounts.remove(bankAccount);
    }
}
