package main.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.animation.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.data.RecordRepository;
import main.data.RentableRepository;
import main.data.Settings;
import main.model.datastructure.binarytree.BinarySearchTree;
import main.model.person.Admin;
import main.model.person.Employee;
import main.model.person.Person;
import main.model.record.HallRecord;
import main.model.record.RentableRecord;
import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;
import main.ui.booking.BookingController;
import main.ui.login.LoginView;
import main.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private enum CurrentMenu {
        MENU_MAIN,
        MENU_HALL_BOOKING,
        MENU_ROOM_BOOKING,
        MENU_TICKET_BOOKING,
        MENU_CUSTOMER_DETAILS,
        MENU_ADMIN
    }

    private static class SubMenu {
        private FXMLLoader loader;
        Parent root;

        SubMenu(URL url) {
            try {
                loader = new FXMLLoader(url);
                root = loader.load();

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        <T> T getController() {
            return loader.getController();
        }
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

    private BinarySearchTree<String, RentableRecord> records;
    private ListProperty<Hall> halls;
    private ListProperty<Room> rooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonMainMenu.getStyleClass().add("selected_menu");
        currentMenu = CurrentMenu.MENU_MAIN;

        records = RecordRepository.getInstance().getRecords();
        halls = RentableRepository.getInstance().hallsProperty();
        rooms = RentableRepository.getInstance().roomsProperty();

        drawerBox.setVisible(false);
        animateDrawerClose();

        setUpListeners();

        loadPieCharts();
        loadSettings();
        drawerOnClick();

        onActionLogOut();
    }

    private void setUpListeners() {

    }

    private void loadPieCharts() {
        int bookedRoomCount = 0,
            bookedHallCount = 0;

        for(RentableRecord r : records) {
            if(r instanceof HallRecord) {
                bookedHallCount++;
            } else {
                bookedRoomCount++;
            }
        }

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

    @FXML private void onAction(ActionEvent event) throws Exception {
        if(event.getSource().equals(buttonMainMenu)) {
            if(!updateMenu(CurrentMenu.MENU_MAIN)) return;

            labelMenu.setText("MAIN MENU");

            panelMain.setCenter(mainPane);
        }

        if(event.getSource().equals(buttonBookRoom)) {
            if(!updateMenu(CurrentMenu.MENU_ROOM_BOOKING)) return;

            labelMenu.setText("ROOM BOOKING");

            if(roomBookingMenu == null) {
                roomBookingMenu = new SubMenu(getClass().getResource("/res/ui/booking/booking_view.fxml"));
                ((BookingController) roomBookingMenu.getController()).setRentableType(Rentable.Type.ROOM);
            }

            panelMain.setCenter(roomBookingMenu.root);
        }

        if(event.getSource().equals(buttonBookHall)) {
            if(!updateMenu(CurrentMenu.MENU_HALL_BOOKING)) return;

            labelMenu.setText("HALL BOOKING");

            if(hallBookingMenu == null) {
                hallBookingMenu = new SubMenu(getClass().getResource("/res/ui/booking/booking_view.fxml"));
                ((BookingController) hallBookingMenu.getController()).setRentableType(Rentable.Type.HALL);
            }

            panelMain.setCenter(hallBookingMenu.root);
        }

        if(event.getSource().equals(buttonBookTicket)) {
            if(!updateMenu(CurrentMenu.MENU_TICKET_BOOKING)) return;
        }

        if(event.getSource().equals(buttonCustomerDetails)) {
            if(!updateMenu(CurrentMenu.MENU_CUSTOMER_DETAILS)) return;
        }

        if(event.getSource().equals(buttonAdminMenu)) {
            if(!updateMenu(CurrentMenu.MENU_ADMIN)) return;
        }

        animateDrawerClose();
    }

    /**
     * @param selectedMenu : get current selected menu
     * @return returns false if menu did not update, true if menu is updated.
     */
    private boolean updateMenu(CurrentMenu selectedMenu) {
        if(currentMenu == selectedMenu) return false;

        JFXButton currentMenuButton;

        switch (currentMenu) {
            case MENU_ROOM_BOOKING:
                currentMenuButton = buttonBookRoom; break;

            case MENU_HALL_BOOKING:
                currentMenuButton = buttonBookHall; break;

            case MENU_TICKET_BOOKING:
                currentMenuButton = buttonBookTicket; break;

            case MENU_CUSTOMER_DETAILS:
                currentMenuButton = buttonCustomerDetails; break;

            case MENU_ADMIN:
                currentMenuButton = buttonAdminMenu; break;

            default:
                currentMenuButton = buttonMainMenu;

        }

        currentMenuButton.getStyleClass().remove("selected_menu");

        currentMenu = selectedMenu;

        switch (selectedMenu) {
            case MENU_ROOM_BOOKING:
                currentMenuButton = buttonBookRoom; break;

            case MENU_HALL_BOOKING:
                currentMenuButton = buttonBookHall; break;

            case MENU_TICKET_BOOKING:
                currentMenuButton = buttonBookTicket; break;

            case MENU_CUSTOMER_DETAILS:
                currentMenuButton = buttonCustomerDetails; break;

            case MENU_ADMIN:
                currentMenuButton = buttonAdminMenu; break;

            default:
                currentMenuButton = buttonMainMenu; break;
        }

        currentMenuButton.getStyleClass().add("selected_menu");

        return true;
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
