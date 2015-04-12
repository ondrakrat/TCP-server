package cz.ondrak.tcp.utilities;

import cz.ondrak.tcp.handler.AbstractHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Times {@link cz.ondrak.tcp.handler.ClientHandler} thread out after certain interval.
 */
public class ThreadExecutor implements Runnable {

    /**
     * Timeout in seconds
     */
    private static final long TIMEOUT = 45;
    private Runnable runnable;
    private Socket socket;
    private OutputStream os;

    public ThreadExecutor(Runnable runnable, Socket socket) {
        this.runnable = runnable;
        this.socket = socket;
        try {
            this.os = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Can't get output stream.");
        }
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.submit(runnable).get(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.err.println("User connection has timed out after " + TIMEOUT + " seconds.");
            try {
                os.write(AbstractHandler.TIMEOUT.getBytes());
                os.flush();
                os.close();
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println("Writing to stream failed.");
            }
        }
    }
}