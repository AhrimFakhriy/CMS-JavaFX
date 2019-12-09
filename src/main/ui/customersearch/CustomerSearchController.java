package main.ui.customersearch;

import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import main.data.CustomerRepository;
import main.model.person.Customer;
import main.utils.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerSearchController implements Initializable {

    @FXML private JFXTextField searchField;
    @FXML private JFXButton detailButton;
    @FXML private TreeTableView<Customer> tableView;

    private TreeTableColumn<Customer, String> idColumn, nameColumn, nationalityColumn, phoneColumn, spendColumn;

    private CustomerRepository repository;
    private ArrayList<Customer> customers;
    private ArrayList<Customer> searchingCustomers;

    private StringProperty searchingProperty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repository = CustomerRepository.getInstance();
        searchingProperty = searchField.textProperty();

        customers = new ArrayList<>(repository.getCustomers());
        searchingCustomers = new ArrayList<>(customers);

        setUpListeners();
        setUpTable();
    }

    public void updateCustomers() {
        customers = new ArrayList<>(repository.getCustomers());
        searchingCustomers = new ArrayList<>(customers);
    }

    private void setUpListeners() {
        searchingProperty.addListener(((observable, oldValue, newValue) -> {
            String search = newValue;

            if(search == null || search.trim().isEmpty()) {
                searchingCustomers = new ArrayList<>(customers);
            } else {
                search = search.toLowerCase();
                searchingCustomers = new ArrayList<>();

                for(Customer c : customers) {
                    if(c.getID().toLowerCase().contains(search) ||
                        c.getName().toLowerCase().contains(search) ||
                        c.getNationality().toLowerCase().contains(search) ||
                        c.getPhoneNum().toLowerCase().contains(search)
                    ) {
                        searchingCustomers.add(c);
                    }
                }
            }

            updateTableData();
        }));
    }

    private void updateTableData() {
        tableView.getRoot().getChildren().clear();

        for(Customer c : searchingCustomers) {
            tableView.getRoot().getChildren().add(new TreeItem<>(c));
        }

        tableView.refresh();
    }

    @SuppressWarnings("unchecked")
    private void setUpTable() {
        idColumn = new TreeTableColumn<>("ID/Passport");
        nameColumn = new TreeTableColumn<>("Name");
        nationalityColumn = new TreeTableColumn<>("Nationality");
        phoneColumn = new TreeTableColumn<>("Phone Number");
        spendColumn = new TreeTableColumn<>("Spending (RM)");

        idColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Customer, String> cust) -> {
            if(cust.getValue().getValue().isCitizen())
                return new SimpleStringProperty(Utils.idFormat(cust.getValue().getValue().getID()));

            return new SimpleStringProperty(cust.getValue().getValue().getID());
        });

        phoneColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Customer, String> cust) -> {
            if(cust.getValue().getValue().isCitizen())
                return new SimpleStringProperty(Utils.phoneFormat(cust.getValue().getValue().getPhoneNum()));

            return new SimpleStringProperty(cust.getValue().getValue().getPhoneNum());
        });


        spendColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Customer, String> cust) ->
            new SimpleStringProperty(Utils.moneyFormatter.format(cust.getValue().getValue().getTotalSpent()))
        );

        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nationalityColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("nationality"));

        spendColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        TreeItem<Customer> root = new TreeItem<>();
        tableView.setRoot(root);

        tableView.setShowRoot(false);
        tableView.setEditable(false);
        tableView.getColumns().addAll(idColumn, nameColumn, nationalityColumn, phoneColumn, spendColumn);
        tableView.setFixedCellSize(25.0);
        tableView.getColumns().forEach(column -> column.getStyleClass().add("table_column"));

        updateTableData();
    }
}
