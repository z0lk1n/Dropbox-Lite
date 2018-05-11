import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ControllerMain {
    private static ObservableList<String> filesList;
    private static ClientCore core;

    @FXML
    private ListView<String> filesListView;
    @FXML
    private Button uploadBtn;
    @FXML
    private Button downloadBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private void initialize() {
        initData();
        refreshFilesList();
    }

    private void initData() {
        filesList = FXCollections.observableArrayList();
        filesListView.setItems(filesList);

        filesListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(item);
                        } else {
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    public void download() {
        Platform.runLater(() -> {
            core.getFile(focusFile());
        });
        refreshFilesList();
    }

    public void upload() {
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = (Stage) uploadBtn.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
//            stage.display(selectedFile);
            }

            byte[] fileData = null;
            try {
                fileData = Files.readAllBytes(Paths.get(selectedFile.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            core.addFile(selectedFile.getName(), fileData);
        });
        refreshFilesList();
    }

    public void delete() {
        Platform.runLater(() -> {
            core.removeFile(focusFile());
        });
        refreshFilesList();
    }

    public String focusFile() {
        return filesListView.getFocusModel().getFocusedItem();
    }

    public static void setCore(ClientCore core) {
        ControllerMain.core = core;
    }

    public void refreshFilesList() {
        Platform.runLater(() -> {
        filesList.clear();
        filesList.addAll(core.getLocalFiles());
        });
    }
}
