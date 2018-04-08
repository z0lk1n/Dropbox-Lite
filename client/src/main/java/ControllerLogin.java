import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    private DataOutputStream out;
    private DataInputStream in;
    private boolean authorized;

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if (authorized) {
            openMainForm();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
    }

    public void connect() {
        try {
            socket = new Socket(Const.SERVER_ADDRESS, Const.SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String s = in.readUTF();
                        if (s.startsWith(AUTH_SUCCESSFUl)) {
                            setAuthorized(true);
                            break;
                        }
                    }

                } catch (Exception e) {
                    showAlert(Const.LOST_SERVER);
                } finally {
                    setAuthorized(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            showAlert(Const.FAIL_CONNECT_SERVER);
        }
    }

    public void sendAuthMsg() {
        if (loginField.getText().isEmpty() || passField.getText().isEmpty()) {
            showAlert(Const.INCOMPLETE_AUTH);
            return;
        }
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF(AUTH + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Const.OOPS);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    public void openMainForm(){
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.close();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("table.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(Const.TITLE_FORM);
        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        stage.show();
        stage.setResizable(false);
    }
}
