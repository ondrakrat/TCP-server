package cz.ondrak.tcp.handler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles client message after the "200 LOGIN" server message
 */
public class LoginHandler extends AbstractHandler {

    @Override
    public String handleMessage(InputStream is) {
        StringBuilder sb = new StringBuilder(5);
        int previous = 0;
        int current = 0;
        int checksum = 0;
        try {
            while (current != -1) {
                previous = current;
                current = is.read();
                if (previous == '\r' && current == '\n') {
                    // \r was added to the checksum, but shouldn't have been
                    checksum -= previous;
                    break;
                }
                checksum += current;
                if (sb.length() < 5) {
                    sb.append((char) current);
                }
            }
        } catch (IOException e) {
            System.err.println("Cannot read from the stream.");
        }
        setNext(new PasswordHandler(checksum, sb.toString().equals("Robot")));
        return PASSWORD;
    }
}