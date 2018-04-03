import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

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
                        if (msg.startsWith("/auth ")) {
                            String[] data = msg.split("\\s");
                            if (data.length == 3) {
                                String newNick = server.getAuthService().getNickByLoginAndPass(data[1], data[2]);
                                if (newNick != null) {
                                    if (!server.isNickBusy(newNick)) {
                                        nick = newNick;
                                        sendMsg("/authok " + newNick);
                                        server.subscribe(this);
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
                        System.out.println(nick + ": " + msg); //!!!
                        if (msg.startsWith("/")) {
                            if (msg.startsWith("/w ")) { // "/w nick2 hello nick how are you?"
                                String[] data = msg.split("\\s", 3);
                                server.sendPrivateMsg(this, data[1], data[2]);
                            }
                            if (msg.equals("/end")) break;
                        } else {
                            server.broadcastMsg(nick + ":" + msg);
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
}
