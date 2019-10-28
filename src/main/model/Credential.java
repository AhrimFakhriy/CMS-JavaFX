package main.model;

public class Credential {
    private String ID, password;

    public Credential() {
        this.ID = "null";
        this.password = "null";
    }

    public Credential(String ID, String password) {
        this.ID = ID;
        this.password = password;
    }

    public void set(String ID, String password) {
        this.ID = ID;
        this.password = password;
    }

    public void setID(String ID) { this.ID = ID; }
    public void setPassword(String password) { this.password = password; }

    public String getID() { return ID; }
    public String getPassword() { return password; }

    public boolean isNotEmpty() {
        return (!ID.equals("null") && !password.equals("null"));
    }

    public String toFile() {
        return (ID + ";" + password);
    }

    @Override
    public String toString() {
        return (
          "ID: " + ID +
          "\nPassword: " + password
        );
    }
}
