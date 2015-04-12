package cz.ondrak.tcp.robot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import cz.ondrak.tcp.handler.ClientHandler;
import cz.ondrak.tcp.utilities.ThreadExecutor;

/**
 * An instance of the server, that listenes on provided port. Can accept multiple clients and communicate with them
 * at the same time.
 */
public class Server {

    private int port;

    public Server(int port) {
        System.out.println("Starting server at port " + port + "...");
        this.port = port;
        startListening();
    }

    /**
     * Listens on given port, creates a new {@link cz.ondrak.tcp.handler.ClientHandler} thread for each client.
     */
    public void startListening() {
        try {
            ServerSocket socket = new ServerSocket(port);
            while (true) {
                // Waits for a client to connect
                Socket clientSocket = socket.accept();
                // Creates and starts a new thread that handles connected client, including
                // {@link ThreadExecutor} for timeout
                Runnable executor = new ThreadExecutor(new ClientHandler(clientSocket), clientSocket);
                new Thread(executor).start();
                System.out.println("Client accepted from " + clientSocket.getInetAddress() + ":" +
                        clientSocket.getPort());
            }
        } catch (IOException e) {
            System.err.println("An error occured while listening on port " + port + ".");
        }
    }
}