package cz.ondrak.tcp.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Handles messages after the client is authenticated.
 */
public class AuthenticatedMessageHandler extends AbstractHandler {

    /**
     * Map of available message types with character array for comparison by bytes
     */
    private static Map<String, char[]> messageMap;

    static {
        messageMap = new HashMap<>();
        messageMap.put("INFO", new char[]{'I', 'N', 'F', 'O'});
        messageMap.put("FOTO", new char[]{'F', 'O', 'T', 'O'});
    }

    public AuthenticatedMessageHandler() {
        setNext(this);
    }

    @Override
    public String handleMessage(InputStream is) {
        StringBuilder sb = new StringBuilder();
        Map<String, char[]> matchingMessages = new HashMap<>();
        matchingMessages.putAll(messageMap);
        int previous = 0;
        int current = 0;
        int characterCount = 0;
        try {
            while (current != -1) {
                previous = current;
                current = is.read();
                for (Iterator<Map.Entry<String, char[]>> iterator = matchingMessages.entrySet().iterator();
                     iterator.hasNext(); ) {
                    Map.Entry<String, char[]> entry = iterator.next();
                    if (characterCount < entry.getValue().length && entry.getValue()[characterCount] != current) {
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