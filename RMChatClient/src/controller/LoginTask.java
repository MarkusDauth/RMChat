package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.LoginData;
import view.Views;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class LoginTask implements Runnable {

    private static Logger logger = Logger.getLogger("logger");
    private static Thread incomingMessagesTask = null;
    private static Thread keepAliveTask = null;

    private LoginData loginData;
    private Views views;

    LoginTask(LoginData loginData, Views views) {
        this.loginData = loginData;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Logging in: " + loginData.getUsername());

        try{
            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("LOGIN");
            tcpSend.add(loginData.getUsername());
            tcpSend.add(loginData.getPassword());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String code = tcpReceive.readNextString();

            processCode(code,tcpReceive);

        } catch (IOException e) {
            logger.severe(e.getMessage());
            views.showMessage("Unexpected");
        }
    }

    private void processCode(String code, TcpReceive tcpReceive) {
        if(code.equals("OKLOG")){
            processOKLOG(code,tcpReceive);
            finishLogin();
        }
        else{
            String errorMessage = tcpReceive.readNextString();
            logger.info("Recieved Error Message: "+errorMessage);
            views.showMessage(errorMessage);
        }
    }

    private void processOKLOG(String code, TcpReceive tcpReceive) {
        views.showChatUI();
        String sessionID = tcpReceive.readNextString();
        NetworkController.setUsername(loginData.getUsername());
        NetworkController.setPassword(loginData.getPassword());
        NetworkController.setSessionID(sessionID);
        NetworkController.setUserStatus(UserStatus.Online);
        logger.info("Recieved message: "+code);
        logger.info("SessionID: "+sessionID);
    }

    private void finishLogin() {

        //is true if the client connects to the server for the first time
        if(incomingMessagesTask == null || keepAliveTask == null) {
            incomingMessagesTask = new Thread(new IncomingMessagesTask(views));
            incomingMessagesTask.start();
            keepAliveTask = new Thread(new KeepAliveTask(views));
            keepAliveTask.start();
        }
    }
}
