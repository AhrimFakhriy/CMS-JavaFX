package main.data;

import java.io.File;

import static main.utils.Utils.*;

public class Settings {
    private static Settings instance;
    private String chaletName;
    private String ticketServiceLoc;
    private double ticketPrice;

    private Settings() {
        initializeFiles();
    }

    private void initializeFiles() {
        File file = new File(PATH_TO_DATA_FOLDER);
        if(!file.mkdir()) {
            file.mkdir();
        }

        File log = new File(RESOURCE + File.separator + LOG_FOLDER);
        if(!log.mkdir()) {
            log.mkdir();
        }
    }

    public String getChaletName() { return chaletName; }
    public String getTicketServiceLoc() { return ticketServiceLoc; }
    public double getTicketPrice() { return ticketPrice; }

    public static Settings getInstance() {
        if(instance == null) {
            synchronized (Settings.class) {
                if(instance == null) {
                    instance = new Settings();
                }
            }
        }

        return instance;
    }
}
