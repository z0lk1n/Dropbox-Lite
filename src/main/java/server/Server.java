package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;

    public Server() {
        try(ServerSocket serverSocket = new ServerSocket(8989)) {
            clients = new Vector<>();
            System.out.println("Server started... Waiting clients...");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected" + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort());
                new ClientHandler(this, socket);
            }
        }catch(IOException e)   {
            e.printStackTrace();
        }
    }
}
