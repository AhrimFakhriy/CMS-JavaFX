package main.model.person;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Person {
    protected String name, phoneNum;

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
