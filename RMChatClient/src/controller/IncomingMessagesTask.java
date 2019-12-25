package controller;

import controller.chatDatabase.FileChatDatabase;
import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import javafx.application.Platform;
import model.Message;
import view.IndexEventHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class IncomingMessagesTask implements Runnable{

    Logger logger = Logger.getLogger("logger");
    IndexEventHandler indexEventHandler;

    public IncomingMessagesTask(IndexEventHandler indexEventHandler) {
        this.indexEventHandler = indexEventHandler;
    }

    @Override
    public void run() {

        ServerSocket serverSocket = createServerSocket();

        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                logger.info("Got new Message " + socket);

                TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());
                TcpSend tcpSend = new TcpSend(socket.getOutputStream());

                tcpReceive.receive();
                String code = tcpReceive.readNextString();
                logger.info("Received Code: "+code);
                switch(code){
                    case "RECMSG":
                        String sender = tcpReceive.readNextString();
                        String text = tcpReceive.readNextString();
                        //indexEventHandler.saveMessage(sender,text);

                        Message message = new Message(NetworkController.getUsername(),sender,text);

                        FileChatDatabase.getInstance().addMessage(message);
                        break;
                    default:
                        logger.info("didnt catch that error");
                        continue;
                }
                tcpSend.add("OKREC");
                tcpSend.add(NetworkController.sessionID.get());
                tcpSend.send();
                socket.close();
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
