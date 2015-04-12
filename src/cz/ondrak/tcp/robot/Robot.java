package cz.ondrak.tcp.robot;

/**
 * TCP Server for communication via socket. Supports multiple message types, authentication and can receive
 * image data and save it into file.
 *
 * @author Ondrej Kratochvil
 */
public class Robot {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing port number. Please enter port number in range 3000 - 3999.");
        } else {
            try {
                int port = Integer.parseInt(args[0]);
                if (port < 3000 || port > 3999) {
                    throw new IllegalArgumentException("Port number " + port + " is not within range 3000 - 3999.");
                }

                // Starting server
                Server server = new Server(port);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The first argument \"" + args[0] + "\" is not an integer. " +
                        "Please enter port number in range 3000 - 3999.");
            }
        }
    }
}





















