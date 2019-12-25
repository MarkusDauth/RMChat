package controller;

import controller.chatDatabase.FileChatDatabase;
import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.Message;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class SendMessageTask implements Runnable{

    private Logger logger = Logger.getLogger("logger");
    private Message message;
    private Views views;

    public SendMessageTask(Message message, Views views) {
        this.message = message;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Sending message: " + message.getText() + " to "+message.getRecipient());

        try {

            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            if(!NetworkController.isSessionIDSet()) {
                logger.info("Trying to send Message without logging in.");
                return;
            }
            String sessionID = NetworkController.getSessionID();

            //Data to send here
            tcpSend.add("SENDMSG");
            tcpSend.add(sessionID);
            tcpSend.add(message.getRecipient());
            tcpSend.add(message.getText());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String code = tcpReceive.readNextString();
            logger.info("Received message: "+code);
            processCode(code,message);

            views.showMessage(tcpReceive.readNextString());

        } catch (Exception e) {
            logger.severe(e.getMessage());
            views.showMessage("Unexpected");
        }

    }

    private void processCode(String code, Message message) {
        switch(code){
            case "OKSEN":
                FileChatDatabase.getInstance().addMessage(message);
                views.addMessageToHistory(message);
                views.finishSend();
                break;
            case "UserNotLoggedIn":
                views.showMessage("UserNotLoggedIn");
                logger.severe("Received Code UserNotLoggedIn");
                break;
            case "RecipientNotLoggedIn":
                views.showMessage("RecipientNotLoggedIn");
                logger.severe("Received Code RecipientNotLoggedIn");
                break;
            default:
                logger.severe("ERROR TEST");
        }
    }
}
