package main.model.person;

public class Person {
    private String name, phoneNum;

    Person(String name, String phoneNum) {
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
