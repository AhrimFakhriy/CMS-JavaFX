package main.ui.admin;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import main.data.RentableRepository;
import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;
import main.model.ui.SubMenu;
import main.ui.crudtable.RentableCrudController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private TabPane tabPane;
    @FXML private JFXButton saveButton;

    private StackPane mainStackPane;

    private RentableRepository rentableRepository;
    private SubMenu editRoomMenu, editHallMenu, editStaffMenu;
    private RentableCrudController roomController, hallController;
    private Tab roomTab, hallTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(rootPane, 0.0);
        AnchorPane.setBottomAnchor(rootPane, 0.0);
        AnchorPane.setLeftAnchor(rootPane, 0.0);
        AnchorPane.setRightAnchor(rootPane, 0.0);

        rentableRepository = RentableRepository.getInstance();

        setUpTabs();
    }

    private void setUpTabs() {
        editRoomMenu = new SubMenu(getClass().getResource("/res/ui/crud_table_tab/rent_crud_table_view.fxml"));
        roomController = editRoomMenu.getController();
        roomController.setType(Rentable.Type.ROOM);
        roomController.setMainStackPane(mainStackPane);

        roomTab = new Tab("Room List", editRoomMenu.getRoot());

        editHallMenu = new SubMenu(getClass().getResource("/res/ui/crud_table_tab/rent_crud_table_view.fxml"));
        hallController = editHallMenu.getController();
        hallController.setType(Rentable.Type.HALL);
        hallController.setMainStackPane(mainStackPane);

        hallTab = new Tab("Hall List", editHallMenu.getRoot());

        tabPane.getTabs().addAll(
                roomTab,
                hallTab
        );


    }

    @FXML
    private void onSaveItem() {
        if(tabPane.getSelectionModel().getSelectedItem().equals(roomTab)) {
            if (roomController.isDirty()) {
                ObservableList<Room> rooms = FXCollections.observableArrayList();

                for(Rentable r : roomController.getTempRentables()) {
                    rooms.add((Room) r);
                }

                rentableRepository.roomsProperty().set(rooms);
                rentableRepository.saveRooms();

                roomController.setDirty(false);
            }

        } else {
            if(hallController.isDirty()) {
                ObservableList<Hall> halls = FXCollections.observableArrayList();

                for(Rentable r : hallController.getTempRentables()) {
                    halls.add((Hall) r);
                }

                rentableRepository.hallsProperty().set(halls);
                rentableRepository.saveHalls();

                hallController.setDirty(false);
            }

        }
    }

    public void setMainStackPane(StackPane mainStackPane) { this.mainStackPane = mainStackPane; }
}
