package controller.network;

import controller.Controller;
import controller.database.chatDatabase.FileChatDatabase;
import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.Friend;
import model.FriendRequest;
import model.Message;
import properties.Properties;
import view.Views;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class ServerHandler implements Runnable {
    private final Socket socket;
    private static final Logger logger = Logger.getLogger("logger");
    private final TcpSend tcpSend;
    private final TcpReceive tcpReceive;
    private final Views views;

    public ServerHandler(Socket socket, OutputStream outputStream, InputStream inputStream, Views views) {
        this.socket = socket;
        tcpSend = new TcpSend(outputStream);
        tcpReceive = new TcpReceive(inputStream);
        this.views = views;
    }

    @Override
    public void run() {
        {
            try {
                tcpReceive.receive();
                String code = tcpReceive.readNextString();
                logger.fine("New Client message. Code: " + code);
                switch (code) {
                    case "RECMSG":
                        processReceiveMessage(tcpReceive,tcpSend,socket);
                        break;
                    case "FRIENDREQ":
                        int timeout = Properties.getInt("tcp.friendRequestTimeout");
                        socket.setSoTimeout(timeout*1000);
                        processFriendRequest(tcpReceive);
                        break;
                    default:
                        logger.severe("didnt catch that error");
                }
                socket.close(); //Can't be in finally block, because of exception
                logger.fine("Socket closed. Port: " + socket);
            } catch (IOException e) {
                logger.severe(String.valueOf(e.getMessage()));
            }
        }
    }

    private void processFriendRequest(TcpReceive tcpReceive) {
        String sender = tcpReceive.readNextString();
        Friend friend = new Friend(sender);
        Future<FriendRequest> friendRequestFuture= views.showFriendRequest(friend);
        try {
            FriendRequest friendRequest = friendRequestFuture.get();
            tcpSend.add("OKFRIENDREQ");
            tcpSend.add(Controller.getSessionID());
            tcpSend.add(friendRequest.isRequestAcceptedString());
            tcpSend.send();
            tcpReceive.receive();
            processCode(tcpReceive);
            if(friendRequest.isRequestAccepted()){
                views.showMessage("OKFRIENDREQ");
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private void processCode(TcpReceive tcpReceive) {
        String code = tcpReceive.readNextString();
        views.showMessage(code);
    }


    private void processReceiveMessage(TcpReceive tcpReceive, TcpSend tcpSend, Socket socket) throws IOException {
        String sender = tcpReceive.readNextString();
        String text = tcpReceive.readNextString();
        Message message = new Message(sender, Controller.getUsername(),text);
        FileChatDatabase.getInstance().addMessage(message);
        views.addMessageToHistory(message);
        tcpSend.add("OKREC");
        tcpSend.add(Controller.getSessionID());
        tcpSend.send();
        socket.close();
    }
}