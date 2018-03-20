package server;

import auth_reg.BaseAuthService;
import common.Const;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server implements Const {
    private Vector<ClientHandler> clients;
    private BaseAuthService authService;

    public Server() {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            clients = new Vector<>();
            authService = new BaseAuthService();
            authService.connect();
            System.out.println("Server started... Waiting clients...");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected" + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort());
                new ClientHandler(this, socket);
            }
        }catch(IOException e)   {
            e.printStackTrace();
        }catch(SQLException | ClassNotFoundException e)   {
            System.out.println("Не удалось запустить сервис авторизации");
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
