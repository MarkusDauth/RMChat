package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.FriendRequest;
import view.Views;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class FriendRequestAnswerTask implements Runnable {
    private static Logger logger = Logger.getLogger("logger");
    private FriendRequest friendRequest;
    private Views views;

    public FriendRequestAnswerTask(FriendRequest friendRequest, Views views) {
        this.friendRequest = friendRequest;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Accepting friend request of "+friendRequest.getRequester());
        try {
            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("OKFRIENDREQ");
            tcpSend.add(NetworkController.getSessionID());
            tcpSend.add(friendRequest.isRequestAcceptedString());
            tcpSend.send();

            //According to the protocol no response here

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
