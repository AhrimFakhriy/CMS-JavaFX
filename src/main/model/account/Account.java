package main.model.account;

public class Account {
    private String userID, password;

    public Account(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }

    public String getUserID() { return userID; }
    public String getPassword() { return password; }

    public void setUserID(String userID) { this.userID = userID; }
    public void setPassword(String password) { this.password = password; }

    public String toFile() { return (userID + ";" + password); }
}
