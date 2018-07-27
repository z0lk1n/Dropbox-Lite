import java.net.Socket;

public class ConnectProxy implements ConnectInterface {
    private ClientCore clientCore;
    private Socket socket;

    ConnectProxy(ClientCore clientCore, Socket socket) {
        this.clientCore = clientCore;
        this.socket = socket;
    }

    @Override
    public void setAuthorized(boolean authorized, String client) {
        clientCore.setAuthorized(authorized, client);
    }

    @Override
    public void connect() {
        if (socket == null || socket.isClosed()) {
            clientCore.connect();
        }
    }

    @Override
    public void login(String login, String password) {
        clientCore.login(login, password);
    }

    @Override
    public void registration(String login, String password) {
        clientCore.registration(login, password);
    }

}
