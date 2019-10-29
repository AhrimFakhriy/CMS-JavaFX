package main.data;

import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;

import java.io.*;
import java.util.*;

import static main.utils.Utils.DATA_FOLDER;

public class RentableRepository {
    private static RentableRepository instance;
    private Set<String> hallTypes, roomTypes;
    private ArrayList<Room> rooms;
    private ArrayList<Hall> halls;

    private RentableRepository() {
        rooms = new ArrayList<>();
        halls = new ArrayList<>();
        hallTypes = new HashSet<>();
        roomTypes = new HashSet<>();
    }

    public void saveHalls() { saveData(Rentable.Type.HALL); }
    public void saveRooms() { saveData(Rentable.Type.ROOM); }
    public void loadHalls() { getData(Rentable.Type.HALL); }
    public void loadRooms() { getData(Rentable.Type.ROOM); }

    private void saveData(Rentable.Type rentable) {
        try {
            String filename;

            if(rentable == Rentable.Type.ROOM)
                filename = "rooms.dat";
            else
                filename = "halls.dat";

            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(new FileWriter(DATA_FOLDER + File.separator + filename))
            );

            switch (rentable) {
                case ROOM: {
                    for (Room r : rooms)
                        pw.println(r.toFile());

                    break;
                }
                case HALL: {
                    for (Hall h : halls)
                        pw.println(h.toFile());

                    break;
                }
            }

            pw.close();


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData(Rentable.Type rentable) {
        try {
            String filename;

            if (rentable == Rentable.Type.ROOM) {
                rooms.clear();
                roomTypes.clear();
                filename = "rooms.dat";
            } else {
                halls.clear();
                hallTypes.clear();
                filename = "halls.dat";
            }

            BufferedReader br = new BufferedReader(
                new FileReader(DATA_FOLDER + File.separator + filename)
            );

            for (String data = br.readLine(); data != null; data = br.readLine()) {
                StringTokenizer token = new StringTokenizer(data, ";");

                String name = token.nextToken();
                String type = token.nextToken();
                double price = Double.parseDouble(token.nextToken());
                String ID = token.nextToken();

                switch (rentable) {
                    case ROOM:
                        roomTypes.add(type);
                        rooms.add(new Room(name, type, price, ID));
                        break;

                    case HALL:
                        hallTypes.add(type);
                        halls.add(new Hall(name, type, price, ID));
                        break;
                }
            }

            br.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Room> getRooms() { return rooms; }
    public ArrayList<Hall> getHalls() { return halls; }
    public Set<String> getHallTypes() { return hallTypes; }
    public Set<String> getRoomTypes() { return roomTypes; }

    public static RentableRepository getInstance() {
        if(instance == null) {
            synchronized (RentableRepository.class) {
                if(instance == null) {
                    instance = new RentableRepository();
                }
            }
        }

        return instance;
    }
}
