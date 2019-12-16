package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    final DataInputStream inputStream;
    final DataOutputStream outputStream;
    final Socket socket;
    final Logger logger;

    public ClientHandler(Socket socket,
                         DataInputStream inputStream,
                         DataOutputStream outputStream,
                         Logger logger) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.logger = logger;
    }

    @Override
    public void run() {
        String received;
        String response;

        try {
            while (true) {
                outputStream.writeUTF("Hello, this is Server");

                received = inputStream.readUTF();
                logger.info("Received: " + received);
            }

            /**
             *
            //Done
            logger.info("Client " + this.socket + " sends exit...");
            this.socket.close();
            logger.info("Connection closed");

            // closing resources
            this.inputStream.close();
            this.outputStream.close();

             **/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
