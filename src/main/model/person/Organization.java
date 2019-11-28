package main.model.person;

public class Organization {
    private String name;
    private Address address;
    private Person contact;
    private double totalSpent;

    public Organization(String name, Address address, Person contact, double totalSpent) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.totalSpent = totalSpent;
    }

    public String getName() { return name; }
    public Address getAddress() { return address; }
    public Person getContact() { return contact; }
    public double getTotalSpent() { return totalSpent; }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String toFile() {
        StringBuilder out = new StringBuilder(name).append(";");

        if(address != null)
            out.append(address.toFile()).append(";");
        else
            out.append("null;null;null;null;null;");

        return out.append(contact.getName()).append(";")
                .append(contact.getPhoneNum()).append(";")
                .append(totalSpent).toString();
    }
}
