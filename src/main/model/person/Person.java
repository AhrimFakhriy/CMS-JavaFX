package main.model.person;

public abstract class Person {
    private String name, phoneNum;

    public Person(String name, String phoneNum) {
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    @Override
    public String toString() {
        return (
            "Name: " + name +
            "\nPassword: " + phoneNum
        );
    }
}
