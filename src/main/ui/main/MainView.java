package main.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.data.Settings;
import main.model.person.Admin;
import main.model.person.Employee;
import main.model.person.Person;
import main.ui.login.LoginView;

import java.io.InputStream;

public class MainView extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if(Settings.getInstance().getCurrentUser() == null) {
            LoginView loginView = new LoginView();
            loginView.start(primaryStage);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/ui/main/main_view.fxml"));
        BorderPane root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(getClass().getResource("/res/ui/main/style.css").toExternalForm());

        primaryStage.setResizable(true);
        primaryStage.setTitle("Chalet Management System");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();

        root.requestFocus();

        primaryStage.show();
        primaryStage.setMaximized(true);
    }
}
