package controller;

import javafx.application.Platform;
import model.NewUser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class NetworkController {
    private final Logger logger;
    private final Properties properties;

    public NetworkController(Logger logger, Properties properties) {
        this.logger = logger;
        this.properties = properties;
    }

    public void registerNewUser(NewUser newUser) {
        logger.info("Creating new user: " + newUser.getUserName());

        try {
            //TODO Change to server
            InetAddress ip = InetAddress.getByName("localhost");
            int serverPort = properties.getInt("server.port");
            Socket socket = new Socket(ip, serverPort);

            //TODO Change to ByteBuffer
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            while (true) {
                //Send message
                String message = newUser.getUserName();
                outputStream.writeUTF(message);

                //Read answer
                String received = inputStream.readUTF();
                logger.info("Recieved: "+received);

                Thread.sleep(1000);
            }

            /**

            //Done
            logger.info("Closing this connection : " + socket);
            socket.close();
            logger.info("Connection closed");

            // closing resources
            inputStream.close();
            outputStream.close();

             **/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
