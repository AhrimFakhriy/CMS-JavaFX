package main.model.person;

import static main.utils.Utils.MALAYSIA;

public class Customer extends Person {
    private String nationality, ID;
    private Address address;
    private double totalSpent;

    public Customer(String name, String phoneNum, String ID, String nationality) {
        super(name, phoneNum);
        this.ID = ID;
        this.nationality = nationality;
        this.totalSpent = 0;
    }

    public Customer(String name, String phoneNum, String ID, String nationality, Address address) {
        this(name, phoneNum, ID, nationality);
        this.address = address;
    }

    public Customer(String name, String phoneNum, String ID, String nationality, Address address, double totalSpent) {
        this(name, phoneNum, ID, nationality, address);
        this.totalSpent = totalSpent;
    }

    public void setAddress(Address address) { this.address = address; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void addTransaction(double price) {
        totalSpent += price;
    }

    public String getNationality() {
        return nationality;
    }
    public String getID() {
        return ID;
    }
    public Address getAddress() {
        return address;
    }
    public double getTotalSpent() {
        return totalSpent;
    }
    public boolean isCitizen() { return nationality.equalsIgnoreCase(MALAYSIA); }

    public String toFile() {
        StringBuilder out = new StringBuilder(super.getName()).append(";")
                .append(super.getPhoneNum()).append(";")
                .append(ID).append(";")
                .append(nationality).append(";");

        if(address != null)
            out.append(address.toFile()).append(";");
        else
            out.append("null;null;null;null;null;");

        return out.append(totalSpent).toString();
    }
}
