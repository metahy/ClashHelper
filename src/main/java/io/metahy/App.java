package io.metahy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.ini4j.Wini;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        final String userHome = System.getProperty("user.home");
        final String userHomeCfgDir = userHome + File.separator + ".config";
        final String targetDir = userHomeCfgDir + File.separator + "clash_helper";
        final String targetFile = targetDir + File.separator + "config.ini";
        final String defaultClashDir = userHomeCfgDir + File.separator + "clash";

        System.out.println("");
        System.out.println("===== System =====");
        System.out.println(" => OS: " + System.getProperty("os.name"));
        System.out.println(" => Uh: " + userHome);
        System.out.println("==================");
        System.out.println("");

        final File td = FileUtils.getFile(targetDir);
        if (td.exists() && td.isFile()) {
            System.err.println("ERROR-0: Target config directory is file, please remove " + targetDir);
            return;
        }

        if (!td.exists()) {
            System.out.println("INFO: Create config directory:[" + targetDir + "], result:[" + td.mkdirs() + "]");
        }

        final File tf = FileUtils.getFile(targetFile);
        if (!tf.exists()) {
            System.out.println("INFO: Create config file:[" + targetFile + "], result:[" + tf.createNewFile() + "]");
        }

        Wini ini = new Wini(new FileInputStream(tf));
        String currCd = ini.get("Clash", "config_dir", String.class);

        System.out.println("INFO: Current Clash config directory:[" + currCd + "]");

        if (Objects.isNull(currCd) || currCd.length() == 0) {
            System.out.println("INFO: Use default Clash config directory:[" + defaultClashDir + "]");
            currCd = defaultClashDir;
        }

        ini.put("Clash", "config_dir", defaultClashDir);
        ini.store(new FileOutputStream(tf));

        MainController.DIR = currCd;

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/main.fxml")));
        stage.setTitle("Clash Helper");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/logo.png")).openStream()));
        stage.setScene(new Scene(root, 300, 320));
        stage.show();
    }
}
