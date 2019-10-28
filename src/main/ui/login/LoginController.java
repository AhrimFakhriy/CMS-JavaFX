package main.ui.login;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class LoginController {

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

    public JFXButton getLogInButton() {
        return logInButton;
    }

    public ImageView getUserImg() {
        return userImg;
    }

    public ImageView getLockImg() {
        return lockImg;
    }
}
