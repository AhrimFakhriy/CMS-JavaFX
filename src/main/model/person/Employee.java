package main.model.person;

import main.model.account.Account;

public class Employee extends Person {
    private Account account;
    private String rank;
    private double salary;

    public Employee(String name, String phoneNum, String rank, double salary) {
        super(name, phoneNum);
        this.rank = rank;
        this.salary = salary;
        this.account = null;
    }

    public void setAccount(Account account) { this.account = account; }
    public void setRank(String rank) { this.rank = rank; }
    public void setSalary(double salary) { this.salary = salary; }

    public Account getAccount() { return account; }
    public String getRank() { return rank; }
    public double getSalary() { return salary; }

    public boolean logIn(String id, String pass) {
        if(account != null)
            return id.equalsIgnoreCase(account.getUserID()) && pass.equals(account.getPassword());

        return false;
    }

    public String toFile() {
        StringBuilder out = new StringBuilder(super.getName()).append(";")
                .append(super.getPhoneNum()).append(";")
                .append(rank).append(";")
                .append(salary).append(";");

        if(account != null)
            out.append(account.toFile());
        else
            out.append("null").append(";").append("null");

        return out.toString();
    }

    @Override
    public String toString() {
        return (
            super.toString() +
            "\nRank: " + rank +
            "\nSalary: RM" + salary
        );
    }
}
