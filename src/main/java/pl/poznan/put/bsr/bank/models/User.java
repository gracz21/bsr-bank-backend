package pl.poznan.put.bsr.bank.models;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * @author Kamil Walkowiak
 */
public class User {
    private String userName;
    private byte[] password;
    private List<Account> accounts;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = Base64.getEncoder().encode(password.getBytes());
        accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }
}
