import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientCore {
    public List<File> localFiles = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ControllerLogin login;
    private boolean authorized;

    public ClientCore(Socket socket) throws Exception   {
        this.socket = socket;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(Const.FILES_LIST);
        out.flush();
        Object obj = in.readObject();
        localFiles = (List<File>)obj;
    }

    public void connect() {
        try {
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String s = in.readUTF();
                        if (s.startsWith(Const.AUTH_SUCCESSFUl)) {
                            login.setAuthorized(true);
                            break;
                        }
                    }
                    while (true) {
                        String s = in.readUTF();
                        if (s.startsWith("/")) {
                            if (s.startsWith("/fileslist ")) {
//                                String[] data = s.split("\\s");
                                Platform.runLater(() -> {
//                                    filesList.clear();
//                                    for (int i = 1; i < data.length; i++) {
//                                        filesList.addAll(data[i]);
//                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    login.showAlert(Const.LOST_SERVER);
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
            login.showAlert(Const.FAIL_CONNECT_SERVER);
        }
    }

    public void addFile(File file)  {
        if(localFiles.contains(file)) return;
        localFiles.add(file);
        try{
            out.writeObject(file);
            out.flush();
        }catch (Exception e) {
            System.out.println("Can't send file");
        }
    }
    public void removeFile(File file) {
        if(!localFiles.contains(file)) return;
        localFiles.remove(file);
        try{
            out.writeObject(file);
            out.flush();
        }catch(Exception e) {
            System.out.println("Can't remove file from server");
        }
    }

    public void login(String login, String password)    {
        try {
            out.writeUTF(Const.AUTH + login + " " + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
