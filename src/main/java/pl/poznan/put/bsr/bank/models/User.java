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
    @Reference
    private List<BankAccount> bankAccounts;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = Base64.getEncoder().encode(password.getBytes());
        bankAccounts = new ArrayList<>();
    }

    public List<BankAccount> getBankAccounts() {
        return Collections.unmodifiableList(bankAccounts);
    }

    public void addAccount(BankAccount bankAccount) {
        bankAccounts.add(bankAccount);
    }
}
