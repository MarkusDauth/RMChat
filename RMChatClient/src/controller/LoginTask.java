package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.LoginData;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class LoginTask implements Runnable {

    private LoginData loginData;
    private Views views;
    private Logger logger;

    LoginTask(LoginData loginData, Views views, Logger logger) {
        this.loginData = loginData;
        this.views = views;
        this.logger = logger;
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
            String result = tcpReceive.readNextString();

            logger.info("Recieved message: "+result);
            if(result.equals("OKLOG")){
                views.showIndexUI();
                String sessionID = tcpReceive.readNextString();
                logger.info("SessionID: "+sessionID);
                new Thread(new KeepAliveTask(views,logger,sessionID)).start();
            }
            else{
                views.showMessage(tcpReceive.readNextString());
            }
        } catch (Exception e) {
            views.showMessage("Unexpected");
        }
    }
}
