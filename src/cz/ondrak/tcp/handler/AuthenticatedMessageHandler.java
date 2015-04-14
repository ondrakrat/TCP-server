package cz.ondrak.tcp.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Handles messages after the client is authenticated.
 */
public class AuthenticatedMessageHandler extends AbstractHandler {

    /**
     * Map of available message types with character array for comparison by bytes
     */
    private static List<String> messages;

    static {
        messages = new ArrayList<>();
        messages.add("INFO");
        messages.add("FOTO");
    }

    public AuthenticatedMessageHandler() {
        setNext(this);
    }

    @Override
    public String handleMessage(InputStream is) {
        StringBuilder sb = new StringBuilder();
        List<String> matchingMessages = new ArrayList<>();
        matchingMessages.addAll(messages);
        int previous = 0;
        int current = 0;
        int characterCount = 0;
        try {
            while (current != -1) {
                previous = current;
                current = is.read();
                for (Iterator<String> iterator = matchingMessages.iterator();
                     iterator.hasNext(); ) {
                    String next = iterator.next();
                    if (characterCount < next.length() && next.charAt(characterCount) != current) {
                        iterator.remove();
                    }
                }
                if (matchingMessages.size() == 0) {
                    System.err.println("Invalid message type.");
                    return SYNTAX_ERROR;
                }
                ++characterCount;
                if (previous == '\r' && current == '\n') {
                    // line terminator is not expected here
                    return SYNTAX_ERROR;
                }
                if (current == ' ') {
                    break;
                }
                sb.append((char) current);
            }
        } catch (IOException e) {
            System.err.println("Cannot read from the stream.");
        }

        // choose correct handler for the message
        switch (sb.toString()) {
            case "INFO":
                return new InfoHandler().handleMessage(is);
            case "FOTO":
                return new FotoHandler().handleMessage(is);
            default:
                System.err.println("Invalid message type: " + sb.toString());
                return SYNTAX_ERROR;
        }
    }
}