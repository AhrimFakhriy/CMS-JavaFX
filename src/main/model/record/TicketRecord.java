package main.model.record;

import main.utils.Utils;

import java.time.LocalDate;

public class TicketRecord extends Record{
    private LocalDate date;
    private int amount;
    private double price;

    public TicketRecord(String custID, LocalDate date, int amount, double price) {
        super(custID);
        this.date = date;
        this.amount = amount;
        this.price = price;
    }

    public LocalDate getDate() { return date; }
    public int getAmount() { return amount; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return (
            "Customer ID: " + custID +
            "\nDate: " + Utils.dateTimeFormatter.format(date) +
            "\nAmount: " + amount +
            "\nPrice: RM" + price
        );
    }

    public String toFile() {
        return (custID + ";" + Utils.dateTimeFormatter.format(date) + ";" + amount + ";" + price);
    }
}
