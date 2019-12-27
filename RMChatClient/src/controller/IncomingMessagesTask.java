package controller;

import model.Friend;
import properties.Properties;
import controller.chatDatabase.FileChatDatabase;
import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.Message;
import view.Views;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class IncomingMessagesTask implements Runnable{

    private Logger logger = Logger.getLogger("logger");
    private Views views;

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
                logger.info("Got new Message " + socket);
//                InetSocketAddress sockaddr = (InetSocketAddress)socket.getRemoteSocketAddress();
//                if(!socketAddress.equals(Properties.getString("server.ip"))){
//                    logger.severe("Message does not correspond with the server ip: " + socketAddress);
//                    socket.close();
//                    continue;
//                }
                if(!(NetworkController.getUserStatus() == UserStatus.Online)){
                    logger.severe("Client is not online. Discarding Message.");
                    socket.close();
                    continue;
                }
                TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());
                TcpSend tcpSend = new TcpSend(socket.getOutputStream());

                tcpReceive.receive();
                String code = tcpReceive.readNextString();
                logger.info("Received Code: "+code);
                switch(code){
                    case "RECMSG":
                        processReceiveMessage(tcpReceive,tcpSend,socket);
                        break;
                    case "FRIENDREQ":
                        processFriendRequest(tcpReceive);
                        break;
                    default:
                        logger.severe("didnt catch that error");
                }
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        }

    }

    private void processFriendRequest(TcpReceive tcpReceive) {
        String sender = tcpReceive.readNextString();
        views.showFriendRequest(sender);
    }

    private void processReceiveMessage(TcpReceive tcpReceive, TcpSend tcpSend, Socket socket) throws IOException {
        String sender = tcpReceive.readNextString();
        String text = tcpReceive.readNextString();
        Message message = new Message(sender,NetworkController.getUsername(),text);
        FileChatDatabase.getInstance().addMessage(message);
        views.addMessageToHistory(message);
        tcpSend.add("OKREC");
        tcpSend.add(NetworkController.getSessionID());
        tcpSend.send();
        socket.close();
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
