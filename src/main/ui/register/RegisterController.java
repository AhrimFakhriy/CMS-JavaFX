package main.ui.register;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import main.data.CustomerRepository;
import main.model.interfaces.CancelRegistration;
import main.model.person.Address;
import main.model.person.Customer;
import main.model.person.Person;
import main.model.ui.SubMenu;
import main.ui.confirm.ConfirmBookingController;
import main.ui.main.MainController;
import main.utils.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private BorderPane mainPane;
    @FXML private HBox emptyHBox, fillerHBox;
    @FXML private Pane emptyPane;
    @FXML private Label labelHeader, labelNational;
    @FXML private GridPane addressPane;
    @FXML private JFXTextField idTextField, nameTextField, phoneTextField, streetOneTextField,
            streetTwoTextField, postalCodeTextField, nationalTextField;

    @FXML private JFXComboBox<String> districtBox, stateBox;
    @FXML private JFXRadioButton radioCitizen, radioNonCitizen;

    private MainController mainViewController;

    private ToggleGroup citizenToggle;
    private CancelRegistration cancelRegistrationHandler;
    private JFXDialog dialog;

    private CustomerRepository customerRepository;

    private StringProperty idProperty;
    private StringProperty nameProperty;
    private StringProperty phoneProperty;
    private StringProperty streetOneProperty;
    private StringProperty streetTwoProperty;
    private StringProperty postalCodeProperty;
    private StringProperty nationalProperty;
    private ReadOnlyObjectProperty<String> districtProperty;
    private ReadOnlyObjectProperty<String> stateProperty;

    private ArrayList<JFXTextField> textFields;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(rootPane, 0.0);
        AnchorPane.setRightAnchor(rootPane, 0.0);
        AnchorPane.setLeftAnchor(rootPane, 0.0);
        AnchorPane.setBottomAnchor(rootPane, 0.0);

        HBox.setHgrow(emptyHBox, Priority.ALWAYS);
        HBox.setHgrow(fillerHBox, Priority.ALWAYS);
        HBox.setHgrow(emptyPane, Priority.ALWAYS);
        HBox.setHgrow(labelHeader, Priority.ALWAYS);

        customerRepository = CustomerRepository.getInstance();
        textFields = new ArrayList<>();

        textFields.addAll(
            Arrays.asList(
                    idTextField,
                    nameTextField,
                    phoneTextField,
                    streetOneTextField,
                    streetTwoTextField,
                    postalCodeTextField,
                    nationalTextField
            )
        );

        bindProperties();
        handleCitizenToggle();

        setUpListeners();
        setUpValidators();
    }

    private void setUpValidators() {
        phoneProperty.addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("^[0-9]*$"))
                phoneProperty.setValue(oldValue);
        });

        postalCodeProperty.addListener(((observable, oldValue, newValue) -> {
            if(!newValue.matches("^[0-9]*$"))
                phoneProperty.setValue(oldValue);
        }));

        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Field cannot be empty!");

        idTextField.getValidators().add(requiredFieldValidator);
        nameTextField.getValidators().add(requiredFieldValidator);
        phoneTextField.getValidators().add(requiredFieldValidator);
        nationalTextField.getValidators().add(requiredFieldValidator);

        streetOneTextField.getValidators().add(requiredFieldValidator);
        postalCodeTextField.getValidators().add(requiredFieldValidator);
    }

    @FXML
    private void onConfirmRegister(ActionEvent e) {
        if(idTextField.validate() && nameTextField.validate() && phoneTextField.validate()) {
            boolean valid = false;

            String name = nameProperty.getValue().trim(),
                    phone = phoneProperty.getValue().trim(),
                    id = idProperty.getValue().trim(),
                    nationality = nationalProperty.getValue().trim(),
                    street1 = streetOneProperty.getValue().trim(),
                    street2 = streetTwoProperty.getValue().trim(),
                    postcode = postalCodeProperty.getValue().trim(),
                    district = districtProperty.getValue(),
                    state = stateProperty.getValue().trim();

            if(street2.isEmpty())
                street2 = "null";

            if(district == null)
                district = "null";
            else {
                district = district.trim();

                if(district.isEmpty())
                    district = "null";
            }

            if(radioCitizen.isSelected()) {
                if (streetOneTextField.validate() && postalCodeTextField.validate())
                    valid = true;
            } else {
                if (nationalTextField.validate())
                    valid = true;
            }

            if(valid) {
                Customer cust = new Customer(name, phone, id, Utils.MALAYSIA);

                if(radioCitizen.isSelected()) {
                    cust.setAddress(new Address(street1, street2, postcode, district, state));
                } else {
                    cust.setNationality(nationality);
                }

                SubMenu confirmBookingMenu = new SubMenu(getClass().getResource("/res/ui/confirm/confirm_booking.fxml"));

                rootPane.getChildren().clear();
                rootPane.getChildren().add(confirmBookingMenu.getRoot());

                ((ConfirmBookingController) confirmBookingMenu.getController()).setMainViewController(mainViewController);
                ((ConfirmBookingController) confirmBookingMenu.getController()).setDialog(dialog);
                ((ConfirmBookingController) confirmBookingMenu.getController()).setCustomer(cust);
                ((ConfirmBookingController) confirmBookingMenu.getController()).onCancellation(() -> {
                    rootPane.getChildren().clear();
                    rootPane.getChildren().add(mainPane);
                });

            }

        }
    }

    private void setUpListeners() {
        idProperty.addListener(((observable, oldValue, newValue) -> {
            boolean found = false;

            for(Customer c : customerRepository.getCustomers()) {
                if(c.getID().equalsIgnoreCase(newValue)) {
                    found = true;
                    nameProperty.setValue(c.getName());
                    phoneProperty.setValue(c.getPhoneNum());

                    nameTextField.setEditable(false);
                    phoneTextField.setEditable(false);

                    if(c.isCitizen()) {
                        citizenToggle.selectToggle(radioCitizen);
                        radioNonCitizen.setDisable(true);

                        streetOneProperty.setValue(c.getAddress().getStreet1());
                        streetTwoProperty.setValue(c.getAddress().getStreet2());
                        postalCodeProperty.setValue(c.getAddress().getPostCode());
                        stateBox.getSelectionModel().select(c.getAddress().getState());
                        districtBox.getSelectionModel().select(c.getAddress().getDistrict());

                        streetOneTextField.setEditable(false);
                        streetTwoTextField.setEditable(false);
                        postalCodeTextField.setEditable(false);
                        stateBox.setDisable(true);
                        districtBox.setDisable(true);

                    } else {
                        citizenToggle.selectToggle(radioNonCitizen);
                        radioCitizen.setDisable(true);

                        nationalProperty.setValue(c.getNationality());
                        nationalTextField.setEditable(false);

                    }

                    for(JFXTextField t : textFields) {
                        t.validate();
                    }

                    break;
                }
            }

            if(!found) {
                nameProperty.setValue("");
                nameTextField.setEditable(true);

                phoneProperty.setValue("");
                phoneTextField.setEditable(true);

                radioCitizen.setDisable(false);
                radioNonCitizen.setDisable(false);

                streetOneProperty.setValue("");
                streetOneTextField.setEditable(true);

                streetTwoProperty.setValue("");
                streetTwoTextField.setEditable(true);

                postalCodeProperty.setValue("");
                postalCodeTextField.setEditable(true);

                stateBox.setDisable(false);
                districtBox.setDisable(false);

                nationalProperty.setValue("");
                nationalTextField.setEditable(true);
            }

        }));

        stateBox.getItems().addAll(Utils.STATES);

        stateBox.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            if(oldValue.equals(newValue)) return;

            districtBox.getItems().clear();
            districtBox.setDisable(false);

            if(newValue.intValue() >= 0 && newValue.intValue() <= 7) {
                districtBox.getItems().addAll(Utils.DISTRICTS[newValue.intValue()]);
            } else if (newValue.intValue() == 8) {
                districtBox.setDisable(true);
            } else {
                districtBox.getItems().addAll(Utils.DISTRICTS[newValue.intValue()-1]);
            }

            districtBox.getSelectionModel().selectFirst();
        }));

        stateBox.getSelectionModel().selectFirst();
    }

    private void bindProperties() {
        idProperty = idTextField.textProperty();
        nameProperty = nameTextField.textProperty();
        phoneProperty = phoneTextField.textProperty();
        streetOneProperty = streetOneTextField.textProperty();
        streetTwoProperty = streetTwoTextField.textProperty();
        postalCodeProperty = postalCodeTextField.textProperty();
        nationalProperty = nationalTextField.textProperty();
        districtProperty = districtBox.getSelectionModel().selectedItemProperty();
        stateProperty = stateBox.getSelectionModel().selectedItemProperty();
    }

    private void handleCitizenToggle() {
        citizenToggle = new ToggleGroup();
        citizenToggle.getToggles().addAll(radioCitizen, radioNonCitizen);

        citizenToggle.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue.equals(radioCitizen)) {
                addressPane.setVisible(true);

                labelNational.setVisible(false);
                nationalTextField.setVisible(false);

            } else {
                addressPane.setVisible(false);

                labelNational.setVisible(true);
                nationalTextField.setVisible(true);
            }
        }));

        radioNonCitizen.setSelected(true);
    }

    @FXML
    private void onCancelRegistration(ActionEvent event) {
        cancelRegistrationHandler.cancel();
    }

    public void onCancellation(CancelRegistration cancelRegistrationHandler) {
        this.cancelRegistrationHandler = cancelRegistrationHandler;
    }

    public void setDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }

    public void setMainViewController(MainController mainViewController) {
        this.mainViewController = mainViewController;
    }
}