package main.model.person;

import static main.utils.Utils.MALAYSIA;

public class Customer extends Person {
    private String nationality, ID;
    private Address address;
    private double totalSpent;

    public Customer(String name, String phoneNum, String nationality, String ID, Address address, double totalSpent) {
        super(name, phoneNum);
        this.nationality = nationality;
        this.ID = ID;
        this.address = address;
        this.totalSpent = totalSpent;
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

    public void addTransaction(double price) {
        totalSpent += price;
    }

    public boolean isCitizen() {
        return nationality.equalsIgnoreCase(MALAYSIA);
    }

    public String toFile() {
        StringBuilder out = new StringBuilder(super.getName()).append(";")
                .append(super.getPhoneNum()).append(";")
                .append(nationality).append(";")
                .append(ID).append(";");

        if(address != null)
            out.append(address.toFile()).append(";");
        else
            out.append("null;null;null;null;null;");

        return out.append(totalSpent).toString();
    }
}
