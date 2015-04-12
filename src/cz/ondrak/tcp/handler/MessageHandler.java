package cz.ondrak.tcp.handler;

import java.io.InputStream;

/**
 * An interface of the message handlers.
 */
public interface MessageHandler {

    /**
     * Handles the next message from the input stream until the "\r\n" bytes according to current handler.
     * Returns response message.
     *
     * @param is input stream from where the messege is read
     * @return response message
     */
    String handleMessage(InputStream is);

    MessageHandler getNext();
}