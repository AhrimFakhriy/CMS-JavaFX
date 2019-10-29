package main.model.record;

import main.model.rentable.Room;
import main.utils.Utils;

import java.time.LocalDate;

public class RoomRecord extends RentableRecord {
    private Room room;

    public RoomRecord(String custID, LocalDate dateIn, int duration, Room room) {
        super(custID, dateIn, duration);
        this.room = room;
    }

    public RoomRecord(String custID, LocalDate dateIn, LocalDate dateOut, int duration, Room room) {
        super(custID, dateIn, dateOut, duration);
        this.room = room;
    }

    public Room getRoom() { return room; }

    @Override
    public String toString() {
        return (super.toString() + "\n\nRoom Details:\n" + room.toString());
    }

    @Override
    public String toFile() {
        return (super.toFile() + ";" + room.toFile());
    }
}
