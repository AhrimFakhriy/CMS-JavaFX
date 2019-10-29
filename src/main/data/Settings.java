package main.data;

import javafx.beans.property.SimpleObjectProperty;
import main.model.Credential;
import main.model.person.Person;

import java.io.*;
import java.util.StringTokenizer;

import static main.utils.Utils.*;

public class Settings {
    private static Settings instance;

    private SimpleObjectProperty<Person> currentUser;
    private String chaletName;
    private String ticketServiceLoc;
    private boolean ticketServiceEnabled;
    private double ticketPrice;

    private Settings() { }

    public void loadSettings() {
        try {
            FileReader fr = new FileReader(DATA_FOLDER + File.separator + "settings.dat");
            BufferedReader br = new BufferedReader(fr);

            String data = br.readLine();

            if(data != null) {
                StringTokenizer token = new StringTokenizer(data, ";");

                chaletName = token.nextToken();
                ticketServiceEnabled = Boolean.parseBoolean(token.nextToken());
                ticketServiceLoc = token.nextToken();
                ticketPrice = Double.parseDouble(token.nextToken());
            } else {
                initiateDefaultSettings();
            }

            br.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSettings() {
        try {
            FileWriter fw = new FileWriter(DATA_FOLDER + File.separator + "settings.dat");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            String settings = chaletName + ";" +
                    ticketServiceEnabled + ";" +
                    ticketServiceLoc + ";" +
                    ticketPrice;

            pw.print(settings);

            pw.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initiateDefaultSettings() {
        chaletName = CHALET_NAME;
        ticketServiceLoc = TICKET_SERVICE_LOCATION;
        ticketServiceEnabled = false;
        ticketPrice = 20.00;

        saveSettings();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void initializeFiles() {
        try {
            new File(DATA_FOLDER).mkdir();
            new File(LOG_FOLDER).mkdir();

            for(String fileName : FILE_NAMES) {
                if(fileName.contains("removed")) {
                    new File(LOG_FOLDER + File.separator + fileName + ".log").mkdir();
                } else {
                    new File(DATA_FOLDER + File.separator + fileName + ".dat").mkdir();
                }
            }
        }catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public Credential loadCredentials() {
        Credential credential = new Credential();

        try {
            FileReader fr = new FileReader(DATA_FOLDER + File.separator + "remember_me.dat");
            BufferedReader br = new BufferedReader(fr);

            String data = br.readLine();

            if(data != null) {
                StringTokenizer token = new StringTokenizer(data, ";");
                credential.set(token.nextToken(), token.nextToken());
            }

            br.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return credential;
    }

    public void setCurrentUser(SimpleObjectProperty<Person> currentUser) { this.currentUser = currentUser; }
    public void setChaletName(String chaletName) { this.chaletName = chaletName; }
    public void setTicketServiceLoc(String ticketServiceLoc) { this.ticketServiceLoc = ticketServiceLoc; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }
    public void setTicketServiceEnabled(boolean ticketServiceEnabled) { this.ticketServiceEnabled = ticketServiceEnabled; }

    public SimpleObjectProperty<Person> getCurrentUser() { return currentUser; }
    public String getChaletName() { return chaletName; }
    public String getTicketServiceLoc() { return ticketServiceLoc; }
    public double getTicketPrice() { return ticketPrice; }
    public boolean isTicketServiceEnabled() { return ticketServiceEnabled; }

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
