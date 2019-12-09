package main.ui.booking;

import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import main.data.RecordRepository;
import main.data.RentableRepository;
import main.model.datastructure.LinkedList;
import main.model.datastructure.Queue;
import main.model.datastructure.binarytree.BinarySearchTree;
import main.model.record.RentableRecord;
import main.model.rentable.Rentable;
import main.model.ui.SubMenu;
import main.ui.confirm.ConfirmBookingController;
import main.ui.main.MainController;
import main.ui.register.RegisterController;

import javax.management.InstanceNotFoundException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.temporal.ChronoUnit.DAYS;

public class BookingController implements Initializable {
    private static final int NUMBER_OF_BUTTONS = 40;
    private static final String TYPE_DATE_ENTER = "DATE_ENTER";
    private static final String TYPE_DATE_EXIT = "DATE_EXIT";

    @FXML private JFXDatePicker pickerDateIn, pickerDateOut;
    @FXML private ChoiceBox<String> choiceTypeBox;
    @FXML private Label labelDuration, labelAmountSelected, labelPage;
    @FXML private GridPane buttonGrid;
    @FXML private List<JFXToggleNode> toggleButtons;
    @FXML private JFXButton prevPageButton, nextPageButton, nextButton, clearButton;
    @FXML private JFXListView<String> selectedList;

    private MainController mainViewController;
    private StackPane mainStackPane;
    private SubMenu menu_Confirmation;

    private RentableRepository repository;
    private BinarySearchTree<String, LinkedList<RentableRecord>> records;

    private ListChangeListener<Rentable> itemChangeListener;
    private ObjectProperty<LocalDate> dateIn, dateOut;
    private Queue<Rentable> prevPageItems, currentPageItems, nextPageItems;
    private ListProperty<Rentable> items;
    private SetProperty<String> itemTypes;
    private Rentable.Type TYPE;
    private MapProperty<String, Rentable> selectedItems;
    private SimpleIntegerProperty currentPage, maxPage;
    private SimpleIntegerProperty count;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repository = RentableRepository.getInstance();
        records = RecordRepository.getInstance().getRecords();

        selectedItems = repository.getSelectedBookings();
        currentPageItems = new Queue<>();
        prevPageItems = new Queue<>();
        nextPageItems = new Queue<>();
        currentPage = new SimpleIntegerProperty(0);
        maxPage = new SimpleIntegerProperty(0);
        items = new SimpleListProperty<>(FXCollections.observableArrayList());
        itemTypes = new SimpleSetProperty<>();
        toggleButtons = new ArrayList<>();

        count = new SimpleIntegerProperty(0);
        count.bind(selectedItems.sizeProperty());

