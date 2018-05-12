import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements Const {
    private Vector<ClientHandler> clients;
    private BaseAuthService authService;
    private BaseRegService regService;

    public BaseAuthService getAuthService() {
        return authService;
    }

    public BaseRegService getRegService() {
        return regService;
    }

    public Server() {
        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            clients = new Vector<>();
            authService = new BaseAuthService();
            regService = new BaseRegService();
            authService.connect();
            regService.connect();
            System.out.println(Const.RUN_SERVER);
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println(Const.CLIENT_CONNECT + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort());
                new ClientHandler(this, socket);
            }
        }catch(IOException e)   {
            e.printStackTrace();
        }finally {
            authService.disconnect();
            regService.disconnect();
        }
    }

    public void addClient(ClientHandler ch) {
        clients.add(ch);
    }

    public void removeClient(ClientHandler ch)  {
        clients.remove(ch);
    }
}
