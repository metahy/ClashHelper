package io.metahy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Hyleon
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ConfigUtil.prepare();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/main.fxml")));
        stage.setTitle("Clash Helper");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/logo.png")).openStream()));
        stage.setScene(new Scene(root, 365, 365));
        stage.show();
    }
}
