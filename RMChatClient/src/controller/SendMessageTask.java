package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.Message;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class SendMessageTask implements Runnable{

    private Message message;
    private Views views;
    private Logger logger;

    public SendMessageTask(Message message, Views views, Logger logger) {
        this.message = message;
        this.views = views;
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.info("Sending message: " + message.getText() + " to "+message.getRecipient());

        try {

            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            if(!NetworkController.sessionID.isPresent()) {
                logger.info("Trying to send Message without logging in.");
                return;
            }
            String sessionID = NetworkController.sessionID.get();

            //Data to send here
            tcpSend.add("SENDMSG");
            tcpSend.add(sessionID);
            tcpSend.add(message.getRecipient());
            tcpSend.add(message.getText());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String result = tcpReceive.readNextString();
            switch(result){
                case "OKSEN":
                    views.showMessage("OKSEN");
                case "UserNotLoggedIn":
                    views.showMessage("UserNotLoggedIn");
                    break;
                case "RecipientNotLoggedIn":
                    views.showMessage("RecipientNotLoggedIn");
                    break;
            }
            logger.info("Received message: "+result);

            views.showMessage(tcpReceive.readNextString());

        } catch (Exception e) {
            logger.severe(e.getMessage());
            views.showMessage("Unexpected");
        }

    }
}