        setUpDatePickers();
        setUpToggleButtons();
        setUpListeners();
    }

    @FXML private void onClearSelection() {
        selectedItems.clear();
        updateButtons();
    }

    private void setUpPageItems() {
        currentPageItems.clear();
        nextPageItems.clear();
        prevPageItems.clear();

        FilteredList<Rentable> filteredList = items.filtered(
            r -> r.getType().equalsIgnoreCase(choiceTypeBox.getValue())
        );

        int current = 0;

        while (current < NUMBER_OF_BUTTONS && current < filteredList.size()) {
            currentPageItems.enqueue(filteredList.get(current));
            current++;
        }

        while (current < filteredList.size()) {
            nextPageItems.enqueue(filteredList.get(current));
            current++;
        }

        maxPage.set(current / NUMBER_OF_BUTTONS);

        if (current % NUMBER_OF_BUTTONS != 0)
                maxPage.set(maxPage.getValue() + 1);

        if (currentPage.get() == 1) {
            updateButtons();
            nextPageButton.setDisable(maxPage.get() == 1);

        } else {
            currentPage.set(1);
        }
    }

    private void setUpListeners() {

        labelPage.textProperty().bind(
            Bindings.createStringBinding(() ->
                MessageFormat.format("{0} / {1}", currentPage.get(), maxPage.get()),
                currentPage,
                maxPage
            )
        );

        choiceTypeBox.valueProperty().addListener(((observable, oldValue, newValue) -> setUpPageItems() ));

        labelAmountSelected.textProperty().bind(count.asString());

        itemChangeListener = c -> {
          for(Rentable r : c.getRemoved())
              items.remove(r);

          items.addAll(c.getAddedSubList());
            // todo :: check for changes in selected room, if exists, warns user or do something.
        };

        nextButton.setDisable(true);
        selectedItems.addListener((MapChangeListener<? super String, ? super Rentable>) change ->
                nextButton.setDisable(selectedItems.isEmpty())
        );

        nextPageButton.setOnMouseClicked(e -> currentPage.set(currentPage.getValue() + 1));
        prevPageButton.setOnMouseClicked(e -> currentPage.set(currentPage.getValue() - 1));

        nextButton.setOnMouseClicked(event -> {
            try {
                if (mainStackPane == null)
                    throw new InstanceNotFoundException("Main Stack Pane should be initialized first! (setMainStackPane())");
            }catch (InstanceNotFoundException error) {
                error.printStackTrace();
            }

            /*menu_Confirmation = new SubMenu(getClass().getResource("/res/ui/confirm/confirm_booking.fxml"));
            JFXDialog dialog = new JFXDialog(mainStackPane, menu_Confirmation.getRoot(), JFXDialog.DialogTransition.LEFT); */

            menu_Confirmation = new SubMenu(getClass().getResource("/res/ui/register/register_view.fxml"));
            JFXDialog dialog = new JFXDialog(mainStackPane, menu_Confirmation.getRoot(), JFXDialog.DialogTransition.LEFT);

            ((AnchorPane) menu_Confirmation.getRoot()).setPrefSize(
                    mainStackPane.getWidth() * 0.8,
                    mainStackPane.getHeight() * 0.8
            );

            ChangeListener<Number> sizeChangeListener = (observable, oldValue, newValue) ->
                ((AnchorPane) menu_Confirmation.getRoot()).setPrefSize(
                        mainStackPane.getWidth() * 0.8,
                        mainStackPane.getHeight() * 0.8
            );


            mainStackPane.heightProperty().addListener(sizeChangeListener);

            ((RegisterController) menu_Confirmation.getController()).onCancellation(dialog::close);
            ((RegisterController) menu_Confirmation.getController()).setMainViewController(mainViewController);
            ((RegisterController) menu_Confirmation.getController()).setDialog(dialog);

            dialog.show();
            dialog.setOnDialogClosed(closeEvent ->
                mainStackPane.heightProperty().removeListener(sizeChangeListener)
            );
        });

        currentPage.addListener(((observable, oldValue, newValue) -> {
            if(oldValue.intValue() != 0) {
                if (newValue.intValue() > oldValue.intValue()) {
                    while (!currentPageItems.isEmpty())
                        prevPageItems.enqueue(currentPageItems.dequeue());

                    for (int i = 0; i < NUMBER_OF_BUTTONS && !nextPageItems.isEmpty(); i++)
                        currentPageItems.enqueue(nextPageItems.dequeue());

                } else {
                    if (!prevPageItems.isEmpty()) {
                        while (!currentPageItems.isEmpty())
                            nextPageItems.enqueue(currentPageItems.dequeue());

                        for (int i = 0; i < NUMBER_OF_BUTTONS && !prevPageItems.isEmpty(); i++)
                            currentPageItems.enqueue(prevPageItems.dequeue());
                    }
                }

            }

            nextPageButton.setDisable(nextPageItems.isEmpty());
            prevPageButton.setDisable(prevPageItems.isEmpty());

            updateButtons();
        }));

        selectedList.setCellFactory(param -> {
            AtomicInteger clickCount = new AtomicInteger(0);

            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if(item != null && !empty) {
                        HBox box = new HBox(new Label(item));
                        setGraphic(box);
                    } else {
                        setGraphic(new HBox());
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if(cell.isEmpty())
                    event.consume();
                else {
                    if(clickCount.get() != 1)
                        clickCount.getAndIncrement();
                    else {
                        System.out.println(cell.itemProperty().get());
                        clickCount.getAndSet(0);
                    }
                }

            });

            return cell;
        });
    }

    public void updateButtons() {
        Iterator<Rentable> rentableIterator = currentPageItems.iterator();
        for(JFXToggleNode node : toggleButtons) {
            node.setVisible(rentableIterator.hasNext());
            node.setDisable(false);

            if(node.isVisible()) {
                Rentable r = rentableIterator.next();
                node.setText(r.getName());
                node.setSelected(selectedItems.containsKey(r.getName()));

                LinkedList<RentableRecord> recordList = records.get(r.getID());

                if(recordList != null) {
                    for(RentableRecord record : recordList) {
                        if(!isAvailable(record)) {
                            node.setDisable(true);
                            break;
                        }
                    }
                }

            } else {
                node.setDisable(true);
            }
        }
    }

    private boolean isAvailable(RentableRecord record) {
        return dateIn.get().isAfter(record.getDateOut()) || dateOut.get().isBefore(record.getDateIn());
    }

    public void setMainStackPane(StackPane pane) throws Exception {
        if(mainStackPane != null) throw new Exception("Pane cannot be changed after initialized!");
        mainStackPane = pane;
    }

    public void setRentableType(Rentable.Type type) throws Exception {
        if(TYPE != null) throw new Exception("Type cannot be changed!");

        TYPE = type;

        if(TYPE == Rentable.Type.ROOM) {
            items.addAll(repository.roomsProperty());
            itemTypes = repository.roomTypesProperty();

            repository.roomsProperty().addListener(itemChangeListener);

        } else {
            items.addAll(repository.hallsProperty());
            itemTypes = repository.hallTypesProperty();

            repository.hallsProperty().addListener(itemChangeListener);
        }

        choiceTypeBox.itemsProperty().bind(Bindings.createObjectBinding(() ->
            FXCollections.observableArrayList(itemTypes.getValue()), itemTypes
        ));

        choiceTypeBox.getSelectionModel().selectFirst();
        selectedList.itemsProperty().bind(Bindings.createObjectBinding(() ->
            FXCollections.observableArrayList(selectedItems.keySet()), selectedItems
        ));
    }

    private void setUpToggleButtons() {
        buttonGrid.getChildren()
                .filtered(node -> node instanceof JFXToggleNode)
                .forEach(node -> toggleButtons.add((JFXToggleNode) node));

        for (JFXToggleNode node : toggleButtons) {
            node.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if(node.isSelected()) {
                    selectedItems.put(node.getText(), findRentableByName(node.getText()));
                } else {
                    selectedItems.remove(node.getText(), findRentableByName(node.getText()));
                }
            });
        }
    }

    private String findRentableIDByName(String name) {
        for(Rentable r : currentPageItems) {
            if(r.getName().equals(name)) return r.getID();
        }

        return null;
    }

    private Rentable findRentableByName(String name) {
        for(Rentable r : currentPageItems) {
            if(r.getName().equals(name)) return r;
        }

        return null;
    }

    private void setUpDatePickers() {
        dateIn = pickerDateIn.valueProperty();
        dateOut = pickerDateOut.valueProperty();

        StringConverter<LocalDate> localDateStringConverter = new StringConverter<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            public String toString(LocalDate localDate) {
                if(localDate == null) return "";
                return formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if(dateString == null || dateString.trim().isEmpty()) return null;
                return LocalDate.parse(dateString, formatter);
            }
        };

        pickerDateIn.setConverter(localDateStringConverter);
        pickerDateIn.setDayCellFactory(createDateFactory(TYPE_DATE_ENTER));

        pickerDateOut.setConverter(localDateStringConverter);
        pickerDateOut.setDayCellFactory(createDateFactory(TYPE_DATE_EXIT));

        dateIn.addListener(((observable, oldValue, newValue) -> {
            if(oldValue != newValue) {
                if(dateOut.get() == null) {
                    dateOut.set(newValue.plusDays(1));
                } else {
                    if ((dateOut.get().isEqual(newValue) || dateOut.get().isBefore(newValue))) {
                        dateOut.set(newValue.plusDays(1));
                    }
                }

                repository.getDateIn().setValue(newValue);

                labelDuration.setText(String.valueOf(DAYS.between(dateIn.get(), dateOut.get())));
                updateButtons();
            }
        }));

        pickerDateIn.setValue(repository.getDateIn().get());
        pickerDateOut.setValue(repository.getDateOut().get());

        dateOut.addListener(((observable, oldValue, newValue) -> {
            if(oldValue != newValue) {
                repository.getDateOut().setValue(newValue);

                labelDuration.setText(String.valueOf(DAYS.between(dateIn.get(), dateOut.get())));
                updateButtons();
            }
        }));

    }

    private Callback<DatePicker, DateCell> createDateFactory(String dateType) {
        return new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);

                        if(dateType.equals(TYPE_DATE_ENTER))
                            setDisable(empty || date.compareTo(LocalDate.now()) < 0);
                        else
                            setDisable(empty || date.compareTo(dateIn.getValue()) < 1);
                    }
                };
            }
        };
    }

    public void setMainViewController(MainController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
