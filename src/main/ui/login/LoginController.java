package main.ui.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class LoginController {

    @FXML private Label labelChaletName;

    @FXML private JFXTextField fieldName;
    @FXML private JFXPasswordField fieldPassword;

    @FXML private ImageView userImg;
    @FXML private ImageView lockImg;

    @FXML private JFXButton closeButton;
    @FXML private JFXButton logInButton;

    @FXML protected void onAttemptLogIn(ActionEvent event) {
       if(event.getSource().equals(logInButton)) {
           System.out.println("aaa");
       }
    }

    @FXML protected void onActionClose(ActionEvent event) {
        if(event.getSource().equals(closeButton)) {
            System.exit(0);
        }
    }

    JFXButton getLogInButton() {
        return logInButton;
    }

    ImageView getUserImg() {
        return userImg;
    }

    ImageView getLockImg() {
        return lockImg;
    }
}
