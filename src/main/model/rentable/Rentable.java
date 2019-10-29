package main.model.rentable;

public abstract class Rentable {
    public enum Type {
        HALL,
        ROOM
    }

    private String name, type;
    private double price;
    private final String ID;

    Rentable(String name, String type, double price, String ID) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.ID = ID;
    }

    public void setData(String name, String type, double price) {
        this.name = name;
        this.type = type;
        this.price = price;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getID() { return ID; }
    public String toFile() { return (name + ";" + type + ";" + price + ";" + ID); }

    @Override
    public String toString() {
        return (
          "Name: " + name +
          "\nType: " + type +
          "\nPrice: RM" + price +
          "\nID: " + ID
        );
    }
}
