package main.utils;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static final String CHALET_NAME = "Azimos Chalet";
    public static final String TICKET_SERVICE_LOCATION = "Pulau Perhentian";
    public static final String MALAYSIA = "MALAYSIA";

    public static final String RESOURCE = "src";
    public static final String DATA_FOLDER = "src/data";
    public static final String LOG_FOLDER = "src/logs";

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMMM/yyyy");
    public static final DecimalFormat moneyFormatter = new DecimalFormat("###,###,###.00");

    public static final String[] FILE_NAMES = {
            "rooms",
            "removed_rooms",
            "halls",
            "removed_halls",
            "customers",
            "employees",
            "removed_employees",
            "remember_me",
            "settings",
            "records",
            "old_records",
            "ticket_records"
    };

    public static void setLoginStage(Stage stage) {
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Log In");
        stage.centerOnScreen();
        stage.sizeToScene();
    }

    // STATES
    public static final String[] STATES = {
            "Johor", // 0
            "Kedah", // 1
            "Kelantan", // 2
            "Melaka", // 3
            "Negeri Sembilan", // 4
            "Pahang", // 5
            "Penang", // 6
            "Perak", // 7
            "Perlis", // 8
            "Sabah", // 9
            "Sarawak", // 10
            "Selangor", // 11
            "Terengganu", // 12
            "Wilayah Persekutuan" // 13
    };

    public static final String[][] DISTRICTS = {
            {"Batu Pahat", "Johor Bahru", "Kluang", "Kota Tinggi", "Kulai", "Mersing", "Muar", "Pontian", "Segamat", "Tangkak", "Others"},
            {"Baling", "Bandar Baharu", "Kota Setar", "Kuala Muda", "Kubang Pasu", "Kulim", "Langkawi", "Padang Terap", "Pendang", "Pokok Sena", "Sik", "Yan", "Others"},
            {"Bachok", "Gua Musang", "Jeli", "Kota Bharu", "Kuala Krai", "Machang", "Pasir Mas", "Pasir Puteh", "Tanah Merah", "Tumpat", "Others"},
            {"Alor Gajah", "Melaka Tengah", "Jasin", "Others"},
            {"Jelebu", "Jempol", "Kuala Pilah", "Port Dickson", "Rembau", "Seremban", "Tampin", "Others"},
            {"Bentong", "Bera", "Cameron Highlands", "Jerantut", "Kuantan", "Lipis", "Maran", "Pekan", "Raub", "Rompin", "Temerloh", "Others"},
            {"Seberang Perai Tengah", "Seberang Perai Utara", "Timur Laut Pulau Pinang", "Seberang Perai Selatan", "Barat Daya Pulau Pinang", "Others"},
            {"Bagan Datuk", "Batang Padang", "Hilir Perak", "Hulu Perak", "Kampar", "Kerian", "Kinta", "Kuala Kangsar", "Larut, Matang dan Selama", "Manjung", "Muallim", "Perak Tengah", "Others"},
            {"Beaufort", "Beluran", "Keningau", "Kota Belud", "Kinabatangan", "Kota Kinabalu", "Kota Marudu", "Kuala Penyu", "Kudat", "Kunak", "Lahad Datu", "Nabawan", "Papar", "Penampang", "Putatan", "Pitas", "Ranau", "Sandakan", "Semporna", "Sipitang", "Tambunan", "Tawau", "Telupid", "Tenom", "Tongod", "Tuaran", "Others"},
            {"Betong", "Kabong", "Pusa", "Saratok", "Bintulu", "Tatau", "Belaga", "Kapit", "Song", "Bukit Mabong", "Bau", "Kuching", "Lundu", "Lawas", "Limbang", "Beluru", "Marudi", "Miri", "Subis", "Telang Usan", "Dalat", "Daro", "Matu", "Mukah", "Tanjung Manis", "Asajaya", "Samarahan", "Simunjan", "Julau", "Meradong", "Pakan", "Sarikei", "Serian", "Tebedu", "Kanowit", "Sibu", "Selangau", "Lubok Antu", "Sri Aman", "Others"},
            {"Gombak", "Hulu Langat", "Hulu Selangor", "Klang", "Kuala Langat", "Kuala Selangor", "Petaling", "Sabak Bernam", "Sepang", "Others"},
            {"Besut", "Dungun", "Hulu Terengganu", "Kemaman", "Kuala Nerus", "Kuala Terengganu", "Marang", "Setiu", "Others"},
            {"Kuala Lumpur", "Putrajaya", "Labuan"}
    };
}
