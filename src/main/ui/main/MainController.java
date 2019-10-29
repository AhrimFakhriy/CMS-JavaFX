package main.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.data.Settings;
import main.model.person.Admin;
import main.model.person.Employee;
import main.model.person.Person;
import main.ui.login.LoginView;
import main.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label labelName;
    @FXML private Label labelRank;

    @FXML private JFXButton logOutButton;

    @FXML private Pane opaquePane;
    @FXML private HBox drawerBox;
    @FXML private GridPane drawerPane;
    @FXML private Pane fillerPane;
    @FXML private VBox toolbarVBox;

    @FXML private JFXHamburger hamburger;

    private int MENU_STATUS = 2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawerBox.setVisible(false);
        animateDrawerClose();

        loadUserSettings();
        drawerOnClick();

        onActionLogOut();
    }

    private void animateDrawerClose() {
        MENU_STATUS = 1;
        FadeTransition opaquePaneTransition = new FadeTransition(Duration.seconds(0.1), opaquePane);
        opaquePaneTransition.setFromValue(0.65);
        opaquePaneTransition.setToValue(0.0);

        TranslateTransition drawerPaneTransition = new TranslateTransition(Duration.seconds(0.5), drawerPane);
        drawerPaneTransition.setByX(-600);

        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(opaquePaneTransition, drawerPaneTransition);
        sequentialTransition.play();

        sequentialTransition.setOnFinished(event -> {
            drawerBox.setVisible(false);
            MENU_STATUS = 0;
        });
    }

    private void animateDrawerOpen() {
        MENU_STATUS = 1;
        drawerBox.setVisible(true);

        FadeTransition opaquePaneTransition = new FadeTransition(Duration.millis(500), opaquePane);
        opaquePaneTransition.setFromValue(0.0);
        opaquePaneTransition.setToValue(0.65);

        TranslateTransition drawerPaneTransition = new TranslateTransition(Duration.seconds(0.5), drawerPane);
        drawerPaneTransition.setByX(+600);

        SequentialTransition sequentialTransition = new SequentialTransition(drawerPaneTransition, opaquePaneTransition);
        sequentialTransition.play();

        sequentialTransition.setOnFinished(event -> MENU_STATUS = 2);
    }

    private void drawerOnClick() {
        hamburger.setOnMouseClicked(event -> {
            if (MENU_STATUS == 2) {
                animateDrawerClose();
            } else if(MENU_STATUS == 0){
                animateDrawerOpen();
            }
        });

        opaquePane.setOnMouseClicked(event -> {
            if(MENU_STATUS == 2)
                animateDrawerClose();
        });
    }

    private void onActionLogOut() {
        logOutButton.setOnMouseClicked(event -> {
            try {
                Settings.getInstance().setCurrentUser(null);

                Stage stage = new Stage();
                LoginView loginView = new LoginView();

                Utils.setLoginStage(stage);
                loginView.start(stage);

                logOutButton.getScene().getWindow().hide();

            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadUserSettings() {
        Person currentUser = Settings.getInstance().getCurrentUser().get();

        labelName.setText(currentUser.getName());
        if(currentUser instanceof Admin)
            labelRank.setText("Administrator");
        else
            labelRank.setText(((Employee) currentUser).getRank());

        HBox.setHgrow(toolbarVBox, Priority.ALWAYS);
        HBox.setHgrow(fillerPane, Priority.ALWAYS);
    }
}
