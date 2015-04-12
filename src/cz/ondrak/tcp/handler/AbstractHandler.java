package cz.ondrak.tcp.handler;

import java.io.InputStream;

/**
 * An abstract implementation of the {@link MessageHandler} interface.
 */
public abstract class AbstractHandler implements MessageHandler {

    public static final String LOGIN = "200 LOGIN\r\n";
    public static final String PASSWORD = "201 PASSWORD\r\n";
    public static final String OK = "202 OK\r\n";
    public static final String BAD_CHECKSUM = "300 BAD CHECKSUM\r\n";
    public static final String LOGIN_FAILED = "500 LOGIN FAILED\r\n";
    public static final String SYNTAX_ERROR = "501 SYNTAX ERROR\r\n";
    public static final String TIMEOUT = "502 TIMEOUT\r\n";
    /**
     * Next expected message.
     */
    private MessageHandler next;

    @Override
    abstract public String handleMessage(InputStream is);

    @Override
    public MessageHandler getNext() {
        return next;
    }

    public void setNext(MessageHandler next) {
        this.next = next;
    }
}