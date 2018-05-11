import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientCore {
    private List<String> localFiles = new ArrayList<>();
    private String localDir;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean authorized;
    private Stage stageLogin;
    private String login;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientCore(Socket socket) {
        this.socket = socket;
        this.localDir = "/home/vitaly/tmpFiles/";
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if (authorized) {
            openMainForm();
        }
    }

    public void connect() {
        try {
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        in = new ObjectInputStream(inputStream);
                        AuthMessage msg = (AuthMessage) in.readObject();
                        if (msg.getCommand().equals(Commands.AUTH_SUCCESSFUl)) {
                            setAuthorized(true);
                            break;
                        }
                    }
                    while (true) {
                        in = new ObjectInputStream(inputStream);
                        FileMessage msg = (FileMessage) in.readObject();
                        if (msg.getCommand().equals(Commands.FILES_LIST)) {
                            localFiles.clear();
                            localFiles = msg.getFileList();
                        }
                        if (msg.getCommand().equals(Commands.DOWNLOAD_FILE)) {
                            String fileName = msg.getFileName();
                            byte[] fileData = msg.getFileData();
                            try {
                                Path path = Paths.get(localDir);
                                if (!Files.exists(path))
                                    Files.createDirectories(path);

                                Files.write(Paths.get(path + "/" + fileName), fileData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    showAlert(Const.LOST_SERVER);
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();

        } catch (Exception e) {
            showAlert(Const.FAIL_CONNECT_SERVER);
        }
    }

    public void getFile(String file) {
        if (!localFiles.contains(file)) return;
        sendMsg(new FileMessage(Commands.DOWNLOAD_FILE, login, file));

    }

    public void addFile(String file, byte[] fileData) {
        if (localFiles.contains(file)) return;
        localFiles.add(file);
        sendMsg(new FileMessage(Commands.UPLOAD_FILE, login, file, fileData));
    }

    public void removeFile(String file) {
        if (!localFiles.contains(file)) return;
        localFiles.remove(file);
        sendMsg(new FileMessage(Commands.DELETE_FILE, login, file));
    }

    public void getFilesList() {
        sendMsg(new FileMessage(Commands.FILES_LIST, login));
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

    public void login(String login, String password) {
        this.login = login;
        sendMsg(new AuthMessage(Commands.AUTH, login, password));
    }

    public void setStageLogin(Stage stageLogin) {
        this.stageLogin = stageLogin;
    }

    public void openMainForm() {
        Platform.runLater(() -> {
            try {
                stageLogin.close();
                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle(Const.TITLE_FORM);
                stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
                stage.show();
                stage.setResizable(false);
                ControllerMain.setCore(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getLocalFiles() {
        return localFiles;
    }

    public synchronized void sendMsg(Object msg) {
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e.printStackTrace();
            }
        }
    }
}
