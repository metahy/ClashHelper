package io.metahy;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hyleon
 */
public class MainController {
    //    private static final String DIR = "/home/hyleon/.config/clash";
    public static String DIR = "D:\\configs";

    @FXML
    private TextField url;

    @FXML
    private TextField fileName;

    @FXML
    private Button loadBtn;

    @FXML
    private TextField dir;

    @FXML
    private Button setBtn;

    @FXML
    private ListView<String> configFiles;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button startBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Button delBtn;

    public void initialize() {
        System.setProperty("https.protocols", "TLSv1.2");
        url.setText("https://cdn.jsdelivr.net/gh/changfengoss/pub@main/data/2022_01_17/yIaJTa.yaml");
        dir.setText(DIR);
        startBtn.setDisable(true);
        delBtn.setDisable(true);
        configFiles.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            startBtn.setDisable(false);
            delBtn.setDisable(false);
        });
        refresh(DIR);
    }

    @FXML
    public void onLoad(ActionEvent event) {
        loadBtn.setText("Loading...");
        loadBtn.setDisable(true);
        final String urlStr = this.url.getText();
        final String fileNameStr = this.fileName.getText();
        System.out.println("Preparing download file from url:[" + urlStr + "] to file:[" + fileNameStr + "]");
        Platform.runLater(() -> {
            savePage(urlStr, fileNameStr);
            System.out.println("Download finished.");
            refresh(DIR);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFO");
            alert.setHeaderText("Download success!");
            alert.setContentText("Select the download from list and start!");
            alert.showAndWait();
            loadBtn.setText("Load");
            loadBtn.setDisable(false);
            configFiles.scrollTo(configFiles.getItems().size());
        });
    }

    @FXML
    public void onSet(ActionEvent event) throws IOException {
        DIR = dir.getText();
        System.out.println("Set config dir to " + DIR);
        ConfigUtil.setClashDir(DIR);
        refresh(DIR);
    }

    @FXML
    public void onRefresh(ActionEvent event) {
        System.out.println("Refresh...");
        refresh(DIR);
    }

    @FXML
    public void onStart(ActionEvent event) throws IOException, InterruptedException {
        Process pro = Runtime.getRuntime().exec(new String[]{"killall", "clash"});
        pro.waitFor();

        configFiles.getFocusModel().getFocusedItem();
        String[] startScript = {"/bin/sh", "-c", "clash -f ~/.config/clash/" + configFiles.getFocusModel().getFocusedItem() + " >/dev/null &"};
        pro = Runtime.getRuntime().exec(startScript);
        pro.waitFor();
    }

    @FXML
    public void onStop(ActionEvent event) throws IOException, InterruptedException {
        Process pro = Runtime.getRuntime().exec(new String[]{"killall", "clash"});
        pro.waitFor();
    }

    @FXML
    public void onDel(ActionEvent event) throws IOException, InterruptedException {
        System.out.println("Deleting file [" + configFiles.getFocusModel().getFocusedItem() + "]...");
        Platform.runLater(() -> {
            final File file = new File(DIR + File.separator + configFiles.getFocusModel().getFocusedItem());
            if (file.exists() && file.isFile()) {
                final boolean delete = file.delete();
                System.out.println("Delete " + (delete ? "success" : "failed") + "!");
            }
            refresh(DIR);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFO");
            alert.setHeaderText("Delete success!");
            alert.showAndWait();
            startBtn.setDisable(true);
            delBtn.setDisable(true);
        });
    }

    private void refresh(String path) {
        List<String> data = loadConfigFiles(path);
        configFiles.setItems(FXCollections.observableArrayList(data));
    }

    private List<String> loadConfigFiles(String path) {
        File dir = FileUtils.getFile(path);
        if (dir == null || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .sorted(Comparator.comparingLong(File::lastModified))
                .map(File::getName)
                .filter(f -> f.endsWith("yml") || f.endsWith("yaml"))
                .collect(Collectors.toList());
    }

    public boolean savePage(final String url, final String fileName) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
             BufferedWriter out = new BufferedWriter(new FileWriter(DIR + File.separator + fileName))) {
            String line;
            while ((line = in.readLine()) != null) {
                out.write(line);
                out.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
