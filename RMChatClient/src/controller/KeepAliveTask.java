package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import view.Views;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class KeepAliveTask implements Runnable{

    private static Logger logger = Logger.getLogger("logger");

    private Views views;
    private String sessionID;
    private int keepAliveTimeout = Properties.getInt("client.keepAliveTimeout.seconds");

    public KeepAliveTask(Views views, String sessionID) {
        this.views = views;
        this.sessionID = sessionID;
    }

    @Override
    public void run() {

        while(true){
            keepAliveRoutine();
            try {
                TimeUnit.SECONDS.sleep(keepAliveTimeout);
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        }
    }
    private void keepAliveRoutine() {
        logger.fine("Sending Keep-Alive-Message");

        try{
            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("ALIVE");
            tcpSend.add(sessionID);
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String code = tcpReceive.readNextString();
            logger.fine("Recieved message: "+code);
            processCode(code,tcpReceive);


        } catch(IOException e) {
            views.showMessage("Unexpected");
            views.setIndexStatus("OfflineStatus");
            logger.severe(e.getMessage());
        }
    }

    private void processCode(String code, TcpReceive tcpReceive) {
        if(code.equals("OKALV")){
            views.setIndexStatus("OnlineStatus");
            return;
        }
        if(tcpReceive.readNextString().equals("UserNotLoggedIn")){
            views.setIndexStatus("OfflineStatus");
            views.showMessage("UserNotLoggedIn");
        }
    }
}
