import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientHandler implements Const {
    private Server server;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BaseFileService fileService;
    private String username;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            new Thread(() -> {
                try {
                    ObjectInputStream in = new ObjectInputStream(inputStream);
                    while (true) {
                        AuthMessage msg = (AuthMessage)in.readObject();
                        if (msg.getCommand().equals(Commands.AUTH)) {
                                String newUsername = msg.getClient();
                                String password = fileService.getHash(msg.getPassword());
                                if (server.getAuthService().authentication(newUsername, password)) {
                                    username = newUsername;
                                    sendMsg(new AuthMessage(Commands.AUTH_SUCCESSFUl, username));
                                    server.addClient(this);
                                    break;
                                }
                        }
                    }
                    while (true) {
                        FileMessage msg = (FileMessage) in.readObject();
                        Commands command = msg.getCommand();

                        if (command.equals(Commands.DELETE_FILE)) {
//                            fileService.deleteFile(msg);
                            String client = msg.getClient();
                            String fileName = msg.getFileName();
                            try {
                                Files.delete(Paths.get(Const.CORE_PATH + client + "/" + fileName));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (command.equals(Commands.DOWNLOAD_FILE)) {
//                            fileService.downloadFile(msg);
                            String client = msg.getClient();
                            String fileName = msg.getFileName();
                            byte[] fileData = null;
                            try {
                                fileData = Files.readAllBytes(Paths.get(Const.CORE_PATH + client + "/" + fileName));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sendMsg(new FileMessage(Commands.DOWNLOAD_FILE, client, fileName, fileData));
                        }
                        if (command.equals(Commands.UPLOAD_FILE)) {
//                            fileService.uploadFile(msg);
                            String client = msg.getClient();
                            String fileName = msg.getFileName();
                            byte[] fileData = msg.getFileData();
                            try {
                                Files.createDirectories(Paths.get(Const.CORE_PATH + client));
                                Files.write(Paths.get(Const.CORE_PATH + client + "/" + fileName), fileData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (command.equals(Commands.FILES_LIST)) {
//                            fileService.filesList(msg);

                        }
                        if (command.equals(Commands.CLOSE_CONNECTION)) break;
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

    public synchronized void sendMsg(Message msg) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
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
