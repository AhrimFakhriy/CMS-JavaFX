package main.ui.crudtable;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import main.data.RentableRepository;
import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;
import main.model.ui.SubMenu;
import main.ui.customerdetails.CustomerDetailsController;
import main.ui.rentableupdate.RentableUpdateController;
import main.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class RentableCrudController implements Initializable {

    @FXML protected TreeTableView<Rentable> tableView;
    @FXML protected AnchorPane rootPane;
    @FXML protected JFXButton deleteButton, addButton;

    private TreeTableColumn<Rentable, String> nameColumn, typeColumn, priceColumn;

    private StackPane mainStackPane;

    private Rentable.Type type;

    private RentableRepository rentableRepository;
    private ListProperty<Rentable> tempRentables;

    private boolean isDirty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(rootPane, 0.0);
        AnchorPane.setBottomAnchor(rootPane, 0.0);
        AnchorPane.setLeftAnchor(rootPane, 0.0);
        AnchorPane.setRightAnchor(rootPane, 0.0);

        rentableRepository = RentableRepository.getInstance();
        tempRentables = new SimpleListProperty<>(FXCollections.observableArrayList());

        isDirty = false;

        setUpTables();
        setUpColumns();
        setUpListeners();
    }

    private void setUpListeners() {

    }

    private void setUpTables() {
        tableView.setShowRoot(false);
        tableView.setFixedCellSize(25.0);

        tableView.setRowFactory( tv -> {
            TreeTableRow<Rentable> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    updateItem(row.getItem());
                }
            });
            return row;
        });
    }

    private void setUpColumns() {
        nameColumn = new TreeTableColumn<>("Name");
        typeColumn = new TreeTableColumn<>("Type");
        priceColumn = new TreeTableColumn<>("Price (RM)");

        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

        priceColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Rentable, String> rent) ->
                new SimpleStringProperty(Utils.moneyFormatter.format(rent.getValue().getValue().getPrice()))
        );

        priceColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        TreeItem<Rentable> root = new TreeItem<>();
        tableView.setRoot(root);
        tableView.getColumns().addAll(nameColumn, typeColumn, priceColumn);
        tableView.getColumns().forEach(column -> column.getStyleClass().add("table_column"));
    }

    public void setType(Rentable.Type type) {
        if(this.type != null) throw new UnsupportedOperationException("Type cannot be changed after set!");
        this.type = type;

        tempRentables.addListener(((observable, oldValue, newValue) -> {
            updateView();
        }));

        if(type == Rentable.Type.ROOM) {
            tempRentables.addAll(rentableRepository.roomsProperty());

            rentableRepository.roomsProperty().addListener(((observable, oldValue, newValue) -> {
                tempRentables.clear();
                tempRentables.addAll(newValue);
            }));

        } else {
            tempRentables.addAll(rentableRepository.hallsProperty());

            rentableRepository.hallsProperty().addListener(((observable, oldValue, newValue) -> {
                tempRentables.clear();
                tempRentables.addAll(newValue);
            }));
        }
    }

    private void updateView() {
        tableView.getRoot().getChildren().clear();
        tempRentables.get().forEach(rent -> tableView.getRoot().getChildren().add(new TreeItem<>(rent)));
        tableView.refresh();
    }

    @FXML
    private void onAddItem() {
        SubMenu addUpdateMenu = new SubMenu(getClass().getResource("/res/ui/rentable_update/rentable_update_view.fxml"));
        ((RentableUpdateController) addUpdateMenu.getController()).configure(type, null);

        JFXDialog dialog = new JFXDialog(mainStackPane, addUpdateMenu.getRoot(), JFXDialog.DialogTransition.CENTER);
        dialog.show();
    }

    @FXML
    private void onDeleteItem() {
        if(tableView.getSelectionModel().getSelectedItem() != null) {
            isDirty = true;

            tempRentables.remove(tableView.getSelectionModel().getSelectedItem().getValue());
        }
    }

    private void updateItem(Rentable item) {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public ObservableList<Rentable> getTempRentables() { return tempRentables.get(); }

    public void setMainStackPane(StackPane mainStackPane) { this.mainStackPane = mainStackPane; }
}
