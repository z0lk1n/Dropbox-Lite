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
            socket = new Socket(Const.SERVER_ADDR, Const.SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String s = in.readUTF();
                        if (s.startsWith("/authok ")) {
                            setAuthorized(true);
                            break;
                        }
                    }

                } catch (Exception e) {
                    showAlert("Сервер перестал отвечать, повторите попытку соединения.");
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
            showAlert("Не удалось подключиться к серверу. Проверьте сетевое соединение.");
        }
    }

    public void sendAuthMsg() {
        if (loginField.getText().isEmpty() || passField.getText().isEmpty()) {
            showAlert("Указаны неполные данные авторизации");
            return;
        }
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Возникли проблемы");
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
        stage.setTitle("Dropbox-Lite");
        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        stage.show();
        stage.setResizable(false);
    }
}