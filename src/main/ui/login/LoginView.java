package main.ui.login;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.data.Settings;
import main.ui.login.LoginController;

import java.io.IOException;

public class LoginView extends Application {
    private LoginController controller;
    private Stage stage;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/login/login_view.fxml"));
        Parent root = loader.load();

        controller = loader.getController();

        Scene scene = new Scene(root, 800, 533);
        scene.getStylesheets().addAll(getClass().getResource("/res/login/style.css").toExternalForm());

        stage = primaryStage;
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Log In");
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        loadImages();

        Settings.getInstance();
    }

    private void loadImages() {
        Image userImg = new Image("/res/images/user.png", 32, 32, true, true);
        Image lockImg = new Image("/res/images/lock.png", 32, 32, true, true);
        controller.getUserImg().setImage(userImg);
        controller.getLockImg().setImage(lockImg);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
