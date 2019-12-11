package main.ui.rentableupdate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import main.data.RentableRepository;
import main.model.rentable.Rentable;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ResourceBundle;

public class RentableUpdateController implements Initializable {

    @FXML
    private CustomTextField nameField, typeField, priceField;

    private RentableRepository rentableRepository;

    private Rentable rentable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rentableRepository = RentableRepository.getInstance();

        setUpListeners();
    }

    private void setUpListeners() {
        priceField.textProperty().setValue("100.00");

        priceField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue.matches("/^[+]?([1-9][0-9]*(?:[\\.][0-9]*)?|0*\\.0*[1-9][0-9]*)(?:[eE][+-][0-9]+)?$/")) {
                priceField.textProperty().setValue(oldValue);
            }
        }));
    }

    public void configure(Rentable.Type type, Rentable rentable) {
        this.rentable = rentable;

        if(type == Rentable.Type.ROOM) {
            TextFields.bindAutoCompletion(typeField, rentableRepository.roomTypesProperty());


        } else {
            TextFields.bindAutoCompletion(typeField, rentableRepository.hallTypesProperty());

        }
    }
}
