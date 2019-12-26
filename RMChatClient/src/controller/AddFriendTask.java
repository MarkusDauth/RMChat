package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.Friend;
import model.LoginData;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class AddFriendTask implements Runnable{

    private static Logger logger = Logger.getLogger("logger");

    private Friend friend;
    private Views views;

    AddFriendTask(Friend friend, Views views) {
        this.friend = friend;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Adding Friend: " + friend.getUsername());

        try{
            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("ADDFRIEND");
            tcpSend.add(NetworkController.getSessionID());
            tcpSend.add(friend.getUsername());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String code = tcpReceive.readNextString();

            processCode(code,tcpReceive);

        } catch (Exception e) {
            views.showMessage("Unexpected");
        }
    }

    private void processCode(String code, TcpReceive tcpReceive) {
        if(code.equals("OKFRIEND")){
            logger.info("Recieved message: "+code);
            views.showMessage(code);
        }
        else{
            String errorMessage = tcpReceive.readNextString();
            logger.info("Received Error Message: "+errorMessage);
            views.showMessage(errorMessage);
        }
    }
}
