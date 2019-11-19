package main.ui.booking;

import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import main.data.RecordRepository;
import main.data.RentableRepository;
import main.model.datastructure.LinkedList;
import main.model.datastructure.Queue;
import main.model.datastructure.binarytree.BinarySearchTree;
import main.model.record.RentableRecord;
import main.model.rentable.Rentable;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
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
    @FXML private LinkedList<JFXToggleNode> toggleButtons;
    @FXML private JFXButton prevPageButton, nextPageButton, nextButton, clearButton;
    @FXML private JFXListView<String> selectedList;


    private RentableRepository repository;
    private BinarySearchTree<String, RentableRecord> records;

    private ListChangeListener<Rentable> itemChangeListener;
    private Queue<Rentable> prevPageItems, currentPageItems, nextPageItems;
    private ObjectProperty<LocalDate> dateIn, dateOut;
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
        toggleButtons = new LinkedList<>();

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

        nextPageButton.setOnMouseClicked(e -> currentPage.set(currentPage.getValue() + 1));
        prevPageButton.setOnMouseClicked(e -> currentPage.set(currentPage.getValue() - 1));

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

                    if(item != null) {
                        HBox box = new HBox(new Label(item));
                        setGraphic(box);
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

    private void updateButtons() {
        Iterator<Rentable> rentableIterator = currentPageItems.iterator();
        for(JFXToggleNode node : toggleButtons) {
            node.setVisible(rentableIterator.hasNext());

            if(node.isVisible()) {
                Rentable r = rentableIterator.next();
                node.setText(r.getName());
                node.setSelected(selectedItems.containsKey(r.getName()));

                node.setDisable(records.get(r.getID()) != null);

            } else {
                node.setDisable(true);
            }
        }
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
                .forEach(node -> toggleButtons.insertAtBack((JFXToggleNode) node));

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
            public String toString(LocalDate localdate) {
                if(localdate == null) return "";
                return formatter.format(localdate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if(dateString == null || dateString.trim().isEmpty()) return null;
                return LocalDate.parse(dateString, formatter);
            }
        };

        pickerDateIn.setConverter(localDateStringConverter);
        pickerDateIn.setDayCellFactory(createFactory(TYPE_DATE_ENTER));

        pickerDateOut.setConverter(localDateStringConverter);
        pickerDateOut.setDayCellFactory(createFactory(TYPE_DATE_EXIT));

        dateIn.addListener(((observable, oldValue, newValue) -> {
            dateOut.set(newValue.plusDays(1));


            labelDuration.setText(String.valueOf(DAYS.between(dateIn.get(), dateOut.get())));
        }));

        pickerDateIn.setValue(LocalDate.now());

        dateOut.addListener(((observable, oldValue, newValue) -> {
            labelDuration.setText(String.valueOf(DAYS.between(dateIn.get(), dateOut.get())));
        }));

    }

    private Callback<DatePicker, DateCell> createFactory(String dateType) {
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
}
