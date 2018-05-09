import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Const {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private BaseFileService fileService;
    private String username;
    private String password;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith(Const.AUTH)) {
                            String[] data = msg.split("\\s");
                            if (data.length == 3) {
                                String newUsername = data[1];
                                String password = BaseFileService.getHash(data[2]);
                                if (server.getAuthService().authentication(newUsername, password)) {
                                    username = newUsername;
                                    sendMsg(Const.AUTH_SUCCESSFUl + username);
                                    server.addClient(this);
                                    break;
                                }
                            }
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith(Const.SYSTEM_SYMBOL)) {
                            if (msg.startsWith(Const.DELETE_FILE)) {
                                String[] data = msg.split("\\s", 2);
                                fileService.deleteFile(username, data[1]);
                            }
                            if (msg.startsWith(Const.DOWNLOAD_FILE)) {
                                String[] data = msg.split("\\s", 2);
                                fileService.downloadFile(username, data[1]);
                            }
                            if (msg.startsWith(Const.UPLOAD_FILE)) {
                                String[] data = msg.split("\\s", 2);
                                fileService.uploadFile(username, data[1]);
                            }
                            if (msg.startsWith(Const.CHANGE_FILE)) {
                                String[] data = msg.split("\\s", 2);
                                fileService.changeFile(username, data[1]);
                            }
                            if (msg.equals(Const.FILES_LIST)) {
                                fileService.filesList(username);
                            }
                            if (msg.equals(Const.CLOSE_CONNECTION)) break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    username = null;
                    server.removeClient(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
