package main.ui.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.data.Settings;
import main.model.Credential;
import main.utils.Utils;

public class LoginView extends Application {
    private Stage stage;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/ui/login/login_view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 533);
        scene.getStylesheets().addAll(getClass().getResource("/res/styles/style.css").toExternalForm());

        stage = primaryStage;
        stage.setScene(scene);
        Utils.setLoginStage(stage);
        root.requestFocus();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
