import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable, Const {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passField;
    @FXML
    private Button loginBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField regLoginField;
    @FXML
    private PasswordField regPassField;
    @FXML
    private PasswordField regRePassField;
    @FXML
    private Button regLoginBtn;
    @FXML
    private Button regCancelBtn;

    private Socket socket;
    private ClientCore core;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(Const.SERVER_ADDRESS, Const.SERVER_PORT);
            core = new ClientCore(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        core.setAuthorized(false);
    }

    public void sendAuthMsg() {
        if (loginField.getText().isEmpty() || passField.getText().isEmpty()) {
            core.showAlert(Const.INCOMPLETE_AUTH);
            return;
        }
        if (socket == null || socket.isClosed()) {
            core.connect();
        }
        core.login(loginField.getText(), passField.getText());
        loginField.clear();
        passField.clear();
    }

    public void openMainForm() {
        try {
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.close();
            Parent root = FXMLLoader.load(getClass().getResource("table.fxml"));
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(Const.TITLE_FORM);
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.show();
            stage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}