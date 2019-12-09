package main.ui.confirm;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.*;
import main.data.CustomerRepository;
import main.data.RecordRepository;
import main.data.RentableRepository;
import main.model.datastructure.LinkedList;
import main.model.datastructure.binarytree.BinarySearchTree;
import main.model.interfaces.CancelRegistration;
import main.model.person.Customer;
import main.model.record.HallRecord;
import main.model.record.RentableRecord;
import main.model.record.RoomRecord;
import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;
import main.model.ui.SubMenu;
import main.ui.main.MainController;
import main.ui.register.RegisterController;
import main.utils.Utils;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import static java.time.temporal.ChronoUnit.DAYS;

public class ConfirmBookingController implements Initializable {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    @FXML private AnchorPane rootPane;
    @FXML private BorderPane tablePane;
    @FXML private Pane toolbarPane;
    @FXML private HBox toolbarHBox;
    @FXML private JFXTreeTableView<Rentable> tableView;

    @FXML private Label labelDateIn, labelDateOut, labelDuration, labelPrice, labelName, labelPhone, labelID;

    private MainController mainViewController;

    private JFXDialog dialog;

    private JFXTreeTableColumn<Rentable, String> nameColumn, typeColumn, priceColumn;
    private ObservableList<Rentable> selectedItems;

    private CancelRegistration cancelRegistrationHandler;

    private BinarySearchTree<String, LinkedList<RentableRecord>> records;
    private RecordRepository recordRepository;
    private RentableRepository rentableRepository;
    private CustomerRepository customerRepository;

    private Customer cust;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(rootPane, 0.0);
        AnchorPane.setRightAnchor(rootPane, 0.0);
        AnchorPane.setLeftAnchor(rootPane, 0.0);
        AnchorPane.setBottomAnchor(rootPane, 0.0);

        HBox.setHgrow(toolbarHBox, Priority.ALWAYS);
        HBox.setHgrow(toolbarPane, Priority.ALWAYS);

        rentableRepository = RentableRepository.getInstance();
        customerRepository = CustomerRepository.getInstance();
        recordRepository = RecordRepository.getInstance();
        records = recordRepository.getRecords();

        setUpData();
        setUpTable();
    }

    private void setUpData() {
        selectedItems = FXCollections.observableArrayList();
        selectedItems.clear();
        selectedItems.addAll(rentableRepository.getSelectedBookings().values());

        LocalDate dateIn = rentableRepository.getDateIn().get(),
                dateOut = rentableRepository.getDateOut().get();

        labelDateIn.setText(dateTimeFormatter.format(dateIn));
        labelDateOut.setText(dateTimeFormatter.format(dateOut));

        int duration = Math.toIntExact(DAYS.between(dateIn, dateOut));

        if(duration > 1)
            labelDuration.setText(duration + " Days");
        else
            labelDuration.setText(duration + " Day");

        double price = selectedItems.stream().mapToDouble(Rentable::getPrice).sum() * duration;

        labelPrice.setText("RM " + Utils.moneyFormatter.format(price));
    }

    @FXML
    private void onConfirm(ActionEvent event) throws Exception {
        boolean existingCust = false;
        int duration = Math.toIntExact(
            DAYS.between(rentableRepository.getDateIn().get(), rentableRepository.getDateOut().get())
        );

        for(Customer c : customerRepository.getCustomers()) {
            if(c.getID().equalsIgnoreCase(cust.getID())) {
                existingCust = true;
                cust = c;
                break;
            }
        }

        cust.addTransaction(selectedItems.stream().mapToDouble(Rentable::getPrice).sum() * duration);

        if(!existingCust) {
            customerRepository.getCustomers().add(cust);
        }

        customerRepository.saveCustomers();

        rentableRepository.getSelectedBookings().forEach((key, rentable) -> {
            if (records.get(rentable.getID()) == null) {
                records.put(rentable.getID(), new LinkedList<>());
            }

            RentableRecord record;

            if(rentable instanceof Room)
                record = new RoomRecord(cust.getID(), rentableRepository.getDateIn().get(), duration, (Room) rentable);
            else
                record = new HallRecord(cust.getID(), rentableRepository.getDateIn().get(), duration, (Hall) rentable);

            records.get(rentable.getID()).insertAtFront(record);

        });

        recordRepository.saveRecords(RecordRepository.RecordType.RECORD);

        dialog.close();
        rentableRepository.getSelectedBookings().clear();
        rentableRepository.getDateIn().setValue(LocalDate.now());
        rentableRepository.getDateOut().setValue(LocalDate.now().plusDays(1));

        mainViewController.loadPieCharts();
        mainViewController.updateMenu(MainController.CurrentMenu.MENU_MAIN);

        Alert bookingAlert = new Alert(Alert.AlertType.INFORMATION);
        bookingAlert.setTitle("Booking Confirmed");
        bookingAlert.setContentText("Booking has been made.\nReturning to Main Menu");
        bookingAlert.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private void setUpTable() {
        nameColumn = new JFXTreeTableColumn<>("Name");
        typeColumn = new JFXTreeTableColumn<>("Type");
        priceColumn = new JFXTreeTableColumn<>("Price (RM)");

        nameColumn.getStyleClass().add("table_column");
        typeColumn.getStyleClass().add("table_column");
        priceColumn.getStyleClass().add("table_column");

        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Rentable, String> rentable) -> {
            if(nameColumn.validateValue(rentable)) {
                return new SimpleStringProperty(rentable.getValue().getValue().getName());
            }

            return nameColumn.getComputedValue(rentable);
        });

        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Rentable, String> rentable) -> {
            if(typeColumn.validateValue(rentable)) {
                return new SimpleStringProperty(rentable.getValue().getValue().getType());
            }

            return typeColumn.getComputedValue(rentable);
        });

        priceColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Rentable, String> rentable) -> {
            if(priceColumn.validateValue(rentable)) {
                return new SimpleStringProperty(Utils.moneyFormatter.format(rentable.getValue().getValue().getPrice()));
            }

            return priceColumn.getComputedValue(rentable);
        });

        priceColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        final TreeItem<Rentable> root = new RecursiveTreeItem<>(selectedItems, RecursiveTreeObject::getChildren);

        tableView.setRoot(root);
        tableView.setShowRoot(false);
        tableView.setEditable(false);
        tableView.getColumns().setAll(nameColumn, typeColumn, priceColumn);
        tableView.setFixedCellSize(25.0);
    }

    public void setCustomer(Customer cust) {
        this.cust = cust;
        labelName.setText(cust.getName());
        labelPhone.setText(cust.getPhoneNum());
        labelID.setText(cust.getID());
    }

    public void setDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }

    @FXML
    private void onCancelRegistration(ActionEvent event) {
        cancelRegistrationHandler.cancel();
    }

    public void onCancellation(CancelRegistration cancelRegistrationHandler) {
        this.cancelRegistrationHandler = cancelRegistrationHandler;
    }

    public void setMainViewController(MainController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
