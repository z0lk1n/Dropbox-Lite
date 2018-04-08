import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server implements Const {
    private Vector<ClientHandler> clients;
    private BaseAuthService authService;

    public BaseAuthService getAuthService() {
        return authService;
    }

    public Server() {
        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            clients = new Vector<>();
            authService = new BaseAuthService();
            authService.connect();
            System.out.println(Const.RUN_SERVER);
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println(Const.CLIENT_CONNECT + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort());
                new ClientHandler(this, socket);
            }
        }catch(IOException e)   {
            e.printStackTrace();
        }catch(SQLException | ClassNotFoundException e)   {
            System.out.println(Const.FAIL_AUTH_SERVICE);
        }finally {
            authService.disconnect();
        }
    }

    public void addClient(ClientHandler ch) {
        clients.add(ch);
    }

    public void removeClient(ClientHandler ch)  {
        clients.remove(ch);
    }
}
