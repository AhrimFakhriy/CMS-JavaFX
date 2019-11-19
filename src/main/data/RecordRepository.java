package main.data;

import main.model.datastructure.binarytree.BinarySearchTree;
import main.model.record.HallRecord;
import main.model.record.RentableRecord;
import main.model.record.RoomRecord;
import main.model.record.TicketRecord;
import main.model.rentable.Hall;
import main.model.rentable.Room;
import org.apache.commons.text.WordUtils;

import java.io.*;
import java.time.LocalDate;
import java.util.StringTokenizer;

import static main.utils.Utils.DATA_FOLDER;
import static main.utils.Utils.dateTimeFormatter;

public class RecordRepository {
    private enum RecordType {
        RECORD,
        OLD_RECORD,
        TICKETS
    }

    private static RecordRepository instance;
    private BinarySearchTree<String, RentableRecord> records;
    private BinarySearchTree<String, RentableRecord> oldRecords;
    private BinarySearchTree<String, TicketRecord> ticketRecords;
    private RentableRepository rentableRepository;

    private RecordRepository() {
        records = new BinarySearchTree<>();
        oldRecords = new BinarySearchTree<>();
        ticketRecords = new BinarySearchTree<>();
        rentableRepository = RentableRepository.getInstance();
    }

    public void loadRecords() { loadData(RecordType.RECORD); }
    public void loadOldRecords() { loadData(RecordType.OLD_RECORD); }
    public void loadTicketRecords() { loadData(RecordType.TICKETS); }

    private void saveData(RecordType type) {
        try {
            String filename;

            switch (type) {
                case RECORD:
                    filename = "records.dat";
                    break;

                case OLD_RECORD:
                    filename = "old_records.dat";
                    break;

                default:
                    filename = "ticket_records.dat";
            }

            PrintWriter pw = new PrintWriter(
              new BufferedWriter(new FileWriter(DATA_FOLDER + File.separator + filename))
            );

            switch (type) {
                case RECORD:
                    for(RentableRecord r : records)
                        pw.println(r.toFile());

                    break;
                case OLD_RECORD:

                    for(RentableRecord r : oldRecords)
                        pw.println(r.toFile());

                    break;

                default:
                    for(TicketRecord t : ticketRecords)
                        pw.println(t.toFile());
            }

            pw.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadData(RecordType type) {
        try {
            String filename;

            switch (type) {
                case RECORD: {
                    records.clear();
                    filename = "records.dat";
                    break;
                }
                case OLD_RECORD: {
                    oldRecords.clear();
                    filename = "old_records.dat";
                    break;
                }

                default:
                    ticketRecords.clear();
                    filename = "ticket_records.dat";
            }

            BufferedReader br = new BufferedReader(
                new FileReader(DATA_FOLDER + File.separator + filename)
            );

            if (type == RecordType.TICKETS) {
                for (String data = br.readLine(); data != null; data = br.readLine()) {
                    StringTokenizer token = new StringTokenizer(data, ";");

                    String custID = token.nextToken();
                    LocalDate date = LocalDate.parse(token.nextToken(), dateTimeFormatter);
                    int amount = Integer.parseInt(token.nextToken());
                    double price = Double.parseDouble(token.nextToken());

                    ticketRecords.put(custID, new TicketRecord(custID, date, amount, price));
                }
            } else {
                for (String data = br.readLine(); data != null; data = br.readLine()) {
                    boolean isRoom = true;

                    StringTokenizer token = new StringTokenizer(data, ";");

                    String custID = token.nextToken();
                    LocalDate dateIn = LocalDate.parse(token.nextToken(), dateTimeFormatter);
                    LocalDate dateOut = LocalDate.parse(token.nextToken(), dateTimeFormatter);
                    int duration = Integer.parseInt(token.nextToken());
                    String rentableName = token.nextToken();
                    String rentableType = WordUtils.capitalizeFully(token.nextToken());
                    double rentablePrice = Double.parseDouble(token.nextToken());
                    String rentableID = token.nextToken();

                    for (String hallTypes : rentableRepository.hallTypesProperty().get()) {
                        if (hallTypes.equalsIgnoreCase(rentableType)) {
                            isRoom = false;
                            break;
                        }
                    }

                    if (isRoom) {
                        for (Hall hall : rentableRepository.hallsProperty().get()) {
                            if (rentableID.equalsIgnoreCase(hall.getID())) {
                                isRoom = false;
                                break;
                            }
                        }
                    }

                    RentableRecord record;
                    if (isRoom) {
                        record = new RoomRecord(custID, dateIn, dateOut, duration, new Room(
                                rentableName, rentableType, rentablePrice, rentableID
                        ));

                    } else {
                        record = new HallRecord(custID, dateIn, dateOut, duration, new Hall(
                                rentableName, rentableType, rentablePrice, rentableID
                        ));
                    }

                    if (type == RecordType.RECORD) {
                        if(record instanceof HallRecord)
                            records.put(((HallRecord) record).getHall().getID(), record);
                        else
                            records.put(((RoomRecord) record).getRoom().getID(), record);
                    } else {
                        if (record instanceof HallRecord)
                            oldRecords.put(((HallRecord) record).getHall().getID(), record);
                        else
                            oldRecords.put(((RoomRecord) record).getRoom().getID(), record);
                    }
                }
            }

            br.close();


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BinarySearchTree<String, RentableRecord> getRecords() { return records; }
    public BinarySearchTree<String, RentableRecord> getOldRecords() { return oldRecords; }
    public BinarySearchTree<String, TicketRecord> getTicketRecords() { return ticketRecords; }

    public static RecordRepository getInstance() {
        if(instance == null) {
            synchronized (RecordRepository.class) {
                if(instance == null) {
                    instance = new RecordRepository();
                }
            }
        }

        return instance;
    }
}
