package network;

import properties.Properties;
import session.KeepAliveCycle;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class NetworkController {
    private static final Logger logger = Logger.getLogger("logger");

    @SuppressWarnings("InfiniteLoopStatement")
    public void start() {
        runKeepAliveCycle();

        ServerSocket serverSocket = createServerSocket();

        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                logger.fine("A new client is connected: " + socket);

                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                //New Thread
                Runnable runnable  = new ClientHandler(socket, outputStream, inputStream);
                Thread thread = new Thread(runnable);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void runKeepAliveCycle() {
        KeepAliveCycle cycle = new KeepAliveCycle();
        Thread thread = new Thread(cycle);
        thread.start();
    }

    private ServerSocket createServerSocket() {
        int serverPort = Properties.getInt("server.port");
        logger.info(Integer.toString(serverPort));

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            logger.severe("Can't create server socket. Is port already used?\n"+e.getMessage());
            System.exit(-1);
        }
        return serverSocket;
    }
}
