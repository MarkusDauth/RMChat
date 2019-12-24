package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.LoginData;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class KeepAliveTask implements Runnable{

    private Views views;
    private Logger logger;
    private String sessionID;

    KeepAliveTask(Views views, Logger logger,String sessionID) {
        this.views = views;
        this.logger = logger;
        this.sessionID = sessionID;
    }

    @Override
    public void run() {
        logger.info("Sending Keep-Alive-Message");

        try{
            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("ALIVE");
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String result = tcpReceive.readNextString();

            logger.info("Recieved message: "+result);

        } catch (Exception e) {
            views.showMessage("Unexpected");
            logger.severe(e.getMessage());
        }
    }
}
