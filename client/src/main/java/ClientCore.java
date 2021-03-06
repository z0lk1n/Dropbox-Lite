import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class ClientCore implements ConnectInterface {
    private List<String> localFiles = new ArrayList<>();
    private String localDir = "/home/vitaly/tmpFiles/";
    private Socket socket;
    private InputStream inputStream;
    private Stage stageLogin;
    private Stage stageMain;
    private ObjectInputStream in;
    private SendMessages sendMsg;

    ClientCore(Socket socket) {
        this.socket = socket;
        try {
            this.inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            this.sendMsg = new SendMessages(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAuthorized(boolean authorized, String client) {
        if (authorized) {
            openMainForm(client);
        }
    }

    @Override
    public void connect() {
        try {
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        in = new ObjectInputStream(inputStream);
                        AuthMessage msg = (AuthMessage) in.readObject();

                        if (msg.getCommand().equals(Commands.AUTH_SUCCESSFUl) ||
                                msg.getCommand().equals(Commands.REG_SUCCESSFUl)) {
                            setAuthorized(true, msg.getClient());
                            break;
                        }

                        if (msg.getCommand().equals(Commands.REG_BAD)) {
                            showAlert(Const.ACC_BUSY);
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
                            try {
                                Path path = Paths.get(localDir);
                                if (!Files.exists(path)) {
                                    Files.createDirectories(path);
                                }
                                Files.write(Paths.get(path + "/" + msg.getFileName()), msg.getFileData());
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
//                        openLoginForm();
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

    void getFile(String file) {
        if (!localFiles.contains(file)) return;
        sendMsg.send(new FileMessage(Commands.DOWNLOAD_FILE, file));
    }

    void addFile(String file, byte[] fileData) {
        if (localFiles.contains(file)) return;
        localFiles.add(file);
        sendMsg.send(new FileMessage(Commands.UPLOAD_FILE, file, fileData));
    }

    void removeFile(String file) {
        if (!localFiles.contains(file)) return;
        localFiles.remove(file);
        sendMsg.send(new FileMessage(Commands.DELETE_FILE, file));
    }

    void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Const.OOPS);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    @Override
    public void login(String login, String password) {
        sendMsg.send(new AuthMessage(Commands.AUTH, login, password));
    }

    @Override
    public void registration(String login, String password) {
        sendMsg.send(new AuthMessage(Commands.REG, login, password));
    }

    void setStageLogin(Stage stageLogin) {
        this.stageLogin = stageLogin;
    }

    private void openMainForm(String client) {
        Platform.runLater(() -> {
            try {
                stageLogin.close();
                stageLogin = null;
                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                stageMain = new Stage();
                stageMain.initModality(Modality.APPLICATION_MODAL);
                stageMain.setTitle(Const.TITLE_FORM + " - [" + client + "]");
                stageMain.setScene(new Scene(root, stageMain.getWidth(), stageMain.getHeight()));
                stageMain.show();
                stageMain.setResizable(false);
                ControllerMain.setCore(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    List<String> getLocalFiles() {
        return localFiles;
    }

//    private void openLoginForm() {
//        Platform.runLater(() -> {
//            try {
//                stageMain.close();
//                stageMain = null;
//                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
//                Stage stage = new Stage();
//                stage.initModality(Modality.APPLICATION_MODAL);
//                stage.setTitle(Const.TITLE_FORM);
//                stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
//                stage.show();
//                stage.setResizable(false);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
}
