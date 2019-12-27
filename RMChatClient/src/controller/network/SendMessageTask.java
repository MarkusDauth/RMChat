package controller.network;

import controller.database.chatDatabase.FileChatDatabase;
import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.Message;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class SendMessageTask implements Runnable{

    private final Logger logger = Logger.getLogger("logger");
    private final Message message;
    private final Views views;

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
            processCode(code,tcpReceive,message);

        } catch (Exception e) {
            logger.severe(e.getMessage());
            views.showMessage("Unexpected");
        }

    }

    private void processCode(String code, TcpReceive tcpReceive, Message message) {
        if(code.equals("OKSEN")){
                FileChatDatabase.getInstance().addMessage(message);
                views.addMessageToHistory(message);
                views.finishSend();
        }
        else{
            String errorMsg = tcpReceive.readNextString();
            switch(errorMsg){
                case "UserNotLoggedIn":
                    views.showMessage("UserNotLoggedIn");
                    logger.severe("Received Code UserNotLoggedIn");
                    break;
                case "RecipientNotLoggedIn":
                    views.showMessage("RecipientNotLoggedIn");
                    logger.severe("Received Code RecipientNotLoggedIn");
                    break;
                case "RecipientNotAFriend":
                    views.showMessage("RecipientNotAFriend");
                    logger.severe("Received Code RecipientNotAFriend");
                    break;
                default:
                    views.showMessage(code);
                    logger.severe("Did not catch that error at SendMessageTask: "+code);
            }
        }
    }
}
