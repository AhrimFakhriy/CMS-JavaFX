package main.model.record;

import main.model.rentable.Hall;
import main.utils.Utils;

import java.time.LocalDate;

public class HallRecord extends RentableRecord {
    private Hall hall;

    public HallRecord(String custID, LocalDate dateIn, int duration, Hall hall) {
        super(custID, dateIn, duration);
        this.hall = hall;
    }

    public HallRecord(String custID, LocalDate dateIn, LocalDate dateOut, int duration, Hall hall) {
        super(custID, dateIn, dateOut, duration);
        this.hall = hall;
    }

    public Hall getHall() { return hall; }

    @Override
    public String toString() {
        return (super.toString() + "\n\nHall Details:\n" + hall.toString());
    }


    @Override
    public String toFile() {
        return (super.toFile() + ";" + hall.toFile());
    }
}
