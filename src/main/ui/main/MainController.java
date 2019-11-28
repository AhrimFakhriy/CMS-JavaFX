package main.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.animation.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.data.RecordRepository;
import main.data.RentableRepository;
import main.data.Settings;
import main.model.datastructure.LinkedList;
import main.model.datastructure.binarytree.BinarySearchTree;
import main.model.person.Admin;
import main.model.person.Employee;
import main.model.person.Person;
import main.model.record.HallRecord;
import main.model.record.RentableRecord;
import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;
import main.model.ui.SubMenu;
import main.ui.booking.BookingController;
import main.ui.login.LoginView;
import main.utils.Utils;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private enum CurrentMenu {
        MENU_MAIN(null),
        MENU_HALL_BOOKING(null),
        MENU_ROOM_BOOKING(null),
        MENU_TICKET_BOOKING(null),
        MENU_CUSTOMER_DETAILS(null),
        MENU_ADMIN(null);

        private JFXButton button;

        CurrentMenu(JFXButton button) {
            this.button = button;
        }

        void setButton(JFXButton button) { this.button = button; }
        JFXButton getButton() { return button; }
    }

    @FXML private Label labelMenu;
    @FXML private Label labelName;
    @FXML private Label labelRank;

    @FXML private JFXButton logOutButton;

    @FXML private StackPane stackPane;
    @FXML private BorderPane panelMain;
    @FXML private Pane opaquePane;
    @FXML private HBox drawerBox;
    @FXML private GridPane drawerPane;
    @FXML private Pane fillerPane;
    @FXML private VBox toolbarVBox;
    @FXML private HBox mainPane;

    @FXML private PieChart roomPieChart;
    @FXML private PieChart hallPieChart;

    @FXML private JFXHamburger hamburger;

    @FXML private JFXButton buttonMainMenu;
    @FXML private JFXButton buttonBookRoom;
    @FXML private JFXButton buttonBookHall;
    @FXML private JFXButton buttonBookTicket;
    @FXML private JFXButton buttonCustomerDetails;
    @FXML private JFXButton buttonAdminMenu;

    private int MENU_STATUS = 2;
    private boolean FIRST_RUN = true;
    private CurrentMenu currentMenu;

    private SubMenu hallBookingMenu;
    private SubMenu roomBookingMenu;
    private SubMenu ticketBookingMenu;
    private SubMenu customerDetailsMenu;
    private SubMenu adminMenu;

    private BinarySearchTree<String, LinkedList<RentableRecord>> records;
    private ListProperty<Hall> halls;
    private ListProperty<Room> rooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonMainMenu.getStyleClass().add("selected_menu");

        records = RecordRepository.getInstance().getRecords();
        halls = RentableRepository.getInstance().hallsProperty();
        rooms = RentableRepository.getInstance().roomsProperty();

        updateMenu();

        drawerBox.setVisible(false);
        animateDrawerClose();

        setUpListeners();

        loadPieCharts();
        loadSettings();
        drawerOnClick();

        onActionLogOut();
    }

    private void updateMenu() {
        CurrentMenu.MENU_MAIN.setButton(buttonMainMenu);
        CurrentMenu.MENU_ROOM_BOOKING.setButton(buttonBookRoom);
        CurrentMenu.MENU_HALL_BOOKING.setButton(buttonBookHall);
        CurrentMenu.MENU_TICKET_BOOKING.setButton(buttonBookTicket);
        CurrentMenu.MENU_CUSTOMER_DETAILS.setButton(buttonCustomerDetails);
        CurrentMenu.MENU_ADMIN.setButton(buttonAdminMenu);

        currentMenu = CurrentMenu.MENU_MAIN;
    }

    private void setUpListeners() {

    }

    private void loadPieCharts() {
        int bookedRoomCount = 0,
            bookedHallCount = 0;

        for(LinkedList<RentableRecord> list : records) {
            for(RentableRecord r : list) {
                LocalDate today = LocalDate.now();

                if(today.isBefore(r.getDateOut()) && today.isAfter(r.getDateIn())) {
                    if (r instanceof HallRecord) {
                        bookedHallCount++;
                    } else {
                        bookedRoomCount++;
                    }
                }
            }
        }

        // todo :: check for same room/hall that's booked on different dates.

        PieChart.Data bookedRoom = new PieChart.Data("Booked - " + bookedRoomCount, bookedRoomCount);
        PieChart.Data unbookedRoom = new PieChart.Data(
                "Available - " + (rooms.getSize()-bookedRoomCount),
                rooms.getSize()-bookedRoomCount
        );

        roomPieChart.getData().addAll(bookedRoom, unbookedRoom);

        PieChart.Data bookedHall = new PieChart.Data("Booked - " + bookedHallCount, bookedHallCount);
        PieChart.Data unbookedHall = new PieChart.Data(
                "Available - " + (halls.getSize()-bookedHallCount),
                halls.getSize()-bookedHallCount
        );

        hallPieChart.getData().addAll(bookedHall, unbookedHall);

        ListProperty<PieChart.Data> chartData = new SimpleListProperty<>(FXCollections.observableArrayList());
        chartData.addAll(bookedRoom, unbookedRoom, bookedHall, unbookedHall);
    }

    @FXML private void onAction(ActionEvent event) {
        try {
            if (event.getSource().equals(buttonMainMenu)) {
                updateMenu(CurrentMenu.MENU_MAIN);
            }
            else if (event.getSource().equals(buttonBookRoom)) {
                updateMenu(CurrentMenu.MENU_ROOM_BOOKING);
            }
            else if (event.getSource().equals(buttonBookHall)) {
                updateMenu(CurrentMenu.MENU_HALL_BOOKING);
            }
            else if (event.getSource().equals(buttonBookTicket)) {
                updateMenu(CurrentMenu.MENU_TICKET_BOOKING);
            }
            else if (event.getSource().equals(buttonCustomerDetails)) {
                updateMenu(CurrentMenu.MENU_CUSTOMER_DETAILS);
            }
            else if (event.getSource().equals(buttonAdminMenu)) {
                updateMenu(CurrentMenu.MENU_ADMIN);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            animateDrawerClose();
        }

    }

    private void updateMenu(CurrentMenu selectedMenu) throws Exception {
        if(currentMenu == selectedMenu) return;

        drawerPane.getChildren().forEach(node -> node.getStyleClass().remove("selected_menu"));
        currentMenu = selectedMenu;


        switch (selectedMenu) {
            case MENU_ROOM_BOOKING: {
                labelMenu.setText("ROOM BOOKING");

                if(roomBookingMenu == null) {
                    roomBookingMenu = new SubMenu(getClass().getResource("/res/ui/booking/booking_view.fxml"));
                    ((BookingController) roomBookingMenu.getController()).setRentableType(Rentable.Type.ROOM);
                    ((BookingController) roomBookingMenu.getController()).setMainStackPane(stackPane);
                }

                panelMain.setCenter(roomBookingMenu.getRoot());
                break;
            }
            case MENU_HALL_BOOKING: {
                labelMenu.setText("HALL BOOKING");

                if(hallBookingMenu == null) {
                    hallBookingMenu = new SubMenu(getClass().getResource("/res/ui/booking/booking_view.fxml"));
                    ((BookingController) hallBookingMenu.getController()).setRentableType(Rentable.Type.HALL);
                    ((BookingController) roomBookingMenu.getController()).setMainStackPane(stackPane);
                }

                panelMain.setCenter(hallBookingMenu.getRoot());
                break;
            }
            case MENU_TICKET_BOOKING: {
                labelMenu.setText("TICKET BOOKING");
                break;
            }
            case MENU_CUSTOMER_DETAILS: {
                labelMenu.setText("CUSTOMER DETAILS");
                break;
            }
            case MENU_ADMIN: {
                labelMenu.setText("ADMIN MENU");
                break;
            }
            default: {
                labelMenu.setText("MAIN MENU");
                panelMain.setCenter(mainPane);
            }
        }

        currentMenu.getButton().getStyleClass().add("selected_menu");
    }

    private void loadSettings() {
        if(!Settings.getInstance().isTicketServiceEnabled())
            buttonBookTicket.setDisable(true);

        Person currentUser = Settings.getInstance().getCurrentUser().get();
        labelName.setText(currentUser.getName());

        if(currentUser instanceof Admin)
            labelRank.setText("Administrator");
        else {
            if(!((Employee) currentUser).getRank().contains("Manager"))
                drawerPane.getChildren().remove(buttonAdminMenu);

            labelRank.setText(((Employee) currentUser).getRank());
        }
        HBox.setHgrow(toolbarVBox, Priority.ALWAYS);
        HBox.setHgrow(fillerPane, Priority.ALWAYS);
    }

    private void animateDrawerClose() {
        MENU_STATUS = 1;

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), hamburger);

        if(!FIRST_RUN) {
            rotateTransition.setFromAngle(90);
            rotateTransition.setToAngle(0);
        }else {
            FIRST_RUN = false;
        }

        FadeTransition opaquePaneTransition = new FadeTransition(Duration.seconds(0.1), opaquePane);
        opaquePaneTransition.setFromValue(0.65);
        opaquePaneTransition.setToValue(0.0);

        TranslateTransition drawerPaneTransition = new TranslateTransition(Duration.seconds(0.5), drawerPane);
        drawerPaneTransition.setByX(-600);

        SequentialTransition sequentialTransition = new SequentialTransition(opaquePaneTransition, drawerPaneTransition);
        ParallelTransition parallelTransition = new ParallelTransition(rotateTransition, sequentialTransition);

        parallelTransition.play();

        parallelTransition.setOnFinished(event -> {
            drawerBox.setVisible(false);
            MENU_STATUS = 0;
        });
    }

    private void animateDrawerOpen() {
        MENU_STATUS = 1;
        drawerBox.setVisible(true);

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), hamburger);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(90);

        FadeTransition opaquePaneTransition = new FadeTransition(Duration.seconds(0.1), opaquePane);
        opaquePaneTransition.setFromValue(0.0);
        opaquePaneTransition.setToValue(0.65);

        TranslateTransition drawerPaneTransition = new TranslateTransition(Duration.seconds(0.5), drawerPane);
        drawerPaneTransition.setByX(+600);

        SequentialTransition sequentialTransition = new SequentialTransition(drawerPaneTransition, opaquePaneTransition);

        ParallelTransition transition = new ParallelTransition(rotateTransition, sequentialTransition);

        transition.play();

        transition.setOnFinished(event -> MENU_STATUS = 2);
    }

    private void drawerOnClick() {
        hamburger.setOnMouseClicked(event -> {
            if (MENU_STATUS == 2) {
                animateDrawerClose();
            } else if(MENU_STATUS == 0){
                animateDrawerOpen();
            }
        });

        opaquePane.setOnMouseClicked(event -> {
            if(MENU_STATUS == 2)
                animateDrawerClose();
        });
    }

    private void onActionLogOut() {
        logOutButton.setOnMouseClicked(event -> {
            try {
                Settings.getInstance().setCurrentUser(null);

                Stage stage = new Stage();
                LoginView loginView = new LoginView();

                Utils.setLoginStage(stage);
                loginView.start(stage);

                logOutButton.getScene().getWindow().hide();

            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
