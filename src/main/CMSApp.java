package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.data.Settings;
import main.ui.login.LoginView;

public class CMSApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Settings.getInstance().initializeFiles();
        Settings.getInstance().loadSettings();

        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
