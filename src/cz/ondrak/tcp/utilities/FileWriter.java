package cz.ondrak.tcp.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Writes data into file.
 */
public class FileWriter {

    private static int fileNo = 1;
    private FileOutputStream fos;
    private String fileName;

    public FileWriter() {
        try {
            fileName = "foto" + String.format("%03d", fileNo++) + ".png";
            this.fos = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            System.err.println("File not found while opening FileOutputStream.");
        }
    }

    /**
     * Appends the file with incoming data
     *
     * @param i data to write to file
     * @return true if writing was successful
     */
    public boolean append(int i) {
        try {
            if (fos != null) {
                fos.write(i);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error while writing to file.");
            return false;
        }
    }

    /**
     * Closes the stream.
     *
     * @return true if closing was successful
     */
    public boolean close() {
        try {
            if (fos != null) {
                fos.flush();
                fos.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error while closing FileOutputStream.");
            return false;
        }
    }

    /**
     * Remove the created file.
     *
     * @return true if file was successfully removed
     */
    public boolean removeFile() {
        File file = new File(fileName);
        return file.delete();
    }

    public String getFileName() {
        return fileName;
    }
}