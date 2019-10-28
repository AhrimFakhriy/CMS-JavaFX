package main.model.person;

public class Admin extends Person {
    private String userID, password;

    public Admin() {
        super("ADMIN", "000");
        userID = "Admin";
        password = "fw/jYp4ahvnQpatsG8i3Lg=="; // PLAINTEXT: Admin
    }

    public Admin(String name, String phoneNum, String userID, String password) {
        super(name, phoneNum);
        this.userID = userID;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public boolean logIn(String id, String pass) {
        return id.equalsIgnoreCase(userID) && pass.equalsIgnoreCase(password);
    }

    @Override
    public String toString() {
        return (
            super.toString() +
            "\nUser ID: " + userID +
            "\nPassword: " + password
        );
    }
}
