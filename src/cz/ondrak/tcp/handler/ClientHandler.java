package cz.ondrak.tcp.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Handles individual clients, accepts their messages and communicates with them via provided socket.
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private MessageHandler handler;
    private boolean isConnectionClosed = false;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());
            handler = new LoginHandler();
        } catch (IOException e) {
            System.err.println("Can't get I/O stream.");
        }
    }

    @Override
    public void run() {
        write(AbstractHandler.LOGIN);
        while (!isConnectionClosed) {
            handleResponse(handler.handleMessage(bis));
            handler = handler.getNext();
        }
    }

    /**
     * Handles response from the {@link MessageHandler}
     *
     * @param response response to be written to the client
     */
    private void handleResponse(String response) {
        write(response);
        if (response.startsWith("5")) {
            isConnectionClosed = true;
            disconnect();
        }
    }

    /**
     * Method for writing into this client's Output Stream.
     *
     * @param message
     */
    private void write(String message) {
        try {
            System.out.print("Sending message: " + message);
            bos.write(message.getBytes());
            bos.flush();
        } catch (IOException e) {
            System.err.println("Writing to stream failed.");
        }
    }

    /**
     * Closes up the streams and sockets when a client is disconnected
     */
    private void disconnect() {
        if (!socket.isClosed()) {
            try {
                bis.close();
                bos.flush();
                bos.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error while closing streams/socket.");
            }
            System.out.println("Client disconnected.");
        }
    }
}