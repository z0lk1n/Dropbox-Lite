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
    private String login;


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
                                int id = server.getAuthService().getIdByLoginAndPass(data[1], data[2]);
                                if (id > 0) {
                                    if (!server.isNickBusy(newNick)) {
                                        nick = newNick;
                                        server.addClient(this);
                                        break;
                                    } else {
                                        sendMsg("Учетная запись занята");
                                    }
                                }
                            }
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith(Const.SYSTEM_SYMBOL)) {
                            if (msg.startsWith(Const.DELETE_FILE))  {
                                String[] data = msg.split("\\s", 2);
                                fileService.deleteFile(this, data[1]);
                            }
                            if (msg.startsWith(Const.DOWNLOAD_FILE))    {

                            }
                            if (msg.startsWith(Const.UPLOAD_FILE))  {

                            }
                            if (msg.startsWith(Const.CHANGE_FILE))  {

                            }
                            if(msg.equals(Const.FILES_LIST))    {

                            }
                            if (msg.equals(Const.CLOSE_CONNECTION)) break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }catch(IOException e)   {
            e.printStackTrace();
        }
    }

    public String getLogin()    {
        return login;
    }
}
