package main.model.record;

import main.model.rentable.Rentable;
import main.utils.Utils;

import java.time.LocalDate;

public abstract class RentableRecord extends Record{
    private LocalDate dateIn, dateOut;
    private int duration;

    RentableRecord(String custID, LocalDate dateIn, int duration){
        super(custID);
        this.dateIn = dateIn;
        this.duration = duration;
        dateOut = dateIn.plusDays(duration);
    }

    RentableRecord(String custID, LocalDate dateIn, LocalDate dateOut, int duration) {
        super(custID);
        this.dateIn = dateIn;
        this.dateOut = dateOut;
        this.duration = duration;
    }

    public LocalDate getDateIn() { return dateIn; }
    public LocalDate getDateOut() { return dateOut; }
    public int getDuration() { return duration; }

    @Override
    public String toString() {
        return (
          "Customer ID: " + custID +
          "\nDate Check-In: " + Utils.dateTimeFormatter.format(dateIn) +
          "\nDate Check-Out: " + Utils.dateTimeFormatter.format(dateOut) +
          "\nDuration of Stay: " + duration + " days"
        );
    }

    public String toFile() {
        return (
            custID + ";" +
            Utils.dateTimeFormatter.format(dateIn) + ";" +
            Utils.dateTimeFormatter.format(dateOut) + ";" +
            duration
        );
    }
}
