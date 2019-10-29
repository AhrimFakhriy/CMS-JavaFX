package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.data.EmployeeRepository;
import main.data.RentableRepository;
import main.data.Settings;
import main.ui.login.LoginView;

public class CMSApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Settings.getInstance().initializeFiles();
        Settings.getInstance().loadSettings();

        EmployeeRepository.getInstance().loadEmployees();

        RentableRepository.getInstance().loadHalls();
        RentableRepository.getInstance().loadRooms();

        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
