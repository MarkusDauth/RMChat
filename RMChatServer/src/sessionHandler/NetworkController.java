package sessionHandler;

import properties.Properties;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class NetworkController {
    private static Logger logger = Logger.getLogger("logger");

    public void start() {
        int serverPort = Properties.getInt("server.port");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();
                logger.info("A new client is connected: " + socket);

                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                //New Thread
                Runnable runnable  = new ClientHandler(socket, outputStream, inputStream);
                Thread thread = new Thread(runnable);
                thread.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
