package controller;

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


    //TODO wird gebraucht für View??? Eventuell Properties Konstruktor in main stecken
    public NetworkController() {
        this.logger = Logger.getLogger("logger");
        this.properties = new Properties();
    }

    public void registerNewUser(NewUser newUser) {
        logger.info("Creating new user: " + newUser.getUserName());

        try {
            InetAddress ip = InetAddress.getByName("localhost");
            int port = properties.getInt("server.port");

            // establish the connection with server port 5056
            Socket socket = new Socket(ip, port);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true) {
                System.out.println(dis.readUTF());
                String tosend = newUser.getUserName();
                dos.writeUTF(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if (tosend.equals("EXIT")) {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }

                // printing date or time as requested by client
                String received = dis.readUTF();
                System.out.println(received);
            }

            // closing resources
            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
