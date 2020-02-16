package controller.network;

import properties.Properties;
import view.Views;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class IncomingMessagesTask implements Runnable{

    private final Logger logger = Logger.getLogger("logger");
    private final Views views;
    private ServerSocket serverSocket;

    public IncomingMessagesTask(ServerSocket serverSocket, Views views) {
        this.views = views;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                logger.fine("A new client is connected: " + socket);

                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                if(!isConnectionFromServer(socket)) {
                    logger.severe("Message does not correspond with the server ip: " + socket);
                    socket.close();
                    continue;
                }

                //New Thread
                Runnable runnable  = new ServerHandler(socket, outputStream, inputStream, views);
                Thread thread = new Thread(runnable);
                thread.start();
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    private boolean isConnectionFromServer(Socket socket) {
        InetAddress serverINetAddress = null;
        try {
            serverINetAddress = InetAddress.getByName(Properties.getString("server.ip"));
        } catch (UnknownHostException e) {
            logger.info(e.getMessage());
        }
        InetAddress socketINetAddress = socket.getInetAddress();
        return serverINetAddress.equals(socketINetAddress);
    }
}
