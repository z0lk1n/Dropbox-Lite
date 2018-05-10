import java.io.*;
import java.net.Socket;

public class ClientHandler implements Const {
    private Server server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private BaseFileService fileService;
    private String username;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith(Const.AUTH)) {
                            String[] data = msg.split("\\s");
                            if (data.length == 3) {
                                String newUsername = data[1];
                                String password = fileService.getHash(data[2]);
                                if (server.getAuthService().authentication(newUsername, password)) {
                                    username = newUsername;
                                    sendMsg(new Message(Const.AUTH_SUCCESSFUl + username));
                                    server.addClient(this);
                                    break;
                                }
                            }
                        }
                    }
                    while (true) {
                        Message msg = (Message) in.readObject();
                        String command = msg.getCommand();

                        if (command.equals(Const.DELETE_FILE)) {
                            fileService.deleteFile(new Message(Const.DELETE_FILE, username, msg.getFileName()));
                        }
                        if (command.equals(Const.DOWNLOAD_FILE)) {
                            fileService.downloadFile(new Message(Const.DOWNLOAD_FILE, username, msg.getFileName()));
                        }
                        if (command.equals(Const.UPLOAD_FILE)) {
                            fileService.uploadFile(new Message(Const.UPLOAD_FILE, username, msg.getFileName(), msg.getFileData()));
                        }
                        if (command.equals(Const.CHANGE_FILE)) {
                            fileService.changeFile(new Message(Const.CHANGE_FILE, username, msg.getFileName()));
                        }
                        if (command.equals(Const.FILES_LIST)) {
                            fileService.filesList(new Message(Const.FILES_LIST, username));
                        }
                        if (command.equals(Const.CLOSE_CONNECTION)) break;
                    }
                } catch (Exception e) {
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

    public void sendMsg(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}
