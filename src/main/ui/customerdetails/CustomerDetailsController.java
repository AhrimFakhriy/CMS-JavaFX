package main.ui.customerdetails;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.data.RecordRepository;
import main.model.datastructure.LinkedList;
import main.model.interfaces.CancelDialog;
import main.model.person.Customer;
import main.model.record.HallRecord;
import main.model.record.RentableRecord;
import main.model.record.RoomRecord;
import main.utils.Utils;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CustomerDetailsController implements Initializable {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox addressBox;

    @FXML
    private Label labelID, labelName, labelPhoneNumber, labelNationality, labelPrice, labelStreetOne, labelStreetTwo,
    labelPostalCode, labelDistrict, labelState;

    @FXML
    private TreeTableView<RentableRecord> tableView;
    private TreeTableColumn<RentableRecord, String> nameColumn, typeColumn, dateInColumn, dateOutColumn;

    private CancelDialog cancelDialogHandler;


    private RecordRepository recordRepository;

    private Customer customer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(rootPane, 0.0);
        AnchorPane.setRightAnchor(rootPane, 0.0);
        AnchorPane.setLeftAnchor(rootPane, 0.0);
        AnchorPane.setBottomAnchor(rootPane, 0.0);

        recordRepository = RecordRepository.getInstance();

        setUpTable();
    }

    @SuppressWarnings("unchecked")
    private void setUpTable() {
        nameColumn = new TreeTableColumn<>("Name");
        typeColumn = new TreeTableColumn<>("Type");
        dateInColumn = new TreeTableColumn<>("Date In");
        dateOutColumn = new TreeTableColumn<>("Date Out");

        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<RentableRecord, String> rentable) -> {
            if(rentable.getValue().getValue() instanceof RoomRecord) {
                return new SimpleStringProperty(((RoomRecord) rentable.getValue().getValue()).getRoom().getName());
            } else {
                return new SimpleStringProperty(((HallRecord) rentable.getValue().getValue()).getHall().getName());
            }
        });

        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<RentableRecord, String> rentable) -> {
            if(rentable.getValue().getValue() instanceof RoomRecord) {
                return new SimpleStringProperty(((RoomRecord) rentable.getValue().getValue()).getRoom().getType());
            } else {
                return new SimpleStringProperty(((HallRecord) rentable.getValue().getValue()).getHall().getType());
            }
        });

        dateInColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<RentableRecord, String> rentable) ->
                new SimpleStringProperty(dtf.format(rentable.getValue().getValue().getDateIn()))
        );

        dateOutColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<RentableRecord, String> rentable) ->
                new SimpleStringProperty(dtf.format(rentable.getValue().getValue().getDateOut()))
        );

        TreeItem<RentableRecord> root = new TreeItem<>();
        tableView.setRoot(root);

        tableView.setShowRoot(false);
        tableView.setEditable(false);
        tableView.getColumns().addAll(nameColumn, typeColumn, dateInColumn, dateOutColumn);
        tableView.setFixedCellSize(25.0);
        tableView.getColumns().forEach(column -> column.getStyleClass().add("table_column"));
    }

    private void updateView() {
        if(customer.isCitizen())
            labelID.setText(Utils.idFormat(customer.getID()));
        else
            labelID.setText(customer.getID());

        labelName.setText(customer.getName());

        if(customer.isCitizen())
            labelPhoneNumber.setText(Utils.phoneFormat(customer.getPhoneNum()));
        else
            labelPhoneNumber.setText(customer.getPhoneNum());

        labelNationality.setText(customer.getNationality());
        labelPrice.setText(Utils.moneyFormatter.format(customer.getTotalSpent()));

        addressBox.setVisible(customer.isCitizen());

        if(customer.isCitizen()) {
            labelStreetOne.setText(customer.getAddress().getStreet1());
            labelStreetTwo.setText(customer.getAddress().getStreet2());
            labelPostalCode.setText(customer.getAddress().getPostCode());
            labelDistrict.setText(customer.getAddress().getDistrict());
            labelState.setText(customer.getAddress().getState());
        }

        updateTableData();
    }

    private void updateTableData() {
        tableView.getRoot().getChildren().clear();

        for(LinkedList<RentableRecord> list : recordRepository.getRecords()) {
            for(RentableRecord r : list) {
                if(r.getCustID().equalsIgnoreCase(customer.getID())) {
                    tableView.getRoot().getChildren().add(new TreeItem<>(r));
                }
            }
        }

        for(LinkedList<RentableRecord> list : recordRepository.getOldRecords()) {
            for(RentableRecord r : list) {
                if(r.getCustID().equalsIgnoreCase(customer.getID())) {
                    tableView.getRoot().getChildren().add(new TreeItem<>(r));
                }
            }
        }

        tableView.refresh();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateView();
    }

    @FXML
    private void onCancelDialog(ActionEvent event) {
        cancelDialogHandler.cancel();
    }

    @FXML
    public void onCancellation(CancelDialog cancelDialogHandler) {
        this.cancelDialogHandler = cancelDialogHandler;
    }
}
