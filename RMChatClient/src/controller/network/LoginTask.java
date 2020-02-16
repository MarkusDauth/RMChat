package controller.network;

import controller.Controller;
import controller.UserStatus;
import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.LoginData;
import view.Views;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class LoginTask implements Runnable {

    private static final Logger logger = Logger.getLogger("logger");
    private static Thread incomingMessagesTask = null;
    private static Thread keepAliveTask = null;

    private final LoginData loginData;
    private final Views views;
    private ServerSocket serverSocket;

    public LoginTask(LoginData loginData, ServerSocket serverSocket, Views views) {
        this.loginData = loginData;
        this.views = views;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        logger.info("Logging in: " + loginData.getUsername());

        try{
            Socket socket = Controller.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());



            //Data to send here
            tcpSend.add("LOGIN");
            tcpSend.add(loginData.getUsername());
            tcpSend.add(loginData.getPassword());
            tcpSend.add(Integer.toString(serverSocket.getLocalPort()));
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String code = tcpReceive.readNextString();

            processCode(code,tcpReceive);

        } catch (IOException e) {
            logger.severe(e.getMessage());
            if(firstConnect()) {
                views.showMessage("Unexpected");
            }
        }
    }

    private void processCode(String code, TcpReceive tcpReceive) {
        if(code.equals("OKLOG")){
            processOKLOG(code,tcpReceive);
            finishLogin();
        }
        else{
            String errorMessage = tcpReceive.readNextString();
            logger.severe("Received Error Message: "+errorMessage);
            if(firstConnect()) {
                views.showMessage(errorMessage);
            }
        }
    }

    private void processOKLOG(String code, TcpReceive tcpReceive) {
        views.showChatUI();
        String sessionID = tcpReceive.readNextString();
        Controller.setUsername(loginData.getUsername());
        Controller.setPassword(loginData.getPassword());
        Controller.setSessionID(sessionID);
        Controller.setUserStatus(UserStatus.Online);
        logger.info("Received message: "+code);
        logger.info("SessionID: "+sessionID);
    }

    private void finishLogin() {
        //is true if the client connects to the server for the first time
        if(firstConnect()) {
            incomingMessagesTask = new Thread(new IncomingMessagesTask(serverSocket, views));
            incomingMessagesTask.start();
            keepAliveTask = new Thread(new KeepAliveTask(views));
            keepAliveTask.start();
        }
    }

    private boolean firstConnect(){
        return (incomingMessagesTask == null || keepAliveTask == null);
    }
}
