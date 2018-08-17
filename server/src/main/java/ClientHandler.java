import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

class ClientHandler implements Const {
    private Server server;
    private InputStream inputStream;
    private ObjectInputStream in;
    private BaseFileService fileService;
    private String client;
    private List<String> filesList;
    private SendMessages sendMsg;

    ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            this.fileService = new BaseFileService();
            this.sendMsg = new SendMessages(outputStream);
            this.filesList = new ArrayList<>();

            new Thread(() -> {
                try {
                    while (true) {
                        in = new ObjectInputStream(inputStream);
                        AuthMessage msg = (AuthMessage) in.readObject();
                        String newClient = msg.getClient();
                        String password = getHash(msg.getPassword());

                        if (msg.getCommand().equals(Commands.AUTH)) {
                            if (server.getAuthService().authentication(newClient, password)) {
                                sendFilesList(newClient, Commands.AUTH_SUCCESSFUl);
                                break;
                            }
                        }
                        if (msg.getCommand().equals(Commands.REG)) {
                            if (server.getRegService().registration(newClient, password)) {
                                sendFilesList(newClient, Commands.REG_SUCCESSFUl);
                                break;
                            } else {
                                sendMsg.send(new AuthMessage(Commands.REG_BAD));
                            }
                        }
                    }
                    while (true) {
                        in = new ObjectInputStream(inputStream);
                        FileMessage msg = (FileMessage) in.readObject();
                        Commands command = msg.getCommand();

                        if (command.equals(Commands.DELETE_FILE)) {
                            fileService.deleteFile(msg, client);
                        }
                        if (command.equals(Commands.DOWNLOAD_FILE)) {
                            sendMsg.send(fileService.downloadFile(msg, client));
                        }
                        if (command.equals(Commands.UPLOAD_FILE)) {
                            fileService.uploadFile(msg, client);
                        }
                        if (command.equals(Commands.FILES_LIST)) {
                            getFilesList();
                            sendMsg.send(new FileMessage(Commands.FILES_LIST, filesList));
                        }
                        if (command.equals(Commands.CLOSE_CONNECTION)) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    client = null;
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

    private void getFilesList() {
        try {
            Path path = Paths.get(Const.CORE_PATH + client);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            filesList.clear();
            Files.list(path)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .forEach(s -> filesList.add(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFilesList(String newClient, Commands response) {
        client = newClient;
        sendMsg.send(new AuthMessage(response, client));
        server.addClient(this);
        getFilesList();
        sendMsg.send(new FileMessage(Commands.FILES_LIST, filesList));
    }

    private String getHash(String password) {
        String algorithm = "SHA-256";
        String salt = "salt";

        byte[] passwordArr = password.getBytes();
        byte[] saltArr = salt.getBytes();

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unknown algorithm");
        }

        int len1 = saltArr.length;
        int len2 = passwordArr.length;

        byte[] saltPassword = new byte[len1 + len2];

        System.arraycopy(saltArr, 0, saltPassword, 0, len1);
        System.arraycopy(passwordArr, 0, saltPassword, len1, len2);

        assert messageDigest != null;
        byte[] saltedHash = messageDigest.digest(saltPassword);

        Base64.Encoder base64Encoder = Base64.getEncoder();

        return base64Encoder.encodeToString(saltedHash);
    }
}
