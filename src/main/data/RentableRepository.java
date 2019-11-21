package main.data;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;
import org.apache.commons.text.WordUtils;

import java.io.*;
import java.util.*;

import static main.utils.Utils.DATA_FOLDER;

public class RentableRepository {
    private static RentableRepository instance;

    private SetProperty<String> hallTypesProperty, roomTypesProperty;
    private Set<String> hallTypes, roomTypes;

    private MapProperty<String, Rentable> selectedBookingsProperty;

    private ListProperty<Room> roomsProperty;
    private ListProperty<Hall> hallsProperty;

    private List<Room> rooms;
    private List<Hall> halls;

    private RentableRepository() {
        selectedBookingsProperty = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));

        roomTypesProperty = new SimpleSetProperty<>();
        roomsProperty = new SimpleListProperty<>();
        roomTypes = new HashSet<>();
        rooms = new ArrayList<>();

        hallTypesProperty = new SimpleSetProperty<>();
        hallsProperty = new SimpleListProperty<>();
        hallTypes = new HashSet<>();
        halls = new ArrayList<>();

    }

    public void saveHalls() { saveData(Rentable.Type.HALL); }
    public void saveRooms() { saveData(Rentable.Type.ROOM); }

    public void loadHalls() {
        getData(Rentable.Type.HALL);

        hallTypesProperty.set(FXCollections.observableSet(hallTypes));
        hallsProperty.set(FXCollections.observableList(halls));
    }

    public void loadRooms() {
        getData(Rentable.Type.ROOM);

        roomTypesProperty.set(FXCollections.observableSet(roomTypes));
        roomsProperty.set(FXCollections.observableList(rooms));
    }

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

                String name = WordUtils.capitalizeFully(token.nextToken());
                String type = WordUtils.capitalizeFully(token.nextToken());
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

    //public List<Room> getRooms() { return rooms; }
    //public List<Hall> getHalls() { return halls; }

    //public Set<String> getHallTypes() { return hallTypes; }
    //public Set<String> getRoomTypes() { return roomTypes; }

    public MapProperty<String, Rentable> getSelectedBookings() { return selectedBookingsProperty; }

    public ListProperty<Hall> hallsProperty() { return hallsProperty; }
    public ListProperty<Room> roomsProperty() { return roomsProperty; }

    public SetProperty<String> hallTypesProperty() { return hallTypesProperty; }
    public SetProperty<String> roomTypesProperty() { return roomTypesProperty; }

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
