package cz.ondrak.tcp.handler;

import cz.ondrak.tcp.utilities.FileWriter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles client "FOTO" messages
 */
public class FotoHandler extends AbstractHandler {

    @Override
    public String handleMessage(InputStream is) {
        int dataLength = readLength(is);
        if (dataLength == -1) {
            return SYNTAX_ERROR;
        }
        FileWriter fileWriter = new FileWriter();
        int checksum = readData(is, dataLength, fileWriter);
        if (checksum == -1) {
            fileWriter.close();
            fileWriter.removeFile();
            return SYNTAX_ERROR;
        }
        int receivedChecksum = readChecksum(is);
        if (receivedChecksum == -1) {
            fileWriter.close();
            fileWriter.removeFile();
            return SYNTAX_ERROR;
        }
        System.out.println("Calculated checksum: " + checksum);
        System.out.println("Received checksum: " + receivedChecksum);
        if (receivedChecksum == checksum) {
            System.out.println("Photo was received successfully and saved into file " + fileWriter.getFileName());
            return OK;
        } else {
            fileWriter.close();
            fileWriter.removeFile();
            System.err.println("Incorrect checksum.");
            return BAD_CHECKSUM;
        }
    }

    /**
     * Reads the length of photo data
     *
     * @param is
     * @return byte length of photo data
     */
    private int readLength(InputStream is) {
        try {
            StringBuilder sb = new StringBuilder();
            int current = is.read();
            while ((char) current != ' ') {
                if (current == -1) {
                    System.err.println("Unexpected end of stream while reading photo data length.");
                    return -1;
                }
                if (Character.isDigit(current)) {
                    sb.append((char) current);
                } else {
                    System.err.println("Unexpected character when expecting digit: " + (char) current);
                    return -1;
                }
                current = is.read();
            }
            return Integer.parseInt(sb.toString().trim());
        } catch (IOException e) {
            System.err.println("Cannot read from the stream.");
            return -1;
        } catch (NumberFormatException e) {
            System.err.println("Received data length cannot be parsed to Integer.");
            return -1;
        }
    }

    /**
     * Reads the photo from the stream and saves it to file
     *
     * @param is
     * @param length     the length of photo data
     * @param fileWriter writer to use to output to file
     * @return photo checksum
     */
    private int readData(InputStream is, int length, FileWriter fileWriter) {
        int current = 0;
        int checksum = 0;
        try {
            for (int i = 0; i < length; ++i) {
                current = is.read();
                if (current == -1) {
                    System.err.println("Unexpected end of stream while reading photo data, expected length: " +
                            length + ", actual: " + i);
                    return -1;
                }
                checksum += current;
                // write read rata into file
                fileWriter.append(current);
            }
            // close the stream
            fileWriter.close();
            return checksum;
        } catch (IOException e) {
            System.err.println("Cannot read from the stream.");
            return -1;
        }
    }

    /**
     * Reads the photo checksum
     *
     * @param is
     * @return received checksum
     */
    private int readChecksum(InputStream is) {
        StringBuilder sb = new StringBuilder();
        int previous = 0;
        int current = 0;
        try {
            for (int i = 0; i < 4; ++i) {
                previous = current;
                current = is.read();
                if (current == -1) {
                    System.err.println("Unexpected end of stream while reading checksum, expected length: 4" +
                            ", actual: " + i);
                    return -1;
                }
                if (previous == '\r' && current == '\n') {
                    System.err.println("Unexpected end of line while reading checksum.");
                    return -1;
                }
                sb.append(Integer.toHexString(current));
            }
            // next two characters are expected to be line terminators
//            if (is.read() != '\r' || is.read() != '\n') {
//                System.err.println("End of line expected while reading checksum, but not encountered.");
//                return -1;
//            }
            return Integer.parseInt(sb.toString().trim(), 16);
        } catch (IOException e) {
            System.err.println("Cannot read from the stream.");
            return -1;
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse received checksum.");
            return -1;
        }
    }
}