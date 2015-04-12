package cz.ondrak.tcp.handler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles client message after the "201 PASSWORD" server message
 */
public class PasswordHandler extends AbstractHandler {

    /**
     * Client message, that was entered after the "200 LOGIN" server message
     */
    private int checksum;

    public PasswordHandler(int checksum, boolean startsOk) {
        if (startsOk) {
            this.checksum = checksum;
        } else {
            this.checksum = -1;
        }
    }

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
                if (Character.isDigit(current)) {
                    sb.append((char) current);
                }
            }
            if (checksum == -1 || Integer.parseInt(sb.toString().trim()) != checksum) {
                System.err.println("Incorrect password or username does not start with 'Robot'");
                return LOGIN_FAILED;
            }
        } catch (IOException e) {
            System.err.println("Cannot read from the stream.");
        } catch (NumberFormatException e) {
            System.err.println("Received password cannot be parsed to Integer.");
            return LOGIN_FAILED;
        }
        setNext(new AuthenticatedMessageHandler());
        System.out.println("Client authentication was successful.");
        return OK;
    }
}