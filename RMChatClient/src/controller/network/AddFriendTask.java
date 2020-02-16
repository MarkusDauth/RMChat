package controller.network;

import controller.Controller;
import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.Friend;
import properties.Properties;
import view.Views;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class AddFriendTask implements Runnable{

    private static final Logger logger = Logger.getLogger("logger");

    private final Friend friend;
    private final Views views;

    public AddFriendTask(Friend friend, Views views) {
        this.friend = friend;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Adding Friend: " + friend.getUsername());

        try{
            Socket socket = Controller.createSocket();
            int timeout = Properties.getInt("tcp.friendRequestTimeout.seconds");
            socket.setSoTimeout(timeout*1000);
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("ADDFRIEND");
            tcpSend.add(Controller.getSessionID());
            tcpSend.add(friend.getUsername());
            tcpSend.send();

            //Server response here
            //Sender does not receive anything yet
            tcpReceive.receive();
            String code = tcpReceive.readNextString();

            processCode(code,tcpReceive);

        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private void processCode(String code, TcpReceive tcpReceive) {
        if(code.equals("OKFRIEND")){
            logger.info("Recieved message: "+code);
            views.showMessage(code);
        }
        else{
            String errorMessage = tcpReceive.readNextString();
            if(!errorMessage.equals("")) {
                logger.severe("Received Error Code: " + code + "Message: " + errorMessage);
                views.showMessage(errorMessage);
            }
        }
    }
}
