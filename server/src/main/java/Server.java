import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class Server implements Const {
    private Vector<ClientHandler> clients;
    private SimpleAuthService authService;
    private SimpleRegService regService;
    private DatabaseConnector databaseConnector;

    SimpleAuthService getAuthService() {
        return authService;
    }

    SimpleRegService getRegService() {
        return regService;
    }

    Server() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            clients = new Vector<>();
            databaseConnector = DatabaseConnector.getInstance();
            databaseConnector.connect();
            authService = new SimpleAuthService(databaseConnector.getConnect());
            regService = new SimpleRegService(databaseConnector.getConnect());
            System.out.println(Const.RUN_SERVER);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(Const.CLIENT_CONNECT + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            databaseConnector.disconnect();
        }
    }

    void addClient(ClientHandler ch) {
        clients.add(ch);
    }

    void removeClient(ClientHandler ch) {
        clients.remove(ch);
    }
}
