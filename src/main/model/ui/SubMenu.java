package main.model.ui;

import javafx.fxml.FXMLLoader;

import java.net.URL;

public class SubMenu {
    private FXMLLoader loader;

    public SubMenu(URL url) {
        try {
            loader = new FXMLLoader(url);
            loader.load();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void setController(T controller) { loader.setController(controller); }
    public <T> T getController() {
        return loader.getController();
    }
    public <T> T getRoot() { return loader.getRoot(); }
}
