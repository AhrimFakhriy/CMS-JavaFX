package main.ui.confirm;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmBookingController implements Initializable {

    @FXML private Pane toolbarPane;
    @FXML private HBox toolbarHBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(toolbarHBox, Priority.ALWAYS);
        HBox.setHgrow(toolbarPane, Priority.ALWAYS);
    }
}
