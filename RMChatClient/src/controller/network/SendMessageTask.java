package controller.network;

import controller.Controller;
import controller.database.chatDatabase.FileChatDatabase;
import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.Message;
import properties.UINotifications;
import view.Views;

import java.io.IOException;
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

            Socket socket = Controller.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            if(!Controller.isSessionIDSet()) {
                logger.info("Trying to send Message without logging in.");
                return;
            }
            String sessionID = Controller.getSessionID();
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

        } catch (IOException e) {
            logger.severe(e.getMessage());
            views.showMessage("UnexpectedSendMessage");
        }
        finally {
            views.finishSendMessage();
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
                    views.showMessage("UnexpectedSendMessage");
                    logger.severe("Server sent unexpected code. Code: "+code);
            }
        }
    }
}
