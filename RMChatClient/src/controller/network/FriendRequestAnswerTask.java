package controller.network;

import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.FriendRequest;
import view.Views;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class FriendRequestAnswerTask implements Runnable {
    private static final Logger logger = Logger.getLogger("logger");
    private final FriendRequest friendRequest;
    private final Views views;

    public FriendRequestAnswerTask(FriendRequest friendRequest, Views views) {
        this.friendRequest = friendRequest;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Sending answer to a friend request from "+friendRequest.getRequester());
        try {
            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
