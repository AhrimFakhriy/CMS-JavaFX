package main.ui.hallbooking;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXToggleNode;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import main.data.RentableRepository;
import main.model.datastructure.LinkedList;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.ResourceBundle;

public class HallBookingController implements Initializable {
    private static final String TYPE_DATE_ENTER = "DATE_ENTER";
    private static final String TYPE_DATE_EXIT = "DATE_EXIT";

    @FXML private JFXDatePicker pickerDateIn, pickerDateOut;
    @FXML private ChoiceBox<String> choiceTypeBox;
    @FXML private Label labelDuration, labelAmountSelected, labelPage;
    @FXML private GridPane buttonGrid;
    @FXML private LinkedList<JFXToggleNode> toggleButtons;
    @FXML private JFXButton prevPageButton, nextPageButton, nextButton;

    private ObjectProperty<LocalDate> dateIn, dateOut;
    private RentableRepository repository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleButtons = new LinkedList<>();
        repository = RentableRepository.getInstance();

        setUpDatePickers();
        setUpToggleButtons();
        setUpChoiceTypeBox();

    }

    private void setUpChoiceTypeBox() {
        choiceTypeBox.getItems().addAll(repository.getRoomTypes());
        choiceTypeBox.getSelectionModel().selectFirst();
    }

    private void setUpToggleButtons() {
        buttonGrid.getChildren()
                .filtered(node -> node instanceof JFXToggleNode)
                .forEach(node -> toggleButtons.insertAtBack((JFXToggleNode) node));

        Iterator<JFXToggleNode> it = toggleButtons.iterator();

        for(int i = 0; it.hasNext(); i++) {
            it.next().setText(String.valueOf(i));
        }
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
