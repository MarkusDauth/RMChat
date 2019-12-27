package controller.network;

import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.NewUser;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class RegistrationTask implements Runnable {

    private final Logger logger = Logger.getLogger("logger");
    private final Views views;
    private final NewUser newUser;

    public RegistrationTask(NewUser newUser, Views views) {
        this.newUser = newUser;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Creating new user: " + newUser.getUsername());

        try {

            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("REGISTER");
            tcpSend.add(newUser.getUsername());
            tcpSend.add(newUser.getPassword());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String result = tcpReceive.readNextString();
            if(result.equals("OKREG")) {
                logger.info("Received message: " + result);
                views.showMessage(tcpReceive.readNextString());
            }
            else{
                String errorMessage = tcpReceive.readNextString();
                logger.info("Recieved Error Message: "+errorMessage);
                views.showMessage(errorMessage);
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            views.showMessage("Unexpected");
        }
    }
}
