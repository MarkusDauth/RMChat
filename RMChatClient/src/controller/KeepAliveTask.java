package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import view.Views;

import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class KeepAliveTask implements Runnable{

    private Views views;
    private Logger logger;
    private String sessionID;
    private int keepAliveTimeout = Properties.getInt("client.keepAliveTimeout.SECONDS");

    public KeepAliveTask(Views views, Logger logger, String sessionID) {
        logger.info("Initializing Keep-Alive-Routine");
        this.logger = logger;
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
