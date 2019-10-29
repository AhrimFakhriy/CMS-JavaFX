package main.model.person;

import main.model.account.Account;

public class Admin extends Person {
    private Account account;

    public Admin() {
        super("Hasyirin Fakhriy", "010-905 4610");
        account = new Account("Admin", "fw/jYp4ahvnQpatsG8i3Lg==");
    }

    public Admin(String name, String phoneNum, String userID, String password) {
        super(name, phoneNum);
        account = new Account(userID, password);
    }

    public Account getAccount() { return account; }

    public boolean logIn(String id, String pass) {
        return id.equalsIgnoreCase(account.getUserID()) && pass.equalsIgnoreCase(account.getPassword());
    }
}
