package main.ui.booking;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleNode;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.SetBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import main.data.RentableRepository;
import main.model.datastructure.LinkedList;
import main.model.datastructure.Queue;
import main.model.datastructure.Stack;
import main.model.rentable.Hall;
import main.model.rentable.Rentable;
import main.model.rentable.Room;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class BookingController implements Initializable {
    private static final int NUMBER_OF_BUTTONS = 40;
    private static final String TYPE_DATE_ENTER = "DATE_ENTER";
    private static final String TYPE_DATE_EXIT = "DATE_EXIT";

    @FXML private JFXDatePicker pickerDateIn, pickerDateOut;
    @FXML private ChoiceBox<String> choiceTypeBox;
    @FXML private Label labelDuration, labelAmountSelected, labelPage;
    @FXML private GridPane buttonGrid;
    @FXML private LinkedList<JFXToggleNode> toggleButtons;
    @FXML private JFXButton prevPageButton, nextPageButton, nextButton;
    @FXML private JFXListView<String> selectedList;

    private ListChangeListener<Rentable> itemChangeListener;
    private Queue<Rentable> prevPageStack, currentPageStack, nextPageStack;
    private ObjectProperty<LocalDate> dateIn, dateOut;
    private ListProperty<Rentable> items;
    private SetProperty<String> itemTypes;
    private RentableRepository repository;
    private Rentable.Type TYPE;
    private MapProperty<String, Rentable> selectedItems;
    private SimpleIntegerProperty currentPage, maxPage;
    private SimpleIntegerProperty count;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repository = RentableRepository.getInstance();

        selectedItems = repository.getSelectedBookings();
        currentPageStack = new Queue<>();
        prevPageStack = new Queue<>();
        nextPageStack = new Queue<>();
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

    private void setUpStacks() {
        currentPageStack.clear();
        nextPageStack.clear();
        prevPageStack.clear();

        //prevPageButton.setDisable(true);

        FilteredList<Rentable> filteredList = items.filtered(
            r -> r.getType().equalsIgnoreCase(choiceTypeBox.getValue())
        );

        int current = 0;

        while (current < NUMBER_OF_BUTTONS && current < filteredList.size()) {
            currentPageStack.enqueue(filteredList.get(current));
            current++;
        }

        while (current < filteredList.size()) {
            nextPageStack.enqueue(filteredList.get(current));
            current++;
        }

        maxPage.set(current / NUMBER_OF_BUTTONS);

        if (current % NUMBER_OF_BUTTONS != 0)
                maxPage.set(maxPage.getValue() + 1);

        if (currentPage.get() == 1) {
            updateButtons();
        } else {
            currentPage.set(1);
        }
    }

    public void setUpListeners() {

        labelPage.textProperty().bind(
            Bindings.createStringBinding(() ->
                MessageFormat.format("{0} / {1}", currentPage.get(), maxPage.get()),
                currentPage,
                maxPage
            )
        );

        choiceTypeBox.valueProperty().addListener(((observable, oldValue, newValue) -> setUpStacks() ));

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
                    while (!currentPageStack.isEmpty())
                        prevPageStack.enqueue(currentPageStack.dequeue());

                    for (int i = 0; i < NUMBER_OF_BUTTONS && !nextPageStack.isEmpty(); i++)
                        currentPageStack.enqueue(nextPageStack.dequeue());

                } else {
                        while (!currentPageStack.isEmpty())
                            nextPageStack.enqueue(currentPageStack.dequeue());

                        for (int i = 0; i < NUMBER_OF_BUTTONS && !prevPageStack.isEmpty(); i++)
                            currentPageStack.enqueue(prevPageStack.dequeue());
                }
            }

            nextPageButton.setDisable(nextPageStack.isEmpty());
            prevPageButton.setDisable(prevPageStack.isEmpty());

            updateButtons();
        }));
    }

    private void updateButtons() {
        Iterator<Rentable> rentableIterator = currentPageStack.iterator();
        for(JFXToggleNode node : toggleButtons) {
            node.setVisible(rentableIterator.hasNext());
            node.setDisable(!node.isVisible());

            if(rentableIterator.hasNext()) {
                node.setText(rentableIterator.next().getName());
                node.setSelected(selectedItems.containsKey(node.getText()));
                node.setVisible(true);
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
        for(Rentable r : currentPageStack) {
            if(r.getName().equals(name)) return r.getID();
        }

        return null;
    }

    private Rentable findRentableByName(String name) {
        for(Rentable r : currentPageStack) {
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

        dateIn.addListener(((observable, oldValue, newValue) -> dateOut.set(newValue.plusDays(1))));
        pickerDateIn.setValue(LocalDate.now());

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
