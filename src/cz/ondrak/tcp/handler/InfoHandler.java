package cz.ondrak.tcp.handler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles client "INFO" messages
 */
public class InfoHandler extends AbstractHandler {

    @Override
    public String handleMessage(InputStream is) {
        StringBuilder sb = new StringBuilder();
        int previous = 0;
        int current = 0;
        try {
            while (current != -1) {
                previous = current;
                current = is.read();
                if (previous == '\r' && current == '\n') {
                    break;
                }
                sb.append((char) current);
            }
        } catch (IOException e) {
            System.err.println("Cannot read from the stream.");
        }
        System.out.println("Received info message: " + sb.toString().trim());
        return OK;
    }
}