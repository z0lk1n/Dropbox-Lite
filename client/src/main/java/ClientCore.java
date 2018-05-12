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
    private String localDir = "/home/vitaly/tmpFiles/";
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Stage stageLogin;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    ClientCore(Socket socket) {
        this.socket = socket;
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setAuthorized(boolean authorized, String client) {
        if (authorized) {
            openMainForm(client);
        }
    }

    void connect() {
        try {
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        in = new ObjectInputStream(inputStream);
                        AuthMessage msg = (AuthMessage) in.readObject();
                        if (msg.getCommand().equals(Commands.AUTH_SUCCESSFUl)) {
                            setAuthorized(true, msg.getClient());
                            break;
                        }
                        if (msg.getCommand().equals(Commands.REG_SUCCESSFUl)) {
                            setAuthorized(true, msg.getClient());
                            break;
                        }
                        if (msg.getCommand().equals(Commands.REG_BAD))
                            showAlert(Const.ACC_BUSY);
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

    void getFile(String file) {
        if (!localFiles.contains(file)) return;
        sendMsg(new FileMessage(Commands.DOWNLOAD_FILE, file));
    }

    void addFile(String file, byte[] fileData) {
        if (localFiles.contains(file)) return;
        localFiles.add(file);
        sendMsg(new FileMessage(Commands.UPLOAD_FILE, file, fileData));
    }

    void removeFile(String file) {
        if (!localFiles.contains(file)) return;
        localFiles.remove(file);
        sendMsg(new FileMessage(Commands.DELETE_FILE, file));
    }

    public void getFilesList() {
        sendMsg(new FileMessage(Commands.FILES_LIST));
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

    void login(String login, String password) {
        sendMsg(new AuthMessage(Commands.AUTH, login, password));
    }

    void registration(String login, String password) {
        sendMsg(new AuthMessage(Commands.REG, login, password));
    }

    void setStageLogin(Stage stageLogin) {
        this.stageLogin = stageLogin;
    }

    private void openMainForm(String client) {
        Platform.runLater(() -> {
            try {
                stageLogin.close();
                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle(Const.TITLE_FORM + " - [" + client + "]");
                stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
                stage.show();
                stage.setResizable(false);
                ControllerMain.setCore(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    List<String> getLocalFiles() {
        return localFiles;
    }

    private synchronized void sendMsg(Object msg) {
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
