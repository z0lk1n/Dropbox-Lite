import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    public Button loginBtn;
    @FXML
    private TextField regLoginField;
    @FXML
    private PasswordField regPassField;
    @FXML
    private PasswordField regRePassField;
    @FXML
    private Button regLoginBtn;

    private Socket socket;
    private ClientCore core;
    private ConnectProxy connectProxy;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(Const.SERVER_ADDRESS, Const.SERVER_PORT);
            core = new ClientCore(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectProxy = new ConnectProxy(core, socket);
        connectProxy.setAuthorized(false, null);
    }

    public void sendAuthMsg() {
        if (loginField.getText().isEmpty() || passField.getText().isEmpty()) {
            core.showAlert(Const.INCOMPLETE_AUTH);
            return;
        }
        connectProxy.connect();
        core.setStageLogin((Stage) loginBtn.getScene().getWindow());
        connectProxy.login(loginField.getText(), passField.getText());
        clearLoginFields();
    }

    public void sendRegMsg() {
        if (regLoginField.getText().isEmpty() || regPassField.getText().isEmpty() || regPassField.getText().isEmpty()) {
            core.showAlert(Const.INCOMPLETE_AUTH);
            return;
        }
        if (!regPassField.getText().equals(regRePassField.getText())) {
            core.showAlert(Const.NOT_MATCH_PASS);
            return;
        }
        connectProxy.connect();
        core.setStageLogin((Stage) regLoginBtn.getScene().getWindow());
        connectProxy.registration(regLoginField.getText(), regPassField.getText());
        clearRegFields();
    }

    public void clearLoginFields() {
        loginField.clear();
        passField.clear();
    }

    public void clearRegFields() {
        regLoginField.clear();
        regPassField.clear();
        regRePassField.clear();
    }
}