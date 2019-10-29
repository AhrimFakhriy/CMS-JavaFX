package main.ui.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import main.data.EmployeeRepository;
import main.data.Settings;
import main.model.Credential;
import main.model.person.Admin;
import main.model.person.Employee;
import main.model.person.Person;
import main.ui.main.MainView;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private Label labelChaletName;
    @FXML private Label labelError;

    @FXML private JFXCheckBox rememberButton;
    @FXML private JFXTextField fieldName;
    @FXML private JFXPasswordField fieldPassword;

    @FXML private ImageView userImg;
    @FXML private ImageView lockImg;

    @FXML private JFXButton closeButton;
    @FXML private JFXButton logInButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image userSymbol = new Image("/res/images/user.png", 32, 32, true, true);
        Image lockSymbol = new Image("/res/images/lock.png", 32, 32, true, true);

        userImg.setImage(userSymbol);
        lockImg.setImage(lockSymbol);

        loadSettings();
    }

    private void loadSettings() {
        labelChaletName.setText(Settings.getInstance().getChaletName());
        Credential credential = Settings.getInstance().loadCredentials();

        if(credential.isNotEmpty()) {
            fieldName.setText(credential.getID());
            fieldPassword.setText(credential.getPassword());
            rememberButton.setSelected(true);
        }
    }

    @FXML protected void onAttemptLogIn(ActionEvent event) throws Exception {
       if(event.getSource().equals(logInButton)) {
           labelError.setVisible(false);

           SimpleObjectProperty<Person> loggedUser = null;

           Admin admin = new Admin();
           Settings settings = Settings.getInstance();

           String id = fieldName.getText();
           String password = fieldPassword.getText();
           password = EmployeeRepository.getInstance().encrypt(password);

           if(admin.logIn(id, password)) {
               loggedUser = new SimpleObjectProperty<>(admin);

           }else {
               for(Employee emp : EmployeeRepository.getInstance().getEmployees()) {
                    if(emp.logIn(id, password)) {
                        loggedUser = new SimpleObjectProperty<>(emp);
                        break;
                    }
               }
           }

           if(loggedUser != null) {
               settings.setCurrentUser(loggedUser);
               MainView main = new MainView();

               main.start(new Stage());
               logInButton.getScene().getWindow().hide();

           }else {
               labelError.setVisible(true);
           }

       }
    }

    @FXML protected void onActionClose(ActionEvent event) {
        if(event.getSource().equals(closeButton)) {
            Platform.exit();
        }
    }
}
