package controller.network;

import model.Friend;
import properties.Properties;
import controller.database.chatDatabase.FileChatDatabase;
import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.Message;
import view.Views;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class IncomingMessagesTask implements Runnable{

    private final Logger logger = Logger.getLogger("logger");
    private final Views views;

    public IncomingMessagesTask(Views views) {
        this.views = views;
    }

    @Override
    public void run() {

        ServerSocket serverSocket = createServerSocket();

        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                logger.fine("A new client is connected: " + socket);

                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                InetAddress serverINetAddress = InetAddress.getByName(Properties.getString("server.ip"));
                InetAddress socketINetAddress = socket.getInetAddress();
                if(!serverINetAddress.equals(socketINetAddress)){
                    logger.severe("Message does not correspond with the server ip: " + socketINetAddress.getHostAddress());
                    socket.close();
                    continue;
                }

                //New Thread
                Runnable runnable  = new ServerHandler(socket, outputStream, inputStream, views);
                Thread thread = new Thread(runnable);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private ServerSocket createServerSocket() {
        int serverPort = Properties.getInt("client.port");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverSocket;
    }
}
