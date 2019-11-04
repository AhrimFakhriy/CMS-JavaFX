package main.model.record;

public abstract class Record {
    String custID;

    Record(String custID) {
        this.custID = custID;
    }

    public String getCustID() { return custID; }
    public void setCustID(String custID) { this.custID = custID; }
}
